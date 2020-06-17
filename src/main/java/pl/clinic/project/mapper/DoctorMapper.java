package pl.clinic.project.mapper;

import pl.clinic.project.entities.DoctorEntity;
import pl.clinic.project.model.Doctor;

public class DoctorMapper {

    public DoctorEntity mapToEntity(Doctor doctor) {
        return new DoctorEntity(null, doctor.getFirstName(), doctor.getLastName(),
                doctor.getSpeciality(), doctor.getPhoneNumber(), null);
    }

    public Doctor mapToApi(DoctorEntity doctorEntity) {
        return new Doctor(doctorEntity.getId(), doctorEntity.getFirstName(),
                doctorEntity.getLastName(), doctorEntity.getSpeciality(), doctorEntity.getPhoneNumber());
    }

}
