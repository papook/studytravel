package com.papook.portfolio3.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a module in a study program.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Module {
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