package de.ffm.rka.rkareddit.controller.rest;



import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.security.UserDetailsServiceImpl;
import de.ffm.rka.rkareddit.service.CommentService;
import de.ffm.rka.rkareddit.service.LinkService;
import de.ffm.rka.rkareddit.service.UserService;
import de.ffm.rka.rkareddit.util.FileNIO;

@RestController
@RequestMapping("/profile")
public class ProfileMetaDataController {

	@Autowired
	private CommentService commentService;
	
	@Autowired
	private LinkService linkService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserDetailsServiceImpl userDetailsServiceImpl;
	
	@Autowired
	private FileNIO fileNIO;
	
	/**
	 * @param request
	 * @return
	 * @throws IOException 
	 */
	@GetMapping("/information/content")
	@ResponseBody
	@Cacheable("userInfo")
//	@CacheEvict(value="userInfo", allEntries=true)
	public List<String> getInformation(@RequestBody String username) throws IOException {
		User user = (User) userDetailsServiceImpl.loadUserByUsername(username);
		List<String> informations = new ArrayList<String>();		
		long userLinkSize = userService.getLinkSizeByUser(user.getUserId()).getUserLinks().size();
		long userCommentSize = userService.getCommentSizeByUser(user.getUserId()).getUserComments().size();
		String picPath = fileNIO.readByteToPic(user.getProfileFoto(), user.getEmail());
		informations.add(String.valueOf(userLinkSize));
		informations.add(String.valueOf(userCommentSize));
		informations.add(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).format(user.getCreationDate()));
		informations.add(picPath);
		return informations;
	}
}
