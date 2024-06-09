package com.papook.studytravel.server.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.papook.studytravel.server.errors.ErrorMessage;
import com.papook.studytravel.server.errors.ModuleNotLinkedToUniException;
import com.papook.studytravel.server.errors.StudyModuleNotFoundException;
import com.papook.studytravel.server.errors.UniversityNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorMessage> handleException(Exception ex) {
		HttpStatus status = HttpStatus.BAD_REQUEST;

		ErrorMessage errorMessage = new ErrorMessage(
				status.value(),
				"Could not read the request body. Please provide a valid JSON object.");

		return ResponseEntity.status(status)
				.body(errorMessage);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorMessage> handleException(MethodArgumentNotValidException ex) {
		HttpStatus status = HttpStatus.BAD_REQUEST;

		ErrorMessage errorMessage = new ErrorMessage(
				status.value(),
				"The request body contains invalid data. Make sure all fields are provided and have the correct format.");

		return ResponseEntity.status(status)
				.body(errorMessage);
	}

	@ExceptionHandler(UniversityNotFoundException.class)
	public ResponseEntity<ErrorMessage> handleException(UniversityNotFoundException ex) {
		HttpStatus status = HttpStatus.NOT_FOUND;

		ErrorMessage errorMessage = new ErrorMessage(
				status.value(),
				"The requested university was not found. Please provide a valid university ID.");

		return ResponseEntity.status(status)
				.body(errorMessage);
	}

	@ExceptionHandler(StudyModuleNotFoundException.class)
	public ResponseEntity<ErrorMessage> handleException(StudyModuleNotFoundException ex) {
		HttpStatus status = HttpStatus.NOT_FOUND;

		ErrorMessage errorMessage = new ErrorMessage(
				status.value(),
				"The requested study module was not found. Please provide a valid study module ID.");

		return ResponseEntity.status(status)
				.body(errorMessage);
	}

	@ExceptionHandler(ModuleNotLinkedToUniException.class)
	public ResponseEntity<ErrorMessage> handleException(ModuleNotLinkedToUniException ex) {
		HttpStatus status = HttpStatus.BAD_REQUEST;

		ErrorMessage errorMessage = new ErrorMessage(
				status.value(),
				"The requested study module is not linked to this university.");

		return ResponseEntity.status(status)
				.body(errorMessage);
	}
}