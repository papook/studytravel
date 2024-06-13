package com.papook.studytravel.server.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.papook.studytravel.server.models.University;

@Repository
public interface UniversityRepository extends CrudRepository<University, Long> {
    /**
     * Find universities by name and country containing the given strings.
     * This method is case-insensitive.
     * 
     * @param name     The university name to search for.
     * @param country  The country name to search for.
     * @param pageable The pageable object to use for pagination.
     * 
     * @return A page of universities that match the search criteria.
     * 
     * @see org.springframework.data.domain.Page
     * @see org.springframework.data.domain.Pageable
     * 
     * @author papook
     */
    public Page<University> findByNameContainingAndCountryContainingIgnoreCase(
            String name,
            String country,
            Pageable pageable);
}
