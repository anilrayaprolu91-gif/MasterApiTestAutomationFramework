package com.api.framework.utils;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StepResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Helper for attaching arbitrary text and byte payloads to the current Allure test result.
 *
 * <p>While the {@link io.qameta.allure.restassured.AllureRestAssured} filter handles
 * REST Assured call attachments automatically, this utility covers supplementary
 * content such as generated payloads, raw SQL queries, or custom diagnostic data.
 */
public final class AllureAttachmentUtil {

    private static final Logger logger = LogManager.getLogger(AllureAttachmentUtil.class);

    private AllureAttachmentUtil() {
        throw new UnsupportedOperationException("AllureAttachmentUtil is a utility class.");
    }

    /**
     * Attaches a plain-text string to the current Allure step.
     *
     * @param name    attachment label displayed in the report
     * @param content content to attach
     */
    public static void attachText(String name, String content) {
        logger.debug("Attaching text to Allure — name='{}'", name);
        Allure.addAttachment(name, "text/plain", content, ".txt");
    }

    /**
     * Attaches a JSON string to the current Allure step with syntax highlighting.
     *
     * @param name    attachment label
     * @param content JSON content
     */
    public static void attachJson(String name, String content) {
        logger.debug("Attaching JSON to Allure — name='{}'", name);
        Allure.addAttachment(name, "application/json", content, ".json");
    }

    /**
     * Attaches an XML string to the current Allure step with syntax highlighting.
     *
     * @param name    attachment label
     * @param content XML content
     */
    public static void attachXml(String name, String content) {
        logger.debug("Attaching XML to Allure — name='{}'", name);
        Allure.addAttachment(name, "application/xml", content, ".xml");
    }

    /**
     * Wraps a logical block inside a named Allure step. Any exception thrown
     * inside {@code action} marks the step as FAILED and re-throws.
     *
     * @param stepName human-readable step description
     * @param action   code block to execute within the step scope
     */
    public static void step(String stepName, Runnable action) {
        AllureLifecycle lifecycle = Allure.getLifecycle();
        String uuid = UUID.randomUUID().toString();
        lifecycle.startStep(uuid, new StepResult().setName(stepName).setStatus(Status.PASSED));
        try {
            action.run();
            lifecycle.updateStep(uuid, s -> s.setStatus(Status.PASSED));
        } catch (Throwable t) {
            lifecycle.updateStep(uuid, s -> s.setStatus(Status.FAILED));
            throw t;
        } finally {
            lifecycle.stopStep(uuid);
        }
    }

    /**
     * Attaches raw bytes (e.g. a binary response body) to the current Allure step.
     *
     * @param name        attachment label
     * @param bytes       raw content
     * @param contentType MIME type (e.g. {@code "application/octet-stream"})
     */
    public static void attachBytes(String name, byte[] bytes, String contentType) {
        logger.debug("Attaching bytes to Allure — name='{}', size={} bytes", name, bytes.length);
        Allure.getLifecycle().addAttachment(name, contentType, "",
                new ByteArrayInputStream(bytes));
    }
}

