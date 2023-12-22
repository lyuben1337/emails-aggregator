package com.example.emailaggregator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class EmailShortDescription {
    private String html;
    private String sender;
    private LocalDate date;
    private String subject;
    private String contentString;
}
