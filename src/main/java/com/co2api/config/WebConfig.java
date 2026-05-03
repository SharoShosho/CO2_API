package com.co2api.config;

import com.co2api.security.ApiKeyInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC configuration class.
 *
 * Registers the ApiKeyInterceptor so that Spring MVC applies it to every
 * incoming HTTP request. Additional interceptors (e.g. logging, rate limiting)
 * can be registered here in the future.
 *
 * @see ApiKeyInterceptor
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    /** The API key interceptor bean, injected via constructor (Lombok @RequiredArgsConstructor). */
    private final ApiKeyInterceptor apiKeyInterceptor;

    /**
     * Adds the ApiKeyInterceptor to the interceptor chain.
     *
     * The interceptor is applied to all paths ("/**").  Swagger paths are
     * excluded inside the interceptor itself so the logic stays in one place.
     *
     * @param registry Spring's interceptor registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiKeyInterceptor)
                .addPathPatterns("/**"); // Apply to all paths; Swagger exclusion handled in the interceptor
    }
}
