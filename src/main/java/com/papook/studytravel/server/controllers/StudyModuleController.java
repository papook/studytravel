package com.papook.studytravel.server.controllers;

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

import com.papook.studytravel.server.ServerConfiguration;
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

    @GetMapping(ServerConfiguration.MODULE_BASE)
    public ResponseEntity<Iterable<StudyModule>> getCollection() {
        // TODO: Set up pagination and filtering
        Iterable<StudyModule> studyModules = studyModuleService.getAllModules();
        return ResponseEntity.ok(studyModules);
    }

    @GetMapping(ServerConfiguration.MODULE_BASE + "/{id}")
    public ResponseEntity<StudyModule> getOne(@PathVariable Long id) {
        Optional<StudyModule> studyModuleOptional = studyModuleService.getModuleById(id);
        if (studyModuleOptional.isPresent()) {
            return ResponseEntity.ok(studyModuleOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(ServerConfiguration.UNIVERSITY_BASE +
            "/{universityId}" +
            ServerConfiguration.MODULE_BASE +
            "/{moduleId}")
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

    @PostMapping(ServerConfiguration.MODULE_BASE)
    public ResponseEntity<Void> create(@RequestBody StudyModule studyModule) {
        URI location = studyModuleService.createModule(studyModule);

        return ResponseEntity.created(location).build();
    }

    @PostMapping(ServerConfiguration.UNIVERSITY_BASE +
            "/{universityId}" +
            ServerConfiguration.MODULE_BASE)
    public ResponseEntity<Void> createForUniversity(
            @PathVariable Long universityId,
            @RequestBody StudyModule studyModule) {
        URI location = studyModuleService.createModuleForUniversity(universityId, studyModule);

        return ResponseEntity.created(location).build();
    }

    @PutMapping(ServerConfiguration.MODULE_BASE + "/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody StudyModule entity) {
        if (entity.getId() != id)
            return ResponseEntity.badRequest().build();

        Optional<URI> locationOptional = studyModuleService.updateModule(id, entity);
        if (locationOptional.isPresent()) {
            return ResponseEntity.created(locationOptional.get()).build();
        } else {
            return ResponseEntity.noContent().build();
        }
    }
}
