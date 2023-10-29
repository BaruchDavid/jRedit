package de.ffm.rka.rkareddit.controller.logout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;

@Component
public class LogoutHandlerImpl implements LogoutHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogoutHandlerImpl.class);

    final
    CacheManager cacheManager;

    public LogoutHandlerImpl(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        cleanUserCache(authentication.getName());


        LOGGER.info("USER {} HAS BEEN LOGOUT", authentication.getName());
    }

    //@CacheEvict(value = "users", key = "#email")
    public void cleanUserCache(String mail) {
        final Cache users = Optional.ofNullable(cacheManager.getCache("users"))
                .orElseGet(() -> new ConcurrentMapCache("noUserCache"));

        final boolean cacheCleaned = Optional.of(users)
                .map(cache -> cache.evictIfPresent(mail))
                .orElse(false);
        LOGGER.info("CACHE FOR USER {} HAS BEEN CLEANED {}", mail, cacheCleaned);
        LOGGER.info("USER-CACHE CONTAINS {} USERS", ((ConcurrentMapCache) Optional.of(users)
                .orElseGet(() -> new ConcurrentMapCache("noUsers")))
                .getNativeCache().size());


    }
}
