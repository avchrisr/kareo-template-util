package com.chrisr.template_util.service;

import com.chrisr.template_util.exception.ResourceAlreadyExistsException;
import com.chrisr.template_util.repository.entity.User;
import com.chrisr.template_util.repository.UserRepository;
import com.chrisr.template_util.request.SignUpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Autowired
    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Transactional
    public User registerUser(SignUpRequest signUpRequest) {

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            String errorMessage = String.format("Username '%s' is already taken.", signUpRequest.getUsername());
            throw new ResourceAlreadyExistsException(errorMessage);
        }

        User user = new User();
        user.setUsername(signUpRequest.getUsername());

        // using bcrypt, declared in SecurityConfig. you can bypass this encryption and just set the password if you want.
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
//        user.setPassword(signUpRequest.getPassword());

        user.setEmail(signUpRequest.getEmail());
        user.setFirstname(signUpRequest.getFirstname());
        user.setLastname(signUpRequest.getLastname());

        addUser(user);
        return user;
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.getUsers();
    }

    @Transactional(readOnly = true)
    public User getUserById(long id) {
        return userRepository.getUserById(id);
    }

    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userRepository.getUserByUsername(username);
    }

    @Transactional
    public void addUser(User user) {
        user.setId(userRepository.getNextPostgresSequence());
        userRepository.addUser(user);
    }

    @Transactional
    public void deleteUserById(long id) {
        userRepository.deleteUserById(id);
    }
}
