package de.ffm.rka.rkareddit.domain.dto;

import de.ffm.rka.rkareddit.domain.Tag;
import lombok.*;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TagDTO implements Serializable {

    private static ModelMapper modelMapper;
    private static final long serialVersionUID = 1748419915925154370L;
    private static final Logger LOGGER = LoggerFactory.getLogger(TagDTO.class);


    @NotEmpty(message = "title is required")
    @Size(min = 2, max = 10, message = "maximal 10 letter allowed")
    private String tagName;

    private Long tagNum;

    public static TagDTO mapTagToTagDTO(Tag tag) {
        return TagDTO.builder()
                .tagName(tag.getTagName())
                .tagNum(Optional.ofNullable(tag.getTagId()).orElse(0L))
                .build();

    }

    public static Tag mapTagDTOtoTag(TagDTO tagDTO) {
        return Tag.builder()
                .tagId(tagDTO.getTagNum())
                .tagName(tagDTO.getTagName())
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagDTO tagDTO = (TagDTO) o;
        return Objects.equals(tagName, tagDTO.tagName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tagName);
    }
}
