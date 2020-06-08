package de.ffm.rka.rkareddit.service;

import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.domain.dto.UserDTO;
import de.ffm.rka.rkareddit.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.Assert.assertTrue;



/**
 * Testclass for Servicelayer
 * @author kaproma
 *
 */
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest
public class EmailServiceTest {

	@Autowired
    private MailService mailService;
	
	@Autowired
    private UserRepository userRepository;
	
	@Autowired
	private ModelMapper modelMapper;

	/**
	 * test for test_env for user on service-layer
	 */
	@Test
	public void linkSizeForUserOne() {
		Optional<User> user = userRepository.findByEmail("romakapt@gmx.de");
		UserDTO userDto = modelMapper.map(user.get(), UserDTO.class); 
		String context = ReflectionTestUtils.invokeMethod(mailService, "createEmailContext", "localhost", userDto, "mail/new_email_activation");
		assertTrue("should contains dear baruch-david rka", context.contains("Dear baruc-david rka"));
	}
}
