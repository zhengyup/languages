package com.huskie.languages.service.user

import com.huskie.languages.domain.user.UserScenarioCompletion
import com.huskie.languages.dto.user.UserScenarioCompletionResponse
import com.huskie.languages.exception.scenario.ScenarioNotFoundException
import com.huskie.languages.exception.user.DuplicateUserScenarioCompletionException
import com.huskie.languages.exception.user.UserNotFoundException
import com.huskie.languages.repository.scenario.ScenarioRepository
import com.huskie.languages.repository.user.UserRepository
import com.huskie.languages.repository.user.UserScenarioCompletionRepository
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class UserScenarioCompletionService(
    private val userRepository: UserRepository,
    private val scenarioRepository: ScenarioRepository,
    private val userScenarioCompletionRepository: UserScenarioCompletionRepository
) {
    fun markScenarioCompleted(userId: Long, scenarioId: Long): UserScenarioCompletionResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { UserNotFoundException(userId) }
        val scenario = scenarioRepository.findById(scenarioId)
            .orElseThrow { ScenarioNotFoundException(scenarioId) }
        checkUserScenarioCompletionAlreadyExists(userId, scenarioId)

        val completion = UserScenarioCompletion(
            user = user,
            scenario = scenario,
            completedAt = Instant.now()
        )

        return userScenarioCompletionRepository.save(completion).toResponse()
    }

    fun checkUserScenarioCompletionAlreadyExists(userId : Long, scenarioId: Long) {
        if (userScenarioCompletionRepository.existsByUserIdAndScenarioId(userId, scenarioId)) {
            throw DuplicateUserScenarioCompletionException(userId, scenarioId)
        }
    }

    fun isScenarioCompleted(userId: Long, scenarioId: Long): Boolean =
        userScenarioCompletionRepository.existsByUserIdAndScenarioId(userId, scenarioId)

    fun getCompletedScenarioIdsForUser(userId: Long): List<Long> {
        ensureUserExists(userId)

        return userScenarioCompletionRepository.findAllByUserIdOrderByCompletedAtAsc(userId)
            .map { checkNotNull(it.scenario.id) }
    }

    fun getScenarioCompletionsForUser(userId: Long): List<UserScenarioCompletionResponse> {
        ensureUserExists(userId)

        return userScenarioCompletionRepository.findAllByUserIdOrderByCompletedAtAsc(userId)
            .map { it.toResponse() }
    }

    private fun ensureUserExists(userId: Long) {
        if (userRepository.existsById(userId).not()) {
            throw UserNotFoundException(userId)
        }
    }

    private fun UserScenarioCompletion.toResponse(): UserScenarioCompletionResponse =
        UserScenarioCompletionResponse(
            userId = checkNotNull(user.id),
            scenarioId = checkNotNull(scenario.id),
            completedAt = completedAt
        )
}
