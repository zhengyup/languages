package com.huskie.languages.exception

import com.huskie.languages.dto.ErrorResponse
import com.huskie.languages.exception.scenario.ScenarioNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.Instant

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(ScenarioNotFoundException::class)
    fun handleScenarioNotFound(exception: ScenarioNotFoundException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(
                ErrorResponse(
                    message = exception.message ?: "Scenario was not found",
                    timestamp = Instant.now()
                )
            )
}
