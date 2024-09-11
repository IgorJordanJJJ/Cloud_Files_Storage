package org.example.cloudfilestorage.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cloudfilestorage.dto.UserRegistrationDto;
import org.example.cloudfilestorage.facade.auth.AuthFacade;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthFacade authFacade;
    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/registration")
    public String registrationPage(@ModelAttribute("user") UserRegistrationDto user){
        return "auth/registration";
    }

    @PostMapping("/registration")
    public String processRegister(@ModelAttribute("user") @Valid UserRegistrationDto user,
                                  BindingResult bindingResult) {
        log.info("Start process registration");
        return authFacade.registerUser(user, bindingResult);
    }


}
