package com.papook.studytravel.models;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
    String name;
    String country;
    String department;
    String contactPersonName;
    Integer outgoingStudentNumber;
    Integer incomingStudentNumber;
    LocalDate nextSpringSemesterStart;
    LocalDate nextFallSemesterStart;

}
