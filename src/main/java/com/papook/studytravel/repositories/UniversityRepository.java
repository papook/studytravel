package com.papook.studytravel.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.papook.studytravel.models.University;

@Repository
public interface UniversityRepository extends CrudRepository<University, Long> {
}
