package com.api.framework.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestNG suite-wide listener that writes structured log entries for every
 * test lifecycle event.
 *
 * <p>Registered in {@code testng.xml} so it applies to all test classes
 * without requiring per-class annotations, keeping test code clean.
 *
 * <p>The listener intentionally does nothing beyond logging.  Allure's own
 * listener ({@link io.qameta.allure.testng.AllureTestNg}) handles report-level
 * events and is registered separately via the Allure Maven plugin configuration.
 */
public class TestNGListener implements ITestListener {

    private static final Logger logger = LogManager.getLogger(TestNGListener.class);

    // -------------------------------------------------------------------------
    // Suite / Context callbacks
    // -------------------------------------------------------------------------

    @Override
    public void onStart(ITestContext context) {
        logger.info("▶ Suite '{}' started — thread='{}'",
                context.getName(), Thread.currentThread().getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        logger.info("■ Suite '{}' finished — passed={}, failed={}, skipped={}",
                context.getName(),
                context.getPassedTests().size(),
                context.getFailedTests().size(),
                context.getSkippedTests().size());
    }

    // -------------------------------------------------------------------------
    // Individual test callbacks
    // -------------------------------------------------------------------------

    @Override
    public void onTestStart(ITestResult result) {
        logger.info("  ├─ [START]  {}.{}",
                result.getTestClass().getName(),
                result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logger.info("  ├─ [PASSED] {}.{} ({}ms)",
                result.getTestClass().getName(),
                result.getMethod().getMethodName(),
                result.getEndMillis() - result.getStartMillis());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        logger.error("  ├─ [FAILED] {}.{} — {}",
                result.getTestClass().getName(),
                result.getMethod().getMethodName(),
                result.getThrowable() != null ? result.getThrowable().getMessage() : "unknown error");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logger.warn("  ├─ [SKIPPED] {}.{} — reason: {}",
                result.getTestClass().getName(),
                result.getMethod().getMethodName(),
                result.getThrowable() != null ? result.getThrowable().getMessage() : "no reason provided");
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        logger.warn("  ├─ [FLAKY]  {}.{} — failed but within success percentage",
                result.getTestClass().getName(),
                result.getMethod().getMethodName());
    }
}

