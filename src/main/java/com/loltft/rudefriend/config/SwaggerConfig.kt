package com.loltft.rudefriend.config;

import java.util.List;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI openAPI() {
    SecurityScheme securityScheme = new SecurityScheme()
        .type(SecurityScheme.Type.HTTP)
        .scheme("bearer")
        .bearerFormat("JWT")
        .in(SecurityScheme.In.HEADER)
        .name("Authorization");

    SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");

    return new OpenAPI()
        .info(getApiInfo())
        .servers(getServers())
        .components(new Components().addSecuritySchemes("bearerAuth", securityScheme))
        .security(List.of(securityRequirement));
  }

  private Info getApiInfo() {
    return new Info()
        .title("Rude Friend API")
        .description("Nexus REST API 문서")
        .version("1.0.0")
        .contact(new Contact().name("keyy1315").email("lovelina1315@gmail.com"));
  }

  private List<Server> getServers() {
    return List.of(new Server().url("http://localhost:8081").description("Local Server"));
    //        new Server().url("http://localhost:8080").description("Production Server"));
  }

  @Bean
  public GroupedOpenApi publicApi() {
    return GroupedOpenApi.builder()
        .group("public-api")
        .pathsToMatch("/**")
        .packagesToScan("com.loltft.rudefriend")
        .build();
  }
}
