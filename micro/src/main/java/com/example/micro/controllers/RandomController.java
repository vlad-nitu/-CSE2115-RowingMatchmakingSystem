package com.example.micro.controllers;

import com.example.micro.domain.Matching;
import com.example.micro.services.MatchingServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RandomController {

    private final MatchingServiceImpl matchingService;

    public RandomController(MatchingServiceImpl matchingService) {
        this.matchingService = matchingService;
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


    @GetMapping("/findAll")
    public ResponseEntity<String> findAllMatchings() {
        String findings = matchingService.findAll().toString();
        return ResponseEntity.ok(findings);
    }

    @PostMapping("/save")
    public ResponseEntity<Matching> saveMatching(
            @RequestBody Matching matching
    ) {
        return ResponseEntity.ok(
                matchingService.save(matching)
        );
    }


}
