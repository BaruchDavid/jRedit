package de.ffm.rka.rkareddit.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class JsonMapper {
    private static ObjectWriter objectWriter;
    private JsonMapper(){}
    public static String createJson(Object obj) throws JsonProcessingException {
        if (objectWriter == null){
            objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
        }
        return objectWriter.writeValueAsString(obj);
    }
}