package de.ffm.rka.rkareddit.rest.controller;


import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.domain.dto.LinkDTO;
import de.ffm.rka.rkareddit.domain.dto.PictureDTO;
import de.ffm.rka.rkareddit.domain.validator.PictureValidator;
import de.ffm.rka.rkareddit.security.UserDetailsServiceImpl;
import de.ffm.rka.rkareddit.service.UserService;
import de.ffm.rka.rkareddit.util.CacheController;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/profile")
public class ProfileMetaDataController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileMetaDataController.class);
    private final UserService userService;
    private final UserDetailsServiceImpl userDetailsService;
    private final CacheController cacheController;

    public ProfileMetaDataController(UserService userService,
                                     UserDetailsServiceImpl userDetailsService,
                                     CacheController cacheController) {
        this.userService = userService;
        this.userDetailsService = userDetailsService;
        this.cacheController = cacheController;
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
    public List<LinkDTO> userClickedLinksHistory(@RequestParam(name = "user") String requestedUser,
                                                 @AuthenticationPrincipal UserDetails userPrincipal) {
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
    public ResponseEntity<byte[]> imageAsByteArray(@AuthenticationPrincipal UserDetails userPrincipal, HttpServletRequest req) {
        HttpHeaders headers = new HttpHeaders();
        byte[] media = new byte[0];
        final HttpStatus responseStatus;
        String requestedUser = Optional.ofNullable(req.getParameter("user"))
                .orElse(StringUtils.EMPTY);
        if (!requestedUser.isEmpty()) {
            User user = userService.getUser(requestedUser);
            media = userService.getUserPic(requestedUser)
                                .orElse(new byte[0]);
            LOGGER.info("GET PICTURE-SIZE {} FOR USER {}", media.length, requestedUser);
            headers = cacheController.setCacheHeader(user.getFotoCreationDate());
            responseStatus = HttpStatus.OK;
        } else {
            responseStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(media, headers, responseStatus);
    }

    @PostMapping(value = "/information/content/user-pic", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> submit(@Valid @ModelAttribute("pic") PictureDTO pictureDTO,
                                         BindingResult result, Model model,
                                         @AuthenticationPrincipal UserDetails userPrincipal) {
        String errors = "";
        try {
            if (result.hasErrors()) {
                errors = result.getAllErrors().stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .collect(Collectors.joining(";"));
            }
            if (errors.isEmpty()) {
                User user = (User) userDetailsService.loadUserByUsername(userPrincipal.getUsername());
                final String requestedUser = userPrincipal.getUsername();
                try (final InputStream imageInputStream = userService.resizeUserPic(pictureDTO.getFormDataWithFile()
                        .getInputStream(), pictureDTO.getPictureExtension())) {
                    if (userService.saveNewUserPicture(imageInputStream, user)) {
                        LOGGER.info("saved new picture {} for user: {}", pictureDTO.getFormDataWithFile().getOriginalFilename(),
                                requestedUser);
                        HttpHeaders headers = cacheController.setCacheHeader(user.getFotoCreationDate());
                        return new ResponseEntity<>("ok", headers, HttpStatus.CREATED);
                    } else {
                        LOGGER.warn("error on saving new picture {} for user: {}", pictureDTO.getFormDataWithFile()
                                .getOriginalFilename(), requestedUser);
                        return new ResponseEntity<>("fail, please check pic-requirements", new HttpHeaders(),
                                HttpStatus.BAD_REQUEST);
                    }
                }
            } else {
                LOGGER.warn("NEW PICTURE VALIDATOR-ERRORS {}", errors);
                return new ResponseEntity<>(errors, new HttpHeaders(), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            LOGGER.error("EXCEPTION MESSAGE {}", ex.getMessage(), ex);
            return new ResponseEntity<>("Error occurred, please try again", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
