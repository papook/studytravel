package com.papook.studytravel.server.services.impl;

import java.net.URI;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.papook.studytravel.server.ServerConfiguration;
import com.papook.studytravel.server.models.University;
import com.papook.studytravel.server.repositories.UniversityRepository;
import com.papook.studytravel.server.services.UniversityService;
import com.papook.studytravel.server.utils.IdGenerator;

@Service
public class UniversityServiceImpl implements UniversityService {

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    UniversityRepository repository;

    /**
     * Retrieves all universities from the database.
     *
     * @return An iterable collection of universities.
     */
    @Override
    public Iterable<University> getAllUniversities() {
        Iterable<University> result = repository.findAll();
        return result;
    }

    /**
     * Retrieves a university by its ID from the database.
     *
     * @param id The ID of the university to retrieve.
     * @return An optional containing the university, or an empty optional if the
     *         university does not exist.
     */
    @Override
    public Optional<University> getUniversityById(Long id) {
        Optional<University> result = repository.findById(id);
        return result;
    }

    /**
     * Creates a new university in the database.
     *
     * @param university The university object to create.
     * @return The URI representing the location of the newly created university.
     */
    @Override
    public URI createUniversity(University university) {
        // Generate a new ID for the university
        long newId = idGenerator.nextId();
        university.setId(newId);

        // Set the modules URI
        university.setModules(
                URI.create(ServerConfiguration.BASE_URI +
                        ServerConfiguration.UNIVERSITY_BASE +
                        "/" + university.getId() +
                        ServerConfiguration.MODULE_BASE));

        University result = repository.save(university);
        URI location = URI
                .create(ServerConfiguration.BASE_URI + ServerConfiguration.UNIVERSITY_BASE + "/" + result.getId());
        return location;
    }

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
     */
    @Override
    public Optional<URI> updateUniversity(Long id, University university) {
        Optional<University> existing = repository.findById(id);
        if (existing.isPresent()) {
            repository.save(university);
            return Optional.empty();
        } else {
            university.setId(id);
            University result = repository.save(university);
            URI location = URI.create(ServerConfiguration.BASE_URI +
                    ServerConfiguration.UNIVERSITY_BASE +
                    "/" + result.getId());
            return Optional.of(location);
        }
    }

    /**
     * Deletes a university from the database.
     *
     * @param id The ID of the university to delete.
     * @throws IllegalArgumentException if the university does not exist.
     */
    @Override
    public void deleteUniversity(Long id) {
        idGenerator.addId(id);
        repository.deleteById(id);
    }

}
