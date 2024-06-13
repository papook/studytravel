package com.papook.studytravel.server.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.papook.studytravel.server.models.StudyModule;

@Repository
public interface StudyModuleRepository extends CrudRepository<StudyModule, Long> {
    /**
     * Find study modules by name and semester containing the given strings.
     * This method is case-insensitive.
     * 
     * @param name     The study module name to search for.
     * @param semester The semester name to search for.
     * @param pageable The pageable object to use for pagination.
     * 
     * @return A page of study modules that match the search criteria.
     * 
     * @see org.springframework.data.domain.Page
     * @see org.springframework.data.domain.Pageable
     * 
     * @author papook
     */
    public Page<StudyModule> findByNameContainingAndSemesterContainingIgnoreCase(
            String name,
            String semester,
            Pageable pageable);

    /**
     * Find study modules by university ID, name and semester containing the given
     * strings. This method is case-insensitive.
     * 
     * @param universityId The university ID to search for.
     * @param name         The study module name to search for.
     * @param semester     The semester name to search for.
     * @param pageable     The pageable object to use for pagination.
     * @return A page of study modules that match the search criteria.
     * 
     * @see org.springframework.data.domain.Page
     * @see org.springframework.data.domain.Pageable
     * 
     * @author papook
     */
    public Page<StudyModule> findAllByUniversityIdAndNameContainingAndSemesterContainingIgnoreCase(
            Long universityId,
            String name,
            String semester,
            Pageable pageable);
}
