package de.ffm.rka.rkareddit.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import de.ffm.rka.rkareddit.domain.Tag;
import de.ffm.rka.rkareddit.repository.TagRepository;

@Service
public class TagServiceImpl {
	
	@Autowired
	private TagRepository tagRepository;

	public List<String> findSuitableTags(String tagName){
		return tagRepository.findTagByName(tagName);
	}

	public long saveTag(Tag tag) {
		Tag newTag = tagRepository.save(tag);
		return newTag.getTagId();
	}

	public Optional<Tag> findTagOnName(String tag) {
		// TODO Auto-generated method stub
		return tagRepository.findByName(tag);
	}
	
	public void deleteTagWithoutRelation(Tag tag) {
		tagRepository.delete(tag);
	}
	
	public Optional<Tag> selectTagWithLinks(long tagId){
		return Optional.ofNullable(tagRepository.selectTagWithLinks(tagId));
	}
}
