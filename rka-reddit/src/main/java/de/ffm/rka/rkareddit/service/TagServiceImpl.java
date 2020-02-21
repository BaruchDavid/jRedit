package de.ffm.rka.rkareddit.service;



import java.util.List;

import javax.validation.Valid;

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

	public void saveTag(Tag tag) {
		tagRepository.save(tag);
	}
}
