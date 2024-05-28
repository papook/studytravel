package com.papook.studytravel.services;

import java.util.Optional;

import com.papook.studytravel.models.University;

public interface UniversityService {
    public Iterable<University> getAllUniversities();

    public Optional<University> getUniversityById(Long id);

    public University createUniversity(University university);

    public University updateUniversity(Long id, University university);

    public void deleteUniversity(Long id);
}
