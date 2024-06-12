package com.papook.studytravel.server.controllers;

import static com.papook.studytravel.server.ServerConfiguration.BASE_URI;
import static com.papook.studytravel.server.ServerConfiguration.MODULE_ENDPOINT;
import static com.papook.studytravel.server.ServerConfiguration.UNIVERSITY_ENDPOINT;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
public class DispatcherController {
	@GetMapping({ "", "/" })
	public ResponseEntity<Void> getDispatcher() {
		String getUniversitiesCollectionLink = String.format(
				"<%s%s>;rel=\"getUniversitiesCollection\"",
				BASE_URI, UNIVERSITY_ENDPOINT);

		String getModulesCollectionLink = String.format(
				"<%s%s>;rel=\"getModulesCollection\"",
				BASE_URI, MODULE_ENDPOINT);

		String[] links = { getUniversitiesCollectionLink, getModulesCollectionLink };

		return ResponseEntity.ok()
				.header(HttpHeaders.LINK, links)
				.build();
	}
}