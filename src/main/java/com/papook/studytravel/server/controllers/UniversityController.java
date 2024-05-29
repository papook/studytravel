package com.papook.studytravel.server.controllers;

import java.net.URI;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.papook.studytravel.server.ServerConfiguration;
import com.papook.studytravel.server.models.University;
import com.papook.studytravel.server.services.UniversityService;

@RestController
@RequestMapping(ServerConfiguration.UNIVERSITY_BASE)
public class UniversityController {
    @Autowired
    private UniversityService universityService;

    @GetMapping
    public ResponseEntity<Iterable<University>> getCollection() {
        // TODO: Set up pagination and filtering
        Iterable<University> universities = universityService.getAllUniversities();
        return ResponseEntity.ok(universities);
    }

    @GetMapping("/{id}")
    public ResponseEntity<University> getOne(@PathVariable Long id) {
        Optional<University> universityOptional = universityService.getUniversityById(id);
        if (universityOptional.isPresent()) {
            return ResponseEntity.ok(universityOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody University university) {
        URI location = universityService.createUniversity(university);

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody University entity) {
        if (entity.getId() != id)
            return ResponseEntity.badRequest().build();

        Optional<URI> locationOptional = universityService.updateUniversity(id, entity);
        if (locationOptional.isPresent()) {
            return ResponseEntity.created(locationOptional.get()).build();
        } else {
            return ResponseEntity.noContent().build();
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        universityService.deleteUniversity(id);
        return ResponseEntity.noContent().build();
    }
}
