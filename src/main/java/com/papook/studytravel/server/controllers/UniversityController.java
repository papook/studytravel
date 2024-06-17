package com.papook.studytravel.server.controllers;

import static com.papook.studytravel.server.ServerConfiguration.UNIVERSITY_ENDPOINT;

import java.net.URI;
import java.util.List;
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

import com.papook.studytravel.server.models.University;
import com.papook.studytravel.server.services.UniversityService;
import com.papook.studytravel.server.utils.HypermediaGenerator;

import jakarta.validation.Valid;

@RestController
@RequestMapping(UNIVERSITY_ENDPOINT)
public class UniversityController {
    @Autowired
    private UniversityService universityService;

    @Autowired
    private HypermediaGenerator hypermediaGenerator;

    @GetMapping
    public ResponseEntity<Iterable<University>> getCollection(
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "") String country,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "id_asc") String sort) {

        page = Math.max(0, page);

        Page<University> universitiesPage = universityService.getUniversities(name, country, page, sort);

        List<University> responseBody = universitiesPage.getContent();
        HttpHeaders headers = hypermediaGenerator.buildPagingLinksHeaders(universitiesPage);

        return ResponseEntity.ok()
                .headers(headers)
                .body(responseBody);

    }

    @GetMapping("/{id}")
    public ResponseEntity<University> getOne(@PathVariable Long id) {
        University university = universityService.getUniversityById(id);

        HttpHeaders headers = new HttpHeaders();

        String formattedEndpoint = String.format("%s/%d", UNIVERSITY_ENDPOINT, university.getId());
        String updateLink = HypermediaGenerator.formatLinkHeader(formattedEndpoint, "putUpdate");
        String deleteLink = HypermediaGenerator.formatLinkHeader(formattedEndpoint, "delete");

        // A template for the URI of a module belonging to the university
        String moduleUriTemplate = university.getModules().getPath() + "/{moduleId}";
        String linkModule = HypermediaGenerator.formatLinkHeader(moduleUriTemplate, "putLinkModule");
        String unlinkModule = HypermediaGenerator.formatLinkHeader(moduleUriTemplate, "delUnlinkModule");
        String getModuleOfUniversity = HypermediaGenerator.formatLinkHeader(moduleUriTemplate, "getModuleOfUniversity");

        headers.add(HttpHeaders.LINK, updateLink);
        headers.add(HttpHeaders.LINK, deleteLink);
        headers.add(HttpHeaders.LINK, linkModule);
        headers.add(HttpHeaders.LINK, unlinkModule);
        headers.add(HttpHeaders.LINK, getModuleOfUniversity);

        return ResponseEntity.ok()
                .headers(headers)
                .body(university);
    }

    @PostMapping
    public ResponseEntity<University> create(@Valid @RequestBody University university) {
        // Call the service to create the University
        // and get the location URI
        URI location = universityService.createUniversity(university);

        // Return the representation with the location header
        // and the newly created University representation
        return ResponseEntity
                .created(location)
                .body(university);
    }

    @PutMapping("/{id}")
    public ResponseEntity<University> update(@PathVariable Long id, @Valid @RequestBody University entity) {
        // Check if the ID in the path and the ID in the entity match
        if (!entity.getId().equals(id))
            return ResponseEntity.badRequest().build();

        // Call the service to update the University
        Optional<URI> locationOptional = universityService.updateUniversity(id, entity);

        // Check if the University was created
        boolean newUniversityCreated = locationOptional.isPresent();

        if (newUniversityCreated) {
            URI location = locationOptional.get();

            // Return the representation with the location header
            return ResponseEntity.created(location).body(entity);

        } else {
            HttpHeaders headers = new HttpHeaders();

            String formattedEndpoint = String.format("%s/%d", UNIVERSITY_ENDPOINT, id);
            String getLink = HypermediaGenerator.formatLinkHeader(formattedEndpoint, "getSelf");

            headers.add(HttpHeaders.LINK, getLink);

            // Return a NO CONTENT status code
            return ResponseEntity.noContent()
                    .headers(headers)
                    .build();
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        universityService.deleteUniversity(id);
        HttpHeaders headers = new HttpHeaders();

        String formattedEndpoint = String.format("%s", UNIVERSITY_ENDPOINT);
        String getUniversitiesLink = HypermediaGenerator.formatLinkHeader(formattedEndpoint,
                "getUniversitiesCollection");

        headers.add(HttpHeaders.LINK, getUniversitiesLink);

        return ResponseEntity.noContent()
                .headers(headers)
                .build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAll() {
        universityService.deleteAllUniversities();
        HttpHeaders headers = new HttpHeaders();

        String formattedEndpoint = String.format("%s", UNIVERSITY_ENDPOINT);
        String getUniversitiesLink = HypermediaGenerator.formatLinkHeader(formattedEndpoint,
                "getUniversitiesCollection");

        headers.add(HttpHeaders.LINK, getUniversitiesLink);

        return ResponseEntity.noContent()
                .headers(headers)
                .build();
    }

}
