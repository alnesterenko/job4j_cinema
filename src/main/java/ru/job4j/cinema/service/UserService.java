package ru.job4j.cinema.service;

import ru.job4j.cinema.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserService {

    Optional<User> saveUser(User user);

    Optional<User> findUserByEmailAndPassword(String email, String password);

    Collection<User> findAllUsers();

    boolean deleteUserById(int id);

    Optional<User> findUserById(int id);
}
