package com.loltft.rudefriend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
@ConfigurationPropertiesScan("com.loltft.rudefriend")
class RudeFriendApiApplication

fun main(args: Array<String>) {
    runApplication<RudeFriendApiApplication>(*args)
}