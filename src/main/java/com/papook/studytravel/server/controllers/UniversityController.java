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
import com.papook.studytravel.server.utils.LinkGenerator;

import jakarta.validation.Valid;

@RestController
@RequestMapping(UNIVERSITY_ENDPOINT)
public class UniversityController {
    @Autowired
    private UniversityService universityService;

    @Autowired
    private LinkGenerator pagingLinkBuilder;

    // TODO: Add Hypermedia links to the response headers

    @GetMapping
    public ResponseEntity<Iterable<University>> getCollection(
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "") String country,
            @RequestParam(defaultValue = "0") Integer page) {

        page = Math.max(0, page);

        Page<University> universitiesPage = universityService.getUniversities(name, country, page);

        List<University> responseBody = universitiesPage.getContent();
        HttpHeaders headers = pagingLinkBuilder.buildHeaders(universitiesPage);

        return ResponseEntity.ok()
                .headers(headers)
                .body(responseBody);

    }

    @GetMapping("/{id}")
    public ResponseEntity<University> getOne(@PathVariable Long id) {
        Optional<University> universityOptional = universityService.getUniversityById(id);
        return ResponseEntity.of(universityOptional);
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
            // Return a NO CONTENT status code
            return ResponseEntity.noContent().build();
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        universityService.deleteUniversity(id);
        return ResponseEntity.noContent().build();
    }

}
