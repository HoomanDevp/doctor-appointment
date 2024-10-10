package com.stts.doctorappointment;

import com.stts.doctorappointment.exception.AlreadyTakenAppointmentException;
import com.stts.doctorappointment.exception.NoAvailableAppointmentException;
import com.stts.doctorappointment.model.Appointment;
import com.stts.doctorappointment.model.Patient;
import com.stts.doctorappointment.repository.AppointmentRepository;
import com.stts.doctorappointment.repository.PatientRepository;
import com.stts.doctorappointment.service.AppointmentService;
import com.stts.doctorappointment.service.PatientService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DoctorAppointmentResponseDtoApplicationTests {

    @Spy
    PatientRepository patientRepository;
    @Spy
    AppointmentRepository appointmentRepository;

    @SpyBean
    PatientService patientService;
    @SpyBean
    AppointmentService appointmentService;

    @Test
    public void testSoonerEndTimeAddOpenAppointment() throws Exception {
        IllegalArgumentException invalidOpenAppointmentException = assertThrows(IllegalArgumentException.class,
                () -> appointmentService.addOpenTimes(LocalDateTime.now(), LocalDateTime.now().minusHours(2))
        );

        String exceptionMessage = invalidOpenAppointmentException.getMessage();
        Assertions.assertEquals("End time cannot be before start time.", exceptionMessage);
    }

    @Test
    public void testEmptyAppointmentIfRangeIsLessThan30Minute() {
        appointmentService.addOpenTimes(LocalDateTime.now(), LocalDateTime.now().plusMinutes(10));

        Assertions.assertTrue(appointmentService.viewOpenAppointments().isEmpty());
    }

    @Test
    public void testEmptyListIfNoAppointmentIsAvail() {
        Assertions.assertTrue(appointmentService.viewTakenAppointments().isEmpty());
    }

    @Test
    @Transactional()
    public void testTakenAppointmentPatientNameAndPhone() {
        Patient patient = new Patient();
        patient.setName("امیرحسین بیات");
        patient.setPhoneNumber("09109039981");

        appointmentService.addOpenTimes(LocalDateTime.now(), LocalDateTime.now().plusHours(2));
        appointmentService.bookAppointment(1L, patient);
        List<Appointment> takenAppointment = appointmentService.viewReservedAppointment();
        Assertions.assertFalse(takenAppointment.isEmpty());
        Assertions.assertTrue(takenAppointment.get(0).toString().contains("name"));
        Assertions.assertTrue(takenAppointment.get(0).toString().contains("phoneNumber"));
    }

    @Test
    public void testConcurrencyInDeleteTakingAppointment() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10); // 10 concurrent threads

        Patient patient = new Patient();
        patient.setName("امیرحسین بیات");
        patient.setPhoneNumber("09109039981");

        appointmentService.addOpenTimes(LocalDateTime.now(), LocalDateTime.now().plusHours(2));

        Runnable insertTask = () -> {
            appointmentService.bookAppointment(2L, patient);
        };

        Runnable deleteTask = () -> {
            appointmentService.deleteOpenAppointment(2L);
        };

        for (int i = 0; i < 5; i++) {
            executorService.submit(insertTask);
            executorService.submit(deleteTask);
        }

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);
    }

    @Test
    public void testDeleteUnavailableAppointment() {
        Assertions.assertThrows(NoAvailableAppointmentException.class, () -> {
            appointmentService.deleteOpenAppointment(125L);
        });
    }

    @Test
    public void testDeleteAppointmentAlreadyTaken() {
        Patient patient = new Patient();
        patient.setName("امیرحسین بیات");
        patient.setPhoneNumber("09109039981");

        Assertions.assertThrows(AlreadyTakenAppointmentException.class, () -> {
            appointmentService.addOpenTimes(LocalDateTime.now(), LocalDateTime.now().plusHours(2));
            appointmentService.bookAppointment(2L, patient);
            appointmentService.deleteOpenAppointment(2L);
        });
    }

    @Test
    public void testEmptyOpenAppointmentIfDoctorNotSpecified() {
        Assertions.assertTrue(appointmentService.viewOpenAppointments(LocalDate.parse("2024-02-02")).isEmpty());
    }

    @Test
    public void testEmptyPatientBookedAppointment() {
        Assertions.assertTrue(appointmentService.viewReservedAppointment().isEmpty());
    }

    @Test
    @Transactional
    public void testMoreAppointmentBookedByPatient() {
        Patient patient = new Patient();
        patient.setName("امیرحسین بیات");
        patient.setPhoneNumber("09109039981");

        appointmentService.addOpenTimes(LocalDateTime.now(), LocalDateTime.now().plusHours(2));
        appointmentService.bookAppointment(1L, patient);
        appointmentService.bookAppointment(2L, patient);
        appointmentService.bookAppointment(3L, patient);

        List<Appointment> appointments = patientService.findByPhone(patient.getPhoneNumber()).getAppointments();

        Assertions.assertTrue(appointments.size() > 1);
    }
}
