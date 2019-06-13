package de.ffm.rka.rkareddit;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import de.ffm.rka.rkareddit.service.UserService;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

	@Autowired
    private UserService userService;
	
	@Test
	public void linkSizeForUserOne() {
		//assertEquals(5l, userService.getLinkSizeByUser(1l));
	}

}
