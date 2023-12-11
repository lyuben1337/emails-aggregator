package com.example.emailaggregator.gui;

import com.example.emailaggregator.gui.controller.MainController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class StageInitializer implements ApplicationListener<EmailAggregatorGui.StageReadyEvent> {
    private final ApplicationContext context;

    @Override
    public void onApplicationEvent(EmailAggregatorGui.StageReadyEvent event) {
        var stage = event.getStage();

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/main_view.fxml"));
            fxmlLoader.setControllerFactory(context::getBean);
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
