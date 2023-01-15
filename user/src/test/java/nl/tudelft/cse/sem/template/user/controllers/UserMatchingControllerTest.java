package nl.tudelft.cse.sem.template.user.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import nl.tudelft.cse.sem.template.user.authentication.AuthManager;
import nl.tudelft.cse.sem.template.user.domain.User;
import nl.tudelft.cse.sem.template.user.publishers.MatchingPublisher;
import nl.tudelft.cse.sem.template.user.services.UserService;
import nl.tudelft.cse.sem.template.user.utils.BaseActivity;
import nl.tudelft.cse.sem.template.user.utils.BaseMatching;
import nl.tudelft.cse.sem.template.user.utils.Pair;
import nl.tudelft.cse.sem.template.user.utils.TimeSlot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserMatchingControllerTest {
    MockMvc mockMvc;
    @Mock
    private MatchingPublisher matchingPublisher;
    @Mock
    private AuthManager authManager;
    @Mock
    private UserService userService;
    private UserMatchingController userMatchingController;
    private final ObjectMapper objectMapper = new ObjectMapper();

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

        this.userMatchingController = new UserMatchingController(matchingPublisher, authManager, userService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(userMatchingController)
                .build();
    }

    @Test
    void getAvailableActivities() throws Exception {
        List<Pair<Long, String>> expected = List.of(new Pair<Long, String>(1L, "testGetAvailableActivities"));
        when(userService.findTimeSlotsById(null)).thenReturn(new HashSet<>());
        when(matchingPublisher.getAvailableActivities(any(), anySet())).thenReturn(expected);
        MvcResult mvcResult = mockMvc
                .perform(get("/getAvailableActivities"))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).doesNotContain("There is no user with the given userId!");
        assertThat(contentAsString).contains("testGetAvailableActivities");

        when(userService.findTimeSlotsById(null)).thenReturn(null);
        mvcResult = mockMvc
                .perform(get("/getAvailableActivities"))
                .andExpect(status().is4xxClientError())
                .andReturn();
        contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).contains("There is no user with the given userId!");

        when(userService.findTimeSlotsById(null)).thenReturn(new HashSet<>());
        expected = null;
        when(matchingPublisher.getAvailableActivities(any(), anySet())).thenReturn(expected);
        mvcResult = mockMvc
                .perform(get("/getAvailableActivities"))
                .andExpect(status().is4xxClientError())
                .andReturn();
        contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).contains("Something went wrong!");
    }

    @Test
    void decideMatch() throws Exception {
        BaseMatching baseMatching = new BaseMatching();
        when(authManager.getUserId()).thenReturn(user.getUserId());
        when(matchingPublisher.decideMatch(user.getUserId(), "accept", baseMatching)).thenReturn(baseMatching);
        MvcResult mvcResult = mockMvc
                .perform(post("/decideMatch/accept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(baseMatching))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        BaseMatching obtained = objectMapper.readValue(contentAsString, BaseMatching.class);
        assertThat(obtained).isEqualTo(baseMatching);

        mvcResult = mockMvc
                .perform(post("/decideMatch/invalid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(baseMatching))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is4xxClientError())
                .andReturn();
        contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).contains("Decision can only be 'accept' or 'decline'.");

        when(authManager.getUserId()).thenReturn("invalid");
        when(matchingPublisher.decideMatch("invalid", "accept", baseMatching)).thenReturn(null);
        mvcResult = mockMvc
                .perform(post("/decideMatch/accept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(baseMatching))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is4xxClientError())
                .andReturn();
        contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).contains("Something went wrong!");
    }

    @Test
    void getUserActivities() throws Exception {
        List<Long> expected = List.of(1L, 2L);
        when(matchingPublisher.getUserActivities(null)).thenReturn(expected);
        MvcResult mvcResult = mockMvc
                .perform(get("/getUserActivities"))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).contains(expected.toString().replace(" ", ""));

        expected = null;
        when(matchingPublisher.getUserActivities(null)).thenReturn(expected);
        mvcResult = mockMvc
                .perform(get("/getUserActivities"))
                .andExpect(status().is4xxClientError())
                .andReturn();
        contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).contains("Something went wrong!");
    }

    @Test
    void chooseActivity() throws Exception {
        BaseMatching baseMatching = new BaseMatching();
        when(authManager.getUserId()).thenReturn(user.getUserId());
        when(matchingPublisher.chooseActivity(any())).thenReturn(baseMatching);
        MvcResult mvcResult = mockMvc
                .perform(post("/chooseActivity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(baseMatching))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        BaseMatching obtained = objectMapper.readValue(contentAsString, BaseMatching.class);
        assertThat(obtained).isEqualTo(baseMatching);

        when(matchingPublisher.chooseActivity(any())).thenReturn(null);
        mvcResult = mockMvc
                .perform(post("/chooseActivity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(baseMatching))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is4xxClientError())
                .andReturn();
        contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).contains("Something went wrong!");

    }

    @Test
    void unenroll() throws Exception {
        BaseActivity baseActivity = new BaseActivity();
        Pair<String, Long> expected = new Pair<>();
        when(authManager.getUserId()).thenReturn(user.getUserId());
        when(matchingPublisher.unenroll(any())).thenReturn(expected);
        MvcResult mvcResult = mockMvc
                .perform(post("/unenroll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(1L))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        Pair<String, Long> obtained = objectMapper.readValue(contentAsString, Pair.class);
        assertThat(obtained).isEqualTo(expected);

        when(matchingPublisher.unenroll(any())).thenReturn(null);
        mvcResult = mockMvc
                .perform(post("/unenroll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(1L))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is4xxClientError())
                .andReturn();
        contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).contains("Something went wrong!");
    }
}
