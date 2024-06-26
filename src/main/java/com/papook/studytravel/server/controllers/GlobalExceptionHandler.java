package com.papook.studytravel.server.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.papook.studytravel.server.errors.ErrorMessage;
import com.papook.studytravel.server.errors.IdMismatchException;
import com.papook.studytravel.server.errors.ModuleTakenException;
import com.papook.studytravel.server.errors.ModuleNotLinkedException;
import com.papook.studytravel.server.errors.StudyModuleNotFoundException;
import com.papook.studytravel.server.errors.UniversityNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * Builds a ResponseEntity object with the specified HTTP status and message.
	 *
	 * @param status  the HTTP status of the response
	 * @param message the error message to be included in the response
	 * @return a ResponseEntity object containing the error message and status
	 * 
	 * @author papook
	 */
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

	@ExceptionHandler(ModuleNotLinkedException.class)
	public ResponseEntity<ErrorMessage> handleException(ModuleNotLinkedException ex) {
		String message = "The requested study module is not linked to this university.";

		return buildResponse(HttpStatus.BAD_REQUEST, message);
	}

	@ExceptionHandler(ModuleTakenException.class)
	public ResponseEntity<ErrorMessage> handleException(ModuleTakenException ex) {
		String message = "The requested study module is linked to another university. " +
				"Consider unlinking the module from the other university first.";

		return buildResponse(HttpStatus.BAD_REQUEST, message);
	}

	@ExceptionHandler(IdMismatchException.class)
	public ResponseEntity<ErrorMessage> handleException(IdMismatchException ex) {
		String message = "The ID provided in the request body does not match the ID in the URL.";

		return buildResponse(HttpStatus.BAD_REQUEST, message);
	}
}