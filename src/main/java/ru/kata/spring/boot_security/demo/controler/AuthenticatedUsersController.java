package ru.kata.spring.boot_security.demo.controler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;
import ru.kata.spring.boot_security.demo.util.UserValidator;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Collection;
import java.util.HashSet;


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
    public String saveUser(@ModelAttribute("newUser") @Valid User user
            , BindingResult bindingResult) {
        userValidator.validate(user, bindingResult);
        if (!bindingResult.hasErrors()) {
            userService.saveUser(user);
        }
        return "redirect:/";
    }

    @PostMapping("/edit")
    public String updateUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult, HttpSession session) {
        userValidator.validate(user, bindingResult);
        if (!bindingResult.hasErrors()) {
            if (!userService.getById(user.getId()).getUsername().equals(user.getUsername())) {
                session.invalidate();
            }
            userService.saveUser(user);
        }
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
        Collection<String> roles = formatRoles(authentication);
        model.addAttribute("authenticatedUser", userService.getByEmail(authentication.getName()).get());
        if (roles.contains("ADMIN")) {
            model.addAttribute("usersList", userService.allUsers());
            model.addAttribute("roles", roleService.getAllRoles());
            model.addAttribute("newUser", new User());
        }
    }

    private Collection<String> formatRoles(Authentication authentication) {
        //Добавляем коллекцию ролей текущего пользователя без слова ROLE_
        Collection<String> roles = new HashSet<>();
        for (String r : AuthorityUtils.authorityListToSet(authentication.getAuthorities())) {
            roles.add(r.replace("ROLE_", ""));
        }
        return roles;
    }
}
