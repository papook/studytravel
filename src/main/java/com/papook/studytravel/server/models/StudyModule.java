package com.papook.studytravel.server.models;

import static com.papook.studytravel.server.ServerConfiguration.BASE_URI;
import static com.papook.studytravel.server.ServerConfiguration.MODULE_ENDPOINT;

import java.net.URI;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import jakarta.persistence.Entity;
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
    @Setter(AccessLevel.NONE)
    Long id;

    @NotNull
    String name;
    @NotNull
    @Setter(AccessLevel.NONE)
    String semester;
    @NotNull
    Integer creditPoints;
    @Setter(AccessLevel.NONE)
    URI self;
    @JsonIgnore
    Long universityId;

    @JsonSetter
    public void setSemester(String semester) {
        // Semester.valueOf() throws an IllegalArgumentException if the given string is
        // not a valid enum value
        semester = semester.toUpperCase();
        Semester.valueOf(semester);

        // If the above line does not throw an exception, the given string is a valid
        // enum value
        this.semester = semester;
    }

    public void setId(Long id) {
        this.id = id;
        setSelf(id);
    }

    public void setSelf(Long id) {
        this.self = URI.create(BASE_URI + MODULE_ENDPOINT + "/" + id);
    }

    /**
     * Represents the semester in which the module is offered.
     */
    public static enum Semester {
        SPRING,
        FALL,
    }
}