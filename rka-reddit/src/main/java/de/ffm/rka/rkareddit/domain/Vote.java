package de.ffm.rka.rkareddit.domain;


import de.ffm.rka.rkareddit.domain.audit.Auditable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@ToString(exclude = "link")
@Getter @Setter
@Entity
public class Vote extends Auditable implements Serializable {
	
	private static final long serialVersionUID = 4242833929963027951L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long voteId;
	
	@NotNull
	private short direction;
	
	@NotNull
	@ManyToOne(fetch = FetchType.EAGER)
	private Link link = new Link();

	public Vote(@NotNull Link link,@NotNull short direction) {
		this.direction = direction;
		this.link = link;
	}

	public Vote() {
		this.direction = 0;
	}
		
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vote))
            return false;
        Vote other = (Vote) o;
 
        return voteId != null &&
        		voteId.equals(other.getVoteId());
    }
	 
    @Override
    public int hashCode() {
        return 31;
    }
}
