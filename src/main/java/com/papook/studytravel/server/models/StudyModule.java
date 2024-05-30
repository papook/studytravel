package com.papook.studytravel.server.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a module that can be taken by students
 * at a partner university.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class StudyModule {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    Long id;
    String name;
    Semester semester;
    Integer creditPoints;

    /**
     * Represents the semester in which the module is offered.
     */
    public static enum Semester {
        SPRING,
        FALL,
    }
}