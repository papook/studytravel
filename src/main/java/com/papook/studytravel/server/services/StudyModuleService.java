package com.papook.studytravel.server.services;

import java.net.URI;
import java.util.Optional;

import com.papook.studytravel.server.models.StudyModule;

public interface StudyModuleService {

    public Iterable<StudyModule> getAllModules();

    public Optional<StudyModule> getModuleById(Long id);

    public URI createModule(StudyModule module);

    public URI createModuleForUniversity(Long universityId, StudyModule module);

    public Optional<URI> updateModule(Long id, StudyModule module);

    public Optional<URI> updateModuleForUniversity(Long universityId, Long moduleId, StudyModule module);

    public void deleteModule(Long id);

}