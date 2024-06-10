package com.papook.studytravel.server.services;

import java.net.URI;
import java.util.Optional;

import com.papook.studytravel.server.models.StudyModule;

public interface StudyModuleService {

    public Iterable<StudyModule> getAllModules();

    public Optional<StudyModule> getModuleById(Long id);

    public Iterable<StudyModule> getModulesForUniversity(Long universityId);

    public URI createModule(StudyModule module);

    public Optional<URI> updateModule(Long id, StudyModule module);

    public void deleteModule(Long id);

    public void linkModuleToUniversity(Long moduleId, Long universityId);

    public void unlinkModuleFromUniversity(Long moduleId, Long universityId);

    public boolean isModuleLinkedToUniversity(Long moduleId, Long universityId);
}