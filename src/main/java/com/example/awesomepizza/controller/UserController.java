package com.example.awesomepizza.controller;

import com.example.awesomepizza.domain.User;
import com.example.awesomepizza.dto.UserDto;
import com.example.awesomepizza.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;
    private final ModelMapper mapper;

    private UserDto convertToDto(User user) {
        return mapper.map(user, UserDto.class);
    }

    private User convertToEntity(@Valid UserDto userDto) {
        return mapper.map(userDto, User.class);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers() {
        List<User> users = userService.getUsers();
        List<UserDto> userDtos = users.stream().map(this::convertToDto).collect(Collectors.toList());
        return new ResponseEntity<>(userDtos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable("id") UUID id) {
        UserDto userDto = convertToDto(userService.getUser(id));
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid UserDto userDto)
        throws NoSuchAlgorithmException {
        User user = userService.createUser(convertToEntity(userDto), userDto.getPassword());
        return new ResponseEntity<>(convertToDto(user), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateUser(
            @PathVariable("id") UUID id,
            @Valid @RequestBody UserDto userDto
    ) throws NoSuchAlgorithmException {
        if (!id.equals(userDto.getId())) throw new
                ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "id does not match."
        );

        userService.updateUser(id, convertToEntity(userDto), userDto.getPassword());
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteUser(@PathVariable("id") UUID id) {
        userService.deleteUser(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
