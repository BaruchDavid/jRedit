package de.ffm.rka.rkareddit.domain.dto;

import de.ffm.rka.rkareddit.domain.Comment;
import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.domain.audit.Auditable;
import de.ffm.rka.rkareddit.domain.validator.comment.CommentValidationgroup;
import de.ffm.rka.rkareddit.domain.validator.link.LinkValidationGroup;
import de.ffm.rka.rkareddit.util.BeanUtil;
import lombok.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZoneId;
import java.util.Objects;

import static java.util.Date.from;


@ToString(exclude = "user")
@Getter @Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CommentDTO extends Auditable implements Serializable{
	
	private static ModelMapper modelMapper; 
	private static final long serialVersionUID = -5839947949942907414L;
	private static final ZoneId ZONE_ID = ZoneId.systemDefault();
	private Long commentId;
	
	static {
		modelMapper	= new ModelMapper();
		modelMapper.getConfiguration().setAmbiguityIgnored(true);
		modelMapper.addMappings(new PropertyMap<Comment, CommentDTO>() {
		    @Override
		    protected void configure() {
		        skip(destination.getLinkDTO());
		    }
		});
	}
	
	
	@NotEmpty(message = "comment text must be present",
			groups = CommentValidationgroup.ValidationCommentSize.class)
	@Size(message = "maximal 600 letters are allowed", max=600, groups = CommentValidationgroup.ValidationCommentSize.class)
	private String commentText;

	private User user;

	/**
	 * signature for suitable link
	 * used for frontend
	 */
	@NotEmpty(message = "not valid comment", groups = LinkValidationGroup.signaturSize.class)
	private String lSig;

	private LinkDTO linkDTO;

	@Autowired
	private transient PrettyTime prettyTime;

	
	public String getElapsedTime() {
		prettyTime = BeanUtil.getBeanFromContext(PrettyTime.class);
		return prettyTime.format(from(super.getCreationDate().atZone(ZONE_ID).toInstant()));
	}
	
	public static Comment getMapDtoToComment(CommentDTO commentDto){
		return modelMapper.map(commentDto, Comment.class);
	}
	
	public static CommentDTO getCommentToCommentDto(Comment comment) {
		final CommentDTO commentDTO = modelMapper.map(comment, CommentDTO.class);
		commentDTO.setLinkDTO(LinkDTO.mapLinkToDto(comment.getLink()));
		return commentDTO;
	}
	
	@Override
    public boolean equals(Object o) {
		boolean result;
		if (this == o) {
			result = true;
		} else if (!(o instanceof CommentDTO)) {
			result = false;
		} else {
			CommentDTO other = (CommentDTO) o;
			result = commentId != null &&
					commentId.equals(other.getCommentId());
		}
		return result;
	}
	 
    @Override
    public int hashCode() {
        return Objects.hash(commentId, commentText);
    }

}
