package com.sopotek.aipower.service.telegram;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Logger;

public class Telegram {
    private static final String API_URL = "https://api.telegram.org/bot";

    @Value("${spring.boot.admin.notify.telegram.auth-token}")
    private String token;
    private final OkHttpClient client;
    ObjectMapper objectMapper;
    private final Logger logger = Logger.getLogger(Telegram.class.getName());

    public Telegram() {
        this.client = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Sends a chat action (typing, upload_photo, etc.) to indicate activity.
     */
    protected void sendChatAction(long chatId, String action) {
        HttpUrl url = Objects.requireNonNull(HttpUrl.parse(API_URL + token + "/sendChatAction"))
                .newBuilder()
                .addQueryParameter("chat_id", String.valueOf(chatId))
                .addQueryParameter("action", action)
                .build();

        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                assert response.body() != null;
                logger.warning("Failed to send chat action: " + response.body().string());
            }
        } catch (IOException e) {
            logger.severe("Error sending chat action: " + e.getMessage());
        }
    }

    /**
     * Sends a message with an optional markdown format.
     */
    protected void sendMessage(long chatId, String message, String markdown, boolean disablePreview) {
        sendChatAction(chatId, "typing"); // Show 'typing' action

        HttpUrl url = Objects.requireNonNull(HttpUrl.parse(API_URL + token + "/sendMessage")).newBuilder()
                .addQueryParameter("chat_id", String.valueOf(chatId))
                .addQueryParameter("text", message)
                .addQueryParameter("parse_mode", markdown)
                .addQueryParameter("disable_web_page_preview", String.valueOf(disablePreview))
                .build();

        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                assert response.body() != null;
                logger.warning("Failed to send message: " + response.body().string());
            }
        } catch (IOException e) {
            logger.severe("Error sending message: " + e.getMessage());
        }
    }

    /**
     * Sends a photo to the chat.
     */
    protected void sendScreenshot(long chatId, File imageFile) {
        if (!imageFile.exists()) {
            sendMessage(chatId, "Screenshot file not found!", "Markdown", false);
            return;
        }

        sendChatAction(chatId, "upload_photo"); // Show 'uploading photo' action

        RequestBody fileBody = RequestBody.create(imageFile, MediaType.parse("image/jpeg"));
        MultipartBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("chat_id", String.valueOf(chatId))
                .addFormDataPart("photo", "screenshot.jpg", fileBody)
                .build();

        Request request = new Request.Builder()
                .url(API_URL + token + "/sendPhoto")
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                assert response.body() != null;
                logger.warning("Failed to send photo: " + response.body().string());
            }
        } catch (IOException e) {
            logger.severe("Error sending photo: " + e.getMessage());
        }
    }

    /**
     * Sends a document to the chat.
     */
    public void sendDocument(String chatId, File file) {
        sendChatAction(Long.parseLong(chatId), "upload_document"); // Show 'uploading document' action

        RequestBody fileBody = RequestBody.create(file, MediaType.parse("application/octet-stream"));
        MultipartBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("chat_id", String.valueOf(chatId))
                .addFormDataPart("document", file.getName(), fileBody)
                .build();

        Request request = new Request.Builder()
                .url(API_URL + token + "/sendDocument")
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                assert response.body() != null;
                logger.warning("Failed to send document: " + response.body().string());
            }
        } catch (IOException e) {
            logger.severe("Error sending document: " + e.getMessage());
        }
    }
}
