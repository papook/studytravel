package com.papook.studytravel.server.services;

import java.net.URI;
import java.util.Optional;

import com.papook.studytravel.server.models.Module;

public interface ModuleService {
    public Iterable<Module> getAllModules();

    public Optional<Module> getModuleById(Long id);

    public URI createModule(Module module);

    public Optional<URI> updateModule(Long id, Module module);

    public void deleteModule(Long id);
}