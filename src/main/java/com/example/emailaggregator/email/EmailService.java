package com.example.emailaggregator.email;

import com.example.emailaggregator.model.EmailShortDescription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private static final int MESSAGES_LIMIT = 10;

    private final EmailConfiguration emailConfiguration;

    public String getSelectedAccountName() {
        try {
            var store = emailConfiguration.getStore();
            var folder = store.getFolder("inbox");
            folder.open(Folder.READ_ONLY);
            var message = folder.getMessages(folder.getMessageCount() - 1, folder.getMessageCount())[0];
            var name = Arrays.toString(message.getRecipients(Message.RecipientType.TO));
            log.error(name);
            folder.close();
            store.close();
            return name;
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Folder> getFolders() {
        try {
            var store = emailConfiguration.getStore();
            var folders = Arrays.stream(store.getDefaultFolder().list("*")).toList();
            store.close();
            return folders;
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public List<EmailShortDescription> read(String folderName) {
        try {
            var store = emailConfiguration.getStore();
            Folder folder = store.getFolder(folderName);
            folder.open(Folder.READ_ONLY);
            Message[] messages = getMessages(folder);

            var mailDescriptions = new ArrayList<>(Arrays.stream(messages)
                    .map(EmailMapper::toEmailShortDescription)
                    .toList()
            );

            Collections.reverse(mailDescriptions);

            folder.close(true);
            store.close();
            return mailDescriptions;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private Message[] getMessages(Folder inbox) throws MessagingException {
        return inbox.getMessageCount() > MESSAGES_LIMIT ?
                inbox.getMessages(inbox.getMessageCount() - MESSAGES_LIMIT, inbox.getMessageCount()) : inbox.getMessages();
    }

}
