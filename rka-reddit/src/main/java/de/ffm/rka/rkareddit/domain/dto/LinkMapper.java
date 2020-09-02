package de.ffm.rka.rkareddit.domain.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import de.ffm.rka.rkareddit.domain.Link;

@Mapper
public interface LinkMapper {

	@Mappings({
      @Mapping(source="title", target="linkTitle"),
      @Mapping(source="url", target="url")
    })
	LinkDTO linkToLinkDTO(Link link);
}
