package nl.tudelft.cse.sem.template.user.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import nl.tudelft.cse.sem.template.user.authentication.AuthManager;
import nl.tudelft.cse.sem.template.user.domain.User;
import nl.tudelft.cse.sem.template.user.services.UserService;
import nl.tudelft.cse.sem.template.user.utils.TimeSlot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    MockMvc mockMvc;
    @Mock
    private UserService userService;
    @Mock
    private AuthManager authManager;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private UserController userController;
    private User user;
    private String userId;
    private Boolean competitive;
    private Character gender;
    private String certificate;
    private  String email;
    private String organization;
    private Set<String> positions;
    private Set<TimeSlot> timeSlots;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        userId = "LotteKremer";
        certificate = "C4";
        organization = "Laga";
        competitive = true;
        gender = 'F';
        email = "test@domain.com";
        positions = new HashSet<>();
        positions.add("cox");
        positions.add("coach");
        timeSlots = new HashSet<>();
        timeSlots.add(new TimeSlot(LocalDateTime.of(2022, 12, 14, 7, 00),
                LocalDateTime.of(2022, 12, 14, 19, 15)));
        user = new User(userId, competitive, gender, organization, certificate, email, positions, timeSlots);

        this.userController = new UserController(userService, authManager);

        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();
    }

    @Test
    public void createUserTest() throws Exception {

        TimeSlot t1 = new TimeSlot(LocalDateTime.of(2003, 12, 1, 23, 15),
                LocalDateTime.of(2003, 10, 5, 23, 15));

        TimeSlot t2 = new TimeSlot(LocalDateTime.of(2004, 12, 1, 23, 15),
                LocalDateTime.of(2003, 10, 5, 23, 15));

        TimeSlot t3 = new TimeSlot(LocalDateTime.of(2005, 12, 1, 23, 15),
                LocalDateTime.of(2003, 10, 5, 23, 15));

        user.setTimeSlots(Set.of(t1, t2, t3));
        user.setOrganisation("Laga5");

        lenient().when(userService.save(user))
                .thenReturn(user);
        when(authManager.getUserId()).thenReturn(user.getUserId());

        user.setPositions(new HashSet<>(Set.of("invalid")));
        MvcResult mvcResult = mockMvc
                .perform(post("/createUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is4xxClientError())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        boolean expected = contentAsString.contains("One of the positions that you provided is not valid!");
        assertThat(expected);

        user.setPositions(positions);
        mvcResult = mockMvc
                .perform(post("/createUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user))
                    )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();

        contentAsString = mvcResult.getResponse().getContentAsString();
        User obtained = objectMapper.readValue(contentAsString, User.class);
        assertThat(obtained).isEqualTo(user);

        user.setUserId("Invalid!");
        mvcResult = mockMvc
                .perform(post("/createUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is4xxClientError())
                .andReturn();
        contentAsString = mvcResult.getResponse().getContentAsString();
        expected = contentAsString.contains("The provided ID is invalid!");
        assertThat(expected);

        user.setUserId("Valid");
        user.setGender('N');
        mvcResult = mockMvc
                .perform(post("/createUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is4xxClientError())
                .andReturn();
        contentAsString = mvcResult.getResponse().getContentAsString();
        expected = contentAsString.contains("The provided gender is invalid!");
        assertThat(expected);

        user.setGender('M');
        when(authManager.getUserId()).thenReturn("userId");
        mvcResult = mockMvc
                .perform(post("/createUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is4xxClientError())
                .andReturn();
        contentAsString = mvcResult.getResponse().getContentAsString();
        expected = contentAsString.contains("The provided userId does not match your userId! Use userId as the userId");
        assertThat(expected);

        lenient().when(userService.findUserById(anyString()))
                .thenReturn(Optional.of(new User()));
        when(authManager.getUserId()).thenReturn(user.getUserId());
        mvcResult = mockMvc
                .perform(post("/createUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is4xxClientError())
                .andReturn();
        contentAsString = mvcResult.getResponse().getContentAsString();
        expected = contentAsString.contains("User with the given ID already exists!");
        assertThat(expected);
    }

    @Test
    public void findAllTest() throws Exception {
        List<User> expected = List.of(user);
        when(userService.findAll()).thenReturn(expected);
        MvcResult mvcResult = mockMvc
                .perform(get("/findAll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expected))
                )
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        List<User> obtained = objectMapper.readValue(contentAsString, List.class);
        assertThat(obtained.toString()).contains("LotteKremer", "C4", "Laga", "true", "F");
        assertThat(obtained.size()).isEqualTo(1);
    }

    @Test
    public void sendCompetitivenessTest() throws Exception {
        String expected = "true";
        when(userService.findCompetitivenessByUserId(userId)).thenReturn(expected);
        MvcResult mvcResult = mockMvc
                .perform(get("/sendCompetitiveness/LotteKremer"))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).contains("true");

        expected = "false";
        when(userService.findCompetitivenessByUserId(userId)).thenReturn(expected);
        mvcResult = mockMvc
                .perform(get("/sendCompetitiveness/LotteKremer"))
                .andExpect(status().isOk())
                .andReturn();
        contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).contains("false");

        expected = "error";
        userId = "noSuchUser";
        when(userService.findCompetitivenessByUserId(userId)).thenReturn(expected);
        mvcResult = mockMvc
                .perform(get("/sendCompetitiveness/noSuchUser"))
                .andExpect(status().is4xxClientError())
                .andReturn();
        contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).contains("There is no user with the given userId!");
    }

    @Test
    public void sendGenderTest() throws Exception {
        Character expected = 'F';
        when(userService.findGenderById(userId)).thenReturn(expected);
        MvcResult mvcResult = mockMvc
                .perform(get("/sendGender/LotteKremer"))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).contains("F");

        expected = ' ';
        userId = "noSuchUser";
        when(userService.findGenderById(userId)).thenReturn(expected);
        mvcResult = mockMvc
                .perform(get("/sendGender/noSuchUser"))
                .andExpect(status().is4xxClientError())
                .andReturn();
        contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).contains("There is no user with the given userId!");
    }

    @Test
    public void sendCertificateTest() throws Exception {
        String expected = "C4";
        when(userService.findCertificateById(userId)).thenReturn(expected);
        MvcResult mvcResult = mockMvc
                .perform(get("/sendCertificate/LotteKremer"))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).contains("C4");

        expected = null;
        userId = "noSuchUser";
        when(userService.findCertificateById(userId)).thenReturn(expected);
        mvcResult = mockMvc
                .perform(get("/sendCertificate/noSuchUser"))
                .andExpect(status().is4xxClientError())
                .andReturn();
        contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).contains("There is no user with the given userId!");
    }

    @Test
    public void sendOrganizationTest() throws Exception {
        String expected = "Laga";
        when(userService.findOrganisationById(userId)).thenReturn(expected);
        MvcResult mvcResult = mockMvc
                .perform(get("/sendOrganization/LotteKremer"))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).contains("Laga");

        expected = null;
        userId = "noSuchUser";
        when(userService.findOrganisationById(userId)).thenReturn(expected);
        mvcResult = mockMvc
                .perform(get("/sendOrganization/noSuchUser"))
                .andExpect(status().is4xxClientError())
                .andReturn();
        contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).contains("There is no user with the given userId!");
    }

    @Test
    public void sendPositionsTest() throws Exception {
        Set<String> expected = Set.of("Cox", "Coach");
        when(userService.findPositionsById(userId)).thenReturn(expected);
        MvcResult mvcResult = mockMvc
                .perform(get("/sendPositions/LotteKremer"))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).contains("Cox");
        assertThat(contentAsString).contains("Coach");

        expected = null;
        userId = "noSuchUser";
        when(userService.findPositionsById(userId)).thenReturn(expected);
        mvcResult = mockMvc
                .perform(get("/sendPositions/noSuchUser"))
                .andExpect(status().is4xxClientError())
                .andReturn();
        contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).contains("There is no user with the given userId!");
    }

    @Test
    public void sendTimeSlotsTest() throws Exception {
        Set<TimeSlot> expected = timeSlots;
        when(userService.findTimeSlotsById(userId)).thenReturn(expected);
        MvcResult mvcResult = mockMvc
                .perform(get("/sendTimeSlots/LotteKremer"))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).contains("[{\"start\":\"2022-12-14 07:00\",\"end\":\"2022-12-14 19:15\"}]");

        expected = null;
        userId = "noSuchUser";
        when(userService.findTimeSlotsById(userId)).thenReturn(expected);
        mvcResult = mockMvc
                .perform(get("/sendTimeSlots/noSuchUser"))
                .andExpect(status().is4xxClientError())
                .andReturn();
        contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).contains("There is no user with the given userId!");
    }

    @Test
    void sendEmail() throws Exception {
        String expected = "test@domain.com";
        when(userService.findEmailById(userId)).thenReturn(expected);
        MvcResult mvcResult = mockMvc
                .perform(get("/sendEmail/LotteKremer"))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).contains("test@domain.com");

        expected = null;
        userId = "noSuchUser";
        when(userService.findEmailById(userId)).thenReturn(expected);
        mvcResult = mockMvc
                .perform(get("/sendEmail/noSuchUser"))
                .andExpect(status().is4xxClientError())
                .andReturn();
        contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).contains("There is no user with the given userId!");
    }

    @Test
    void changeTimeSlotBadId() throws Exception {
        when(authManager.getUserId()).thenReturn("userId");
        Set<TimeSlot> times = Set.of(new TimeSlot(LocalDateTime.now(), LocalDateTime.now().plusHours(2)));
        MvcResult mvcResult = mockMvc
                .perform(post("/changeTimeSlot/badUserId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(times))
                )
                .andExpect(status().isForbidden())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).contains("The provided userId does not match your userId");
    }

    @Test
    void changeTimeSlotNoUser() throws Exception {
        when(authManager.getUserId()).thenReturn("userId");
        when(userService.findUserById("userId")).thenReturn(Optional.empty());
        Set<TimeSlot> times = Set.of(new TimeSlot(LocalDateTime.now(), LocalDateTime.now().plusHours(2)));
        MvcResult mvcResult = mockMvc
                .perform(post("/changeTimeSlot/userId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(times))
                )
                .andExpect(status().isBadRequest())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).contains("The user does not exist");
    }

    @Test
    void changeTimeSlotValidation() throws Exception {
        mockMvc
                .perform(post("/changeTimeSlot/userId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(null))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void changeTimeSlotSuccess() throws Exception {
        when(authManager.getUserId()).thenReturn("userId");
        Set<TimeSlot> times = Set.of(new TimeSlot(LocalDateTime.now(), LocalDateTime.now().plusHours(2)));
        when(userService.findUserById("userId")).thenReturn(Optional.of(user));
        when(userService.save(user)).thenReturn(user);
        mockMvc
                .perform(post("/changeTimeSlot/userId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(times))
                )
                .andExpect(status().isOk());
        verify(userService).save(any());
    }
}
