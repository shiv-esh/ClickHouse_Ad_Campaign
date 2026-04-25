package com.adtech.simulator.repository;

import com.adtech.simulator.model.AdEvent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Repository
public class ClickHouseRepository {

    private final JdbcTemplate jdbcTemplate;

    public ClickHouseRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createTableIfNotExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS ad_events (
                event_date Date,
                event_time DateTime,
                campaign_id UInt32,
                user_id String,
                event_type Enum8('view' = 1, 'click' = 2),
                browser LowCardinality(String),
                country LowCardinality(String)
            ) ENGINE = MergeTree()
            ORDER BY (campaign_id, event_time)
            """;
        jdbcTemplate.execute(sql);
    }

    public void batchInsertEvents(List<AdEvent> events) {
        String sql = "INSERT INTO ad_events (event_date, event_time, campaign_id, user_id, event_type, browser, country) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        jdbcTemplate.batchUpdate(sql, events, events.size(), (PreparedStatement ps, AdEvent event) -> {
            ps.setDate(1, java.sql.Date.valueOf(event.getEventDate()));
            ps.setTimestamp(2, Timestamp.valueOf(event.getEventTime()));
            ps.setLong(3, event.getCampaignId());
            ps.setString(4, event.getUserId());
            ps.setString(5, event.getEventType());
            ps.setString(6, event.getBrowser());
            ps.setString(7, event.getCountry());
        });
    }

    public List<Map<String, Object>> getLiveStats(String eventType, String country) {
        StringBuilder sql = new StringBuilder("""
            SELECT 
                campaign_id,
                countIf(event_type = 'view') AS total_views,
                countIf(event_type = 'click') AS total_clicks,
                uniqExact(user_id) AS unique_users
            FROM ad_events
            WHERE 1=1
            """);
        List<Object> params = new java.util.ArrayList<>();
        if (eventType != null && !eventType.isEmpty()) {
            sql.append(" AND event_type = ?");
            params.add(eventType);
        }
        if (country != null && !country.isEmpty()) {
            sql.append(" AND country = ?");
            params.add(country);
        }
        sql.append(" GROUP BY campaign_id");
        return jdbcTemplate.queryForList(sql.toString(), params.toArray());
    }

    public List<Map<String, Object>> getHourlyStats(Long campaignId, String eventType, String country) {
        StringBuilder sql = new StringBuilder("""
            SELECT 
                toStartOfHour(event_time) AS hour,
                count() AS clicks
            FROM ad_events
            WHERE campaign_id = ?
            """);
        List<Object> params = new java.util.ArrayList<>();
        params.add(campaignId);
        
        if (eventType != null && !eventType.isEmpty()) {
            sql.append(" AND event_type = ?");
            params.add(eventType);
        } else {
            sql.append(" AND event_type = 'click'");
        }
        
        if (country != null && !country.isEmpty()) {
            sql.append(" AND country = ?");
            params.add(country);
        }
        
        sql.append(" GROUP BY hour ORDER BY hour ASC");
        return jdbcTemplate.queryForList(sql.toString(), params.toArray());
    }
}
