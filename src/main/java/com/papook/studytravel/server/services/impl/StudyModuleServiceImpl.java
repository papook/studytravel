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
    public StudyModule getModuleById(Long id) {
        StudyModule result = repository.findById(id).orElseThrow(StudyModuleNotFoundException::new);
        return result;
    }

    @Override
    public Page<StudyModule> getModulesForUniversity(
            Long universityId,
            String name,
            String semester,
            Integer page) {
        // Check if the university exists
        universityService.verifyExists(universityId);

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
    public StudyModule getModuleForUniversity(Long universityId, Long moduleId) {
        // Check if the university exists
        universityService.verifyExists(universityId);

        // Get the module
        StudyModule module = this.getModuleById(moduleId);

        // Check if the module is linked to the university
        if (!isModuleLinkedToUniversity(moduleId, universityId)) {
            throw new ModuleNotLinkedToUniException();
        }

        return module;
    }

    @Override
    public URI createModule(StudyModule module) {
        module.setId(idGenerator.nextId());
        URI location = this.createModule(module);

        return location;
    }

    @Override
    public Optional<URI> updateModule(Long id, StudyModule module) {
        try {
            this.verifyExists(id);
        } catch (StudyModuleNotFoundException e) {
            // If the module does not exist, create a new module
            idGenerator.markIdUsed(id);
            StudyModule result = repository.save(module);
            URI location = URI.create(BASE_URI + MODULE_ENDPOINT + "/" + result.getId());
            return Optional.of(location);
        }
        // If the module exists, update it
        repository.save(module);
        return Optional.empty();

    }

    @Override
    public void deleteModule(Long id) {
        // Get the module
        StudyModule module = this.getModuleById(id);
        // Check if a university is linked to the module. If so, remove the module from
        // the university.
        Long universityId = module.getUniversityId();
        if (universityId != null) {
            University university = universityService.getUniversityById(universityId);
            university.removeModule(id);
            universityService.updateUniversity(universityId, university);
        }

        repository.deleteById(id);
    }

    @Override
    public void verifyExists(Long id) {
        if (!repository.existsById(id)) {
            throw new StudyModuleNotFoundException();
        }
    }

    @Override
    public void linkModuleToUniversity(Long moduleId, Long universityId) {
        // Get the university
        University university = universityService.getUniversityById(universityId);
        // Get the module
        StudyModule module = this.getModuleById(moduleId);

        if (!canModuleBeLinked(moduleId, universityId)) {
            throw new ModuleLinkedToOtherUniversityException();
        }

        university.addModule(moduleId);
        module.setUniversityId(universityId);

        // Save the changes
        universityService.updateUniversity(universityId, university);
        this.updateModule(moduleId, module);
    }

    @Override
    public void unlinkModuleFromUniversity(Long moduleId, Long universityId) {
        // Get the university
        University university = universityService.getUniversityById(universityId);
        // Get the module
        StudyModule module = this.getModuleById(moduleId);

        if (!isModuleLinkedToUniversity(moduleId, universityId)) {
            throw new ModuleNotLinkedToUniException();
        }

        university.removeModule(moduleId);
        module.setUniversityId(null);

        // Save the changes
        universityService.updateUniversity(universityId, university);
        this.updateModule(moduleId, module);
    }

    @Override
    public boolean isModuleLinkedToUniversity(Long moduleId, Long universityId) {
        // Check if the university exists
        universityService.verifyExists(universityId);
        // Get the module
        StudyModule module = getModuleById(universityId);
        // Check if the module is linked to the university
        return module.getUniversityId() == universityId;
    }

    private boolean canModuleBeLinked(Long moduleId, Long universityId) {
        // Check if the university exists
        universityService.verifyExists(universityId);
        // Get the module
        StudyModule module = getModuleById(universityId);
        return module.getUniversityId() == null || module.getUniversityId() == universityId;
    }
}
