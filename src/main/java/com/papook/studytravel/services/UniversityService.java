package com.papook.studytravel.services;

import java.net.URI;
import java.util.Optional;

import com.papook.studytravel.models.University;

public interface UniversityService {
    public Iterable<University> getAllUniversities();

    public Optional<University> getUniversityById(Long id);

    public URI createUniversity(University university);

    public Optional<URI> updateUniversity(Long id, University university);

    public void deleteUniversity(Long id);
}
