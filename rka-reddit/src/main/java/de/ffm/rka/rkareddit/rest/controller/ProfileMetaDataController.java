package de.ffm.rka.rkareddit.rest.controller;


import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.service.UserService;
import de.ffm.rka.rkareddit.util.FileNIO;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/profile")
public class ProfileMetaDataController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProfileMetaDataController.class);

	private final UserService userService;

	private final FileNIO fileNIO;

	private final ApplicationContext applicationContext;

	public ProfileMetaDataController(UserService userService, FileNIO fileNIO, ApplicationContext applicationContext) {
		this.userService = userService;
		this.fileNIO = fileNIO;
		this.applicationContext = applicationContext;
	}

	/**
	 * shows user information on profile-page on right side
	 */
	@GetMapping("/information/content")
	@ResponseBody
//	@Cacheable("user")
//	@CacheEvict(value="userInfo", allEntries=true)
	public List<String> informationContent(@AuthenticationPrincipal UserDetails userPrincipal,
			@RequestParam(required = false) String user) {
		List<String> information = new ArrayList<>();
		
		Optional<UserDetails> usrDetail = Optional.ofNullable(userPrincipal);
		String requestedUser = usrDetail.isPresent() && user == null? usrDetail.get().getUsername():user;
		User currentUser = userService.getUserWithLinks(requestedUser);
		long userLinkSize = currentUser.getUserLinks().size();
		long userCommentSize = userService.getUserWithComments(requestedUser).getUserComments().size();
		information.add(String.valueOf(userLinkSize));
		information.add(String.valueOf(userCommentSize));
		information.add(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).format(currentUser.getCreationDate()));
		information.add(requestedUser);
		LOGGER.debug("For user {} has been found {} links", requestedUser,userLinkSize);
		LOGGER.debug("For user {} has been found {} comments", requestedUser, userCommentSize);
		return information;
	}

	@GetMapping(value = "/information/content/user-pic")
	@ResponseBody
	public ResponseEntity<byte[]> imageAsByteArray(@AuthenticationPrincipal UserDetails userPrincipal, HttpServletRequest req) throws IOException {
		Optional<UserDetails> usrDetail = Optional.ofNullable(userPrincipal);
		String requestedUser = usrDetail.isPresent()? usrDetail.get().getUsername():req.getParameter("user");
		HttpHeaders headers = new HttpHeaders();
		headers.setCacheControl(CacheControl.maxAge(1, java.util.concurrent.TimeUnit.HOURS));
		User user = userService.getUserWithLinks(requestedUser);
		String picPath = fileNIO.readByteToPic(user.getProfileFoto(), user.getEmail());
		InputStream in = applicationContext.getResource("classpath:".concat(picPath)).getInputStream();
		byte[] media = IOUtils.toByteArray(in);
		return new ResponseEntity<>(media, headers, HttpStatus.OK);
	}
}
