package pl.clinic.project.mapper;

import pl.clinic.project.entities.AppointmentEntity;
import pl.clinic.project.entities.DoctorEntity;
import pl.clinic.project.entities.PatientEntity;
import pl.clinic.project.model.Appointment;

public class AppointmentMapper {

    public AppointmentEntity mapToEntity(Appointment appointment, DoctorEntity doctor, PatientEntity patient) {
        return new AppointmentEntity(null, doctor, patient,
                appointment.getDate(), appointment.getTime());
    }

    public Appointment mapToApi(AppointmentEntity appointmentEntity) {
        return new Appointment(appointmentEntity.getId(), appointmentEntity.getDoctor().getId(),
                appointmentEntity.getPatient().getId(), appointmentEntity.getDate(),
                appointmentEntity.getTime());
    }

}
