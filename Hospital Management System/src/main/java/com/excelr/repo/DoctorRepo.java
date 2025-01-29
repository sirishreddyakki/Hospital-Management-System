package com.excelr.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.excelr.model.Doctor;

public interface DoctorRepo  extends JpaRepository<Doctor, Long>{

	Optional<Doctor> findByEmail(String email);
    List<Doctor> findBySpecialization(String specialization);

}
