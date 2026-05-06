package com.co2api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI / Swagger UI configuration. Exposes an OpenAPI bean that sets API
 * metadata and registers an API key security scheme so Swagger UI shows an
 * "Authorize" button where users can enter their X-API-KEY before testing
 * endpoints. Swagger UI is available at {@code http://localhost:8080/swagger-ui.html}.
 * OpenAPI JSON spec is available at {@code http://localhost:8080/v3/api-docs}.
 */
@Configuration
public class SwaggerConfig {

    /**
     * Builds and returns the OpenAPI metadata and security scheme definition.
     * The "ApiKeyAuth" security scheme matches the name used in the
     * SecurityRequirement annotation on the controller methods, linking the
     * scheme to those endpoints in the generated documentation.
     *
     * @return configured OpenAPI bean
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local development server")
                ))
                .info(new Info()
                        .title("CO2 Emission Calculation API")
                        .description("REST API for estimating CO2 emissions based on shipment weight, distance, and transport mode. "
                                + "Designed to work well in RapidAPI marketplaces with clear headers, examples, and predictable JSON errors.")
                        .version("1.0.2")
                        .contact(new Contact()
                                .name("CO2 API Team")
                                .email("support@co2api.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .components(new Components()
                        .addSecuritySchemes("ApiKeyAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .name("X-API-KEY")
                                        .description("Primary API key header. RapidAPI users can also send the key in X-RapidAPI-Key when the API is proxied through RapidAPI.")));
    }

    // No explicit GroupedOpenApi bean: rely on package scanning and the OpenAPI bean above.
}
