package de.ffm.rka.rkareddit.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.domain.dto.UserDTO;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
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
