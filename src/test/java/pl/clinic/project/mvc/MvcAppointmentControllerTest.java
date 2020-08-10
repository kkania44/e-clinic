package pl.clinic.project.mvc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.clinic.project.UserRole;
import pl.clinic.project.model.Appointment;
import pl.clinic.project.model.Doctor;
import pl.clinic.project.model.User;
import pl.clinic.project.service.AppointmentService;
import pl.clinic.project.service.DoctorService;
import pl.clinic.project.service.UserService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MvcAppointmentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AppointmentService appointmentService;
    @MockBean
    DoctorService doctorService;
    @MockBean
    UserService userService;

    @Test
    @WithMockUser(roles = "USER_PATIENT")
    public void createAppointmentPageTest() throws Exception {
        // when
        when(doctorService.getById(1)).thenReturn(new Doctor());
        // then
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/appointments/book/1"))
                .andDo(print());
        resultActions.andExpect(
                model().attributeExists("appointment", "doctor", "dates"));
    }

    @Test
    @WithMockUser(roles = "USER_PATIENT", username = "user@wp.pl")
    public void createAppointmentTest() throws Exception {
        // given
        User user = new User(1, "user@wp.pl", "pass", UserRole.USER_PATIENT, 1, null);
        Appointment appointment = new Appointment(1, 1, 1, LocalDate.now(), LocalTime.now());
        // when
        when(userService.getByEmail("user@wp.pl")).thenReturn(user);
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/appointments/book/1")
                .sessionAttr("appointment", appointment))
                .andDo(print());
        // then
        verify(appointmentService).createAppointment(Mockito.any(Appointment.class));
        resultActions.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patients/patientPanel"));
    }

    @Test
    @WithMockUser(roles = "USER_PATIENT", username = "user@wp.pl")
    public void showAppointmentsForPatientTest() throws Exception {
        // given
        User user = new User(1, "user@wp.pl", "pass", UserRole.USER_PATIENT, 1, null);
        Doctor doctor = new Doctor(1, "Name", "Name", "rodzinny", "100300500");
        // when
        when(userService.getByEmail("user@wp.pl")).thenReturn(user);
        when(doctorService.getById(1)).thenReturn(doctor);
        when(appointmentService.getAllByPatientId(1))
                .thenReturn(Collections.singletonList(
                        new Appointment(1,1,1, LocalDate.now(), LocalTime.now())));
        // then
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/appointments/appointmentData"))
                .andDo(print());
        resultActions.andExpect(
                model().attributeExists("isAdmin", "appointments"))
                .andExpect(status().is2xxSuccessful());
    }

}
