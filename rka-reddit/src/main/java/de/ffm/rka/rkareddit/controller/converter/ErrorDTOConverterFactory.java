package de.ffm.rka.rkareddit.controller.converter;

import de.ffm.rka.rkareddit.domain.dto.ErrorDTO;
import de.ffm.rka.rkareddit.util.JsonMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import java.util.Optional;

public class ErrorDTOConverterFactory implements ConverterFactory<String, ErrorDTO> {
    @Override
    public <T extends ErrorDTO> Converter<String, T> getConverter(Class<T> targetType) {
        return new ErrorDTOConverter<>(targetType);
    }


   private static class ErrorDTOConverter<T extends ErrorDTO> implements Converter<String, T> {

        private Class<T> targetClass;

        public ErrorDTOConverter(Class<T> targetClass) {
            this.targetClass = targetClass;
        }

        @Override
        public T convert(String source) {
            final ErrorDTO object = Optional.ofNullable(JsonMapper.createObject(source, ErrorDTO.class))
                    .orElseGet(() -> ErrorDTO.builder().build());
            return (T) object;
        }
    }
}
