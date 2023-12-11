package com.example.emailaggregator;

import com.example.emailaggregator.gui.EmailAggregatorGui;
import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EmailAggregatorApplication {

    public static void main(String[] args) {
        Application.launch(EmailAggregatorGui.class, args);
    }

}
