package com.example.micro.controllers;

import com.example.micro.domain.Matching;

import java.util.List;

import com.example.micro.services.MatchingServiceImpl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MatchingController {

    private final transient MatchingServiceImpl matchingServiceImpl;


    public MatchingController(MatchingServiceImpl matchingServiceImpl) {
        this.matchingServiceImpl = matchingServiceImpl;
    }

    /**
     * Gets example by id.
     *
     * @return the example found in the database with the given id
     */
    @GetMapping("/hello")
    public ResponseEntity<String> helloWorld() {
        return ResponseEntity.ok("Hello world!");
    }


    /**
     * Find all matchings.
     *
     * @return - Response of a list of matchings
     */
    @GetMapping(value = "/findAll", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Matching>> findAllMatchings() {
        return ResponseEntity.ok(matchingServiceImpl.findAll());
    }

    /**
     * Save a matching.
     *
     * @param matching - Matching obj
     * @return - saved Matching obj
     */
    @PostMapping("/save")
    public ResponseEntity<Matching> saveMatching(
            @RequestBody Matching matching
    ) {
        return ResponseEntity.ok(
                matchingServiceImpl.save(matching)
        );
    }

}
