package pl.clinic.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.clinic.project.mapper.PatientMapper;

@Configuration
public class MapperConfig {

    @Bean
    PatientMapper patientMapper() {return new PatientMapper();}
}
