package de.ffm.rka.rkareddit.service;

import de.ffm.rka.rkareddit.domain.dto.UserDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"password.time.expiration=10"})
public class UserServiceTest {

	@Test
	public void fullNameUser() {
		UserDTO user = UserDTO.builder()
						.firstName("rka")
						.secondName("blr")
						.build();
		assertEquals("rka blr", user.getFullName());
	}
	
	@Test
	public void fullNameUserPart() {
		UserDTO user = UserDTO.builder()
						.firstName("rka")
						.build();
		assertEquals("rka ", user.getFullName());
	}
}
