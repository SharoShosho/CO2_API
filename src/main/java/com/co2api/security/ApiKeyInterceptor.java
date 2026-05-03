package com.co2api.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * HTTP interceptor that enforces API key authentication for all incoming requests.
 *
 * Reads the configured API key from application properties (api.key) and compares it
 * against the X-API-KEY header in each request.
 *
 * Requests to the Swagger UI and OpenAPI documentation paths are allowed through
 * without a key so that the API can still be explored in a browser.
 *
 * To rotate the API key, update the value in application.properties (or as an
 * environment variable API_KEY) and restart the application — no code changes needed.
 */
@Component
public class ApiKeyInterceptor implements HandlerInterceptor {

    /** Name of the HTTP header that must carry the API key. */
    private static final String API_KEY_HEADER = "X-API-KEY";

    /**
     * The valid API key, injected from the 'api.key' property in application.properties.
     * Can be overridden at runtime via the API_KEY environment variable.
     */
    @Value("${api.key}")
    private String validApiKey;

    /**
     * Intercepts every incoming HTTP request before it reaches the controller.
     *
     * Logic:
     *  1. Allow requests to Swagger / OpenAPI paths without a key.
     *  2. Extract the X-API-KEY header from the request.
     *  3. If the header is missing or the key is invalid, return 401 Unauthorized.
     *  4. Otherwise allow the request to proceed.
     *
     * @param request  the incoming HTTP request
     * @param response the HTTP response (used to send 401 if key is invalid)
     * @param handler  the handler (controller method) that would process the request
     * @return true  if the request should proceed, false if it has been rejected
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String requestPath = request.getRequestURI();

        // Allow Swagger UI and OpenAPI spec endpoints without an API key
        if (isSwaggerPath(requestPath)) {
            return true;
        }

        // Retrieve the API key from the request header
        String providedKey = request.getHeader(API_KEY_HEADER);

        // Reject the request if no key was provided or the key does not match
        if (providedKey == null || !providedKey.equals(validApiKey)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(
                "{\"error\": \"Unauthorized\", \"message\": \"Missing or invalid X-API-KEY header\"}"
            );
            return false;
        }

        // Key is valid — allow the request to proceed to the controller
        return true;
    }

    /**
     * Checks whether the request path targets Swagger UI or OpenAPI documentation.
     * These paths are publicly accessible so developers can explore the API.
     *
     * @param path the URI path of the incoming request
     * @return true if the path is a Swagger/OpenAPI path
     */
    private boolean isSwaggerPath(String path) {
        return path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.equals("/swagger-ui.html");
    }
}
