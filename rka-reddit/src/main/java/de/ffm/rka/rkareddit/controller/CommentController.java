package de.ffm.rka.rkareddit.controller;

import de.ffm.rka.rkareddit.domain.dto.CommentDTO;
import de.ffm.rka.rkareddit.domain.validator.comment.CommentValidationgroup;
import de.ffm.rka.rkareddit.domain.validator.link.LinkValidationGroup;
import de.ffm.rka.rkareddit.exception.ServiceException;
import de.ffm.rka.rkareddit.security.UserDetailsServiceImpl;
import de.ffm.rka.rkareddit.service.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class CommentController {

    private final CommentService commentService;
    private static final Logger LOGGER = LoggerFactory.getLogger(CommentController.class);
    private static final String SUCCESS = "success";
    private static final String ERROR_MESSAGE = "error_message";


    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping(value = "/comments/comment")
    public String newComment(@Validated(value = {CommentValidationgroup.ValidationCommentSize.class,
                                                LinkValidationGroup.signaturSize.class}) CommentDTO comment,
                             BindingResult bindingResult, RedirectAttributes attributes,
                             @AuthenticationPrincipal UserDetails userDetails,
                             HttpServletRequest req, HttpServletResponse res) throws ServiceException {
        userDetails = Optional.ofNullable(userDetails)
                .orElseThrow(() ->
                     UserDetailsServiceImpl.throwUnauthenticatedUserException(req.getRemoteHost() +
                                                                            req.getRemotePort() + req.getRequestURI()));
        if(bindingResult.hasErrors()) {
            final String currentErrors = bindingResult.getAllErrors().stream()
                    .map(error -> {
                        LOGGER.error("VALIDATION ON COMMENT {} : CODES {} MESSAGE: {}",
                                comment.getLSig(), error.getCodes(), error.getDefaultMessage());
                        return error.getDefaultMessage();
                    })
                    .collect(Collectors.joining());
            if(currentErrors.contains("not valid comment")){
                throw new IllegalArgumentException("Invalid comment");
            }

            res.setStatus(HttpStatus.PERMANENT_REDIRECT.value());
            attributes.addFlashAttribute(ERROR_MESSAGE, currentErrors);
        } else {
            commentService.saveNewComment(userDetails.getUsername(), comment);
            attributes.addFlashAttribute(SUCCESS, true);
        }

        return "redirect:/links/link/".concat(comment.getLSig());
    }
}
