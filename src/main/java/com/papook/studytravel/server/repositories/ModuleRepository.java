package com.papook.studytravel.server.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.papook.studytravel.server.models.Module;

@Repository
public interface ModuleRepository extends CrudRepository<Module, Long> {
    List<Module> findByUniversityId(Long universityId);
}
