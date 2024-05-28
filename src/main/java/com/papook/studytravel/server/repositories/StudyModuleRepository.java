package com.papook.studytravel.server.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.papook.studytravel.server.models.StudyModule;

@Repository
public interface StudyModuleRepository extends CrudRepository<StudyModule, Long> {
    List<StudyModule> findByUniversityId(Long universityId);
}
