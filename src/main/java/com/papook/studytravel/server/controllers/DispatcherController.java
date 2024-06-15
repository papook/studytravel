package com.papook.studytravel.server.controllers;

import static com.papook.studytravel.server.ServerConfiguration.MODULE_ENDPOINT;
import static com.papook.studytravel.server.ServerConfiguration.UNIVERSITY_ENDPOINT;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.papook.studytravel.server.utils.HypermediaGenerator;

@RestController
@RequestMapping("")
public class DispatcherController {
	@GetMapping({ "", "/" })
	public ResponseEntity<Void> getDispatcher() {
		String getUniversitiesCollectionLink = HypermediaGenerator.formatLinkHeader(
				UNIVERSITY_ENDPOINT, "getUniversitiesCollection");
		String getModulesCollectionLink = HypermediaGenerator.formatLinkHeader(
				MODULE_ENDPOINT, "getModulesCollection");
		String createUniversityLink = HypermediaGenerator.formatLinkHeader(
				UNIVERSITY_ENDPOINT, "postCreateUniversity");
		String createModuleLink = HypermediaGenerator.formatLinkHeader(
				MODULE_ENDPOINT, "postCreateModule");

		String[] links = {
				getUniversitiesCollectionLink,
				getModulesCollectionLink,
				createUniversityLink,
				createModuleLink };

		return ResponseEntity.ok()
				.header(HttpHeaders.LINK, links)
				.build();
	}
}