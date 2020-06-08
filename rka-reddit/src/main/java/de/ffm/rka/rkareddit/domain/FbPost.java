package de.ffm.rka.rkareddit.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

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
