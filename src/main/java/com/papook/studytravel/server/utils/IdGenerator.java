package com.papook.studytravel.server.utils;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

/**
 * Responsible for generating unique identifiers for new entities.
 * It keeps track of available IDs and provides methods to
 * generate a new ID and add an ID to the available set.
 */
@Component
public class IdGenerator {
    private static Long nextId = 1L;
    private Set<Long> availableIds = new HashSet<>();

    /**
     * Generates a unique identifier for a new entity.
     *
     * @return A unique identifier of type Long.
     */
    public long nextId() {
        if (availableIds.isEmpty()) {
            return nextId++;
        } else {
            Long id = availableIds.iterator().next();
            availableIds.remove(id);
            return id;
        }
    }

    /**
     * Adds an ID to the set of available IDs
     * when deleting an entity.
     * This ID can be reused by the next entity.
     *
     * @param id The ID to be added.
     */
    public void addId(Long id) {
        availableIds.add(id);
    }
}
