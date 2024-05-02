package ru.kata.spring.boot_security.demo.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.Optional;

@Component
public class UserValidator implements Validator {
    private final UserService userService;

    @Autowired
    public UserValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    //Проверка уникальности Email
    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;
        Optional<User> UserForComparison = userService.getByEmail(user.getUsername());
        if (UserForComparison.isEmpty()) {
            return; //валидация прошла успешно (пользователь не найден)
        } else if (UserForComparison.get().getId() == user.getId()) {
            return; //валидация прошла успешно (редактируется текущий пользователь)
        }
        errors.rejectValue("email", "", "The email address is already occupied");
    }
}
