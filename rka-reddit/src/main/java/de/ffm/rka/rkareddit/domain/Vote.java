package de.ffm.rka.rkareddit.domain;


import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import de.ffm.rka.rkareddit.domain.audit.Auditable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString(exclude = "link")
@Getter @Setter
@Entity
public class Vote extends Auditable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long voteId;
	
	@NotNull
	private short direction;
	
	@NotNull
	@ManyToOne(fetch = FetchType.EAGER)
	private Link link;

	public Vote(@NotNull Link link,@NotNull short direction) {
		this.direction = direction;
		this.link = link;
	}

	public Vote() {
		super();
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
