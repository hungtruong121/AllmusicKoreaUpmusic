package com.upmusic.web.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;


@Component
public class UPMusicAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
  
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,  Authentication authentication) throws IOException, ServletException {
		setDefaultTargetUrl("/");
        HttpSession session = request.getSession();
        if (session != null) {
            String redirectTo = (String) session.getAttribute("redirectTo");
            if (redirectTo != null) {
            	logger.debug("onAuthenticationSuccess redirectTo is {}", redirectTo);
                session.removeAttribute("redirectTo");
                getRedirectStrategy().sendRedirect(request, response, redirectTo);
            } else {
                super.onAuthenticationSuccess(request, response, authentication);
            }
        } else {
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }
}
