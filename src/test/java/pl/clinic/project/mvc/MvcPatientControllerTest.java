package pl.clinic.project.mvc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import pl.clinic.project.UserRole;
import pl.clinic.project.entities.PatientEntity;
import pl.clinic.project.model.Patient;
import pl.clinic.project.model.User;
import pl.clinic.project.service.PatientService;
import pl.clinic.project.service.UserService;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MvcPatientControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PatientService patientService;

    @MockBean
    UserService userService;

    @Test
    @WithMockUser(roles = "USER_PATIENT")
    public void shouldDisplayAddPatientPage() throws Exception {
        // when
        ResultActions resultActions = getResultActionFor("/patients");
        // then
        resultActions.andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("patient"));
    }

    @Test
    @WithMockUser(roles = "USER_PATIENT")
    public void shouldAddNewPatient() throws Exception {
        // given
        Patient patient = new Patient(
                1, "Name", "Last", "60071465315", "880880770");
        PatientEntity patientEntity = new PatientEntity(
                1, "Name", "Last", "60071465315", "880880770", Collections.emptyList());
        // when
        Mockito.when(patientService.createPatient(patient)). thenReturn(patientEntity);
        ResultActions resultActions = mockMvc
                .perform(post("/patients", patient)
                .sessionAttrs(Map.of("user", new User(), "patient", patient)))
                .andDo(print());
        // then
        Mockito.verify(userService, Mockito.times(1)).setPatientId(Mockito.any(User.class));
        resultActions.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patients/patientPanel"));
    }

    @Test
    @WithMockUser(roles = "USER_PATIENT", username = "user@wp.pl")
    public void shouldDisplayPatientsPanel() throws Exception {
        // given
        User user = new User(1, "user@wp.pl", "pass", UserRole.USER_PATIENT, 1, null);
        Patient patient = new Patient(1, "Name", "Last", "60071465315", "200200300");
        // when
        Mockito.when(userService.getByEmail("user@wp.pl")).thenReturn(user);
        Mockito.when(patientService.getById(1)).thenReturn(patient);
        ResultActions resultActions = getResultActionFor("/patients/patientPanel");
        // then
        resultActions.andExpect(model().attribute("user", user))
                .andExpect(model().attribute("patient", patient))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @WithMockUser(roles = "USER_PATIENT", username = "user@wp.pl")
    public void shouldRedirectToAddPatientWhenNoPatientId() throws Exception {
        // given
        User user = new User(1, "user@wp.pl", "pass", UserRole.USER_PATIENT, null, null);
        // when
        Mockito.when(userService.getByEmail(Mockito.anyString())).thenReturn(user);
        ResultActions resultActions = getResultActionFor("/patients/patientPanel");
        // then
        resultActions
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patients"));
    }

    @Test
    @WithMockUser(roles = "USER_DOCTOR")
    public void shouldNotLetDoctorToPatientPanel() throws Exception {
        // when
        ResultActions resultActions = getResultActionFor("/patients/patientPanel");
        // then
        resultActions.andExpect(status().is4xxClientError());
    }

    private ResultActions getResultActionFor(String uri) throws Exception {
        return mockMvc.perform(get(uri)).andDo(print());
    }


}
