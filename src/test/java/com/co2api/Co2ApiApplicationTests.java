package com.co2api;

import com.co2api.dto.EmissionResponse;
import com.co2api.dto.ShipmentRequest;
import com.co2api.enums.TransportType;
import com.co2api.service.EmissionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration and unit tests for the CO2 Emission API. SpringBootTest loads
 * the full application context. AutoConfigureMockMvc sets up MockMvc for
 * HTTP-layer testing without a real server.
 */
@SpringBootTest
@AutoConfigureMockMvc
class Co2ApiApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmissionService emissionService;

    // ─── EmissionService unit tests ───────────────────────────────────────────

    @Test
    @DisplayName("EmissionService: diesel truck calculation is correct")
    void dieselTruckEmissionCalculation() {
        // 5000 kg = 5 t, 800 km, factor 0.11 → 5 × 800 × 0.11 = 440 kg CO2
        ShipmentRequest req = new ShipmentRequest();
        req.setWeightKg(5000.0);
        req.setDistanceKm(800.0);
        req.setTransportType(TransportType.DIESEL_TRUCK);

        EmissionResponse resp = emissionService.calculateEmissions(req);

        assertThat(resp.getTotalCo2Kg()).isCloseTo(440.0, within(0.001));
        assertThat(resp.getEmissionFactor()).isEqualTo(0.11);
        assertThat(resp.getTransportType()).isEqualTo(TransportType.DIESEL_TRUCK);
    }

    @Test
    @DisplayName("EmissionService: train emission is significantly lower than diesel truck")
    void trainEmissionIsLowerThanDieselTruck() {
        ShipmentRequest dieselReq = new ShipmentRequest();
        dieselReq.setWeightKg(10000.0);
        dieselReq.setDistanceKm(500.0);
        dieselReq.setTransportType(TransportType.DIESEL_TRUCK);

        ShipmentRequest trainReq = new ShipmentRequest();
        trainReq.setWeightKg(10000.0);
        trainReq.setDistanceKm(500.0);
        trainReq.setTransportType(TransportType.TRAIN);

        double dieselCo2 = emissionService.calculateEmissions(dieselReq).getTotalCo2Kg();
        double trainCo2 = emissionService.calculateEmissions(trainReq).getTotalCo2Kg();

        assertThat(trainCo2).isLessThan(dieselCo2);
    }

    @Test
    @DisplayName("EmissionService: flight has the highest emission factor")
    void flightHasHighestEmission() {
        ShipmentRequest req = new ShipmentRequest();
        req.setWeightKg(1000.0);
        req.setDistanceKm(1000.0);
        req.setTransportType(TransportType.FLIGHT);

        EmissionResponse resp = emissionService.calculateEmissions(req);

        // 1 t × 1000 km × 0.50 = 500 kg CO2
        assertThat(resp.getTotalCo2Kg()).isCloseTo(500.0, within(0.001));
    }

    // ─── Controller + Security integration tests ──────────────────────────────

    @Test
    @DisplayName("POST /api/v1/calculate returns 200 with valid API key")
    void calculateEndpointReturnsCo2WithValidKey() throws Exception {
        ShipmentRequest req = new ShipmentRequest();
        req.setWeightKg(5000.0);
        req.setDistanceKm(800.0);
        req.setTransportType(TransportType.DIESEL_TRUCK);

        mockMvc.perform(post("/api/v1/calculate")
                        .header("X-API-KEY", "changeme-replace-in-production")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCo2Kg").value(440.0))
                .andExpect(jsonPath("$.transportType").value("DIESEL_TRUCK"));
    }

    @Test
    @DisplayName("POST /api/v1/calculate returns 401 without API key")
    void calculateEndpointReturns401WithoutKey() throws Exception {
        ShipmentRequest req = new ShipmentRequest();
        req.setWeightKg(5000.0);
        req.setDistanceKm(800.0);
        req.setTransportType(TransportType.DIESEL_TRUCK);

        mockMvc.perform(post("/api/v1/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/v1/calculate accepts RapidAPI-style API key header")
    void calculateEndpointAcceptsRapidApiKeyHeader() throws Exception {
        ShipmentRequest req = new ShipmentRequest();
        req.setWeightKg(5000.0);
        req.setDistanceKm(800.0);
        req.setTransportType(TransportType.DIESEL_TRUCK);

        mockMvc.perform(post("/api/v1/calculate")
                        .header("X-RapidAPI-Key", "changeme-replace-in-production")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCo2Kg").value(440.0));
    }

    @Test
    @DisplayName("POST /api/v1/calculate returns 401 with wrong API key")
    void calculateEndpointReturns401WithWrongKey() throws Exception {
        ShipmentRequest req = new ShipmentRequest();
        req.setWeightKg(5000.0);
        req.setDistanceKm(800.0);
        req.setTransportType(TransportType.DIESEL_TRUCK);

        mockMvc.perform(post("/api/v1/calculate")
                        .header("X-API-KEY", "wrong-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/v1/calculate returns 400 for invalid request body")
    void calculateEndpointReturns400ForInvalidBody() throws Exception {
        // Missing required fields — should trigger Bean Validation
        String invalidJson = "{}";

        mockMvc.perform(post("/api/v1/calculate")
                        .header("X-API-KEY", "changeme-replace-in-production")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.path").value("/api/v1/calculate"));
    }

    @Test
    @DisplayName("POST /api/v1/calculate returns standardized 401 error payload")
    void calculateEndpointReturnsStandardized401Error() throws Exception {
        ShipmentRequest req = new ShipmentRequest();
        req.setWeightKg(5000.0);
        req.setDistanceKm(800.0);
        req.setTransportType(TransportType.DIESEL_TRUCK);

        mockMvc.perform(post("/api/v1/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.path").value("/api/v1/calculate"));
    }

    @Test
    @DisplayName("Application context loads successfully")
    void contextLoads() {
        // If the Spring context fails to start, this test will fail automatically
        assertThat(emissionService).isNotNull();
    }
}
