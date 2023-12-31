package nl.tudelft.sem.template.authentication.controllers;

import nl.tudelft.sem.template.authentication.authentication.JwtTokenGenerator;
import nl.tudelft.sem.template.authentication.authentication.JwtUserDetailsService;
import nl.tudelft.sem.template.authentication.domain.user.Password;
import nl.tudelft.sem.template.authentication.domain.user.RegistrationService;
import nl.tudelft.sem.template.authentication.domain.user.UserId;
import nl.tudelft.sem.template.authentication.models.AuthenticationRequestModel;
import nl.tudelft.sem.template.authentication.models.AuthenticationResponseModel;
import nl.tudelft.sem.template.authentication.models.RegistrationRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthenticationController {

    private final transient AuthenticationManager authenticationManager;

    private final transient JwtTokenGenerator jwtTokenGenerator;

    private final transient JwtUserDetailsService jwtUserDetailsService;

    private final transient RegistrationService registrationService;

    /**
     * Instantiates a new UsersController.
     *
     * @param authenticationManager the authentication manager
     * @param jwtTokenGenerator     the token generator
     * @param jwtUserDetailsService the user service
     * @param registrationService   the registration service
     */
    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager,
                                    JwtTokenGenerator jwtTokenGenerator,
                                    JwtUserDetailsService jwtUserDetailsService,
                                    RegistrationService registrationService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenGenerator = jwtTokenGenerator;
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.registrationService = registrationService;
    }

    /**
     * Endpoint for authentication.
     *
     * @param request The login model
     * @return JWT token if the login is successful
     * @throws Exception if the user does not exist or the password is incorrect
     */
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponseModel> authenticate(@RequestBody AuthenticationRequestModel request)
            throws Exception {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUserId(),
                            request.getPassword()));
        } catch (DisabledException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", e);
        }

        final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(request.getUserId());
        final String jwtToken = jwtTokenGenerator.generateToken(userDetails);
        return ResponseEntity.ok(new AuthenticationResponseModel(jwtToken));
    }

    /**
     * Endpoint for registration.
     *
     * @param request The registration model
     * @return 200 OK if the registration is successful
     * @throws Exception if a user with this userid already exists
     */
    @PostMapping("/register")
    public ResponseEntity register(@Valid @RequestBody RegistrationRequestModel request) throws Exception {
        try {
            UserId userId = new UserId(request.getUserId());
            Password password = new Password(request.getPassword());
            registrationService.registerUser(userId, password);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The given userId is already in use!");
        }

        return ResponseEntity.status(HttpStatus.OK).body("Thank you, " + request.getUserId()
                + ". Registration successful!");
    }

    /**
     * Handles BAD_REQUEST exceptions thrown by the Validator of User entities
     * Acts as a parser of the BAD_REQUEST exception messages bodies.
     * Triggered for all User entities that do not respect the @Valid annotation
     *
     * @param ex exception
     * @return a Mapping fieldName -> errorMessage
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
