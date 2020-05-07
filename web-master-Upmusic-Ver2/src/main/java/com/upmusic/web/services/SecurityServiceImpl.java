package com.upmusic.web.services;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;


@Service
public class SecurityServiceImpl implements SecurityService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public String findLoggedInEmail() {
    	logger.debug("findLoggedInEmail called");
        Object userDetails = SecurityContextHolder.getContext().getAuthentication().getDetails();
        if (userDetails instanceof UserDetails) {
            return ((UserDetails)userDetails).getUsername();
        }

        return null;
    }
    
    @Override
    public String autologin(String email, String password, HttpServletRequest request) {
    	logger.debug("autologin called");
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
        authenticationManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(authToken);
        logger.debug("autologin successfully!");
//        if (authToken.isAuthenticated()) {
//            SecurityContextHolder.getContext().setAuthentication(authToken);
//            logger.debug("Auto login {} successfully!", email);
//        }
        HttpSession session = request.getSession();
        if (session != null) {
            String redirectTo = (String) session.getAttribute("redirectTo");
            if (redirectTo != null) {
            	logger.debug("autologin redirectTo is {}", redirectTo);
                session.removeAttribute("redirectTo");
                return "redirect:" + redirectTo;
            }
        }
        return "redirect:/";
    }
    
    @Override
    public boolean login(String email, String password) {
    	logger.debug("login called");
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
        authenticationManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(authToken);
        logger.debug("autologin successfully!");
        if (authToken.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(authToken);
            logger.debug("Auto login {} successfully!", email);
            return true;
        }
        return false;
    }

}
