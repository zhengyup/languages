package com.huskie.languages.exception.user

class UserNotFoundException(
    userId: Long
) : RuntimeException("User with id $userId was not found")
