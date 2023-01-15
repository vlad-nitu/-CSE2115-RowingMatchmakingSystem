package nl.tudelft.cse.sem.template.user.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import nl.tudelft.cse.sem.template.user.authentication.AuthManager;
import nl.tudelft.cse.sem.template.user.publishers.ActivityPublisher;
import nl.tudelft.cse.sem.template.user.utils.BaseActivity;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserActivityControllerTest {
    MockMvc mockMvc;
    @Mock
    private ActivityPublisher activityPublisher;
    @Mock
    private AuthManager authManager;
    private UserActivityController userActivityController;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        this.userActivityController = new UserActivityController(activityPublisher, authManager);

        mockMvc = MockMvcBuilders
                .standaloneSetup(userActivityController)
                .build();
    }

    @Test
    void cancelActivityValid() throws Exception {
        when(activityPublisher.cancelActivity(1L)).thenReturn(204);
        MvcResult mvcResult = mockMvc
                .perform(get("/cancelActivity/1"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).contains("Activity was deleted successfully");
    }

    @Test
    void cancelActivityError() throws Exception {
        when(activityPublisher.cancelActivity(1L)).thenReturn(404);
        MvcResult mvcResult = mockMvc
                .perform(get("/cancelActivity/1"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is4xxClientError())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).contains("Activity deletion was not successful");
    }

    @Test
    void createActivity() throws Exception {
        BaseActivity baseActivity = new BaseActivity();
        baseActivity.setOwnerId("valid");
        when(authManager.getUserId()).thenReturn("valid");
        when(activityPublisher.createActivity(any())).thenReturn(baseActivity);
        MvcResult mvcResult = mockMvc
                .perform(post("/createActivity/training")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(baseActivity))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        BaseActivity obtained = objectMapper.readValue(contentAsString, BaseActivity.class);
        assertThat(obtained).isEqualTo(baseActivity);

        when(activityPublisher.createActivity(any())).thenReturn(null);
        mvcResult = mockMvc
                .perform(post("/createActivity/training")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(baseActivity))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is4xxClientError())
                .andReturn();
        contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).contains("Something went wrong!");

        mvcResult = mockMvc
                .perform(post("/createActivity/invalid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(baseActivity))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is4xxClientError())
                .andReturn();
        contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).contains("Type can only be 'training' or 'competition'.");

        when(authManager.getUserId()).thenReturn("valid");
        baseActivity.setOwnerId("invalid");
        mvcResult = mockMvc
                .perform(post("/createActivity/training")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(baseActivity))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is4xxClientError())
                .andReturn();
        contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).contains("The provided ownerId does not match your userId! Use valid as the ownerId.");

    }
}
