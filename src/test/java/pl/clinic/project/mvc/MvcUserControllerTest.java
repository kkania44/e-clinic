package pl.clinic.project.mvc;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.clinic.project.UserRole;
import pl.clinic.project.model.Email;
import pl.clinic.project.model.User;
import pl.clinic.project.repositories.UserRepository;
import pl.clinic.project.service.MailService;
import pl.clinic.project.service.UserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MvcUserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserRepository repository;
    @MockBean
    UserService service;

    MailService mailService = Mockito.mock(MailService.class);

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldShowAdminPanel() throws Exception {
        // when
        ResultActions resultActions = getDefaultResultActions("admin");
        // then
        resultActions.andExpect(content()
                .string(Matchers.containsString("Panel admina")))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @WithMockUser(roles = "USER_PATIENT")
    public void shouldNotLetPatientToAdminPage() throws Exception {
        // when
        ResultActions resultActions = getDefaultResultActions("admin");
        // then
        resultActions.andExpect(status().is4xxClientError());
    }

    @Test
    @WithAnonymousUser
    public void shouldRedirectAnonymousUserToRegister() throws Exception {
        // when
        ResultActions resultActions = getDefaultResultActions("add");
        // then
        resultActions.andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("user"))
                .andExpect(content().string(Matchers.containsString("Uzupełnij dane i zarejestruj się:")));
    }

    @Test
    @WithMockUser(username = "sampleUser", roles = "USER_PATIENT")
    public void shouldNotLetLoggedUserToRegister() throws Exception {
        // when
        ResultActions resultActions = getDefaultResultActions("/add");
        // then
        resultActions.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    public void addingNewUserTest() throws Exception {
        User user = new User(1, "user@wp.pl", "pass", UserRole.USER_PATIENT, null,null);
        //when
        ResultActions resultActions = mockMvc
                .perform(post("/users/add", user))
                .andDo(print());
        // then
        verify(service).registerUserAsPatient(any(User.class));
        resultActions.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    public void passwordResetPageTest() throws Exception {
        // when
        ResultActions resultActions = mockMvc
                .perform(get("/users/resetPassword"))
                .andDo(print());
        // then
        resultActions.andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("user"));
    }

    private ResultActions getDefaultResultActions(String url) throws Exception {
        return mockMvc.perform(get("/users/"+url)).andDo(print());
    }
}
