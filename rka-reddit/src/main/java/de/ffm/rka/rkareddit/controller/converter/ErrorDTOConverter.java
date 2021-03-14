package de.ffm.rka.rkareddit.controller.converter;

import de.ffm.rka.rkareddit.domain.dto.ErrorDTO;
import de.ffm.rka.rkareddit.util.JsonMapper;
import org.springframework.core.convert.converter.Converter;


import java.util.Optional;

public class ErrorDTOConverter implements Converter<String, ErrorDTO> {
    @Override
    public ErrorDTO convert(String source) {
        return Optional.ofNullable(JsonMapper.createObject(source, ErrorDTO.class))
                                    .orElseGet(() -> ErrorDTO.builder().build());
    }
}
