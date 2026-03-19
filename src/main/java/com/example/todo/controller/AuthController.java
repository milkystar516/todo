package com.example.todo.controller;

import com.example.todo.service.SignupForm;
import com.example.todo.service.SignupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {
    
    private final SignupService signupService;

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("signupForm", new SignupForm());
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(
        @Valid @ModelAttribute SignupForm signupForm,
        BindingResult bindingResult,
        Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "signup";
        } try {
            signupService.signup(signupForm);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "signup";
        }

        return "redirect:/signup?success";
    }

    @GetMapping("/signup/verify")
    public String verify(@RequestParam String token, Model model) {
        try {
            signupService.verify(token);
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "verify-result";
        }

        return "redirect:/login?verified";
    }
}
