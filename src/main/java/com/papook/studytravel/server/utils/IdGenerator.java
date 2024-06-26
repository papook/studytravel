package com.papook.studytravel.server.utils;

import java.util.HashSet;
import java.util.Set;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Responsible for generating unique identifiers for new entities.
 * It keeps track of available IDs and provides methods to
 * generate a new ID and add an ID to the available set.
 */
@Component
@Scope("prototype")
public class IdGenerator {
    private Long nextId = 1L;
    private Set<Long> availableIds = new HashSet<>();
    private Set<Long> usedIds = new HashSet<>();

    /**
     * Generates a unique identifier for a new entity.
     *
     * @return A unique identifier of type Long.
     */
    public long nextId() {
        if (!availableIds.isEmpty()) {
            Long id = availableIds.iterator().next();
            availableIds.remove(id);
            return id;
        }

        while (usedIds.contains(nextId)) {
            usedIds.remove(nextId);
            nextId++;
        }

        usedIds.add(nextId);
        return nextId;
    }

    /**
     * Adds an ID to the set of available IDs
     * when deleting an entity.
     * This ID can be reused by the next entity.
     *
     * @param id The ID to be added.
     */
    public void markIdAvailable(Long id) {
        availableIds.add(id);
    }

    /**
     * Adds an ID to the set of used IDs
     * when creating a new entity using a specific ID
     * and PUT method.
     * 
     * @param id The ID to be marked as used.
     */
    public void markIdUsed(Long id) {
        if (availableIds.contains(id)) {
            availableIds.remove(id);
        }
        usedIds.add(id);
    }

    /**
     * Resets the ID generator.
     * Clears all available and used IDs.
     * Sets the next ID to 1.
     */
    public void reset() {
        availableIds.clear();
        usedIds.clear();
        nextId = 1L;
    }
}
