package com.papook.studytravel.server.models;

import static com.papook.studytravel.server.ServerConfiguration.BASE_URI;
import static com.papook.studytravel.server.ServerConfiguration.MODULE_ENDPOINT;
import static com.papook.studytravel.server.ServerConfiguration.UNIVERSITY_ENDPOINT;

import java.net.URI;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a partner university for student exchange programs
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class University {
    @Id
    @Setter(AccessLevel.NONE)
    Long id;
    @NotNull
    String name;
    @NotNull
    String country;
    @NotNull
    String department;
    @NotNull
    String contactPersonName;
    @NotNull
    Integer outgoingStudentNumber;
    @NotNull
    Integer incomingStudentNumber;
    @NotNull
    LocalDate springSemesterStart;
    @NotNull
    LocalDate fallSemesterStart;
    @Setter(AccessLevel.NONE)
    URI modules;
    @JsonIgnore
    Set<Long> moduleIds = new HashSet<>();

    public void addModule(Long moduleId) {
        moduleIds.add(moduleId);
    }

    public void removeModule(Long moduleId) {
        moduleIds.remove(moduleId);
    }

    public void setId(Long id) {
        this.id = id;
        setModules(this.id);
    }

    public void setModules(Long id) {
        this.modules = URI.create(BASE_URI + UNIVERSITY_ENDPOINT + "/" + id + MODULE_ENDPOINT);
    }
}