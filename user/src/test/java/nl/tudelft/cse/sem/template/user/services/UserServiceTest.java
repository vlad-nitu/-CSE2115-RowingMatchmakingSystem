package nl.tudelft.cse.sem.template.user.services;

import nl.tudelft.cse.sem.template.user.domain.User;
import nl.tudelft.cse.sem.template.user.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository);
    }

    @Test
    void findAllTest() {
        List<User> expected = new ArrayList<>(List.of(new User()));
        when(userService.findAll()).thenReturn(expected);
        assertThat(userService.findAll()).isEqualTo(expected);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void findUserByIdTest() {
        Optional<User> expected = Optional.of(new User("id", true, 'f', "organisation",
                "certificate", new HashSet<>(), new HashSet<>()));
        when(userService.findUserById("id")).thenReturn(expected);
        assertThat(userService.findUserById("id")).isEqualTo(expected);
        verify(userRepository, times(1)).findById("id");
    }

    @Test
    void saveTest() {
        User user = new User();
        userService.save(user);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void findCompetitivenessByUserIdTest() {
        Optional<User> expected = Optional.of(new User("id", true, 'f', "organisation",
                "certificate", new HashSet<>(), new HashSet<>()));
        when(userRepository.findById("id")).thenReturn(expected);
        assertThat(userService.findCompetitivenessByUserId("id")).isEqualTo(expected.get().isCompetitive());
    }

    @Test
    void findGenderByIdTest() {
        Optional<User> expected = Optional.of(new User("id", true, 'f', "organisation",
                "certificate", new HashSet<>(), new HashSet<>()));
        when(userRepository.findById("id")).thenReturn(expected);
        assertThat(userService.findGenderById("id")).isEqualTo(expected.get().getGender());
    }

    @Test
    void findCertificateByIdTest() {
        Optional<User> expected = Optional.of(new User("id", true, 'f', "organisation",
                "certificate", new HashSet<>(), new HashSet<>()));
        when(userRepository.findById("id")).thenReturn(expected);
        assertThat(userService.findCertificateById("id")).isEqualTo(expected.get().getCertificate());
    }

    @Test
    void findOrganisationByIdTest() {
        Optional<User> expected = Optional.of(new User("id", true, 'f', "organisation",
                "certificate", new HashSet<>(), new HashSet<>()));
        when(userRepository.findById("id")).thenReturn(expected);
        assertThat(userService.findOrganisationById("id")).isEqualTo(expected.get().getOrganisation());
    }

    @Test
    void findPositionsByIdTest() {
        Optional<User> expected = Optional.of(new User("id", true, 'f', "organisation",
                "certificate", new HashSet<>(), new HashSet<>()));
        when(userRepository.findById("id")).thenReturn(expected);
        assertThat(userService.findPositionsById("id")).isEqualTo(expected.get().getPositions());
    }
}