package pl.clinic.project.mapper;

import pl.clinic.project.entities.PatientEntity;
import pl.clinic.project.model.Patient;

public class PatientMapper {

    public Patient mapToApi(PatientEntity patientEntity) {
        return new Patient(patientEntity.getId(), patientEntity.getFirstName(), patientEntity.getLastName(),
                patientEntity.getPeselNumber(), patientEntity.getPhoneNumber());
    }

    public PatientEntity mapToEntity(Patient patient) {
        return new PatientEntity(patient.getId(), patient.getFirstName(), patient.getLastName(),
                patient.getPeselNumber(), patient.getPhoneNumber());
    }

}
