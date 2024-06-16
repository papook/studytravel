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
		String getStudyModulesCollectionLink = HypermediaGenerator.formatLinkHeader(
				MODULE_ENDPOINT, "getStudyModulesCollection");
		String createUniversityLink = HypermediaGenerator.formatLinkHeader(
				UNIVERSITY_ENDPOINT, "postCreateUniversity");
		String createStudyModuleLink = HypermediaGenerator.formatLinkHeader(
				MODULE_ENDPOINT, "postCreateStudyModule");

		String[] links = {
				getUniversitiesCollectionLink,
				getStudyModulesCollectionLink,
				createUniversityLink,
				createStudyModuleLink };

		return ResponseEntity.ok()
				.header(HttpHeaders.LINK, links)
				.build();
	}
}