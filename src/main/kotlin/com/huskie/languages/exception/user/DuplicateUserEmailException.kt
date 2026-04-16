package com.huskie.languages.exception.user

class DuplicateUserEmailException(
    email: String
) : RuntimeException("User with email $email already exists")
