package de.ffm.rka.rkareddit.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonMapper {
    public static final Logger logger = LoggerFactory.getLogger(JsonMapper.class);
    private static ObjectWriter objectWriter;
    private static ObjectMapper objectMapper;

    private JsonMapper(){}
    public static String createJson(Object obj) throws JsonProcessingException {
        if (objectWriter == null){
            objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
        }
        return objectWriter.writeValueAsString(obj);
    }

    public static <T> T createObject(String json, Class<T> clazz) {
        if (objectMapper == null){
            objectMapper = new ObjectMapper();
        }
        try{
            return objectMapper.readValue(json, clazz);
        } catch(Exception ex){
            logger.error("EXCEPTION {} DURING CONVERTING JSON {} INTO class-type {}",ex.getMessage(), json, clazz.getName());
            return null;
        }
    }
}
