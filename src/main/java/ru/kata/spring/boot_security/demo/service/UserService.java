package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> allUsers();
    void saveUser(User user);
    void deleteById(long id);
    User getById(long id);

    Optional<User> getByEmail(String email);
}
