package com.excelr.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.excelr.model.Appointments;
import com.excelr.model.Doctor;
import com.excelr.model.Staff;
import com.excelr.model.User;
import com.excelr.repo.StaffRepository;
import com.excelr.repo.UserRepo;
import com.excelr.service.DoctorService;
import com.excelr.service.HmsService;
import com.excelr.service.UserMailService;

import jakarta.validation.Valid;

@RestController
@CrossOrigin("*")
public class HmsController {
	
	@Autowired
	public HmsService hmsService;
	
	@Autowired
	public StaffRepository staffRepository;
	
	@Autowired
	private UserMailService userMailService;
	
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private DoctorService doctorService;
	
	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestBody User user){
		try {
			String email=user.getEmail();
			if(userMailService.checkEmailExist(email)) 
				return ResponseEntity.status(401).body("Email already Logged in");
				userMailService.SendOTP(user);
				return ResponseEntity.ok("OTP sent to "+ user.getEmail());
			} catch (Exception e) {
			return ResponseEntity.status(500).body("Fail to Send Opt " +e.getMessage());
		}
	}
	
	@PostMapping("/verify")
	public ResponseEntity<String> verify(@RequestParam String email, @RequestParam String otp){
		try {
			boolean isVerified=userMailService.VerifyEmail(email, otp);
			if(isVerified) {
				return ResponseEntity.ok("OTP verified successfully! User is logged in.");
				
			}else {
				return ResponseEntity.status(500).body("Invalid OTP. Please try again.");
			}
			
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Verification Failed "+e.getMessage());
		}
	}
	
	@PostMapping("/login")
	public ResponseEntity<Map<String,String>> Userlogin(@RequestBody Map<String,String> logindata){
		String email=logindata.get("email");
		String password=logindata.get("password");
		Optional<User>user=userRepo.findByEmail(email);
		if(user.isEmpty() && user.get().getPassword().equals(password)) {
			Map<String,String> response=new HashMap<>();
			response.put("login", "success");
			return ResponseEntity.ok(response);
		}else {
			Map<String,String> response1=new HashMap<>();
			response1.put("login", "fail");
			return ResponseEntity.status(401).body(response1);
		}
	}

	
	@PostMapping("/doctor/register")
	public ResponseEntity<Doctor> register(@RequestBody Doctor doctor) {
		Doctor res=doctorService.registerDoctor(doctor);
		return ResponseEntity.ok(res);
	}
	
	@DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/availability")
    public ResponseEntity<Void> toggleAvailability(@PathVariable Long id) {
        doctorService.toggleAvailability(id);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Doctor> updateDoctor(
            @PathVariable Long id,
            @Valid @RequestBody Doctor doctor) {
        Doctor updatedDoctor = doctorService.updateDoctor(id, doctor);
        return ResponseEntity.ok(updatedDoctor);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Doctor> getDoctorById(@PathVariable Long id) {
        Doctor doctor = doctorService.getDoctorById(id);
        return ResponseEntity.ok(doctor);
    }

    @GetMapping
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        List<Doctor> doctors = doctorService.getAllDoctors();
        return ResponseEntity.ok(doctors);
    }
	
	@PostMapping("/staff/login")
	public ResponseEntity<Map<String, String>> login(@RequestBody  Map<String, String> loginData){
		String username = loginData.get("username");
		String password = loginData.get("password");
		Optional<Staff> staff =staffRepository.findByUsername(username);
		if(staff.isPresent() && staff.get().getPassword().equals(password)) {
			Map<String, String> response = new HashMap<>();
//			String token = jwtUtil.generateToken(username);
			response.put("login", "success");
//			response.put("token", token);
			response.put("role",staff.get().getRole());
			return ResponseEntity.ok(response);
		}else {
			Map<String, String> response1 = new HashMap<>();
			response1.put("login", "fail");
			return ResponseEntity.ok(response1);
		}
			
	}
	
	// admin dashboard functions...
	//patient handle
	
	@GetMapping("/staff/admin/appointments")
	public ResponseEntity<List<Appointments>> manageAppointments() {
		List<Appointments> appointment= hmsService.getAllAppointments();
		return ResponseEntity.ok(appointment);
		
	}
	
	@PostMapping("/staff/admin/newuser")
	 public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = hmsService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }
	@PutMapping("/staff/admin/user/{id}")
	public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        User user = hmsService.updatedUser(id, updatedUser);
        return ResponseEntity.ok(user);
    }
	
	@DeleteMapping("/staff/admin/deleteuser")
	public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        hmsService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully!");
    }
	@GetMapping("/staff/admin/users")
	public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = hmsService.getAllUsers();
        return ResponseEntity.ok(users);
    }
	
	//doctor handle
	
	@GetMapping("/staff/doctors")
	public String manageDoctors() {
		return "doctors will appear here.";
		
	}
	@GetMapping("/staff/patients")
	public String managePatients() {
		return "patients will appear here";
	}
}
