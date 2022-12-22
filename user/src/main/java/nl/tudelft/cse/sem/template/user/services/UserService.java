package nl.tudelft.cse.sem.template.user.services;


import lombok.AllArgsConstructor;
import nl.tudelft.cse.sem.template.user.domain.User;
import nl.tudelft.cse.sem.template.user.repositories.UserRepository;
import nl.tudelft.cse.sem.template.user.utils.TimeSlot;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@AllArgsConstructor
@Service
@SuppressWarnings("PMD.ReturnEmptyCollectionRatherThanNull")
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
        return user;
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
     * @return String defining whether the user is competitive or an indication that an error was encountered
     */
    public String findCompetitivenessByUserId(String userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return user.get().getIsCompetitive() ? "true" : "false";
        }
        return "error";
    }

    /**
     * Finds the gender corresponding to the given userId.
     *
     * @param userId - the ID of the user
     * @return character defining the user's gender or an indication that an error was encountered
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
     * @return String containing the user's certificate or an indication that an error was encountered
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
     * @return String containing the organisation the user is a part of or an indication that an error was encountered
     */
    public String findOrganisationById(String userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return user.get().getOrganisation();
        }
        return null;
    }

    /**
     * Finds the e-mail address corresponding to the given userId.
     *
     * @param userId - the ID of the user
     * @return String containing the user's e-mail address or an indication that an error was encountered
     */
    public String findEmailById(String userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return user.get().getEmail();
        }
        return null;
    }


    /**
     * Finds the positions the user with the given userId is capable of filling.
     *
     * @param userId - the ID of the user
     * @return Set {@literal <}String{@literal >} containing the positions the user is able to fulfill
     *      or an indication that an error was encountered
     */
    public Set<String> findPositionsById(String userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return user.get().getPositions();
        }
        return null;
    }

    /**
     * Finds the timeslots that the user with the given userId is available for.
     *
     * @param userId - the ID of the user
     * @return Set of Timeslots that the user is available for enrollment
     */
    public Set<TimeSlot> findTimeSlotsById(String userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return user.get().getTimeSlots();
        } else {
            return null;
        }
    }
}
