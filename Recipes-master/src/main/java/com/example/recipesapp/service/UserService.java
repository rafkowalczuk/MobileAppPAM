package com.example.recipesapp.service;

import com.example.recipesapp.entity.User;
import com.example.recipesapp.entity.UserDetailsImplementation;
import com.example.recipesapp.exceptions.EmailAlreadyInUseException;
import com.example.recipesapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public void addUser(User user) {
        Optional<User> userWithEmail = userRepository.findByEmail(user.getEmail());

        if (userWithEmail.isPresent())
            throw new EmailAlreadyInUseException();

        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userWithEmail = userRepository.findByEmail(username);
        if (userWithEmail.isPresent()) {
            return new UserDetailsImplementation(userWithEmail.get());
        } else {
            throw new UsernameNotFoundException("Not found: " + username);
        }
    }

    public User findByEmail(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            return userRepository.findByEmail(email).get();
        } else {
            throw new UsernameNotFoundException("Not found: " + email);
        }
    }
}
