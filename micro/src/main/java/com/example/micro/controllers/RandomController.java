package com.example.micro.controllers;

import com.example.micro.domain.Matching;
import com.example.micro.repositories.MatchingRepository;
import com.example.micro.services.MatchingServiceImpl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class RandomController {

    private final MatchingRepository matchingRepository;

    public RandomController(MatchingRepository matchingRepository) {
        this.matchingRepository = matchingRepository;
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


    @GetMapping(value = "/findAll", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Matching>> findAllMatchings() {
       return ResponseEntity.ok(matchingRepository.findAll());
    }

    @PostMapping("/save")
    public ResponseEntity<Matching> saveMatching(
            @RequestBody Matching matching
    ) {
        return ResponseEntity.ok(
                matchingRepository.save(matching)
        );
    }

}
