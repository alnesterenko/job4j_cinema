package ru.job4j.cinema.service;

import org.springframework.stereotype.Service;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.repository.UserRepository;

import java.util.Collection;
import java.util.Optional;

@Service
public class SimpleUserService implements UserService {

    private final UserRepository userRepository;

    public SimpleUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findUserByEmailAndPassword(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password);
    }

    @Override
    public Collection<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public boolean deleteUserById(int id) {
        return userRepository.deleteById(id);
    }

    @Override
    public Optional<User> findUserById(int id) {
        return userRepository.findById(id);
    }
}
