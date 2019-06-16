package de.ffm.rka.rkareddit.config;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.service.UserService;
import de.ffm.rka.rkareddit.util.BeanUtil;
import de.ffm.rka.rkareddit.util.FileNIO;

public class DatabaseLoader implements CommandLineRunner{

	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseLoader.class);
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private FileNIO fileNIO;
	
	@Override
	public void run(String... args) throws Exception {
		
		Optional<User> user = userService.findUserById("romakapt@gmx.de");
		User userObj = user.get();
		userObj.setProfileFoto(fileNIO.readPictureToByte("static/images/profile_small.png"));	
		userService.save(user.get());
	}

}
