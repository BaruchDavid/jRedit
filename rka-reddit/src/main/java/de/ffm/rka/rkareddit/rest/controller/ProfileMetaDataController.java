package de.ffm.rka.rkareddit.rest.controller;



import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;


import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.service.UserService;
import de.ffm.rka.rkareddit.util.FileNIO;

@RestController
@RequestMapping("/profile")
public class ProfileMetaDataController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProfileMetaDataController.class);
	@Autowired
	private UserService userService;
	
	@Autowired
	private FileNIO fileNIO;
	
	@Autowired
	ApplicationContext applicationContext;

	/**
	 * shows user informations on profile-page on right side
	 */
	@GetMapping("/information/content")
	@ResponseBody
//	@Cacheable("user")
//	@CacheEvict(value="userInfo", allEntries=true)
	public List<String> getInformation(@AuthenticationPrincipal UserDetails userPrincipal, Model model) throws IOException {
		List<String> informations = new ArrayList<String>();
		User user = userService.getUserWithLinks(userPrincipal.getUsername());
		long userLinkSize = user.getUserLinks().size();
		long userCommentSize = userService.getUserWithComments(userPrincipal.getUsername()).getUserComments().size();
		informations.add(String.valueOf(userLinkSize));
		informations.add(String.valueOf(userCommentSize));
		informations.add(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).format(user.getCreationDate()));
		informations.add(userPrincipal.getUsername());
		LOGGER.debug("For user {} has been found {} links", userPrincipal.getUsername(),userLinkSize);
		LOGGER.debug("For user {} has been found {} comments", userPrincipal.getUsername(), userCommentSize);
		return informations;
	}

	@RequestMapping(value = "/information/content/user-pic", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<byte[]> getImageAsByteArray(@AuthenticationPrincipal UserDetails userPrincipal, HttpServletRequest req) throws IOException {

		HttpHeaders headers = new HttpHeaders();
		headers.setCacheControl(CacheControl.maxAge(1, java.util.concurrent.TimeUnit.HOURS));
		User user = userService.getUserWithLinks(userPrincipal.getUsername());
		String picPath = fileNIO.readByteToPic(user.getProfileFoto(), user.getEmail());
		InputStream in = applicationContext.getResource("classpath:".concat(picPath)).getInputStream();
		byte[] media = IOUtils.toByteArray(in);
		return new ResponseEntity<>(media, headers, HttpStatus.OK);
	}
}
