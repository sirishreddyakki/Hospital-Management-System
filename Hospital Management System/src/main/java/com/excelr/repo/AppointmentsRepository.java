package com.excelr.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import com.excelr.model.Appointments;
@Repository
public interface AppointmentsRepository extends JpaRepository<Appointments, Long> {

	List<Appointments> findByPatientId(Long patientId);
    List<Appointments> findByDoctorId(Long doctorId);


}
