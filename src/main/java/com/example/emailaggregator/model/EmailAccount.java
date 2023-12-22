package com.example.emailaggregator.model;

import com.example.emailaggregator.email.EmailProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class EmailAccount {
    private EmailProvider provider;
    private String login;
    private String password;
}
