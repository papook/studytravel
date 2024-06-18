package com.papook.studytravel.server.services.impl;

import static com.papook.studytravel.server.ServerConfiguration.BASE_URI;
import static com.papook.studytravel.server.ServerConfiguration.PAGE_SIZE;
import static com.papook.studytravel.server.ServerConfiguration.UNIVERSITY_ENDPOINT;

import java.net.URI;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.stereotype.Service;

import com.papook.studytravel.server.errors.UniversityNotFoundException;
import com.papook.studytravel.server.models.University;
import com.papook.studytravel.server.repositories.StudyModuleRepository;
import com.papook.studytravel.server.repositories.UniversityRepository;
import com.papook.studytravel.server.services.UniversityService;
import com.papook.studytravel.server.utils.IdGenerator;

@Service
public class UniversityServiceImpl implements UniversityService {

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    UniversityRepository repository;

    @Autowired
    StudyModuleRepository moduleRepository;

    @Override
    public Page<University> getUniversities(
            String name,
            String country,
            Integer page,
            String sort) {
        // Split the sort string into field and direction and create a sort object
        String[] sortParts = sort.split("_");
        String sortField = sortParts[0];
        String sortDirection = sortParts[1];
        Sort sortConstraint;

        PageRequest pageRequest;

        Page<University> result;

        try {
            sortConstraint = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
            pageRequest = PageRequest.of(page, PAGE_SIZE, sortConstraint);
            result = repository.findByNameContainingAndCountryContainingIgnoreCase(name, country, pageRequest);
        } catch (PropertyReferenceException | IllegalArgumentException e) {
            // If the sort field is invalid, default to sorting by ID in ascending order
            sortConstraint = Sort.by(Sort.Order.asc("id"));
            pageRequest = PageRequest.of(page, PAGE_SIZE, sortConstraint);

            result = repository.findByNameContainingAndCountryContainingIgnoreCase(name, country, pageRequest);
        }

        return result;
    }

    @Override
    public University getUniversityById(Long id) {
        University result = repository.findById(id).orElseThrow(UniversityNotFoundException::new);
        return result;
    }

    @Override
    public URI createUniversity(University university) {
        // Generate a new ID for the university
        long newId = idGenerator.nextId();
        university.setId(newId);

        University result = repository.save(university);
        URI location = URI
                .create(BASE_URI + UNIVERSITY_ENDPOINT + "/" + result.getId());
        return location;
    }

    @Override
    public Optional<URI> updateUniversity(Long id, University university) {
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

    @Override
    public void deleteUniversity(Long id) {
        Set<Long> moduleIds = this.getUniversityById(id).getModuleIds();
        // Delete all modules linked to the university
        moduleRepository.deleteAllById(moduleIds);
        // Mark the University ID as available
        idGenerator.markIdAvailable(id);
        // Delete the university
        repository.deleteById(id);
    }

    @Override
    public void verifyExists(Long id) {
        if (!repository.existsById(id)) {
            throw new UniversityNotFoundException();
        }
    }

    @Override
    public void deleteAllUniversities() {
        repository.deleteAll();
        idGenerator.reset();
    }

}
