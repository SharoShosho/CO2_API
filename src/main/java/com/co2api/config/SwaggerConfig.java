package com.co2api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI / Swagger UI configuration.
 *
 * <p>Exposes an {@link OpenAPI} bean that sets API metadata and registers an
 * API key security scheme so Swagger UI shows an "Authorize" button.
 *
 * <p>A {@link GroupedOpenApi} bean groups all v1 paths under the label "v1".
 * When a v2 is introduced, add a matching {@code v2Api()} bean and a new
 * set of controllers under {@code /api/v2/}.
 *
 * <ul>
 *   <li>Swagger UI: {@code http://localhost:8080/swagger-ui.html}</li>
 *   <li>OpenAPI JSON: {@code http://localhost:8080/v3/api-docs}</li>
 * </ul>
 */
@Configuration
public class SwaggerConfig {

    /**
     * Builds and returns the OpenAPI metadata and security scheme definition.
     * The "ApiKeyAuth" security scheme matches the name used in the
     * {@code @SecurityRequirement} annotations on the controller methods.
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
                        .version(ApiConstants.VERSION)
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

    /**
     * Groups all v1 endpoints under the Swagger UI "v1" dropdown.
     * Add a {@code v2Api()} bean here when a second API version is introduced.
     *
     * @return GroupedOpenApi covering /api/v1/**
     */
    @Bean
    public GroupedOpenApi v1Api() {
        return GroupedOpenApi.builder()
                .group("v1")
                .pathsToMatch("/api/v1/**")
                .build();
    }
}
