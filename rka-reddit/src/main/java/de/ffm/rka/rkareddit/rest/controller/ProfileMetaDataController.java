package de.ffm.rka.rkareddit.rest.controller;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.dto.LinkDTO;
import de.ffm.rka.rkareddit.service.UserService;
import de.ffm.rka.rkareddit.util.FileNIO;

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
	 * @param userPrincipal for authentication
	 * @return list of clicked Links
	 */
	@GetMapping("/information/userClickedLinks")
	@ResponseBody
	public List<LinkDTO> userClickedLinksHistory(@RequestParam(name="user") String user, @AuthenticationPrincipal UserDetails userPrincipal) {
		List<LinkDTO> userClickedLinksDTO = new ArrayList<>();
		String requestedUser= Optional.ofNullable(userPrincipal)
				.map(UserDetails::getUsername)
				.orElse("");
		if(user.equals(requestedUser) && !requestedUser.isEmpty()){
			Set<Link> userClickedLinks = userService.findUserClickedLinks(requestedUser);		
			userClickedLinks.forEach(link -> {
				userClickedLinksDTO.add(LinkDTO.getMapLinkToDto(link));
			});
		}
		LOGGER.info("User {} clicked links history contains {} links", userPrincipal.getUsername(), userClickedLinksDTO.size() );
		return userClickedLinksDTO;
	}

	@GetMapping(value = "/information/content/user-pic")
	@ResponseBody
	public ResponseEntity<byte[]> imageAsByteArray(@AuthenticationPrincipal UserDetails userPrincipal, HttpServletRequest req) throws IOException {
		Optional<UserDetails> usrDetail = Optional.ofNullable(userPrincipal);
		String requestedUser = usrDetail.isPresent()? usrDetail.get().getUsername():req.getParameter("user");
		HttpHeaders headers = new HttpHeaders();
		headers.setCacheControl(CacheControl.maxAge(1, java.util.concurrent.TimeUnit.HOURS));
		byte[] media = new byte[0];
		Optional<byte[]> userPic = userService.getUserPic(requestedUser);
		if(userPic.isPresent()) {
			String picPath = fileNIO.readByteToPic(userPic.get(), requestedUser);
			InputStream in = applicationContext.getResource("classpath:".concat(picPath)).getInputStream();
			media = IOUtils.toByteArray(in);
		}		
		return new ResponseEntity<>(media, headers, HttpStatus.OK);
	}
}
