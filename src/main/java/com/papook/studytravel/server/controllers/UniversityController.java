package com.papook.studytravel.server.controllers;

import static com.papook.studytravel.server.ServerConfiguration.UNIVERSITY_BASE;

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

import com.papook.studytravel.server.models.University;
import com.papook.studytravel.server.services.UniversityService;

@RestController
@RequestMapping(UNIVERSITY_BASE)
public class UniversityController {
    @Autowired
    private UniversityService universityService;

    // TODO: Add Hypermedia links to the response headers

    @GetMapping
    public ResponseEntity<Iterable<University>> getCollection() {
        // TODO: Set up pagination and filtering
        Iterable<University> universities = universityService.getAllUniversities();
        return ResponseEntity.ok(universities);
    }

    @GetMapping("/{id}")
    public ResponseEntity<University> getOne(@PathVariable Long id) {
        Optional<University> universityOptional = universityService.getUniversityById(id);
        return ResponseEntity.of(universityOptional);
    }

    @PostMapping
    public ResponseEntity<University> create(@RequestBody University university) {
        // Call the service to create the University
        // and get the location URI
        URI location = universityService.createUniversity(university);

        // Extract the ID from the location URI
        long createdUniversityId = Long
                .parseLong(location.getPath().substring(location.getPath().lastIndexOf('/') + 1));
        // Get the University representation
        University representation = universityService
                .getUniversityById(createdUniversityId)
                .get();

        // Return the representation with the location header
        // and the newly created University representation
        return ResponseEntity
                .created(location)
                .body(representation);
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
