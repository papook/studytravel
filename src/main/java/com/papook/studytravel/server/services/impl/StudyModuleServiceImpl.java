package com.papook.studytravel.server.services.impl;

import java.net.URI;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.papook.studytravel.server.models.StudyModule;
import com.papook.studytravel.server.repositories.StudyModuleRepository;
import com.papook.studytravel.server.services.StudyModuleService;
import com.papook.studytravel.server.utils.IdGenerator;

@Service
public class StudyModuleServiceImpl implements StudyModuleService {

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    StudyModuleRepository repository;

    @Override
    public Iterable<StudyModule> getAllModules() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllModules'");
    }

    @Override
    public Optional<StudyModule> getModuleById(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getModuleById'");
    }

    @Override
    public URI createModule(StudyModule module) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createModule'");
    }

    @Override
    public Optional<URI> updateModule(Long id, StudyModule module) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateModule'");
    }

    @Override
    public void deleteModule(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteModule'");
    }

}
