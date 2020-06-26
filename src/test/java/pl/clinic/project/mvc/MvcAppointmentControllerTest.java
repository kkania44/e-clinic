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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.clinic.project.model.Doctor;
import pl.clinic.project.service.AppointmentService;
import pl.clinic.project.service.DoctorService;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

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

    @Test
    @WithMockUser(roles = "USER_PATIENT")
    public void shouldGoToBookingModelWithAttributes() throws Exception {
        // given
        Mockito.when(doctorService.getById(1)).thenReturn(java.util.Optional.of(new Doctor()));
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/appointments/book/1"))
                .andDo(print());
        resultActions.andExpect(
                MockMvcResultMatchers.model().attributeExists("appointment", "doctor", "dates"));
    }

}