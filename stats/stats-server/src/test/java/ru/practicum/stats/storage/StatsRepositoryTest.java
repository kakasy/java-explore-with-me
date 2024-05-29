package ru.practicum.stats.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.entity.EndpointHit;


import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class StatsRepositoryTest {

    @Autowired
    StatsRepository statsRepository;

    @BeforeEach
    void startUp() {
        EndpointHit endpointHit1 = EndpointHit.builder()
                .app("ewm-main-service")
                .uri("/events/3")
                .ip("192.168.0.3")
                .times(LocalDateTime.of(2023, 1, 1, 15, 14, 53))
                .build();

        EndpointHit endpointHit2 = EndpointHit.builder()
                .app("ewm-main-service")
                .uri("/events/2")
                .ip("192.168.0.3")
                .times(LocalDateTime.of(2023, 2, 2, 15, 14, 53))
                .build();

        statsRepository.save(endpointHit1);
        statsRepository.save(endpointHit2);
    }

    @AfterEach
    void tearDown() {

        statsRepository.deleteAll();

    }

    @Test
    void getUniqueByTimes_whenValidParam_thenReturnList() {

        List<ViewStatsDto> actualList = statsRepository.getUniqueByTimes(
                LocalDateTime.of(2023, 1, 1, 15, 0, 53),
                LocalDateTime.of(2023, 11, 11, 11, 0, 53));

        assertEquals(2, actualList.size());

        assertEquals("ewm-main-service", actualList.get(0).getApp());
        assertEquals("/events/2", actualList.get(0).getUri());
        assertEquals(1, actualList.get(0).getHits());

        assertEquals("ewm-main-service", actualList.get(1).getApp());
        assertEquals("/events/3", actualList.get(1).getUri());
        assertEquals(1, actualList.get(1).getHits());
    }

    @Test
    void getUniqueByTimesAndList_whenValidParam_thenReturnList() {

        List<ViewStatsDto> actualList = statsRepository.getUniqueByTimesAndList(
                List.of("/events/2"),
                LocalDateTime.of(2023, 1, 1, 15, 0, 53),
                LocalDateTime.of(2023, 11, 11, 11, 0, 53));

        assertEquals(1, actualList.size());
        assertEquals("ewm-main-service", actualList.get(0).getApp());
        assertEquals("/events/2", actualList.get(0).getUri());
        assertEquals(1, actualList.get(0).getHits());
    }

    @Test
    void getAllByTimes_whenValidParam_thenReturnList() {

        List<ViewStatsDto> actualList = statsRepository.getAllByTime(
                LocalDateTime.of(2023, 1, 1, 15, 0, 53),
                LocalDateTime.of(2023, 11, 11, 11, 0, 53));

        assertEquals(2, actualList.size());

        assertEquals("ewm-main-service", actualList.get(0).getApp());
        assertEquals("/events/2", actualList.get(0).getUri());
        assertEquals(1, actualList.get(0).getHits());

        assertEquals("ewm-main-service", actualList.get(1).getApp());
        assertEquals("/events/3", actualList.get(1).getUri());
        assertEquals(1, actualList.get(1).getHits());
    }

    @Test
    void getAllByTimeAndList_whenValidParam_thenReturnList() {

        List<ViewStatsDto> actualList = statsRepository.getAllByTimeAndList(
                List.of("/events/2"),
                LocalDateTime.of(2023, 1, 1, 15, 0, 53),
                LocalDateTime.of(2023, 11, 11, 11, 0, 53));

        assertEquals(1, actualList.size());
        assertEquals("ewm-main-service", actualList.get(0).getApp());
        assertEquals("/events/2", actualList.get(0).getUri());
        assertEquals(1, actualList.get(0).getHits());
    }

    @Test
    void getUniqueByTimes_whenNotFoundByTimes_thenReturnEmptyList() {

        List<ViewStatsDto> actualList = statsRepository.getUniqueByTimes(
                LocalDateTime.of(2021, 1, 1, 15, 0, 53),
                LocalDateTime.of(2021, 11, 11, 11, 0, 53));

        assertEquals(0, actualList.size());
    }

    @Test
    void getUniqueByTimesAndList_whenNotFoundByTimes_thenReturnEmptyList() {

        List<ViewStatsDto> actualList = statsRepository.getUniqueByTimesAndList(
                List.of("/events/2"),
                LocalDateTime.of(2021, 1, 1, 15, 0, 53),
                LocalDateTime.of(2021, 11, 11, 11, 0, 53));

        assertEquals(0, actualList.size());
    }

    @Test
    void getAllByTimes_whenNotFoundByTimes_thenReturnEmptyList() {

        List<ViewStatsDto> actualList = statsRepository.getAllByTime(
                LocalDateTime.of(2021, 1, 1, 15, 0, 53),
                LocalDateTime.of(2021, 11, 11, 11, 0, 53));

        assertEquals(0, actualList.size());
    }

    @Test
    void getAllByTimeAndList_whenNotFoundByTimes_thenReturnEmptyList() {

        List<ViewStatsDto> actualList = statsRepository.getAllByTimeAndList(
                List.of("/events/2"),
                LocalDateTime.of(2021, 1, 1, 15, 0, 53),
                LocalDateTime.of(2021, 11, 11, 11, 0, 53));

        assertEquals(0, actualList.size());
    }

    @Test
    void getUniqueByTimesAndList_whenNotFoundByUri_thenReturnEmptyList() {

        List<ViewStatsDto> actualList = statsRepository.getUniqueByTimesAndList(
                List.of("/events/99"),
                LocalDateTime.of(2023, 1, 1, 15, 0, 53),
                LocalDateTime.of(2023, 11, 11, 11, 0, 53));

        assertEquals(0, actualList.size());
    }

    @Test
    void getAllByTimeAndList_whenNotFoundByUri_thenReturnEmptyList() {

        List<ViewStatsDto> actualList = statsRepository.getAllByTimeAndList(
                List.of("/events/99"),
                LocalDateTime.of(2023, 1, 1, 15, 0, 53),
                LocalDateTime.of(2023, 11, 11, 11, 0, 53));

        assertEquals(0, actualList.size());
    }
}
