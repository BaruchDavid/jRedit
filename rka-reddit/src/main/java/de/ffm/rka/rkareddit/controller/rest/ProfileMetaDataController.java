package de.ffm.rka.rkareddit.controller.rest;



import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.security.UserDetailsServiceImpl;
import de.ffm.rka.rkareddit.service.CommentService;
import de.ffm.rka.rkareddit.service.LinkService;
import de.ffm.rka.rkareddit.service.UserService;
import de.ffm.rka.rkareddit.util.FileNIO;

@RestController
@RequestMapping("/profile")
@SessionAttributes("user")
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
	public List<String> getInformation(Model model) throws IOException {
		Optional<User> user = Optional.ofNullable((User) model.asMap().get("user"));
		User activeUser;
		org.springframework.security.core.userdetails.User userDetails;
		if(user.isPresent()) {
			activeUser = user.get();
		}else {
			activeUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		}
		List<String> informations = new ArrayList<String>();		
		long userLinkSize = userService.getLinkSizeByUser(activeUser.getUserId()).getUserLinks().size();
		long userCommentSize = userService.getCommentSizeByUser(activeUser.getUserId()).getUserComments().size();
		String picPath = fileNIO.readByteToPic(activeUser.getProfileFoto(), activeUser.getEmail());
		informations.add(String.valueOf(userLinkSize));
		informations.add(String.valueOf(userCommentSize));
		informations.add(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).format(activeUser.getCreationDate()));
		informations.add(picPath);
		return informations;
	}
}
