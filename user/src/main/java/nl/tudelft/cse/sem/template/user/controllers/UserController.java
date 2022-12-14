package nl.tudelft.cse.sem.template.user.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import nl.tudelft.cse.sem.template.user.domain.User;

import javax.ws.rs.Path;

@RestController
public class UserController {

    private final transient UserService userService;

    /**
     * All argument constructor, injects the main service component and all (4 now 0) publishers into the controller.
     *
     * @param UserService - user service implementation
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(/createUser)
    public ResponseEntity<User> createUser(@RequestBody User user){
        return ResponseEntity.ok(userService.save(user));
    }

    @GetMapping(/sendCompetitiveness/{userId})
    public ResponseEntity<Boolean> sendCompetitiveness(@PathVariable userId){
        return ResponseEntity.ok(userService.)
    }

    @GetMapping(/sendGender/{userId})
    public ResponseEntity<Character> sendGender(@PathVariable userId) {
        return ResponseEntity.ok(userService.);
    }
}
