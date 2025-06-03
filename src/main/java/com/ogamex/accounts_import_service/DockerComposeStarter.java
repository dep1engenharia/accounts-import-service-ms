package com.ogamex.accounts_import_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * This component will:
 * 1) On application startup: run "docker-compose -f <path> up -d"
 * 2) On application shutdown: run "docker-compose -f <path> down"
 */
@Component
public class DockerComposeStarter implements ApplicationRunner, ApplicationListener<ContextClosedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(DockerComposeStarter.class);

    /**
     * Adjust this path so it points exactly to your docker-compose.yml file.
     * In this example:
     *   D:\PROJECTOS\OGAMEX MICROSERVICES\accounts-import-service\docker-compose.yml
     */
    private final File composeFile = new File("docker-compose.yml");

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!composeFile.exists()) {
            logger.error("docker-compose.yml not found at {}", composeFile.getAbsolutePath());
            return;
        }

        // Build the "docker-compose" command. On Windows,
        // if you have Docker Desktop installed, "docker-compose" should be in your PATH.
        // If you’re using Docker Compose v2, you can also use: "docker", "compose", "up", "-d"
        ProcessBuilder pb = new ProcessBuilder(
                "docker-compose",
                "-f", composeFile.getAbsolutePath(),
                "up",
                "-d"
        );

        // Ensure the working dir is the same folder as the compose file (not strictly required, but often cleaner)
        pb.directory(composeFile.getParentFile());
        pb.redirectErrorStream(true);

        logger.info("Starting docker-compose services: {}", Arrays.toString(pb.command().toArray()));
        try {
            Process p = pb.start();
            // You might want to read/consume stdout in a separate thread, but for simplicity:
            StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), logger::info);
            outputGobbler.start();

            int exitCode = p.waitFor();
            if (exitCode != 0) {
                logger.error("docker-compose up exited with code {}", exitCode);
            } else {
                logger.info("docker-compose up succeeded.");
            }
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Failed to run docker-compose up", e);
        }
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        // When Spring is shutting down, run "docker-compose down"
        if (!composeFile.exists()) {
            logger.warn("Skipping docker-compose down; file not found at {}", composeFile.getAbsolutePath());
            return;
        }

        ProcessBuilder pb = new ProcessBuilder(
                "docker-compose",
                "-f", composeFile.getAbsolutePath(),
                "down"
        );
        pb.directory(composeFile.getParentFile());
        pb.redirectErrorStream(true);

        logger.info("Stopping docker-compose services: {}", Arrays.toString(pb.command().toArray()));
        try {
            Process p = pb.start();
            StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), logger::info);
            outputGobbler.start();
            int exitCode = p.waitFor();
            if (exitCode != 0) {
                logger.error("docker-compose down exited with code {}", exitCode);
            } else {
                logger.info("docker-compose down succeeded.");
            }
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Failed to run docker-compose down", e);
        }
    }

    /**
     * A simple thread that reads an InputStream line by line and logs it.
     * This prevents the process from blocking if the buffer fills up.
     */
    private static class StreamGobbler extends Thread {
        private final java.io.InputStream is;
        private final java.util.function.Consumer<String> consumer;

        StreamGobbler(java.io.InputStream is, java.util.function.Consumer<String> consumer) {
            this.is = is;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(is))) {
                String line;
                while ((line = br.readLine()) != null) {
                    consumer.accept(line);
                }
            } catch (IOException e) {
                // Swallow or log as needed
            }
        }
    }
}
