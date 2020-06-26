package de.ffm.rka.rkareddit.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.ffm.rka.rkareddit.domain.Link;
import de.ffm.rka.rkareddit.domain.User;
import de.ffm.rka.rkareddit.domain.audit.Auditable;
import de.ffm.rka.rkareddit.util.BeanUtil;
import lombok.*;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZoneId;

import static java.util.Date.from;


@ToString(exclude = "user")
@Getter @Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CommentDTO extends Auditable implements Serializable{
	
	private static final long serialVersionUID = -5839947949942907414L;
	private static final ZoneId ZONE_ID = ZoneId.systemDefault();
	private Long commentId;
	
	@NotEmpty(message = "comment text must be present")
	private String commentText;

	private User user;

	/**
	 * signatur for suitable link
	 * used for frontend
	 */
	private String lSig;

	private LinkDTO linkDTO;

	@Autowired
	private transient PrettyTime prettyTime;

	
	public String getElapsedTime() {
		prettyTime = BeanUtil.getBeanFromContext(PrettyTime.class);
		return prettyTime.format(from(super.getCreationDate().atZone(ZONE_ID).toInstant()));
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
        return 31;
    }

}
