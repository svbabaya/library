package ru.maxima.springbootapp.library.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {

//    @GetMapping("/registration")
//    public String registrationPage(@ModelAttribute("person") Person person) {
//        return "auth/registration";
//    }
//
//    @PostMapping("/registration")
//    public String performRegistration(@ModelAttribute("person") @Valid Person person,
//                                      BindingResult bindingResult) {
//        validator.validate(person, bindingResult);
//        service.register(person);
//        return "redirect:/auth/login";
//    }

}
