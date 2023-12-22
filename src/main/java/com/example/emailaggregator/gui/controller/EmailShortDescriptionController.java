package com.example.emailaggregator.gui.controller;


import com.example.emailaggregator.model.EmailShortDescription;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
public class EmailShortDescriptionController {
    @FXML
    private Label subject;
    @FXML
    private Label content;
    @FXML
    private Label sender;
    @FXML
    private Label date;

    public void setEmail(EmailShortDescription email) {
        content.setText(email.getContentString());
        subject.setText(email.getSubject());
        sender.setText(email.getSender());
        date.setText(email.getDate().toString());
    }
}
