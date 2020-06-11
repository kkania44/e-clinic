package pl.clinic.project.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.clinic.project.entities.AppointmentEntity;
import pl.clinic.project.entities.DoctorEntity;
import pl.clinic.project.entities.PatientEntity;
import pl.clinic.project.exception.NotFoundException;
import pl.clinic.project.mapper.AppointmentMapper;
import pl.clinic.project.model.Appointment;
import pl.clinic.project.model.Patient;
import pl.clinic.project.repositories.AppointmentRepository;
import pl.clinic.project.repositories.DoctorRepository;
import pl.clinic.project.repositories.PatientRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    private AppointmentRepository appointmentRepository;
    private DoctorRepository doctorRepository;
    private PatientRepository patientRepository;
    private AppointmentMapper mapper;

    public AppointmentService(AppointmentRepository appointmentRepository, DoctorRepository doctorRepository,
                              PatientRepository patientRepository, AppointmentMapper mapper) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.mapper = mapper;
    }

    public void createAppointment(Appointment appointment) {
        DoctorEntity doctor = doctorRepository.findById(appointment.getDoctorId()).get();
        PatientEntity patient = patientRepository.findById(appointment.getPatientId()).get();
        AppointmentEntity appointmentEntity = mapper.mapToEntity(appointment, doctor, patient);
        appointmentRepository.save(appointmentEntity);
    }

    public Appointment getById(Integer id) {
        return appointmentRepository.findById(id)
                .map(mapper::mapToApi)
                .orElseThrow(() -> new NotFoundException("Wizyta o tym id nie istnieje"));
    }

    public List<Appointment> getAllByPatientId(Integer patientId) {
        return appointmentRepository.findAllByPatientId(patientId)
                .stream()
                .map(mapper::mapToApi)
                .collect(Collectors.toList());
    }

    public List<LocalTime> getAllHoursByDoctorIdAndDate(Integer doctorId, LocalDate date) {
        return appointmentRepository.findAllByDoctorIdAndDate(doctorId, date).stream()
                .map(ent -> ent.getTime())
                .collect(Collectors.toList());
    }

    public List<Appointment> getAllByDoctorIdAndDate(Integer id, LocalDate date) {
        return appointmentRepository.findAllByDoctorIdAndDate(id, date).stream()
                .map(mapper::mapToApi)
                .collect(Collectors.toList());
    }

    public void deleteById(Integer id){
        appointmentRepository.deleteById(id);
    }

    @Transactional
    public void deleteAppointmentsWithPastDate(LocalDate date) {
        appointmentRepository.deleteByDateBefore(date);
    }

}
