package com.todo.demo.controllers;

import com.todo.demo.http.request.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    @Autowired
    public LoginController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @PostMapping(path = "/login")
    public void loginController(@Valid @RequestBody LoginRequest loginRequest,
                                HttpServletRequest request,
                                HttpServletResponse response) {
        // Create an unauthenticated authentication request
        Authentication authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(
                loginRequest.getUsername(), loginRequest.getPassword()
        );

        // Authenticate the request using auth manager(all beans required in webSecurityConfig
        Authentication authenticationResponse = authenticationManager.authenticate(authenticationRequest);

        // Create and set the SecurityContext
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authenticationResponse);

        /* Save the SecurityContext in the session (in-memory store default)
           When Subsequent request comes it fetches the security context using sessionID and validates
         */
        securityContextRepository.saveContext(context, request, response);
        System.out.println(request.getSession().getId());
        System.out.println("hello world, you have logged in!");
    }


    @PostMapping(path = "/logout")
    public void logoutController(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
            request.getSession().invalidate();

        }
    }
}
