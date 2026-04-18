package com.huskie.languages.controller.user

import com.huskie.languages.dto.user.CreateUserRequest
import com.huskie.languages.dto.user.UserResponse
import com.huskie.languages.service.user.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createUser(@Valid @RequestBody request: CreateUserRequest): UserResponse =
        userService.createUser(request)

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): UserResponse =
        userService.getUserById(id)
}
