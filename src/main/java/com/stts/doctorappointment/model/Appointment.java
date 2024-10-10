package com.stts.doctorappointment.model;

import com.stts.doctorappointment.dto.response.AppointmentResponseDto;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "APPOINTMENT_SEQ", sequenceName = "APPOINTMENT_SEQ")
    private Long id;

    private boolean taken = false;
    private LocalDateTime endTime;
    private LocalDateTime startTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    @ToString.Exclude
    private Patient patient;

    @Version
    private int version;

    public Appointment(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.taken = false;
    }
}