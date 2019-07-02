package de.ffm.rka.rkareddit.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class FbPost {

	@Id
	String fbId;
	
	String  link;
	String  linkName;
	String  linkCaption; 
	String  linkDesc;

}
