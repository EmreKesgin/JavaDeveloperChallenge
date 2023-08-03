package com.API.JavaChallengeDemo.controller;

import com.API.JavaChallengeDemo.models.ApplicationUser;
import com.API.JavaChallengeDemo.models.LoginResponseDTO;
import com.API.JavaChallengeDemo.models.RegistrationDTO;
import com.API.JavaChallengeDemo.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/register")
    public ApplicationUser registerUser(@RequestBody RegistrationDTO body){
        return authenticationService.registerUser(body.getUsername(),body.getPassword());
    }

    @PostMapping("/login")
    public LoginResponseDTO loginUser(@RequestBody RegistrationDTO body){
        return authenticationService.loginUser(body.getUsername(), body.getPassword());
    }
}
