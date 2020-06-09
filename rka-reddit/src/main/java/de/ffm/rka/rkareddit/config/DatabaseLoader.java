package de.ffm.rka.rkareddit.config;

import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.service.UserService;
import de.ffm.rka.rkareddit.util.FileNIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import java.util.List;
import java.util.Optional;

public class DatabaseLoader implements CommandLineRunner{

	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseLoader.class);
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private FileNIO fileNIO;
	
	@Override
	public void run(String... args) throws Exception {
		
		List<User> users = userService.findAll();
		Optional<byte[]> pic = fileNIO.readPictureToByte("static/images/profile_small.png");
		users.forEach(user ->
			{
				pic.ifPresent(picture -> user.setProfileFoto(pic.get()));
				if(user.getUserId()==3) {
					user.setEnabled(false);
				}
				user.setActivationCode("activation");
				userService.save(user);
				LOGGER.info("DEFAULT USER CONFIG {}", user);
			});
	}

}
