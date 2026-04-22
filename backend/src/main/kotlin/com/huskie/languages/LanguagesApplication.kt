package com.huskie.languages

import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class LanguagesApplication

fun main(args: Array<String>) {
	runApplication<LanguagesApplication>(*args)
}
