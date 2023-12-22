package com.example.emailaggregator.gui.controller;

import com.example.emailaggregator.email.EmailConfiguration;
import com.example.emailaggregator.email.EmailService;
import com.example.emailaggregator.model.EmailAccount;
import com.example.emailaggregator.model.EmailShortDescription;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
public class MainController {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailConfiguration emailConfiguration;

    private boolean isAccountsShowed = false;
    private List<EmailAccount> accounts = new ArrayList<>();
    private List<EmailShortDescription> emails = new ArrayList<>();
    private int gachiButtonClickCount = 0;

    @FXML
    private VBox accountsVBox;

    @FXML
    private Button showHideAccountsButton;

    @FXML
    private VBox emailsVbox;

    @FXML
    private Label messageLabel;

    @FXML
    private WebView messageView;
    @FXML
    private VBox foldersVBox;
    @FXML
    private Label noMessagesInFolderLabel;

    public void initialize() {
        accounts.addAll(emailConfiguration.getEmailAccounts());
        messageView.setContextMenuEnabled(false);
        emailConfiguration.switchToAccount(accounts.get(0));
        ((Label)showHideAccountsButton.getGraphic()).setText(accounts.get(0).getLogin());
        messageLabel.setOnMouseClicked(args -> {
                    if (gachiButtonClickCount++ >= 3) {
                        messageView.setVisible(true);
                        messageLabel.setVisible(false);
                        messageView.getEngine().load("https://soundcloud.com/uinston-cherchill/sets/gachi-version");
                    }
                }
        );
        loadFolders();
    }

    private void loadFolders() {
        emailService.getFolders().forEach(folder -> {
            var button = new Button();
            button.setText(folder.getName());
            button.setOnMouseClicked(ev -> loadEmails(folder.getFullName()));
            foldersVBox.getChildren().add(button);
        });
    }

    private void loadEmails(String folder) {
        emailsVbox.getChildren().clear();
        emails.clear();
        emails.addAll(emailService.read(folder));
        noMessagesInFolderLabel.setVisible(emails.isEmpty());
        for (EmailShortDescription email : emails) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/email_short_description.fxml"));
                loader.setControllerFactory(context::getBean);
                Node node = loader.load();

                EmailShortDescriptionController controller = loader.getController();
                controller.setEmail(email);
                node.setOnMouseClicked(a -> {
                    messageView.setVisible(true);
                    messageLabel.setVisible(false);
                    messageView.getEngine().loadContent(email.getHtml());
                });
                emailsVbox.getChildren().add(node);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        log.error("Folder {} loaded", folder);
    }

    @FXML
    private void showHideAccounts() {
        isAccountsShowed = !isAccountsShowed;
        if (!isAccountsShowed) {
            showHideAccountsButton.setText("▼");
            accounts.forEach(account -> {
                final Button accountButton = new Button(account.getLogin());
                accountButton.setOnMouseClicked(args -> {
                    emailConfiguration.switchToAccount(account);
                    foldersVBox.getChildren().clear();
                    emailsVbox.getChildren().clear();
                    noMessagesInFolderLabel.setVisible(true);
                    messageView.setVisible(false);
                    messageLabel.setVisible(true);
                    ((Label)showHideAccountsButton.getGraphic()).setText(account.getLogin());
                    loadFolders();
                });
                accountsVBox.getChildren().add(accountButton);
            });
        } else {
            showHideAccountsButton.setText("▶");
            accountsVBox.getChildren().clear();
        }
    }
}
