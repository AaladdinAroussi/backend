package com.pfe.smsworkflow.Security.Services;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import com.pfe.smsworkflow.Models.RefreshToken;
import com.pfe.smsworkflow.Repository.RefreshTokenRepository;
import com.pfe.smsworkflow.Repository.UsersRepository;
import com.pfe.smsworkflow.exception.TokenRefreshException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/*Le service RefreshTokenService est essentiel pour implémenter une gestion robuste des sessions
 utilisateur, en particulier dans les systèmes utilisant des JWT pour l'authentification.
  En séparant la gestion des access tokens et des refresh tokens, on permet à l'application
  de maintenir une expérience utilisateur fluide tout en renforçant la sécurité.*/

@Service
public class RefreshTokenService {
    @Value("${app.jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UsersRepository userRepository;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken createRefreshToken(Long userId) {
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(userRepository.findById(userId).get());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());

        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
        }

        return token;
    }

    @Transactional
    public int deleteByUserId(Long userId) {
        return refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
    }
    public boolean deleteByToken(String token) {
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByToken(token);
        if (refreshToken.isPresent()) {
            refreshTokenRepository.delete(refreshToken.get());
            return true;
        }
        return false;
    }

}