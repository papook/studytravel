package com.papook.studytravel.server.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.papook.studytravel.server.errors.ErrorMessage;
import com.papook.studytravel.server.errors.ModuleLinkedToOtherUniversityException;
import com.papook.studytravel.server.errors.ModuleNotLinkedToUniException;
import com.papook.studytravel.server.errors.StudyModuleNotFoundException;
import com.papook.studytravel.server.errors.UniversityNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

	private ResponseEntity<ErrorMessage> buildResponse(
			final HttpStatus status,
			final String message) {

		ErrorMessage errorMessage = new ErrorMessage(
				status.value(),
				message);

		return ResponseEntity.status(status)
				.body(errorMessage);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorMessage> handleException(Exception ex) {
		String message = "Could not read the request body. Please provide a valid JSON object.";

		return buildResponse(HttpStatus.BAD_REQUEST, message);

	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorMessage> handleException(MethodArgumentNotValidException ex) {
		String message = "The request body contains invalid data." +
				" Make sure all fields are provided and have the correct format.";

		return buildResponse(HttpStatus.BAD_REQUEST, message);
	}

	@ExceptionHandler(UniversityNotFoundException.class)
	public ResponseEntity<ErrorMessage> handleException(UniversityNotFoundException ex) {
		String message = "The requested university was not found. Please provide a valid university ID.";

		return buildResponse(HttpStatus.NOT_FOUND, message);
	}

	@ExceptionHandler(StudyModuleNotFoundException.class)
	public ResponseEntity<ErrorMessage> handleException(StudyModuleNotFoundException ex) {
		String message = "The requested study module was not found. Please provide a valid study module ID.";

		return buildResponse(HttpStatus.NOT_FOUND, message);
	}

	@ExceptionHandler(ModuleNotLinkedToUniException.class)
	public ResponseEntity<ErrorMessage> handleException(ModuleNotLinkedToUniException ex) {
		String message = "The requested study module is not linked to this university.";

		return buildResponse(HttpStatus.BAD_REQUEST, message);
	}

	@ExceptionHandler(ModuleLinkedToOtherUniversityException.class)
	public ResponseEntity<ErrorMessage> handleException(ModuleLinkedToOtherUniversityException ex) {
		String message = "The requested study module is linked to another university. " +
				"Consider unlinking the module from the other university first.";

		return buildResponse(HttpStatus.BAD_REQUEST, message);
	}
}