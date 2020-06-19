package pl.clinic.project.mvc;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MvcLoginControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    public void shouldDisplayLoginPage() throws Exception {
        // when
        ResultActions resultActions = getActionForLoginPage();
        // then
        resultActions.andExpect
                (MockMvcResultMatchers.content()
                .string(Matchers.containsString("Zaloguj się")))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    @Test
    @WithMockUser(username = "user33@gmail.com", roles = "USER_PATIENT")
    public void shouldRedirectLoggedUser() throws Exception {
        // when
        ResultActions resultActions = getActionForLoginPage();
        // then

        resultActions.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/patients/patientPanel"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldRedirectToAdminPanel() throws Exception {
        // when
        ResultActions resultActions = getActionForLoginPage();
        // then
        resultActions.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/users/admin"));
    }

    private ResultActions getActionForLoginPage() throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.get("/login")).andDo(MockMvcResultHandlers.print());
    }

}