package de.ffm.rka.rkareddit.util;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
public class CacheController {

    private static final long MAX_CACHE_DURATION = 25;
    private static final int PAST_MINUTES_OF_CACHE_EXPIRATION = 3;
    private static final String USER_VISIT_NO_CACHE_CONTROL = "cache";


    public String setCacheHeader(String username) {
        if(StringUtils.isEmpty(username)){
            return "no-cache";
        } else {
            return "";
        }
    }

    public HttpHeaders setCacheHeader(LocalDateTime date) {
        HttpHeaders headers = new HttpHeaders();
        if (checkForExpiredModification(date)) {
            headers.setCacheControl(org.springframework.http.CacheControl.maxAge(MAX_CACHE_DURATION, TimeUnit.DAYS));
        } else {
            headers.setCacheControl(org.springframework.http.CacheControl.noCache());
        }
        return headers;
    }

    /**
     * modification date is expired, when it is wished timed ago
     *
     * @param modificationDate of resource
     * @return true when modificationDate is wished time ago
     */
    private boolean checkForExpiredModification(LocalDateTime modificationDate) {
        return LocalDateTime.now().minusMinutes(PAST_MINUTES_OF_CACHE_EXPIRATION).isAfter(modificationDate);
    }

}
