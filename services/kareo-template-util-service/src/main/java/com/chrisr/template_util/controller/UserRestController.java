package com.chrisr.template_util.controller;

import com.chrisr.template_util.repository.entity.User;
import com.chrisr.template_util.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/users")
public interface UserRestController {

    // not specifying path will allow both "" and "/" paths
    // i.e.) specifying "/" will only allow "/" path, and not ""
    @GetMapping
    ResponseEntity<List<User>> getAllUsers();

    @GetMapping(path = "/{id}")
    ResponseEntity<User> getUserById(@PathVariable(name = "id") long id);

    // TODO: probably remove this endpoint...
    @PostMapping
    ResponseEntity<User> addUser(@RequestBody User user);

    @DeleteMapping(path = "/{id}")
    ResponseEntity<ApiResponse> deleteUser(@PathVariable(name = "id") long id);
}