package de.ffm.rka.rkareddit.controller.rest;



import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.service.UserService;
import de.ffm.rka.rkareddit.util.FileNIO;

@RestController
@RequestMapping("/profile")
public class ProfileMetaDataController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private FileNIO fileNIO;
	
	/**
	 * shows user informations on profile-page on right side
	 */
	@GetMapping("/information/content")
	@ResponseBody
//	@Cacheable("user")
//	@CacheEvict(value="userInfo", allEntries=true)
	public List<String> getInformation(@AuthenticationPrincipal UserDetails userPrincipal, Model model) throws IOException {
		List<String> informations = new ArrayList<String>();
		User user = userService.getLinkSizeByUser(userPrincipal.getUsername());
		long userLinkSize = user.getUserLinks().size();
		long userCommentSize = userService.getCommentSizeByUser(userPrincipal.getUsername()).getUserComments().size();
		String picPath = fileNIO.readByteToPic(user.getProfileFoto(), user.getEmail());
		informations.add(String.valueOf(userLinkSize));
		informations.add(String.valueOf(userCommentSize));
		informations.add(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).format(user.getCreationDate()));
		informations.add(picPath);
		return informations;
	}
}
