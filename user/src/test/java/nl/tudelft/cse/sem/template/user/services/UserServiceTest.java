package nl.tudelft.cse.sem.template.user.services;

import nl.tudelft.cse.sem.template.user.domain.User;
import nl.tudelft.cse.sem.template.user.repositories.UserRepository;
import nl.tudelft.cse.sem.template.user.utils.TimeSlot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
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
                "certificate", "test@domain.com", new HashSet<>(), new HashSet<>()));
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
                "certificate", "test@domain.com", new HashSet<>(), new HashSet<>()));
        when(userRepository.findById("id")).thenReturn(expected);
        assertThat(userService.findCompetitivenessByUserId("id"))
                .isEqualTo(String.valueOf(expected.get().getIsCompetitive()));
        expected.get().setIsCompetitive(false);
        assertThat(userService.findCompetitivenessByUserId("id"))
                .isEqualTo(String.valueOf(expected.get().getIsCompetitive()));
        when(userRepository.findById("")).thenReturn(Optional.empty());
        assertThat(userService.findCompetitivenessByUserId("")).isEqualTo("error");
    }

    @Test
    void findGenderByIdTest() {
        Optional<User> expected = Optional.of(new User("id", true, 'f', "organisation",
                "certificate", "test@domain.com", new HashSet<>(), new HashSet<>()));
        when(userRepository.findById("id")).thenReturn(expected);
        assertThat(userService.findGenderById("id")).isEqualTo(expected.get().getGender());
        when(userRepository.findById("")).thenReturn(Optional.empty());
        assertThat(userService.findGenderById("")).isEqualTo(' ');
    }

    @Test
    void findCertificateByIdTest() {
        Optional<User> expected = Optional.of(new User("id", true, 'f', "organisation",
                "certificate", "test@domain.com", new HashSet<>(), new HashSet<>()));
        when(userRepository.findById("id")).thenReturn(expected);
        assertThat(userService.findCertificateById("id")).isEqualTo(expected.get().getCertificate());
        when(userRepository.findById("")).thenReturn(Optional.empty());
        assertThat(userService.findCertificateById("")).isEqualTo(null);
    }

    @Test
    void findOrganisationByIdTest() {
        Optional<User> expected = Optional.of(new User("id", true, 'f', "organisation",
                "certificate", "test@domain.com", new HashSet<>(), new HashSet<>()));
        when(userRepository.findById("id")).thenReturn(expected);
        assertThat(userService.findOrganisationById("id")).isEqualTo(expected.get().getOrganisation());
        when(userRepository.findById("")).thenReturn(Optional.empty());
        assertThat(userService.findOrganisationById("")).isEqualTo(null);
    }

    @Test
    void findPositionsByIdTest() {
        Optional<User> expected = Optional.of(new User("id", true, 'f', "organisation",
                "certificate", "test@domain.com", new HashSet<>(), new HashSet<>()));
        when(userRepository.findById("id")).thenReturn(expected);
        assertThat(userService.findPositionsById("id")).isEqualTo(expected.get().getPositions());
        when(userRepository.findById("")).thenReturn(Optional.empty());
        assertThat(userService.findPositionsById("")).isEqualTo(null);
    }

    @Test
    void findEmailById() {
        Optional<User> expected = Optional.of(new User("id", true, 'f', "organisation",
                "certificate", "test@domain.com", new HashSet<>(), new HashSet<>()));
        when(userRepository.findById("id")).thenReturn(expected);
        assertThat(userService.findEmailById("id")).isEqualTo(expected.get().getEmail());
        when(userRepository.findById("")).thenReturn(Optional.empty());
        assertThat(userService.findEmailById("")).isEqualTo(null);
    }

    @Test
    void findTimeSlotsById() {
        Set<TimeSlot> timeSlots = new HashSet<>();
        timeSlots.add(new TimeSlot(LocalDateTime.of(2022, 12, 14, 7, 00),
                LocalDateTime.of(2022, 12, 14, 19, 15)));
        Optional<User> expected = Optional.of(new User("id", true, 'f', "organisation",
                "certificate", "test@domain.com", new HashSet<>(), timeSlots));
        when(userRepository.findById("id")).thenReturn(expected);
        assertThat(userService.findTimeSlotsById("id")).isEqualTo(timeSlots);
        when(userRepository.findById("")).thenReturn(Optional.empty());
        assertThat(userService.findTimeSlotsById("")).isEqualTo(Set.of());
    }
}