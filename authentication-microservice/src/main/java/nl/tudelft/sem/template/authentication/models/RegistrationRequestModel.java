package nl.tudelft.sem.template.authentication.models;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Model representing a registration request.
 */
@Data
public class RegistrationRequestModel {
    @NotBlank(message = "userId is mandatory and cannot be blank")
    @NotNull(message = "userId is mandatory and cannot be null")
    @Size(min = 3, max = 20, message = "userId must be between 3 and 20 characters")
    @Pattern(regexp = "^[a-zA-Z\\d]+$", message = "userId must not contain special characters nor spaces")
    private String userId;

    @NotBlank(message = "Password is mandatory and cannot be blank")
    @NotNull(message = "Password is mandatory and cannot be null")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#\\$%\\^&\\*])[a-zA-Z\\d!@#\\$%\\^&\\*]{8,50}$",
            message = "The password must be between 8 and 50 characters and include at least one lowercase letter,"
                    + " one uppercase letter, one number, and one special character")
    private String password;
}