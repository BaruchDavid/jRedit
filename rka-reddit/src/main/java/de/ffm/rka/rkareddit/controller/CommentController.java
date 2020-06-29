package de.ffm.rka.rkareddit.controller;

import de.ffm.rka.rkareddit.domain.dto.CommentDTO;
import de.ffm.rka.rkareddit.domain.dto.LinkDTO;
import de.ffm.rka.rkareddit.exception.ServiceException;
import de.ffm.rka.rkareddit.service.CommentService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Controller
public class CommentController {

    private final CommentService commentService;
    private static final Logger LOGGER = LoggerFactory.getLogger(CommentService.class);
    private static final String SUCCESS = "success";
    private static final String ERROR = "error";
    private final ModelMapper modelMapper;

    public CommentController(CommentService commentService, ModelMapper modelMapper) {
        this.commentService = commentService;
        this.modelMapper = modelMapper;
    }

    @PostMapping(value = "/comments/comment")
    public String newComment(CommentDTO comment, BindingResult bindingResult,
                             RedirectAttributes attributes, @AuthenticationPrincipal UserDetails userDetails,
                             HttpServletRequest req, HttpServletResponse res) throws ServiceException {
        Optional.ofNullable(userDetails)
                .orElseThrow(() -> new UsernameNotFoundException("no user"));
        if(bindingResult.hasErrors()) {
            long comId = comment.getCommentId();
            bindingResult.getAllErrors().forEach(error -> LOGGER.error("VALIDATION ON COMMENT {} : CODES {} MESSAGE: {}",
                   comId , error.getCodes(), error.getDefaultMessage()));
            res.setStatus(HttpStatus.BAD_REQUEST.value());
            attributes.addFlashAttribute(ERROR, true);
        } else {
            commentService.saveNewComment(userDetails.getUsername(), comment);
            attributes.addFlashAttribute(SUCCESS, true);
        }
        return "redirect:/links/link/".concat(comment.getLSig());
    }
}