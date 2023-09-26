/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.controllers;

import housemate.entities.UserAccount;
import housemate.services.LoginService;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author ThanhF
 */
@RestController
@RequestMapping
public class LoginController {

    @Autowired
    LoginService loginService;

    @GetMapping("/callback/google/user")
    public Object loginSuccessWithGoogle(OAuth2AuthenticationToken oAuth2AuthenticationToken) {
        Map<String, Object> user = oAuth2AuthenticationToken.getPrincipal().getAttributes();
        String token = loginService.loginWithGoogle(user);
        return token;
    }
    
    // TODO: Delete this
    @GetMapping("/test")
    public List<UserAccount> getAll() {
        return loginService.getAll();
    }
}
