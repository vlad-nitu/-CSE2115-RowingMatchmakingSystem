package nl.tudelft.cse.sem.template.user.services;


import lombok.AllArgsConstructor;
import nl.tudelft.cse.sem.template.user.domain.User;
import nl.tudelft.cse.sem.template.user.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@AllArgsConstructor
@Service
public class UserService {

    private final transient UserRepository userRepository;

    /**
     * Finds all the users stored in the database.
     *
     * @return List {@literal <}User{@literal >} containing all the users persisted in the repository
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Find the user with the given ID.
     *
     * @param userId - the ID of the user
     * @return User object if a match is found, null otherwise
     */
    public Optional<User> findUserById(String userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return user;
        }
        return null;
    }

    /**
     * Saves a new user in the database.
     *
     * @param user - the User object to be persisted in the repository
     * @return User object containing the saved user
     */
    public User save(User user) {
        return userRepository.save(user);
    }

    /**
     * Finds competitiveness corresponding to the given userId.
     *
     * @param userId - the ID of the user
     * @return boolean defining whether the user is competetive
     */
    public boolean findCompetitivenessByUserId(String userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return user.get().isCompetitive();
        }
        return false;
    }

    /**
     * Finds the gender corresponding to the given userId.
     *
     * @param userId - the ID of the user
     * @return character defining the user's gender
     */
    public Character findGenderById(String userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return user.get().getGender();
        }
        return ' ';
    }


    /**
     * Finds the certificate possessed by the user with the given ID.
     *
     * @param userId - the ID of the user
     * @return String containing the user's certificate
     */
    public String findCertificateById(String userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return user.get().getCertificate();
        }
        return null;
    }


    /**
     * Finds the organisation corresponding to the given userId.
     *
     * @param userId - the ID of the user
     * @return String containing the organisation the user is a part of
     */
    public String findOrganisationById(String userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return user.get().getOrganisation();
        }
        return null;
    }


    /**
     * Finds the positions the user with the given userId is capable of filling.
     *
     * @param userId - the ID of the user
     * @return Set {@literal <}String{@literal >} containing the positions the user is able to fulfill
     */
    public Set<String> findPositionsById(String userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return user.get().getPositions();
        }
        return new HashSet<>();
    }
}
