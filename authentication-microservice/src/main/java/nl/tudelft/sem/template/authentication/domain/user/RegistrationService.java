package nl.tudelft.sem.template.authentication.domain.user;

import org.springframework.stereotype.Service;

/**
 * A DDD service for registering a new user.
 */
@Service
public class RegistrationService {
    private final transient UserRepository userRepository;
    private final transient PasswordHashingService passwordHashingService;

    /**
     * Instantiates a new UserService.
     *
     * @param userRepository  the user repository
     * @param passwordHashingService the password encoder
     */
    public RegistrationService(UserRepository userRepository, PasswordHashingService passwordHashingService) {
        this.userRepository = userRepository;
        this.passwordHashingService = passwordHashingService;
    }

    /**
     * Register a new user.
     *
     * @param userId    The UserID of the user
     * @param password The password of the user
     * @throws Exception if the user already exists
     */
    public AppUser registerUser(UserId userId, Password password) throws Exception {

        if (checkUserIdIsUnique(userId)) {
            // Hash password
            HashedPassword hashedPassword = passwordHashingService.hash(password);

            // Create new account
            AppUser user = new AppUser(userId, hashedPassword);
            userRepository.save(user);

            return user;
        }

        throw new UserIdAlreadyInUseException(userId);
    }

    public boolean checkUserIdIsUnique(UserId userId) {
        return !userRepository.existsByUserId(userId);
    }
}
