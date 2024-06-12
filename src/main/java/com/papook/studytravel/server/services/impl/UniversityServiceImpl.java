package com.papook.studytravel.server.services.impl;

import static com.papook.studytravel.server.ServerConfiguration.BASE_URI;
import static com.papook.studytravel.server.ServerConfiguration.MODULE_ENDPOINT;
import static com.papook.studytravel.server.ServerConfiguration.PAGE_SIZE;
import static com.papook.studytravel.server.ServerConfiguration.UNIVERSITY_ENDPOINT;

import java.net.URI;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.papook.studytravel.server.errors.UniversityNotFoundException;
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
    public Page<University> getUniversities(
            String name,
            String country,
            Integer page) {
        PageRequest pageRequest = PageRequest.of(page, PAGE_SIZE);
        return repository.findByNameContainingAndCountryContaining(name, country, pageRequest);
    }

    /**
     * Retrieves a university by its ID from the database.
     *
     * @param id The ID of the university to retrieve.
     * @return An optional containing the university, or an empty optional if the
     *         university does not exist.
     */
    @Override
    public University getUniversityById(Long id) {
        University result = repository.findById(id).orElseThrow(UniversityNotFoundException::new);
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
                URI.create(BASE_URI + UNIVERSITY_ENDPOINT + "/" + university.getId() + MODULE_ENDPOINT));

        University result = repository.save(university);
        URI location = URI
                .create(BASE_URI + UNIVERSITY_ENDPOINT + "/" + result.getId());
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
        // Set the modules URI as it is not provided by the client
        university.setModules(
                URI.create(BASE_URI + UNIVERSITY_ENDPOINT + "/" + university.getId() + MODULE_ENDPOINT));
        try {
            this.verifyExists(id);
        } catch (UniversityNotFoundException e) {
            // If the university does not exist, create a new university
            idGenerator.markIdUsed(id);
            University result = repository.save(university);
            URI location = URI.create(BASE_URI + UNIVERSITY_ENDPOINT + "/" + result.getId());
            return Optional.of(location);
        }

        // If the university exists, update it
        repository.save(university);
        return Optional.empty();

    }

    /**
     * Deletes a university from the database.
     *
     * @param id The ID of the university to delete.
     * @throws IllegalArgumentException if the university does not exist.
     */
    @Override
    public void deleteUniversity(Long id) {
        // TODO: Also delete all modules linked to the university
        idGenerator.markIdAvailable(id);
        repository.deleteById(id);
    }

    /**
     * Verifies that a university with the given ID exists. If it does not, throws a
     * UniversityNotFoundException. Otherwise, exits normally.
     *
     * @param id The ID of the university to verify.
     * @throws UniversityNotFoundException if the university does not exist.
     */
    @Override
    public void verifyExists(Long id) {
        if (!repository.existsById(id)) {
            throw new UniversityNotFoundException();
        }
    }

}
