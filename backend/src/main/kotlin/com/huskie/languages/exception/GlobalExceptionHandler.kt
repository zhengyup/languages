package com.huskie.languages.exception

import com.huskie.languages.dto.ErrorResponse
import com.huskie.languages.exception.scenario.IncompleteVocabularyCoverageException
import com.huskie.languages.exception.scenario.ScenarioNotFoundException
import com.huskie.languages.exception.user.DuplicateUserEmailException
import com.huskie.languages.exception.user.DuplicateUserScenarioCompletionException
import com.huskie.languages.exception.user.UserEmailNotFoundException
import com.huskie.languages.exception.user.UserNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.Instant

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFound(exception: UserNotFoundException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(
                ErrorResponse(
                    message = exception.message ?: "User was not found",
                    timestamp = Instant.now()
                )
            )

    @ExceptionHandler(UserEmailNotFoundException::class)
    fun handleUserEmailNotFound(exception: UserEmailNotFoundException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(
                ErrorResponse(
                    message = exception.message ?: "User was not found",
                    timestamp = Instant.now()
                )
            )

    @ExceptionHandler(DuplicateUserEmailException::class)
    fun handleDuplicateUserEmail(exception: DuplicateUserEmailException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.CONFLICT)
            .body(
                ErrorResponse(
                    message = exception.message ?: "User email already exists",
                    timestamp = Instant.now()
                )
            )

    @ExceptionHandler(DuplicateUserScenarioCompletionException::class)
    fun handleDuplicateUserScenarioCompletion(
        exception: DuplicateUserScenarioCompletionException
    ): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.CONFLICT)
            .body(
                ErrorResponse(
                    message = exception.message ?: "Scenario completion already exists",
                    timestamp = Instant.now()
                )
            )

    @ExceptionHandler(ScenarioNotFoundException::class)
    fun handleScenarioNotFound(exception: ScenarioNotFoundException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(
                ErrorResponse(
                    message = exception.message ?: "Scenario was not found",
                    timestamp = Instant.now()
                )
            )

    @ExceptionHandler(IncompleteVocabularyCoverageException::class)
    fun handleIncompleteVocabularyCoverage(
        exception: IncompleteVocabularyCoverageException
    ): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                ErrorResponse(
                    message = exception.message ?: "Scenario content is incomplete",
                    timestamp = Instant.now()
                )
            )
}
