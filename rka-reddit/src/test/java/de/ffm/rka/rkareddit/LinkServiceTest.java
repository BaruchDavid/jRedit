package de.ffm.rka.rkareddit;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.service.LinkService;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
public class LinkServiceTest {

	@Autowired
    private LinkService linkService ;
	
	@Test
	public void linkSizeForUserOne() {
		User user = new User();
		user.setUserId(1l);
		assertEquals(5l, linkService.findAllByUser(user));
	}

}
