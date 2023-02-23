package com.example.awesomepizza.service;

import com.example.awesomepizza.domain.User;
import com.example.awesomepizza.dto.UserDto;
import com.example.awesomepizza.exception.BadRequestException;
import com.example.awesomepizza.exception.NotFoundException;
import com.example.awesomepizza.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private User findOrThrow(UUID id) {
        return userRepository.findById(id).orElseThrow(
                () -> new NotFoundException(
                        "User with id " + id + " was not found")
        );
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

//    public List<User> getUsersByName(String name) {
//        return userRepository.findByNameContaining(name);
//    }

    public User getUser(UUID id) {
        return findOrThrow(id);
    }
    public User getUserByEmail(String email) {
        // return userRepository.getUserByEmail(email);
        return userRepository.findByEmail(email);
    }

    private byte[] createSalt() {
        var random = new SecureRandom();
        var salt = new byte[128];
        random.nextBytes(salt);

        return salt;
    }

    private byte[] createPasswordHash(String password, byte[] salt)
        throws NoSuchAlgorithmException {
        var messageDigest = MessageDigest.getInstance("SHA-512");
        messageDigest.update(salt);

        return messageDigest.digest(
                password.getBytes(StandardCharsets.UTF_8)
        );
    }

    public User createUser(User user, String password)
        throws NoSuchAlgorithmException {

        if (password.isBlank()) throw new IllegalArgumentException(
                "Password is required."
        );

        boolean emailExists = userRepository.findByEmail(user.getEmail()) != null;
        if (emailExists) throw new BadRequestException(
                "Email " + user.getEmail() + " is already in use"
        );

        byte[] salt = createSalt();
        byte[] hashedPassword = createPasswordHash(password, salt);

        user.setStoredSalt(salt);
        user.setStoredHash(hashedPassword);

        return userRepository.save(user);
    }

    public User updateUser(UUID id, User userParam, String password)
        throws NoSuchAlgorithmException {
        User user = findOrThrow(id);

        user.setEmail(userParam.getEmail());
        user.setMobileNumber(userParam.getMobileNumber());

        if (!password.isBlank()) {
            byte[] salt = createSalt();
            byte[] hashedPassword = createPasswordHash(password, salt);

            user.setStoredSalt(salt);
            user.setStoredHash(hashedPassword);
        }

        return userRepository.save(user);
    }

    public void deleteUser(UUID id) {
        findOrThrow(id);
        userRepository.deleteById(id);
    }


}
