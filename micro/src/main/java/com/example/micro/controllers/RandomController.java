package com.example.micro.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RandomController {

        /**
         * Gets example by id.
         *
         * @return the example found in the database with the given id
         */
        @GetMapping("/hello")
        public ResponseEntity<String> helloWorld() {
            return ResponseEntity.ok("Hello world");
        }

}
