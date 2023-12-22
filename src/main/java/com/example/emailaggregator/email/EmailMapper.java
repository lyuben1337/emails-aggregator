package com.example.emailaggregator.email;

import com.example.emailaggregator.model.EmailShortDescription;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Base64;

@Slf4j
public final class EmailMapper {

    private static final int SHORT_EMAIL_CONTENT_LENGTH = 200;

    public static EmailShortDescription toEmailShortDescription(Message message) {
        return EmailShortDescription
                    .builder()
                    .html(removeCidImages(getHtmlFromMessage(message)))
                    .date(getDate(message))
                    .subject(getSubject(message))
                    .contentString(getShortContent(message))
                    .sender(getFrom(message))
                    .build();
    }

    private static String getHtmlFromMessage(Message message) {
        try {
            if (message.isMimeType("multipart/*")) {
                MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
                return getHtmlFromMimeMultipart(mimeMultipart);
            }
            if (message.isMimeType("text/plain")) {
                return message.getContent().toString();
            }
        } catch (MessagingException | IOException e) {
            throw new RuntimeException(e);
        }
        return "";
    }

    private static String getHtmlFromMimeMultipart(MimeMultipart mimeMultipart)
            throws MessagingException, IOException {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < mimeMultipart.getCount(); i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            result.append(parseBodyPart(bodyPart));
        }
        return result.toString();
    }

    private static String parseBodyPart(BodyPart bodyPart) throws MessagingException, IOException {
        if (bodyPart.isMimeType("text/html")) {
            return bodyPart.getContent().toString();
        }
        if (bodyPart.isMimeType("image/*")) {
            try {
                Image image = ImageIO.read(bodyPart.getInputStream());
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ImageIO.write((RenderedImage) image, "jpg", bos);
                byte[] imageBytes = bos.toByteArray();
                String imageBase64 = Base64.getEncoder().encodeToString(imageBytes);
                return "<img src='data:image/jpg;base64," + imageBase64 + "' />";
            } catch (UnsupportedOperationException | IOException e) {
                log.error("Error processing image: {}", e.getMessage());
            }
        }
        if (bodyPart.getContent() instanceof MimeMultipart){
            return getHtmlFromMimeMultipart((MimeMultipart) bodyPart.getContent());
        }
        return "";
    }

    public static String removeCidImages(String htmlString) {
        Document doc = Jsoup.parse(htmlString);
        Elements images = doc.select("img");
        for (Element image : images) {
            String src = image.attr("src");
            if (src.startsWith("cid:")) {
                image.remove();
            }
        }
        return doc.toString();
    }

    private static LocalDate getDate(Message message) {
        try {
            return LocalDate.ofInstant(message.getReceivedDate().toInstant(), ZoneId.systemDefault());
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getSubject(Message message) {
        try {
            return message.getSubject() == null ? "(no subject)" : message.getSubject();
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getShortContent(Message message) {
        try {
            Object content = message.getContent();
            if (content instanceof Multipart multipart) {
                StringBuilder shortContent = new StringBuilder();
                for (int i = 0; i < multipart.getCount(); i++) {
                    BodyPart bodyPart = multipart.getBodyPart(i);
                    shortContent.append(convertBodyPart(bodyPart));
                    if (shortContent.length() > SHORT_EMAIL_CONTENT_LENGTH) {
                        return shortContent.substring(0, SHORT_EMAIL_CONTENT_LENGTH);
                    }
                }
                return shortContent.toString();
            } else {
                return "(No content)";
            }
        } catch (MessagingException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String convertBodyPart(BodyPart bodyPart) throws MessagingException, IOException {
        if (bodyPart.isMimeType("text/plain")) {
            return bodyPart.getContent().toString().replaceAll("\\s+", " ").trim();
        }
        if (bodyPart.isMimeType("image/*")) {
            return "(image)";
        }
        return "";
    }

    private static String getFrom(Message message) {
        try {
            Address[] from = message.getFrom();
            if (from.length == 0 || from[0] == null) {
                return null;
            }
            var fromString = from[0].toString();
            return fromString.substring(0, fromString.indexOf('<')).replaceAll("\"", "");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
