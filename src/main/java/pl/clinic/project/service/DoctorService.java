package pl.clinic.project.service;

import org.springframework.stereotype.Service;
import pl.clinic.project.mapper.DoctorMapper;
import pl.clinic.project.model.Doctor;
import pl.clinic.project.repositories.DoctorRepository;

@Service
public class DoctorService {

    private DoctorRepository doctorRepository;
    private DoctorMapper mapper;

    public DoctorService(DoctorRepository doctorRepository, DoctorMapper mapper) {
        this.doctorRepository = doctorRepository;
        this.mapper = mapper;
    }

    public void createDoctor(Doctor doctor) {

    }
}
