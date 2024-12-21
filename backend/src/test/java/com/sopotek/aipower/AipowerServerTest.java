package com.sopotek.aipower;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class AipowerServerTest {

    @Test
    void shouldLoadEnvironmentVariablesSuccessfully() throws IOException, IOException {
        // Create a temporary .env file
        Path tempEnvFile = Files.createTempFile("test", ".env");
        String envContent = "TEST_VAR=test_value\n";
        Files.write(tempEnvFile, envContent.getBytes());

        // Set the system property to use the temporary .env file
        System.setProperty("dotenv.file", tempEnvFile.toString());

        // Call the main method
        AipowerServer.main(new String[]{});

        // Verify that the environment variable was loaded
        assertEquals("test_value", System.getProperty("TEST_VAR"));

        // Clean up
        Files.delete(tempEnvFile);
    }
}