package pl.clinic.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.clinic.project.mapper.AppointmentMapper;
import pl.clinic.project.mapper.DoctorMapper;
import pl.clinic.project.mapper.PatientMapper;
import pl.clinic.project.mapper.UserMapper;
import pl.clinic.project.model.Doctor;

@Configuration
public class MapperConfig {

    @Bean
    PatientMapper patientMapper() {return new PatientMapper();}

    @Bean
    DoctorMapper doctorMapper() {
        return new DoctorMapper();
    }

    @Bean
    UserMapper userMapper() {
        return new UserMapper();
    }

    @Bean
    AppointmentMapper appointmentMapper() {
        return new AppointmentMapper();
    }

}
