package com.papook.studytravel.server.services.impl;

import static com.papook.studytravel.server.ServerConfiguration.BASE_URI;
import static com.papook.studytravel.server.ServerConfiguration.MODULE_ENDPOINT;
import static com.papook.studytravel.server.ServerConfiguration.PAGE_SIZE;

import java.net.URI;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.papook.studytravel.server.errors.ModuleLinkedToOtherUniversityException;
import com.papook.studytravel.server.errors.ModuleNotLinkedToUniException;
import com.papook.studytravel.server.errors.StudyModuleNotFoundException;
import com.papook.studytravel.server.errors.UniversityNotFoundException;
import com.papook.studytravel.server.models.StudyModule;
import com.papook.studytravel.server.models.University;
import com.papook.studytravel.server.repositories.StudyModuleRepository;
import com.papook.studytravel.server.services.StudyModuleService;
import com.papook.studytravel.server.services.UniversityService;
import com.papook.studytravel.server.utils.IdGenerator;

@Service
public class StudyModuleServiceImpl implements StudyModuleService {

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private StudyModuleRepository repository;

    @Autowired
    private UniversityService universityService;

    @Override
    public Page<StudyModule> getModules(
            String name,
            String semester,
            Integer page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<StudyModule> result = repository
                .findByNameContainingAndSemesterContainingIgnoreCase(name, semester, pageable);
        return result;
    }

    @Override
    public Optional<StudyModule> getModuleById(Long id) {
        Optional<StudyModule> result = repository.findById(id);
        return result;
    }

    @Override
    public Page<StudyModule> getModulesForUniversity(
            Long universityId,
            String name,
            String semester,
            Integer page) {
        // Check if the university exists and throw an exception if it doesn't
        universityService.getUniversityById(universityId)
                .orElseThrow(UniversityNotFoundException::new);

        // Create a pageable object
        PageRequest pageable = PageRequest.of(page, PAGE_SIZE);
        // Get the modules for the university
        Page<StudyModule> modules = repository
                .findAllByUniversityIdAndNameContainingAndSemesterContainingIgnoreCase(
                        universityId,
                        name,
                        semester,
                        pageable);

        return modules;
    }

    @Override
    public URI createModule(StudyModule module) {
        module.setId(idGenerator.nextId());
        StudyModule result = repository.save(module);

        URI location = URI.create(BASE_URI + MODULE_ENDPOINT + "/" + result.getId());

        return location;
    }

    @Override
    public Optional<URI> updateModule(Long id, StudyModule module) {
        Optional<StudyModule> existing = repository.findById(id);
        if (existing.isPresent()) {
            repository.save(module);
            return Optional.empty();
        } else {
            idGenerator.markIdUsed(id);
            module.setId(id);
            StudyModule result = repository.save(module);
            URI location = URI.create(BASE_URI + MODULE_ENDPOINT + "/" + result.getId());
            return Optional.of(location);
        }
    }

    @Override
    public void deleteModule(Long id) {
        // Check if the module exists
        StudyModule module = repository.findById(id).orElseThrow(StudyModuleNotFoundException::new);
        // Check if a university is linked to the module. If so, remove the module from
        // the university.
        universityService.getUniversityById(module.getUniversityId()).ifPresent(university -> {
            university.removeModule(id);
        });

        repository.deleteById(id);
    }

    @Override
    public void linkModuleToUniversity(Long moduleId, Long universityId) {
        // Check if the university exists
        University university = universityService.getUniversityById(universityId)
                .orElseThrow(UniversityNotFoundException::new);
        // Check if the module exists
        StudyModule module = repository.findById(moduleId).orElseThrow(StudyModuleNotFoundException::new);

        if (!canModuleBeLinked(moduleId, universityId)) {
            throw new ModuleLinkedToOtherUniversityException();
        }

        university.addModule(moduleId);
        module.setUniversityId(universityId);

        // Save the changes
        universityService.updateUniversity(universityId, university);
        module = repository.save(module);
    }

    @Override
    public void unlinkModuleFromUniversity(Long moduleId, Long universityId) {
        // Check if the university exists
        University university = universityService.getUniversityById(universityId)
                .orElseThrow(UniversityNotFoundException::new);
        // Check if the module exists
        StudyModule module = repository.findById(moduleId).orElseThrow(StudyModuleNotFoundException::new);

        if (!isModuleLinkedToUniversity(moduleId, universityId)) {
            throw new ModuleNotLinkedToUniException();
        }

        university.removeModule(moduleId);
        module.setUniversityId(null);

        // Save the changes
        universityService.updateUniversity(universityId, university);
        module = repository.save(module);
    }

    @Override
    public boolean isModuleLinkedToUniversity(Long moduleId, Long universityId) {
        StudyModule module = repository.findById(moduleId).orElseThrow(StudyModuleNotFoundException::new);
        return module.getUniversityId() == universityId;
    }

    private boolean canModuleBeLinked(Long moduleId, Long universityId) {
        StudyModule module = repository.findById(moduleId).orElseThrow(StudyModuleNotFoundException::new);
        return module.getUniversityId() == null || module.getUniversityId() == universityId;
    }
}
