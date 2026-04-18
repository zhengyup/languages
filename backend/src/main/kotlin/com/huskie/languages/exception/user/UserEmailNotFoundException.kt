package com.huskie.languages.exception.user

class UserEmailNotFoundException(
    email: String
) : RuntimeException("User with email $email was not found")
