package com.adtech.simulator.controller;

import com.adtech.simulator.model.Campaign;
import com.adtech.simulator.repository.CampaignRepository;
import com.adtech.simulator.repository.ClickHouseRepository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class StatsController {

    private final CampaignRepository campaignRepository;
    private final ClickHouseRepository clickHouseRepository;

    public StatsController(CampaignRepository campaignRepository, ClickHouseRepository clickHouseRepository) {
        this.campaignRepository = campaignRepository;
        this.clickHouseRepository = clickHouseRepository;
    }

    @GetMapping("/campaigns")
    public List<Campaign> getCampaigns() {
        return campaignRepository.findAll();
    }

    @GetMapping("/stats/live")
    public List<Map<String, Object>> getLiveStats(
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) String country) {
        List<Campaign> campaigns = campaignRepository.findAll();
        Map<Long, String> campaignNames = campaigns.stream()
                .collect(Collectors.toMap(Campaign::getId, Campaign::getName));

        List<Map<String, Object>> liveStats = clickHouseRepository.getLiveStats(eventType, country);

        // Enrich ClickHouse stats with MongoDB campaign names
        return liveStats.stream().map(stat -> {
            Map<String, Object> enriched = new HashMap<>(stat);
            Long campaignId = ((Number) stat.get("campaign_id")).longValue();
            enriched.put("campaign_name", campaignNames.getOrDefault(campaignId, "Unknown Campaign"));
            return enriched;
        }).collect(Collectors.toList());
    }

    @GetMapping("/stats/hourly")
    public List<Map<String, Object>> getHourlyStats(
            @RequestParam Long campaignId,
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) String country) {
        return clickHouseRepository.getHourlyStats(campaignId, eventType, country);
    }
}
