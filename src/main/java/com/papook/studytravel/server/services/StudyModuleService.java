package com.papook.studytravel.server.services;

import java.net.URI;
import java.util.Optional;

import org.springframework.data.domain.Page;

import com.papook.studytravel.server.models.StudyModule;

public interface StudyModuleService {

    public Page<StudyModule> getModules(
            String name,
            String semester,
            Integer page);

    public StudyModule getModuleById(Long id);

    public Page<StudyModule> getModulesForUniversity(
            Long universityId,
            String name,
            String semester,
            Integer page);

    public StudyModule getModuleForUniversity(Long universityId, Long moduleId);

    public URI createModule(StudyModule module);

    public Optional<URI> updateModule(Long id, StudyModule module);

    public void deleteModule(Long id);

    public void verifyExists(Long id);

    public void linkModuleToUniversity(Long moduleId, Long universityId);

    public void unlinkModuleFromUniversity(Long moduleId, Long universityId);

    public boolean isModuleLinkedToUniversity(Long moduleId, Long universityId);
}