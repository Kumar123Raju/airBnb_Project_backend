package com.rajukumar.project.airBnbApp;

import com.rajukumar.project.airBnbApp.service.EmailSenderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AirBnbAppApplicationTests {

	@Autowired
	private EmailSenderService emailSenderService;

	@Test
	void contextLoads() {
		emailSenderService.sendEmail(
				"rjrajujames123@gmail.com",
				"This is the testing Email",
				"Regarding your Airbnb Project"
		);
	}

	@Test
	void sendEmailMultiple() {
		String emails[]={"fehon57424@baxima.com","rjrajujames123@gmail.com","2021pgcaca021@nitjsr.ac.in"};
		emailSenderService.sendEmail(
							emails,
				"This is the testing Email",
				"Regarding your Airbnb Project"
		);
	}



}
