package com.papook.studytravel.server.services.impl;

import java.net.URI;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.papook.studytravel.server.models.Module;
import com.papook.studytravel.server.repositories.ModuleRepository;
import com.papook.studytravel.server.services.ModuleService;
import com.papook.studytravel.server.utils.IdGenerator;

@Service
public class ModuleServiceImpl implements ModuleService {

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    ModuleRepository repository;

    @Override
    public Iterable<Module> getAllModules() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllModules'");
    }

    @Override
    public Optional<Module> getModuleById(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getModuleById'");
    }

    @Override
    public URI createModule(Module module) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createModule'");
    }

    @Override
    public Optional<URI> updateModule(Long id, Module module) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateModule'");
    }

    @Override
    public void deleteModule(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteModule'");
    }

}
