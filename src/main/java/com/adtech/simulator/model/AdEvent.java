package com.adtech.simulator.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AdEvent {
    private LocalDate eventDate;
    private LocalDateTime eventTime;
    private Long campaignId;
    private String userId;
    private String eventType; // 'view' or 'click'
    private String browser;
    private String country;

    public AdEvent() {
    }

    public AdEvent(LocalDate eventDate, LocalDateTime eventTime, Long campaignId, String userId, String eventType, String browser, String country) {
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.campaignId = campaignId;
        this.userId = userId;
        this.eventType = eventType;
        this.browser = browser;
        this.country = country;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public LocalDateTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
