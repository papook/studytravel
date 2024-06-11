package com.papook.studytravel.server.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.papook.studytravel.server.models.StudyModule;

@Repository
public interface StudyModuleRepository extends CrudRepository<StudyModule, Long> {
    public Page<StudyModule> findByNameContainingAndSemesterIgnoreCase(
            String name,
            String semester,
            Pageable pageable);
}
