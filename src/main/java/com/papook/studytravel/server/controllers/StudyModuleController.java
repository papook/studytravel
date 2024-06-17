package com.papook.studytravel.server.controllers;

import static com.papook.studytravel.server.ServerConfiguration.MODULE_ENDPOINT;
import static com.papook.studytravel.server.ServerConfiguration.UNIVERSITY_ENDPOINT;
import static com.papook.studytravel.server.utils.HypermediaGenerator.formatLinkHeader;

import java.net.URI;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.papook.studytravel.server.models.StudyModule;
import com.papook.studytravel.server.services.StudyModuleService;
import com.papook.studytravel.server.utils.HypermediaGenerator;

import jakarta.validation.Valid;

@RestController
@RequestMapping
public class StudyModuleController {

    @Autowired
    private StudyModuleService studyModuleService;

    @Autowired
    private HypermediaGenerator hypermediaGenerator;

    @GetMapping(MODULE_ENDPOINT)
    public ResponseEntity<Iterable<StudyModule>> getCollection(
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "") String semester,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "id_asc") String sort) {
        page = Math.max(0, page);

        Page<StudyModule> studyModules = studyModuleService.getModules(name, semester, page, sort);
        HttpHeaders headers = hypermediaGenerator.buildPagingLinksHeaders(studyModules);

        return ResponseEntity.ok()
                .headers(headers)
                .body(studyModules.getContent());
    }

    @GetMapping(MODULE_ENDPOINT + "/{id}")
    public ResponseEntity<StudyModule> getOne(@PathVariable Long id) {
        StudyModule studyModule = studyModuleService.getModuleById(id);

        HttpHeaders headers = new HttpHeaders();

        String formattedEndpoint = String.format("%s/%d", MODULE_ENDPOINT, id);
        String updateLink = formatLinkHeader(formattedEndpoint, "update");
        String deleteLink = formatLinkHeader(formattedEndpoint, "delete");

        headers.add(HttpHeaders.LINK, updateLink);
        headers.add(HttpHeaders.LINK, deleteLink);

        return ResponseEntity.ok()
                .headers(headers)
                .body(studyModule);
    }

    @GetMapping(UNIVERSITY_ENDPOINT + "/{universityId}" + MODULE_ENDPOINT)
    public ResponseEntity<Iterable<StudyModule>> getCollectionOfUniversity(
            @PathVariable Long universityId,
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "") String semester,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "id_asc") String sort) {
        Page<StudyModule> studyModules = studyModuleService.getModulesForUniversity(
                universityId,
                name,
                semester,
                page,
                sort);

        HttpHeaders headers = hypermediaGenerator.buildPagingLinksHeaders(studyModules);

        return ResponseEntity.ok()
                .headers(headers)
                .body(studyModules.getContent());
    }

    @GetMapping(UNIVERSITY_ENDPOINT + "/{universityId}" + MODULE_ENDPOINT + "/{moduleId}")
    public ResponseEntity<StudyModule> getOneOfUniversity(
            @PathVariable Long universityId,
            @PathVariable Long moduleId) {
        StudyModule studyModule = studyModuleService.getModuleForUniversity(universityId, moduleId);

        HttpHeaders headers = new HttpHeaders();

        String updateLink = String.format("%s/%d", MODULE_ENDPOINT, studyModule.getId());
        updateLink = formatLinkHeader(updateLink, "update");

        String deleteLink = String.format("%s/%d", MODULE_ENDPOINT, studyModule.getId());
        deleteLink = formatLinkHeader(deleteLink, "delete");

        headers.add(HttpHeaders.LINK, updateLink);
        headers.add(HttpHeaders.LINK, deleteLink);

        return ResponseEntity.ok()
                .headers(headers)
                .body(studyModule);
    }

    @PostMapping(MODULE_ENDPOINT)
    public ResponseEntity<StudyModule> create(@Valid @RequestBody StudyModule studyModule) {
        URI location = studyModuleService.createModule(studyModule);

        return ResponseEntity.created(location).body(studyModule);
    }

    @PutMapping(MODULE_ENDPOINT + "/{id}")
    public ResponseEntity<StudyModule> update(@PathVariable Long id, @Valid @RequestBody StudyModule entity) {
        if (!entity.getId().equals(id))
            return ResponseEntity.badRequest().build();

        Optional<URI> locationOptional = studyModuleService.updateModule(id, entity);
        boolean isModuleCreated = locationOptional.isPresent();

        if (isModuleCreated) {
            return ResponseEntity.created(locationOptional.get()).body(entity);
        } else {
            HttpHeaders headers = new HttpHeaders();

            String formattedEndpoint = String.format("%s/%d", MODULE_ENDPOINT, id);
            String getModuleLink = formatLinkHeader(formattedEndpoint, "getSelf");

            headers.add(HttpHeaders.LINK, getModuleLink);
            return ResponseEntity.noContent()
                    .headers(headers)
                    .build();
        }
    }

    @DeleteMapping(MODULE_ENDPOINT + "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        String getModulesCollectionLink = formatLinkHeader(MODULE_ENDPOINT, "getModulesCollection");

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LINK, getModulesCollectionLink);

        studyModuleService.deleteModule(id);
        return ResponseEntity.noContent()
                .headers(headers)
                .build();
    }

    /**
     * Links a study module to a specific university.
     */
    @PutMapping(UNIVERSITY_ENDPOINT + "/{universityId}" + MODULE_ENDPOINT + "/{moduleId}")
    public ResponseEntity<Void> linkToUniversity(
            @PathVariable Long universityId,
            @PathVariable Long moduleId) {
        studyModuleService.linkModuleToUniversity(moduleId, universityId);

        HttpHeaders headers = new HttpHeaders();
        String formattedEndpoint = String.format("%s/%d%s", UNIVERSITY_ENDPOINT, universityId, MODULE_ENDPOINT);
        String getModulesLink = formatLinkHeader(formattedEndpoint, "getModulesOfUniversity");

        headers.add(HttpHeaders.LINK, getModulesLink);

        return ResponseEntity.noContent()
                .headers(headers)
                .build();
    }

    /**
     * Removes a study module from a specific university.
     */
    @DeleteMapping(UNIVERSITY_ENDPOINT + "/{universityId}" + MODULE_ENDPOINT + "/{moduleId}")
    public ResponseEntity<Void> unlinkFromUniversity(
            @PathVariable Long universityId,
            @PathVariable Long moduleId) {
        studyModuleService.unlinkModuleFromUniversity(moduleId, universityId);

        HttpHeaders headers = new HttpHeaders();
        String formattedEndpoint = String.format("%s/%d%s", UNIVERSITY_ENDPOINT, universityId, MODULE_ENDPOINT);
        String getModulesLink = formatLinkHeader(formattedEndpoint, "getModulesOfUniversity");

        headers.add(HttpHeaders.LINK, getModulesLink);

        return ResponseEntity.noContent()
                .headers(headers)
                .build();
    }

    @DeleteMapping(MODULE_ENDPOINT)
    public ResponseEntity<Void> deleteAll() {
        studyModuleService.deleteAllModules();
        HttpHeaders headers = new HttpHeaders();

        String formattedEndpoint = String.format("%s", MODULE_ENDPOINT);
        String getModulesLink = formatLinkHeader(formattedEndpoint, "getModulesCollection");

        headers.add(HttpHeaders.LINK, getModulesLink);

        return ResponseEntity.noContent()
                .headers(headers)
                .build();
    }
}
