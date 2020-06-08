package de.ffm.rka.rkareddit.domain;

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


@Entity
@Getter @Setter
@ToString(exclude = {"link", "user"})
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Comment extends Auditable implements Serializable{
	
	private static final long serialVersionUID = -5839947949942907414L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long commentId;
	
	@NotEmpty(message = "comment text must be present")
	private String commentText;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private Link link;

	@Autowired
	private transient PrettyTime prettyTime;

	
	private static final ZoneId ZONE_ID = ZoneId.systemDefault();
	
	public String getElapsedTime() {
		prettyTime = BeanUtil.getBeanFromContext(PrettyTime.class);
		return prettyTime.format(from(super.getCreationDate().atZone(ZONE_ID).toInstant()));
	}
	
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Comment))
            return false;
        Comment other = (Comment) o;
 
        return commentId != null &&
        		commentId.equals(other.getCommentId());
    }
	 
    @Override
    public int hashCode() {
        return 31;
    }
}
