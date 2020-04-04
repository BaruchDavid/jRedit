package de.ffm.rka.rkareddit.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import de.ffm.rka.rkareddit.domain.User;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

	@Test
	public void fullNameUser() {
		User user = User.builder()
						.firstName("rka")
						.secondName("blr")
						.build();
		assertEquals("rka blr", user.getFullName());
	}
	
	@Test
	public void fullNameUserPart() {
		User user = User.builder()
						.firstName("rka")
						.secondName("")
						.build();
		assertEquals("rka ", user.getFullName());
	}
}
