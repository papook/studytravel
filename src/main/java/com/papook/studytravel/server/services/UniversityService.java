package com.papook.studytravel.server.services;

import java.net.URI;
import java.util.Optional;

import org.springframework.data.domain.Page;

import com.papook.studytravel.server.ServerConfiguration;
import com.papook.studytravel.server.errors.IdMismatchException;
import com.papook.studytravel.server.errors.UniversityNotFoundException;
import com.papook.studytravel.server.models.University;

/**
 * Service interface for university operations.
 * 
 * This interface defines methods for creating, reading, updating, and deleting
 * universities in the database.
 * 
 * @see com.papook.studytravel.server.models.University
 * @see org.springframework.data.domain.Page
 * @see java.net.URI
 * @see java.util.Optional
 * @see com.papook.studytravel.server.errors.UniversityNotFoundException
 * @see com.papook.studytravel.server.errors.IdMismatchException
 * 
 * @author papook
 */
public interface UniversityService {
    /**
     * Get a page of universities that match the specified criteria.
     * 
     * @param name    The university name to search for.
     * @param country The country name to search for.
     * @param page    The page number to retrieve. The page number is zero-based and
     *                the size is defined in {@link ServerConfiguration}.
     * 
     * @return A page of universities that match the search criteria.
     * 
     * @see org.springframework.data.domain.Page
     * 
     * @author papook
     */
    public Page<University> getUniversities(
            String name,
            String country,
            Integer page,
            String sort);

    /**
     * Get a university by its ID from the database.
     * 
     * @param id The ID of the university to retrieve.
     * 
     * @return The university with the specified ID.
     * 
     * @throws UniversityNotFoundException If the university does not exist.
     * 
     * @author papook
     */
    public University getUniversityById(Long id);

    /**
     * Create a new university in the database.
     * 
     * @param university The university to create. Should not contain an ID, as it
     *                   is generated automatically by the system. If the ID is set,
     *                   it will be overwritten.
     * 
     * @return The URI of the newly created university.
     * 
     * @author papook
     */
    public URI createUniversity(University university);

    /**
     * If the university with the given id exists, update it with the given
     * university and return an empty optional.
     * Otherwise, create a new university and return an optional containing the URI
     * of the new university.
     *
     * @param id         The ID of the university to update.
     * @param university The university object.
     * @return An optional URI representing the location of the newly created
     *         university, or an empty optional if the university already exists and
     *         was updated.
     * 
     * @throws IdMismatchException If the ID in the university object does not match
     *                             the ID in the path.
     * 
     * @author papook
     */
    public Optional<URI> updateUniversity(Long id, University university);

    /**
     * Delete a university from the database and all associated study modules. If
     * the university does not exist, the method will exit silently.
     * 
     * @param id The ID of the university to delete.
     * 
     * @author papook
     */
    public void deleteUniversity(Long id);

    /**
     * Verify that a university with the specified ID exists in the database.
     * 
     * @param id The ID of the university to verify.
     * 
     * @throws UniversityNotFoundException If the university does not exist.
     * 
     * @author papook
     */
    public void verifyExists(Long id);

    /**
     * Delete all universities from the database
     * 
     * @author papook
     */
    public void deleteAllUniversities();
}
