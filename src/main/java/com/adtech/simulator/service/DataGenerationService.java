package com.adtech.simulator.service;

import com.adtech.simulator.model.AdEvent;
import com.adtech.simulator.model.Campaign;
import com.adtech.simulator.repository.CampaignRepository;
import com.adtech.simulator.repository.ClickHouseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@EnableAsync
public class DataGenerationService implements CommandLineRunner {

    private final CampaignRepository campaignRepository;
    private final ClickHouseRepository clickHouseRepository;
    private final Random random = new Random();

    public DataGenerationService(CampaignRepository campaignRepository, ClickHouseRepository clickHouseRepository) {
        this.campaignRepository = campaignRepository;
        this.clickHouseRepository = clickHouseRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Initializing Database Schemas...");
        clickHouseRepository.createTableIfNotExists();

        System.out.println("Populating Campaigns...");
        if (campaignRepository.count() == 0) {
            campaignRepository.saveAll(List.of(
                    new Campaign(1L, "Summer Sale", 5000.0, "https://via.placeholder.com/150", "ACTIVE"),
                    new Campaign(2L, "Winter Clearance", 3000.0, "https://via.placeholder.com/150", "ACTIVE"),
                    new Campaign(3L, "Spring Collection", 8000.0, "https://via.placeholder.com/150", "ACTIVE"),
                    new Campaign(4L, "Autumn Deals", 4000.0, "https://via.placeholder.com/150", "PAUSED"),
                    new Campaign(5L, "Holiday Special", 10000.0, "https://via.placeholder.com/150", "ACTIVE")
            ));
        }

        System.out.println("Starting Background Data Generation for 1 Million Events...");
        generateAndInsertEventsAsync();
    }

    @Async
    public void generateAndInsertEventsAsync() {
        int totalEvents = 1_000_000;
        int batchSize = 10_000;
        
        String[] browsers = {"Chrome", "Firefox", "Safari", "Edge"};
        String[] countries = {"US", "UK", "CA", "DE", "FR", "IN", "JP"};
        Long[] campaignIds = {1L, 2L, 3L, 4L, 5L};

        for (int i = 0; i < totalEvents; i += batchSize) {
            List<AdEvent> batch = new ArrayList<>(batchSize);
            for (int j = 0; j < batchSize; j++) {
                LocalDateTime eventTime = LocalDateTime.now().minusDays(random.nextInt(7)).minusHours(random.nextInt(24));
                String eventType = random.nextDouble() > 0.9 ? "click" : "view"; // ~10% CTR
                
                AdEvent event = new AdEvent(
                        eventTime.toLocalDate(),
                        eventTime,
                        campaignIds[random.nextInt(campaignIds.length)],
                        UUID.randomUUID().toString(),
                        eventType,
                        browsers[random.nextInt(browsers.length)],
                        countries[random.nextInt(countries.length)]
                );
                batch.add(event);
            }
            clickHouseRepository.batchInsertEvents(batch);
            if (i % 100_000 == 0 && i > 0) {
                System.out.println("Inserted " + i + " events into ClickHouse.");
            }
        }
        System.out.println("Successfully inserted 1,000,000 events into ClickHouse!");
    }
}
