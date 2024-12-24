package com.sopotek.aipower.repository;

import com.sopotek.aipower.domain.PersistentLogin;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Transactional
public class JpaPersistentTokenRepository implements PersistentTokenRepository {

    private final PersistentLoginDao persistentLoginDao;

    public JpaPersistentTokenRepository(PersistentLoginDao persistentLoginDao) {
        this.persistentLoginDao = persistentLoginDao;
    }

    @Override
    @CacheEvict(value = "persistentTokens", key = "#token.series") // Evict any existing cache for the series key when a new token is created
    public void createNewToken(@NotNull PersistentRememberMeToken token) {
        PersistentLogin login = new PersistentLogin();
        login.setSeries(token.getSeries());
        login.setUsername(token.getUsername());
        login.setToken(token.getTokenValue());
        login.setLastUsed(token.getDate());
        persistentLoginDao.save(login);
    }

    @Override
    @CacheEvict(value = "persistentTokens", key = "#series") // Evict the cache for the specific series when the token is updated
    public void updateToken(String series, String tokenValue, Date lastUsed) {
        PersistentLogin login = persistentLoginDao.findBySeries(series).orElse(null);
        if (login != null) {
            login.setToken(tokenValue);
            login.setLastUsed(lastUsed);
            persistentLoginDao.save(login);
        }
    }

    @Override
    @Cacheable(value = "persistentTokens", key = "#seriesId") // Cache the result of fetching token for a given series
    public PersistentRememberMeToken getTokenForSeries(String seriesId) {
        PersistentLogin login = persistentLoginDao.findBySeries(seriesId).orElse(null);
        if (login == null) {
            return null;
        }
        return new PersistentRememberMeToken(
                login.getUsername(),
                login.getSeries(),
                login.getToken(),
                login.getLastUsed()
        );
    }

    @Override
    @CacheEvict(value = "persistentTokens", key = "#username") // Evict all tokens for the given user when tokens are removed
    public void removeUserTokens(String username) {
        persistentLoginDao.deleteByUsername(username);
    }
}
