package com.example.awesomepizza.jwt.service;

import com.example.awesomepizza.domain.User;
import com.example.awesomepizza.jwt.models.UserPrincipal;
import com.example.awesomepizza.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
@AllArgsConstructor
public class ApplicationUserDetailsService implements UserDetailsService {
    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email)
        throws UsernameNotFoundException {
        return new UserPrincipal(
                userService.getUserByEmail(email)
        );
    }

    private Boolean verifyPasswordHash(
            String password,
            byte[] storedHash,
            byte[] storedSalt
    ) throws NoSuchAlgorithmException {
        if (password.isBlank() || password.isEmpty())
            throw new IllegalArgumentException(
                    "Password cannot be empty"
            );

        if (storedHash.length != 64)
            throw new IllegalArgumentException(
                    "Invalid length of password hash (64 bytes expected)"
            );

        if (storedSalt.length != 128)
            throw new IllegalArgumentException(
                    "Invalid length of password salt (64 bytes expected)"
            );

        var messageDigest = MessageDigest.getInstance("SHA-512");
        messageDigest.update(storedSalt);

        var computedHash = messageDigest.digest(password.getBytes(StandardCharsets.UTF_8));

        for (int i = 0; i < computedHash.length; i++) {
            if (computedHash[i] != storedHash[i]) return false;
        }

        return messageDigest.isEqual(computedHash, storedHash);
    }

    public User authenticate(String email, String password)
        throws NoSuchAlgorithmException {
        if (email.isEmpty() || password.isEmpty())
            throw new BadCredentialsException("Unauthorized");

        var user = userService.getUserByEmail(email);

        if (user == null)
            throw new BadCredentialsException("Unauthorized");

        var verified = verifyPasswordHash(
                password,
                user.getStoredHash(),
                user.getStoredSalt()
        );

        if (!verified)
            throw new BadCredentialsException("Unauthorized");

        return user;
    }
}
