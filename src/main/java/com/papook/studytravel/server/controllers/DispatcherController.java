package com.papook.studytravel.server.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.papook.studytravel.server.ServerConfiguration;

@RestController
@RequestMapping("")
public class DispatcherController {
    @GetMapping({ "", "/" })
    public ResponseEntity<Void> getDispatcher() {
        String getUniversitiesCollectionLink = String.format("<%s%s>;rel=\"getUniversitiesCollection\"",
                ServerConfiguration.BASE_URI,
                ServerConfiguration.UNIVERSITY_BASE);

        String getModulesCollectionLink = String.format("<%s%s>;rel=\"getModulesCollection\"",
                ServerConfiguration.BASE_URI,
                ServerConfiguration.MODULE_BASE);

        String[] links = { getUniversitiesCollectionLink, getModulesCollectionLink };

        return ResponseEntity.ok()
                .header("Link", links)
                .build();
    }
}