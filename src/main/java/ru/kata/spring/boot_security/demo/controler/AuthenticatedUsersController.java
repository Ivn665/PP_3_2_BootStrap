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
        if (AuthorityUtils.authorityListToSet(authentication.getAuthorities()).contains("ROLE_ADMIN")) {
            model.addAttribute("newUser", new User());
        }
        return "/WEB-INF/mainPage.html";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute("newUser") @Valid User user
            , BindingResult bindingResult, Model model, Authentication authentication) {
        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            fillCommonsAttributes(model, authentication);
            model.addAttribute("error", "newUserError");
            return "/WEB-INF/mainPage.html";
        } else {
            userService.saveUser(user);
            return "redirect:/";
        }
    }

    @PostMapping("/edit")
    public String updateUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult, Model model,
                             Authentication authentication, HttpSession session) {
        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            fillCommonsAttributes(model, authentication);
            model.addAttribute("error", "EditUserError");
            model.addAttribute("errorMessages", bindingResult.getAllErrors());
            model.addAttribute("UnchangedUserId", user.getId());
            model.addAttribute("newUser", new User());
            return "/WEB-INF/mainPage.html";
        }
        if (!userService.getById(user.getId()).getUsername().equals(user.getUsername())) {
            session.invalidate();
        }
        userService.saveUser(user);
        return "redirect:/";
    }


    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") long id, Authentication authentication, HttpSession session) {
        User userForDelete = userService.getById(id);
        userService.deleteById(id);
        if (authentication.getName().equals(userForDelete.getUsername())) {
            session.invalidate();
        }
        return "redirect:/";
    }

    private void fillCommonsAttributes(Model model, Authentication authentication) {
        Collection<String> roles = formatRoles(authentication);
        model.addAttribute("authenticatedUser", userService.getByEmail(authentication.getName()).get());
        if (roles.contains("ADMIN")) {
            model.addAttribute("usersList", userService.allUsers());
            model.addAttribute("roles", roleService.getAllRoles());
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
