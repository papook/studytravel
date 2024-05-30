package com.papook.studytravel.server.controllers;

import static com.papook.studytravel.server.ServerConfiguration.MODULE_BASE;
import static com.papook.studytravel.server.ServerConfiguration.UNIVERSITY_BASE;

import java.net.URI;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.papook.studytravel.server.models.StudyModule;
import com.papook.studytravel.server.models.University;
import com.papook.studytravel.server.services.StudyModuleService;
import com.papook.studytravel.server.services.UniversityService;

@RestController
@RequestMapping
public class StudyModuleController {

    @Autowired
    private UniversityService universityService;

    @Autowired
    private StudyModuleService studyModuleService;

    @GetMapping(MODULE_BASE)
    public ResponseEntity<Iterable<StudyModule>> getCollection() {
        // TODO: Set up pagination and filtering
        Iterable<StudyModule> studyModules = studyModuleService.getAllModules();
        return ResponseEntity.ok(studyModules);
    }

    @GetMapping(MODULE_BASE + "/{id}")
    public ResponseEntity<StudyModule> getOne(@PathVariable Long id) {
        Optional<StudyModule> studyModuleOptional = studyModuleService.getModuleById(id);
        return ResponseEntity.of(studyModuleOptional);
    }

    @GetMapping(UNIVERSITY_BASE + "/{universityId}" + MODULE_BASE)
    public ResponseEntity<Iterable<StudyModule>> getCollectionOfUniversity(@PathVariable Long universityId) {
        // TODO: Set up pagination and filtering

        Optional<University> universityOptional = universityService.getUniversityById(universityId);

        if (universityOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Iterable<StudyModule> studyModules = studyModuleService.getModulesForUniversity(universityId);

        return ResponseEntity.ok(studyModules);
    }

    @GetMapping(UNIVERSITY_BASE + "/{universityId}" + MODULE_BASE + "/{moduleId}")
    public ResponseEntity<StudyModule> getOneOfUniversity(
            @PathVariable Long universityId,
            @PathVariable Long moduleId) {

        Optional<University> universityOptional = universityService.getUniversityById(universityId);

        if (universityOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<StudyModule> studyModuleOptional = studyModuleService.getModuleById(moduleId);
        if (studyModuleOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        boolean moduleBelongsToUniversity = studyModuleOptional.get().getUniversityId().equals(universityId);
        if (moduleBelongsToUniversity) {
            return ResponseEntity.ok(studyModuleOptional.get());
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping(MODULE_BASE)
    public ResponseEntity<Void> create(@RequestBody StudyModule studyModule) {
        URI location = studyModuleService.createModule(studyModule);

        return ResponseEntity.created(location).build();
    }

    @PostMapping(UNIVERSITY_BASE + "/{universityId}" + MODULE_BASE)
    public ResponseEntity<Void> createForUniversity(
            @PathVariable Long universityId,
            @RequestBody StudyModule studyModule) {
        URI location = studyModuleService.createModuleForUniversity(universityId, studyModule);

        return ResponseEntity.created(location).build();
    }

    @PutMapping(MODULE_BASE + "/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody StudyModule entity) {
        if (!entity.getId().equals(id))
            return ResponseEntity.badRequest().build();

        Optional<URI> locationOptional = studyModuleService.updateModule(id, entity);
        if (locationOptional.isPresent()) {
            return ResponseEntity.created(locationOptional.get()).build();
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    /**
     * Updates a study module for a specific university.
     */
    @PutMapping(UNIVERSITY_BASE + "/{universityId}" + MODULE_BASE + "/{moduleId}")
    public ResponseEntity<Void> updateForUniversity(
            @PathVariable Long universityId,
            @PathVariable Long moduleId,
            @RequestBody StudyModule studyModule) {

        // Check if the ID of the provided StudyModule matches the moduleId in the path
        if (studyModule.getId() != moduleId) {
            // If IDs do not match, return a 400 Bad Request response
            return ResponseEntity.badRequest().build();
        }

        // Retrieve the university by its ID using the university service
        Optional<University> universityOptional = universityService.getUniversityById(universityId);
        // Check if the university exists
        if (universityOptional.isEmpty()) {
            // If the university is not found, return a 404 Not Found response
            return ResponseEntity.notFound().build();
        }

        // Set the university ID of the StudyModule to
        // ensure it is associated with the
        // correct university
        studyModule.setUniversityId(universityId);

        // Update the study module for the specified
        // university and module
        Optional<URI> locationOptional = studyModuleService.updateModuleForUniversity(
                universityId,
                moduleId,
                studyModule);

        // Check if the update operation returned a location URI
        if (locationOptional.isPresent()) {
            // If a location URI is present, return a 201 Created response with the URI of
            // the created resource
            return ResponseEntity.created(locationOptional.get()).build();
        } else {
            // If no location URI is provided, return a 204 No Content response indicating a
            // successful update with no content
            return ResponseEntity.noContent().build();
        }
    }
}
