package com.excelr.service;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.excelr.model.User;
import com.excelr.repo.UserRepo;

@Service
public class UserMailService {

	@Autowired
	private MailService mainMailService;
	@Autowired
	private UserRepo userRepo;
	public UserMailService(MailService mainMailService, UserRepo userRepo) {
		super();
		this.mainMailService = mainMailService;
		this.userRepo = userRepo;
	}
	
	public String generateOTP() {
		Random random = new Random();
		return String.format("%06d",random.nextInt(999999));
	}
	
	public void SendOTP(User user) {
		String otp=generateOTP();
		user.setOtp(otp);
		userRepo.save(user);
		mainMailService.SendOTP(user.getEmail(), otp);
	}
	
	public boolean VerifyEmail(String email,String otp) {
		User user=userRepo.findByEmail(email)
				.orElseThrow(()-> new RuntimeException("User Not Found....."));
		if(user.getOtp().equals(otp)) {
			user.setVerified(true);
			user.setOtp(null);
			userRepo.save(user);
			return true;
		} return false;
	}
	
	public boolean checkEmailExist(String email) {
    	return userRepo.findByEmail(email).isPresent();
    }
}
