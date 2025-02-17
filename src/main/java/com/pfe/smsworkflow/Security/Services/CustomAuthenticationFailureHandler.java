package com.pfe.smsworkflow.Security.Services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        String errorMessage;

        // Vérifiez si l'exception est due à un compte bloqué
        if (exception.getMessage().contains("Ce compte est bloqué")) {
            errorMessage = "Votre compte est bloqué. Contactez l'administrateur.";
        } else {
            errorMessage = "Identifiants incorrects."; // Message par défaut pour les autres erreurs
        }

        // Définir le statut de la réponse et le message
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(errorMessage);
    }
}