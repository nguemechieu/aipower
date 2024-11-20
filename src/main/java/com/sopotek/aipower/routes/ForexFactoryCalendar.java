package com.sopotek.aipower.routes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ForexFactoryCalendar {

    @JsonProperty("events")
    private List<ForexEvent> events;

    // Getters and Setters




    @Setter
    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ForexEvent {

        @JsonProperty("title")
        private String title;
        @JsonProperty("country")
        private String country;
        @JsonProperty("event")
        private String event;
        @JsonProperty("impact")
        private String impact;

        public ForexEvent() {
            Instant tim = Instant.parse(date);
            if (!tim.isBefore(
                    Instant.now()
            )){
                event=this.title;
                time= String.valueOf( Date.from(tim));

            }
        }

        @Override
        public String toString() {
            return "ForexEvent{" +
                    "country='" + country + '\'' +

                    ", impact='" + impact + '\'' +

                    ", date='" + date + '\'' +
                    ", previous='" + previous + '\'' +
                    ", forecast='" + forecast + '\'' +
                    '}';
        }

        @JsonProperty("time")
        private String time;

        @JsonProperty("date")
        private String date;
        @JsonProperty("previous")
        private String previous;

        @JsonProperty("forecast")
        private String forecast;

        // Additional properties can be added as per the JSON structure

        // Getters and Setters

    }
}
