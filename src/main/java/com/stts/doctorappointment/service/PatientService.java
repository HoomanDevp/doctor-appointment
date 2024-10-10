package com.stts.doctorappointment.service;

import com.stts.doctorappointment.exception.PatientNotFoundException;
import com.stts.doctorappointment.model.Patient;
import com.stts.doctorappointment.repository.AppointmentRepository;
import com.stts.doctorappointment.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;

    public Patient findByPhone(String phone) {
        Optional<Patient> optionalPatient = patientRepository.findByPhoneNumber(phone);
        Patient patient = optionalPatient.orElseThrow(() ->
                new PatientNotFoundException("There is no patient with phone: " + phone)
        );
        patient.setAppointments(appointmentRepository.findAllByPatientId(patient.getId()));
        return patient;
    }

    public Patient addPatient(Patient patient) {
        return patientRepository.save(patient);
    }
}
