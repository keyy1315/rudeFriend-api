package com.loltft.rudefriend.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.List

@Configuration
class SwaggerConfig {
    @Bean
    fun openAPI(): OpenAPI? {
        val securityScheme = SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .`in`(SecurityScheme.In.HEADER)
            .name("Authorization")

        val securityRequirement = SecurityRequirement().addList("bearerAuth")

        return OpenAPI()
            .info(this.apiInfo)
            .servers(this.servers)
            .components(Components().addSecuritySchemes("bearerAuth", securityScheme))
            .security(List.of<SecurityRequirement?>(securityRequirement))
    }

    private val apiInfo: Info?
        get() = Info()
            .title("Rude Friend API")
            .description("Nexus REST API 문서")
            .version("1.0.0")
            .contact(
                Contact().name("keyy1315")
                    .email("lovelina1315@gmail.com")
            )

    private val servers: MutableList<Server?>
        get() = List.of<Server?>(
            Server().url("http://localhost:8081")
                .description("Local Server")
        )
    //        new Server().url("http://localhost:8080").description("Production Server"));

    @Bean
    fun publicApi(): GroupedOpenApi? {
        return GroupedOpenApi.builder()
            .group("public-api")
            .pathsToMatch("/**")
            .packagesToScan("com.loltft.rudefriend")
            .build()
    }
}
