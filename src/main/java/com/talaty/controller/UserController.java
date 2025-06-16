package com.talaty.controller;

import com.talaty.mapper.UserMapper;
import com.talaty.model.Admin;
import com.talaty.model.Customer;
import com.talaty.model.User;
import com.talaty.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for managing users.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    /**
     * Retrieve all users.
     *
     * @return List of all users.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<?>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<?> userDtos = users.stream()
                .map(user -> {
                    if (user instanceof Admin) {
                        return userMapper.toAdminResponseDto((Admin) user);
                    } else if (user instanceof Customer) {
                        return userMapper.toCustomerResponseDto((Customer) user);
                    } else {
                        return null;
                    }
                })
                .collect(Collectors.toList());
        return new ResponseEntity<>(userDtos, HttpStatus.OK);
    }

    /**
     * Retrieve a user by ID.
     *
     * @param id The ID of the user to retrieve.
     * @return The user with the specified ID.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/details/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (user instanceof Admin) {
            return new ResponseEntity<>(userMapper.toAdminResponseDto((Admin) user), HttpStatus.OK);
        } else if (user instanceof Customer) {
            return new ResponseEntity<>(userMapper.toCustomerResponseDto((Customer) user), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Update an existing user.
     *
     * @param id The ID of the user to update.
     * @param user The user with updated information.
     * @return The updated user.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        User updatedUser = userService.updateUser(user);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    /**
     * Delete a user by ID.
     *
     * @param id The ID of the user to delete.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
