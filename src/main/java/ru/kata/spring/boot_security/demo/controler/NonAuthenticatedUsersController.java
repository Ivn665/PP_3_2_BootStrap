package ru.kata.spring.boot_security.demo.controler;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NonAuthenticatedUsersController {

    @GetMapping("login")
    public String showLoginPage() {
        return "WEB-INF/login.html";
    }

}
