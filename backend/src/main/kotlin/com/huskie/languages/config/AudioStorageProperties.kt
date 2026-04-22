package com.huskie.languages.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.nio.file.Path
import java.nio.file.Paths

@ConfigurationProperties(prefix = "audio")
data class AudioStorageProperties(
    val storagePath: String = "generated/audio",
    val baseUrlPath: String = "/audio"
) {
    fun absoluteStoragePath(): Path = Paths.get(storagePath).toAbsolutePath().normalize()

    fun resourceHandlerPattern(): String = "${baseUrlPath.trimEnd('/')}/**"

    fun resourceLocation(): String = "${absoluteStoragePath().toUri()}"
}
