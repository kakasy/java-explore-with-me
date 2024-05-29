package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.entity.EndpointHit;
import ru.practicum.stats.mapper.StatsMapper;
import ru.practicum.stats.storage.StatsRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Override
    public void addHit(EndpointHitDto endpointHitDto) {

        EndpointHit added = statsRepository.save(StatsMapper.toEndpointHit(endpointHitDto));
        log.info("Просмотр добавлен в БД c id: {}", added.getId());

    }

    @Override
    @Transactional(readOnly = true)
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uriList, Boolean isUnique) {

        List<ViewStatsDto> endpointHits = new ArrayList<>();

        if (isUnique && uriList == null) {

            endpointHits = statsRepository.getUniqueByTimes(start, end);

        } else if (isUnique && uriList != null) {

            endpointHits = statsRepository.getUniqueByTimesAndList(uriList, start, end);

        } else if (!isUnique && uriList == null) {

            endpointHits = statsRepository.getAllByTime(start, end);

        } else if (!isUnique && uriList != null) {

            endpointHits = statsRepository.getAllByTimeAndList(uriList, start, end);

        }
        log.info("Список из {} объектов", endpointHits.size());
        return endpointHits;
    }
}

