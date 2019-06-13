package de.ffm.rka.rkareddit.controller.rest;



import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.security.UserDetailsServiceImpl;
import de.ffm.rka.rkareddit.service.CommentService;
import de.ffm.rka.rkareddit.service.LinkService;

@RestController
@RequestMapping("/profile")
public class ProfileMetaDataController {

	@Autowired
	private CommentService commentService;
	
	@Autowired
	private LinkService linkService;
	
	@Autowired
	private UserDetailsServiceImpl userDetailsServiceImpl;
	
	/**
	 * @param request
	 * @return
	 */
	@GetMapping("/information/content")
	@ResponseBody
	public List<String> getInformation(Model model) {
		//User user = (User) userDetailsServiceImpl.loadUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		User user = (User) userDetailsServiceImpl.loadUserByUsername("romakapt@gmx.de");
		List<String> informations = new ArrayList<String>();
		informations.add(String.valueOf(linkService.findAllByUser(user)));
		informations.add(String.valueOf(commentService.countAllByUser(user)));
		informations.add(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).format(user.getCreationDate()));
		return informations;
	}
}
