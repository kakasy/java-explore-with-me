package ru.practicum.stats.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.service.StatsServiceImpl;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class StatsControllerTest {

    @Mock
    private StatsServiceImpl service;
    @InjectMocks
    private StatsController controller;
    private MockMvc mvc;
    private EndpointHitDto dto;

    @Autowired
    private final ObjectMapper objectMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    @BeforeEach
    void startUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        dto = EndpointHitDto.builder()
                .app("ewm-main-service")
                .uri("/events/2")
                .ip("192.168.0.2")
                .timestamp(LocalDateTime.of(2023, 1, 1, 15, 14, 53))
                .build();
    }

    @SneakyThrows
    @Test
    void addHit_whenValidDto_thenCreated() {

        mvc.perform(post("/hit")
                        .content(objectMapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(service, times(1)).addHit(dto);
    }

    @SneakyThrows
    @Test
    void addHit_whenEmptyApp_thenReturnBadRequest() {

        dto = EndpointHitDto.builder()
                .app("")
                .build();

       mvc.perform(post("/hit")
                        .content(objectMapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verifyNoInteractions(service);
    }

    @SneakyThrows
    @Test
    void addHit_whenInvalidApp_thenReturnBadRequest() {

        dto = EndpointHitDto.builder()
                .app("daaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaamn " +
                        "thiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiis " +
                        "biiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiig " +
                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaap")
                .build();

        mvc.perform(post("/hit")
                        .content(objectMapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verifyNoInteractions(service);
    }

    @SneakyThrows
    @Test
    void addHit_whenEmptyUri_thenReturnBadRequest() {

        dto = EndpointHitDto.builder()
                .uri("")
                .build();

        mvc.perform(post("/hit")
                        .content(objectMapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verifyNoInteractions(service);
    }

    @SneakyThrows
    @Test
    void addHit_whenInvalidUri_thenReturnBadRequest() {

        dto = EndpointHitDto.builder()
                .uri("daaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaamn " +
                        "thiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiis " +
                        "biiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiig " +
                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaap")
                .build();

        mvc.perform(post("/hit")
                        .content(objectMapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verifyNoInteractions(service);
    }

    @SneakyThrows
    @Test
    void addHit_whenEmptyIp_thenReturnBadRequest() {

        dto = EndpointHitDto.builder()
                .ip("")
                .build();

        mvc.perform(post("/hit")
                        .content(objectMapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verifyNoInteractions(service);
    }

    @SneakyThrows
    @Test
    void addHit_whenInvalidIp_thenReturnBadRequest() {

        dto = EndpointHitDto.builder()
                .app("ip is illusion")
                .build();

        mvc.perform(post("/hit")
                        .content(objectMapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verifyNoInteractions(service);
    }

    @SneakyThrows
    @Test
    void addHit_whenEmptyTimestamp_thenReturnBadRequest() {

        dto = EndpointHitDto.builder()
                .timestamp(null)
                .build();

        mvc.perform(post("/hit")
                        .content(objectMapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verifyNoInteractions(service);
    }

    @SneakyThrows
    @Test
    void getStats_whenValidParam_returnList() {

        String start = "2021-01-01 12:12:12";
        String end = "2027-02-02 13:13:13";

        ViewStatsDto dto = ViewStatsDto.builder()
                .app("ewm-main-service")
                .uri("/events/3")
                .hits(10L)
                .build();

        when(service.getStats(any(LocalDateTime.class), any(LocalDateTime.class), anyList(), anyBoolean()))
                .thenReturn(List.of(dto));

        String result = mvc.perform(get("/stats")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("start", start)
                        .param("end", end)
                        .param("uris", "null")
                        .param("unique", "false")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();


        verify(service, times(1)).getStats(any(LocalDateTime.class),
                any(LocalDateTime.class), anyList(), anyBoolean());

        assertEquals(objectMapper.writeValueAsString(List.of(dto)), result);
    }

    @SneakyThrows
    @Test
    void getStats_whenInvalidDataForm_returnBadRequest() {

        String start = "2024.04.04 04:04:04";
        String end = "2021-01-01 01:01:01";

        mvc.perform(get("/stats")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("start", start)
                        .param("end", end)
                        .param("uris", "null")
                        .param("unique", "false")
                )
                .andDo(print())
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verifyNoInteractions(service);
    }

}
