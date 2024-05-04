package ru.kata.spring.boot_security.demo.controler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;
import ru.kata.spring.boot_security.demo.util.UserValidator;

import javax.servlet.http.HttpSession;


@Controller
@RequestMapping("/")
public class AuthenticatedUsersController {

    private final UserService userService;
    private final RoleService roleService;
    private final UserValidator userValidator;

    @Autowired
    public AuthenticatedUsersController(UserService userService, RoleService roleService, UserValidator userValidator) {
        this.userService = userService;
        this.roleService = roleService;
        this.userValidator = userValidator;
    }

    @GetMapping("")
    public String showMainPage(Model model, Authentication authentication) {
        fillCommonsAttributes(model, authentication);
        return "/WEB-INF/mainPage.html";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute("newUser") User user) {
        userService.saveUser(user);
        return "redirect:/";
    }

    @PostMapping("/edit")
    public String updateUser(@ModelAttribute("user") User user, HttpSession session) {
        if (!userService.getById(user.getId()).getUsername().equals(user.getUsername())) {
            session.invalidate();
        }
        userService.saveUser(user);
        return "redirect:/";
    }


    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") long id, Authentication authentication, HttpSession session) {
        if (authentication.getName().equals(userService.getById(id).getUsername())) {
            session.invalidate();
        }
        userService.deleteById(id);
        return "redirect:/";
    }

    private void fillCommonsAttributes(Model model, Authentication authentication) {
        model.addAttribute("authenticatedUser", userService.getByEmail(authentication.getName()).get());
        if (AuthorityUtils.authorityListToSet(authentication.getAuthorities()).contains("ROLE_ADMIN")) {
            model.addAttribute("usersList", userService.allUsers());
            model.addAttribute("roles", roleService.getAllRoles());
            model.addAttribute("newUser", new User());
        }
    }
}
