package de.ffm.rka.rkareddit.rest.controller;


import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.domain.dto.LinkDTO;
import de.ffm.rka.rkareddit.security.UserDetailsServiceImpl;
import de.ffm.rka.rkareddit.service.UserService;
import de.ffm.rka.rkareddit.util.FileNIO;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.context.ServerPortInfoApplicationContextInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URI;
import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/profile")
public class ProfileMetaDataController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileMetaDataController.class);

    private final UserService userService;

    private final FileNIO fileNIO;

    private final ApplicationContext applicationContext;

    private final UserDetailsServiceImpl userDetailsService;

    public ProfileMetaDataController(UserService userService, FileNIO fileNIO, ApplicationContext applicationContext,
                                     UserDetailsServiceImpl userDetailsService) {
        this.userService = userService;
        this.fileNIO = fileNIO;
        this.applicationContext = applicationContext;
        this.userDetailsService = userDetailsService;
    }

    /**
     * @param userPrincipal for authentication
     * @return list of clicked Links
     */
    @GetMapping("/information/userClickedLinks")
    @ResponseBody
    public List<LinkDTO> userClickedLinksHistory(@RequestParam(name = "user") String requestedUser, @AuthenticationPrincipal UserDetails userPrincipal) {
        List<LinkDTO> userClickedLinksDTO = new ArrayList<>();
        String authenticatedUser = Optional.ofNullable(userPrincipal)
                .map(UserDetails::getUsername)
                .orElse("");
        if (requestedUser.equals(authenticatedUser) && !requestedUser.isEmpty()) {
            Set<Link> userClickedLinks = userService.findUserClickedLinks(requestedUser);
            userClickedLinks.forEach(link -> userClickedLinksDTO.add(LinkDTO.getMapLinkToDto(link)));
        }

        LOGGER.info("AuthenticatedUser {} looks at visited user {} links_history contains {} links",
                authenticatedUser,
                requestedUser, userClickedLinksDTO.size());
        return userClickedLinksDTO;
    }

    @GetMapping(value = "/information/content/user-pic")
    @ResponseBody
    public ResponseEntity<byte[]> imageAsByteArray(@AuthenticationPrincipal UserDetails userPrincipal, HttpServletRequest req) throws IOException {
        Optional<UserDetails> authenticatedUser = Optional.ofNullable(userPrincipal);
        String requestedUser = authenticatedUser.map(UserDetails::getUsername)
                .orElse(req.getParameter("user"));
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.maxAge(1, java.util.concurrent.TimeUnit.HOURS));
        byte[] media = new byte[0];
        Optional<byte[]> userPic = userService.getUserPic(requestedUser);
        if (userPic.isPresent()) {
            String picPath = fileNIO.readByteToPic(userPic.get(), requestedUser);
            InputStream in = applicationContext.getResource("classpath:".concat(picPath)).getInputStream();
            media = IOUtils.toByteArray(in);
        }
        return new ResponseEntity<>(media, headers, HttpStatus.OK);
    }

    @PostMapping("/information/content/user-pic")
    public boolean submit(@RequestParam(name = "pic") MultipartFile formDataWithFile, ModelMap modelMap,
                                       @AuthenticationPrincipal UserDetails userPrincipal, HttpServletRequest req) throws IOException {
        User user = (User) userDetailsService.loadUserByUsername(userPrincipal.getUsername());
        InputStream newPictureStream = formDataWithFile.getInputStream();
        byte[] pictureBuffer = new byte[newPictureStream.available()];
        newPictureStream.read(pictureBuffer);
        userService.saveNewUserPicture(pictureBuffer, user);
        LOGGER.info("saved new picture {} for user: {}", formDataWithFile.getOriginalFilename(), userPrincipal.getUsername());
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(req.getContextPath()+"/profile/information/content/user-pic"));
        return true;
    }
}
