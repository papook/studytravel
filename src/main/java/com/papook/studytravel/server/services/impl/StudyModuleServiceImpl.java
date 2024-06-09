package com.papook.studytravel.server.services.impl;

import static com.papook.studytravel.server.ServerConfiguration.BASE_URI;
import static com.papook.studytravel.server.ServerConfiguration.MODULE_ENDPOINT;

import java.net.URI;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public Iterable<StudyModule> getAllModules() {
        Iterable<StudyModule> result = repository.findAll();
        return result;
    }

    @Override
    public Optional<StudyModule> getModuleById(Long id) {
        Optional<StudyModule> result = repository.findById(id);
        return result;
    }

    @Override
    public Iterable<StudyModule> getModulesForUniversity(Long universityId) {
        University result = universityService.getUniversityById(universityId)
                .orElseThrow(UniversityNotFoundException::new);
        Set<Long> moduleIds = result.getModuleIds();
        Iterable<StudyModule> modules = repository.findAllById(moduleIds);

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
        repository.deleteById(id);
    }

}
