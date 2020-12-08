package de.ffm.rka.rkareddit.rest.controller;


import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.domain.dto.LinkDTO;
import de.ffm.rka.rkareddit.domain.dto.PictureDTO;
import de.ffm.rka.rkareddit.domain.validator.PictureValidator;
import de.ffm.rka.rkareddit.security.UserDetailsServiceImpl;
import de.ffm.rka.rkareddit.service.UserService;
import de.ffm.rka.rkareddit.util.FileNIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/profile")
public class ProfileMetaDataController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileMetaDataController.class);
    private static final long MAX_CACHE_DURATION = 25;
    private static final int PAST_MINUTES_OF_CACHE_EXPIRATION = 3;
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

    @InitBinder
    public void initBinder(final DataBinder binder) {
        binder.setValidator(new PictureValidator());
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
        byte[] media = new byte[0];
        User user = userService.getUser(requestedUser);
        Optional<byte[]> userPic = userService.getUserPic(requestedUser);
        if (userPic.isPresent()) {
            media = userPic.get();
            headers = cacheControl(user.getFotoCreationDate());
            LOGGER.info("GET PICTURE-SIZE {} FOR USER {}", userPic.get().length, requestedUser);
        }
        return new ResponseEntity<>(media, headers, HttpStatus.OK);
    }

    @PostMapping(value = "/information/content/user-pic", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> submit(@Valid @ModelAttribute("pic") PictureDTO pictureDTO,
                                         BindingResult result, Model model,
                                         @AuthenticationPrincipal UserDetails userPrincipal) throws IOException {
        String errors="";
        if(result.hasErrors()){
            errors = result.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(";"));
        }
        if(errors.isEmpty()){
            User user = (User) userDetailsService.loadUserByUsername(userPrincipal.getUsername());
            final String requestedUser = userPrincipal.getUsername();
            if (userService.saveNewUserPicture(pictureDTO.getFormDataWithFile().getInputStream(), user)) {
                LOGGER.info("saved new picture {} for user: {}", pictureDTO.getFormDataWithFile().getOriginalFilename(), requestedUser);
                HttpHeaders headers = cacheControl(user.getFotoCreationDate());
                return new ResponseEntity<>("ok", headers, HttpStatus.CREATED);
            } else {
                LOGGER.warn("error on saving new picture {} for user: {}", pictureDTO.getFormDataWithFile()
                                                                                    .getOriginalFilename(), requestedUser);
                return new ResponseEntity<>("fail, please check pic-requirements", new HttpHeaders(), HttpStatus.BAD_REQUEST);
            }
        } else {
            LOGGER.warn("NEW PICTURE VALIDATOR-ERRORS {}", errors);
            return new ResponseEntity<>(errors, new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }
    }

    private HttpHeaders cacheControl(LocalDateTime date) {
        HttpHeaders headers = new HttpHeaders();
        if (checkForExpiredModification(date)) {
            headers.setCacheControl(CacheControl.maxAge(MAX_CACHE_DURATION, TimeUnit.DAYS));
        } else {
            headers.setCacheControl(CacheControl.noCache());
        }
        return headers;
    }

    /**
     * modification date is expired, when it is wished timed ago
     *
     * @param modificationDate of resource
     * @return true when modificationDate is wished time ago
     */
    private boolean checkForExpiredModification(LocalDateTime modificationDate) {
        return LocalDateTime.now().minusMinutes(PAST_MINUTES_OF_CACHE_EXPIRATION).isAfter(modificationDate);
    }
}
