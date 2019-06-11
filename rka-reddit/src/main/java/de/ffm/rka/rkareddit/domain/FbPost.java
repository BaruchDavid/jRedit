package de.ffm.rka.rkareddit.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class FbPost {

	@Id
	String fbId;
	
	String  link;
	String  linkName;
	String  linkCaption; 
	String  linkDesc;
	public String getFbId() {
		return fbId;
	}
	public void setFbId(String fbId) {
		this.fbId = fbId;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getLinkName() {
		return linkName;
	}
	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}
	public String getLinkCaption() {
		return linkCaption;
	}
	public void setLinkCaption(String linkCaption) {
		this.linkCaption = linkCaption;
	}
	public String getLinkDesc() {
		return linkDesc;
	}
	public void setLinkDesc(String linkDesc) {
		this.linkDesc = linkDesc;
	}
	@Override
	public String toString() {
		return "FbPost [link=" + link + ", linkName=" + linkName + "]";
	}
	
	
	
	
	
}
