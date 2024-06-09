package com.papook.studytravel.server.models;

import java.net.URI;
import java.time.LocalDate;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a partner university for student exchange programs
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class University {
    @Id
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
    URI modules;
    @JsonIgnore
    Set<Long> moduleIds;

    public void addModule(Long moduleId) {
        moduleIds.add(moduleId);
    }

    public void removeModule(Long moduleId) {
        moduleIds.remove(moduleId);
    }
}