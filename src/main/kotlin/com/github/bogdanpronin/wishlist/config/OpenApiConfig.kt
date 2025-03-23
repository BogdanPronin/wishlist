package com.github.bogdanpronin.wishlist.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement as SwaggerSecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme as SwaggerSecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@OpenAPIDefinition
class OpenApiConfig {

    @Bean
    fun customOpenAPI(): OpenAPI = OpenAPI()
        .info(
            Info()
                .title("WishList API")
                .version("1.0")
                .description("API документация для проекта WishList")
        )
        .components(
            Components()
                .addSecuritySchemes(
                    "bearerAuth",
                    SwaggerSecurityScheme()
                        .type(SwaggerSecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                )
        )
        .addSecurityItem(SwaggerSecurityRequirement().addList("bearerAuth"))
}
