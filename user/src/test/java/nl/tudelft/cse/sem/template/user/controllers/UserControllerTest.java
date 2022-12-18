package nl.tudelft.cse.sem.template.user.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import nl.tudelft.cse.sem.template.user.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import nl.tudelft.cse.sem.template.user.domain.User;
import nl.tudelft.cse.sem.template.user.utils.TimeSlot;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.lenient;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    MockMvc mockMvc;
    @Mock
    private UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private UserController userController;
    private User user;
    private String userId;
    private Boolean competitive;
    private Character gender;
    private String certificate;
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
        positions = new HashSet<>();
        positions.add("cox");
        positions.add("coach");
        timeSlots = new HashSet<>();
        timeSlots.add(new TimeSlot(LocalDateTime.of(2022, 12, 14, 7, 00),
                LocalDateTime.of(2022, 12, 14, 19, 15)));
        user = new User(userId, competitive, gender, organization, certificate, positions, timeSlots);

        this.userController = new UserController(userService);

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

        MvcResult mvcResult = mockMvc
                .perform(post("/createUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user))
                    )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        User obtained = objectMapper.readValue(contentAsString, User.class);
        assertThat(obtained).isEqualTo(user);
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
        Boolean expected = true;
        when(userService.findCompetitivenessByUserId(userId)).thenReturn(expected);
        MvcResult mvcResult = mockMvc
                .perform(get("/sendCompetitiveness/LotteKremer"))
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).contains("true");
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
    }

}
