package com.papook.studytravel.server.models;

import com.fasterxml.jackson.annotation.JsonSetter;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @NotNull
    String name;
    @NotNull
    @Setter(AccessLevel.NONE)
    Semester semester;
    @NotNull
    Integer creditPoints;

    @JsonSetter
    public void setSemester(String semester) {
        this.semester = Semester.valueOf(semester.toUpperCase());
    }

    /**
     * Represents the semester in which the module is offered.
     */
    public static enum Semester {
        SPRING,
        FALL,
    }
}