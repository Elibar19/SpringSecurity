package com.app.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class TestAuthController {

    @GetMapping("/hello")
    @PreAuthorize("permitAll()") //Con esta anotacion podemos elegir la autorizacion que vamos a poner en cada endpoint.
    public String hello(){
        return "Hello World Public";
    }

    @GetMapping("/helloSecured")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String helloSecured(){
        return "Hello Secured";
    }

    @GetMapping("/helloSecured2")
    @PreAuthorize("hasAuthority('READ') or hasAuthority('CREATE')")
    public String helloSecured2(){
        return "Hello Secured2";
    }
}
