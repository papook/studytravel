package com.papook.studytravel.server.services;

import java.net.URI;
import java.util.Optional;

import org.springframework.data.domain.Page;

import com.papook.studytravel.server.models.University;

public interface UniversityService {
    public Page<University> getUniversities(
            String name,
            String country,
            Integer page);

    public Optional<University> getUniversityById(Long id);

    public URI createUniversity(University university);

    public Optional<URI> updateUniversity(Long id, University university);

    public void deleteUniversity(Long id);
}
