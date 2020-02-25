package de.ffm.rka.rkareddit.vo;

public class TagVO {

	String name;
	long tagId;
	
	public TagVO(String name, long id) {
		this.name = name;
		this.tagId = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getTagId() {
		return tagId;
	}

	public void setTagId(long tagId) {
		this.tagId = tagId;
	}
	
}
