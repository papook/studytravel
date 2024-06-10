package com.papook.studytravel.server.services;

import java.net.URI;
import java.util.Optional;

import com.papook.studytravel.server.models.University;

public interface UniversityService {
    public Iterable<University> getUniversities();

    public Optional<University> getUniversityById(Long id);

    public URI createUniversity(University university);

    public Optional<URI> updateUniversity(Long id, University university);

    public void deleteUniversity(Long id);
}
