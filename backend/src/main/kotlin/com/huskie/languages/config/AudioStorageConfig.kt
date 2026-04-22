package com.huskie.languages.config

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.nio.file.Files

@Configuration
class AudioStorageConfig(
    private val audioStorageProperties: AudioStorageProperties
) : WebMvcConfigurer, ApplicationRunner {

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler(audioStorageProperties.resourceHandlerPattern())
            .addResourceLocations(audioStorageProperties.resourceLocation())
    }

    override fun run(args: ApplicationArguments) {
        // Generated files live outside src/main/resources because they are created
        // after the app is built. Keeping a stable file name like
        // scenario-line-{scenarioLineId}.wav gives us stable URLs now and makes it
        // easier to swap the backing store for S3 or another object store later.
        Files.createDirectories(audioStorageProperties.absoluteStoragePath())
    }
}
