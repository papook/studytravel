package com.papook.studytravel.server.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.papook.studytravel.server.models.University;

@Repository
public interface UniversityRepository extends CrudRepository<University, Long> {
    public Page<University> findByNameContainingAndCountryContainingIgnoreCase(
            String name,
            String country,
            Pageable pageable);
}
