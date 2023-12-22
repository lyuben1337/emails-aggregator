package com.example.emailaggregator.email;

import com.example.emailaggregator.exception.NoAccountsException;
import com.example.emailaggregator.model.EmailAccount;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Configuration
@Getter
@Setter
public class EmailConfiguration {
    private Properties properties = new Properties();
    private EmailAccount selectedAccount;
    private Session session;
    private List<EmailAccount> emailAccounts = new ArrayList<>(List.of(
            new EmailAccount(EmailProvider.GMAIL, "lyuben1337@gmail.com", "ebnd ctfl iypa ihvg"),
            new EmailAccount(EmailProvider.GMAIL, "vladyslavliubchyk@gmail.com", "qlud pijx dgzm qtsn"),
            new EmailAccount(EmailProvider.OUTLOOK, "vladyslav.liubchyk@outlook.com", "290682Microsoft_")
    ));

    public void setEmailClient(EmailProvider provider) {
        switch (provider) {
            case YAHOO -> properties.put("mail.imap.host", "imap.mail.yahoo.com");
            case OUTLOOK -> properties.put("mail.imap.host", "outlook.office365.com");
            default -> {
                properties.put("mail.imap.host", "imap.gmail.com");
                properties.put("mail.imap.ssl.trust", "imap.gmail.com");
            }
        }
    }

    public EmailConfiguration() {
        properties.put("mail.imap.port", "993");
        switchToAccount(emailAccounts.stream().findFirst().orElseThrow(NoAccountsException::new)
        );
    }

    public void addEmailAccount(EmailAccount account) {
        emailAccounts.add(account);
    }

    public List<EmailAccount> getEmailAccounts() {
        return emailAccounts;
    }

    public void switchToAccount(EmailAccount account) {
        selectedAccount = account;
        setEmailClient(account.getProvider());
        properties.put("mail.imap.user", account.getLogin());
        properties.put("mail.imap.password", account.getPassword());
        this.session = Session.getDefaultInstance(properties);
    }

    public Store getStore() throws MessagingException {
        Store store = session.getStore("imaps");
        store.connect(properties.getProperty("mail.imap.host"),
                properties.getProperty("mail.imap.user"),
                properties.getProperty("mail.imap.password"));
        return store;
    }
}
