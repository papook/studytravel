package com.papook.studytravel.server.services;

import java.net.URI;
import java.util.Optional;

import org.springframework.data.domain.Page;

import com.papook.studytravel.server.ServerConfiguration;
import com.papook.studytravel.server.errors.IdMismatchException;
import com.papook.studytravel.server.errors.ModuleNotLinkedException;
import com.papook.studytravel.server.errors.ModuleTakenException;
import com.papook.studytravel.server.errors.StudyModuleNotFoundException;
import com.papook.studytravel.server.errors.UniversityNotFoundException;
import com.papook.studytravel.server.models.StudyModule;

/**
 * Service interface for managing study modules.
 * 
 * This interface defines methods for creating, reading, updating, and deleting
 * study modules in the database. It also provides methods for linking and
 * unlinking study modules to and from universities.
 * 
 * @see com.papook.studytravel.server.models.StudyModule
 * @see org.springframework.data.domain.Page
 * @see java.net.URI
 * @see java.util.Optional
 * @see com.papook.studytravel.server.errors.StudyModuleNotFoundException
 * @see com.papook.studytravel.server.errors.UniversityNotFoundException
 * @see com.papook.studytravel.server.errors.ModuleNotLinkedException
 * @see com.papook.studytravel.server.errors.ModuleTakenException
 * @see com.papook.studytravel.server.errors.IdMismatchException
 * 
 * @author papook
 */
public interface StudyModuleService {

    /**
     * Get a page of study modules that match the specified criteria.
     * 
     * @param name     The study module name to search for.
     * @param semester The semester name to search for.
     * @param page     The page number to retrieve. The page number is zero-based
     *                 and the size is defined in
     *                 {@link ServerConfiguration}.
     * 
     * @return A page of study modules that match the search criteria.
     * 
     * @see org.springframework.data.domain.Page
     * 
     * @author papook
     */
    public Page<StudyModule> getModules(
            String name,
            String semester,
            Integer page);

    /**
     * Get a study module by its ID from the database.
     * 
     * @param id The ID of the study module to retrieve.
     * 
     * @return The study module with the specified ID.
     * 
     * @throws StudyModuleNotFoundException If the study module does not exist.
     * 
     * @author papook
     */
    public StudyModule getModuleById(Long id);

    /**
     * Get a page of study modules that are linked to the specified university.
     * 
     * @param universityId The ID of the university to search the modules of.
     * @param name         The study module name to search for.
     * @param semester     The semester name to search for.
     * @param page         The page number to retrieve. The page number is
     *                     zero-based
     *                     and the size is defined in
     *                     {@link ServerConfiguration}.
     * 
     * @return A page of study modules that match the search criteria.
     * 
     * @see org.springframework.data.domain.Page
     * 
     * @author papook
     */
    public Page<StudyModule> getModulesForUniversity(
            Long universityId,
            String name,
            String semester,
            Integer page);

    /**
     * Get a study module by its ID from the database that is linked to the
     * specified university.
     * 
     * @param universityId The ID of the university to search the module of.
     * @param moduleId     The ID of the study module to retrieve.
     * 
     * @return The study module with the specified ID.
     * 
     * @throws UniversityNotFoundException  If the university does not exist.
     * @throws StudyModuleNotFoundException If the study module does not exist.
     * @throws ModuleNotLinkedException     If the study module is not linked to
     *                                      the university.
     * 
     * @author papook
     */
    public StudyModule getModuleForUniversity(Long universityId, Long moduleId);

    /**
     * Create a new study module in the database and return the URI of the created
     * resource.
     * 
     * @param module The study module to create.
     * 
     * @return The URI of the created study module.
     * 
     * @see java.net.URI
     * 
     * @author papook
     */
    public URI createModule(StudyModule module);

    /**
     * If the study module with the given id exists, update it with the given
     * module and return an empty optional. Otherwise, create a new module
     * and return an optional containing the URI of the new module.
     *
     * @param id     The ID of the module to update.
     * @param module The study module object.
     * @return An optional URI representing the location of the newly created study
     *         module, or an empty optional if the study module already exists and
     *         was updated.
     * 
     * @throws IdMismatchException If the ID in the module object does not match the
     *                             ID in the path.
     * 
     * @author papook
     */
    public Optional<URI> updateModule(Long id, StudyModule module);

    /**
     * Delete a module from the database and remove the module ID from the
     * associated university. If the module does not exist, the method will exit
     * silently.
     * 
     * @param id The ID of the module to delete.
     * 
     * @author papook
     */
    public void deleteModule(Long id);

    /**
     * Verify that a study module with the specified ID exists in the database.
     * 
     * @param id The ID of the module to verify.
     * 
     * @throws StudyModuleNotFoundException If the study module does not exist.
     * 
     * @author papook
     */
    public void verifyExists(Long id);

    /**
     * Link a study module to a university by adding the module ID to the
     * university's module list and setting the university ID in the module.
     * 
     * @param moduleId     The ID of the study module to link.
     * @param universityId The ID of the university to link the module to.
     * 
     * @throws StudyModuleNotFoundException If the study module does not exist.
     * @throws UniversityNotFoundException  If the university does not exist.
     * @throws ModuleTakenException         If the study module is already linked to
     *                                      another university.
     * 
     * @author papook
     */
    public void linkModuleToUniversity(Long moduleId, Long universityId);

    /**
     * Unlink a study module from a university by removing the module ID from the
     * university's module list and setting the university ID in the module to null.
     * 
     * @param moduleId     The ID of the study module to unlink.
     * @param universityId The ID of the university to unlink the module from.
     * 
     * @throws StudyModuleNotFoundException If the study module does not exist.
     * @throws UniversityNotFoundException  If the university does not exist.
     * @throws ModuleNotLinkedException     If the study module is not linked to the
     *                                      university.
     * 
     * @author papook
     */
    public void unlinkModuleFromUniversity(Long moduleId, Long universityId);

    /**
     * Check if a study module is linked to a university.
     * 
     * @param moduleId     The ID of the study module to check.
     * @param universityId The ID of the university to check.
     * 
     * @return True if the study module is linked to the university, false
     *         otherwise.
     * 
     * @throws StudyModuleNotFoundException If the study module does not exist.
     * @throws UniversityNotFoundException  If the university does not exist.
     * 
     * @author papook
     */
    public boolean isModuleLinkedToUniversity(Long moduleId, Long universityId);

    /**
     * Delete all study modules from the database.
     * 
     * @author papook
     */
    public void deleteAllModules();
}