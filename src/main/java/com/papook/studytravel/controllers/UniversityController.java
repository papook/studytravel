package com.papook.studytravel.controllers;

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

import com.papook.studytravel.Constants;
import com.papook.studytravel.models.University;
import com.papook.studytravel.services.UniversityService;

@RestController
@RequestMapping(Constants.API_BASE)
public class UniversityController {
    @Autowired
    private UniversityService universityService;

    @GetMapping(Constants.UNIVERSITY_BASE)
    public ResponseEntity<Iterable<University>> getCollection() {
        return ResponseEntity.ok(universityService.getAllUniversities());
    }

    @GetMapping(Constants.UNIVERSITY_BASE + "/{id}")
    public ResponseEntity<University> getUniversity(@PathVariable Long id) {
        Optional<University> universityOptional = universityService.getUniversityById(id);
        if (universityOptional.isPresent()) {
            return ResponseEntity.ok(universityOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(Constants.UNIVERSITY_BASE)
    public ResponseEntity<Void> postMethodName(@RequestBody University university) {
        URI location = universityService.createUniversity(university);

        return ResponseEntity.created(location).build();
    }

    @PutMapping(Constants.UNIVERSITY_BASE + "/{id}")
    public ResponseEntity<Void> putMethodName(@PathVariable Long id, @RequestBody University entity) {
        if (entity.getId() != id)
            return ResponseEntity.badRequest().build();

        Optional<URI> locationOptional = universityService.updateUniversity(id, entity);
        if (locationOptional.isPresent()) {
            return ResponseEntity.created(locationOptional.get()).build();
        } else {
            return ResponseEntity.noContent().build();
        }

    }

    @DeleteMapping(Constants.UNIVERSITY_BASE + "/{id}")
    public ResponseEntity<Void> deleteUniversity(@PathVariable Long id) {
        universityService.deleteUniversity(id);
        return ResponseEntity.noContent().build();
    }
}
