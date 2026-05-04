package com.co2api;

import com.co2api.dto.BatchRequest;
import com.co2api.dto.CompareRequest;
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

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration and unit tests for the CO2 Emission API.
 *
 * @SpringBootTest loads the full application context.
 * @AutoConfigureMockMvc sets up MockMvc for HTTP-layer testing without a real server.
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

    private static final String API_KEY = "changeme-replace-in-production";

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
                        .header("X-API-KEY", API_KEY)
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
                        .header("X-API-KEY", API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Application context loads successfully")
    void contextLoads() {
        // If the Spring context fails to start, this test will fail automatically
        assertThat(emissionService).isNotNull();
    }

    // ─── GET /api/v1/transport-types tests ────────────────────────────────────

    @Test
    @DisplayName("GET /api/v1/transport-types returns 200 with valid API key")
    void transportTypesReturns200WithValidKey() throws Exception {
        mockMvc.perform(get("/api/v1/transport-types")
                        .header("X-API-KEY", API_KEY))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET /api/v1/transport-types includes DIESEL_TRUCK with correct factor")
    void transportTypesIncludesDieselTruck() throws Exception {
        mockMvc.perform(get("/api/v1/transport-types")
                        .header("X-API-KEY", API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.type == 'DIESEL_TRUCK')].emissionFactor").value(0.11))
                .andExpect(jsonPath("$[?(@.type == 'DIESEL_TRUCK')].unit").value("kgCO2_per_tkm"));
    }

    @Test
    @DisplayName("GET /api/v1/transport-types returns 401 without API key")
    void transportTypesReturns401WithoutKey() throws Exception {
        mockMvc.perform(get("/api/v1/transport-types"))
                .andExpect(status().isUnauthorized());
    }

    // ─── POST /api/v1/calculate/batch tests ───────────────────────────────────

    @Test
    @DisplayName("POST /api/v1/calculate/batch returns 200 and correct totalCo2Kg")
    void batchCalculateReturns200WithCorrectTotal() throws Exception {
        ShipmentRequest s1 = new ShipmentRequest();
        s1.setWeightKg(5000.0);
        s1.setDistanceKm(800.0);
        s1.setTransportType(TransportType.DIESEL_TRUCK); // 5*800*0.11 = 440

        ShipmentRequest s2 = new ShipmentRequest();
        s2.setWeightKg(1200.0);
        s2.setDistanceKm(300.0);
        s2.setTransportType(TransportType.TRAIN); // 1.2*300*0.02 = 7.2

        BatchRequest batchRequest = new BatchRequest();
        batchRequest.setShipments(List.of(s1, s2));

        mockMvc.perform(post("/api/v1/calculate/batch")
                        .header("X-API-KEY", API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(batchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCo2Kg").value(447.2))
                .andExpect(jsonPath("$.results.length()").value(2))
                .andExpect(jsonPath("$.results[0].totalCo2Kg").value(440.0))
                .andExpect(jsonPath("$.results[1].totalCo2Kg").value(7.2));
    }

    @Test
    @DisplayName("POST /api/v1/calculate/batch returns 400 for empty shipments list")
    void batchCalculateReturns400ForEmptyList() throws Exception {
        BatchRequest batchRequest = new BatchRequest();
        batchRequest.setShipments(List.of());

        mockMvc.perform(post("/api/v1/calculate/batch")
                        .header("X-API-KEY", API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(batchRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/calculate/batch returns 400 when exceeding max batch size")
    void batchCalculateReturns400WhenExceedingMaxSize() throws Exception {
        ShipmentRequest shipment = new ShipmentRequest();
        shipment.setWeightKg(1000.0);
        shipment.setDistanceKm(100.0);
        shipment.setTransportType(TransportType.TRAIN);

        List<ShipmentRequest> oversizedList = new ArrayList<>();
        for (int i = 0; i <= BatchRequest.MAX_BATCH_SIZE; i++) {
            oversizedList.add(shipment);
        }

        BatchRequest batchRequest = new BatchRequest();
        batchRequest.setShipments(oversizedList);

        mockMvc.perform(post("/api/v1/calculate/batch")
                        .header("X-API-KEY", API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(batchRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/calculate/batch returns 401 without API key")
    void batchCalculateReturns401WithoutKey() throws Exception {
        ShipmentRequest s1 = new ShipmentRequest();
        s1.setWeightKg(1000.0);
        s1.setDistanceKm(100.0);
        s1.setTransportType(TransportType.TRAIN);

        BatchRequest batchRequest = new BatchRequest();
        batchRequest.setShipments(List.of(s1));

        mockMvc.perform(post("/api/v1/calculate/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(batchRequest)))
                .andExpect(status().isUnauthorized());
    }

    // ─── POST /api/v1/compare tests ───────────────────────────────────────────

    @Test
    @DisplayName("POST /api/v1/compare returns 200 and correct betterOption")
    void compareReturns200WithCorrectBetterOption() throws Exception {
        ShipmentRequest optA = new ShipmentRequest();
        optA.setWeightKg(5000.0);
        optA.setDistanceKm(800.0);
        optA.setTransportType(TransportType.DIESEL_TRUCK); // 440 kg CO2

        ShipmentRequest optB = new ShipmentRequest();
        optB.setWeightKg(5000.0);
        optB.setDistanceKm(800.0);
        optB.setTransportType(TransportType.TRAIN); // 80 kg CO2

        CompareRequest compareRequest = new CompareRequest();
        compareRequest.setOptionA(optA);
        compareRequest.setOptionB(optB);

        mockMvc.perform(post("/api/v1/compare")
                        .header("X-API-KEY", API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(compareRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.optionA.totalCo2Kg").value(440.0))
                .andExpect(jsonPath("$.optionB.totalCo2Kg").value(80.0))
                .andExpect(jsonPath("$.differenceKg").value(360.0))
                .andExpect(jsonPath("$.betterOption").value("OPTION_B"));
    }

    @Test
    @DisplayName("POST /api/v1/compare returns EQUAL when both options have same emissions")
    void compareReturnsEqualWhenSameEmissions() throws Exception {
        ShipmentRequest shipment = new ShipmentRequest();
        shipment.setWeightKg(5000.0);
        shipment.setDistanceKm(800.0);
        shipment.setTransportType(TransportType.TRAIN);

        CompareRequest compareRequest = new CompareRequest();
        compareRequest.setOptionA(shipment);
        compareRequest.setOptionB(shipment);

        mockMvc.perform(post("/api/v1/compare")
                        .header("X-API-KEY", API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(compareRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.betterOption").value("EQUAL"))
                .andExpect(jsonPath("$.differenceKg").value(0.0));
    }

    @Test
    @DisplayName("POST /api/v1/compare returns 400 when optionA is missing")
    void compareReturns400WhenOptionAMissing() throws Exception {
        ShipmentRequest optB = new ShipmentRequest();
        optB.setWeightKg(5000.0);
        optB.setDistanceKm(800.0);
        optB.setTransportType(TransportType.TRAIN);

        CompareRequest compareRequest = new CompareRequest();
        compareRequest.setOptionB(optB);

        mockMvc.perform(post("/api/v1/compare")
                        .header("X-API-KEY", API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(compareRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/compare returns 401 without API key")
    void compareReturns401WithoutKey() throws Exception {
        ShipmentRequest shipment = new ShipmentRequest();
        shipment.setWeightKg(5000.0);
        shipment.setDistanceKm(800.0);
        shipment.setTransportType(TransportType.DIESEL_TRUCK);

        CompareRequest compareRequest = new CompareRequest();
        compareRequest.setOptionA(shipment);
        compareRequest.setOptionB(shipment);

        mockMvc.perform(post("/api/v1/compare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(compareRequest)))
                .andExpect(status().isUnauthorized());
    }
}
