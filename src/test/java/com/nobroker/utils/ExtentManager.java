package com.nobroker.utils;

import java.lang.reflect.Method;
import com.nobroker.setup.BaseSteps;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentManager {

    private ExtentReports extent;
    private ExtentSparkReporter sparkReport;
    public ExtentTest test;
    public WebDriver driver;
    /**
     * Initializes and configures the Extent report.
     */
    public void reportGeneration() {
        sparkReport = new ExtentSparkReporter("target/Reports/TestReport.html");
        sparkReport.config().setTheme(Theme.DARK);
        sparkReport.config().setDocumentTitle("NoBroker Automation Report");
        sparkReport.config().setReportName("Functional Testing - Web UI");

        extent = new ExtentReports();
        extent.attachReporter(sparkReport);

        // Static system info
        if (driver != null) {
            String browserName = driver.getClass().getSimpleName().toLowerCase();

            if (browserName.contains("chrome")) {
                extent.setSystemInfo("Browser", "Chrome");
            } else if (browserName.contains("firefox")) {
                extent.setSystemInfo("Browser", "Firefox");
            } else if (browserName.contains("edge")) {
                extent.setSystemInfo("Browser", "Edge");
            } else {
                extent.setSystemInfo("Browser", "Unknown (" + browserName + ")");
            }
        } else {
            extent.setSystemInfo("Browser", "Not initialized");
        }

        extent.setSystemInfo("Environment", "QA");
        extent.setSystemInfo("Tester", "Anurag Singh");
    }

    /**
     * Creates a test entry in the report using the method name and description.
     */

    /**
     * Creates a test entry in the report using the method name, description, and actual parameter values.
     */
    public void reportCreation(Method method) {
        String description = "";
        if (method.isAnnotationPresent(Test.class)) {
            description = method.getAnnotation(Test.class).description();
        }

        test = extent.createTest("Test Case: " + method.getName())
                     .assignAuthor("Anurag Singh")
                     .assignCategory("Web UI Test");

        if (!description.isEmpty()) {
            test.log(Status.INFO, " Description: " + description);
        }

       
    }



    /**
     * Adds screenshot and logs based on test result status.
     */
    public void reportTestCompletetion(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        String screenShotPath = Screenshots.captureScreenshot(driver, methodName);

        switch (result.getStatus()) {
            case ITestResult.SUCCESS:
                test.log(Status.PASS, "✅ Test Passed: " + methodName);
                test.pass("Screenshot on success:")
                    .addScreenCaptureFromPath(screenShotPath);
                break;

            case ITestResult.FAILURE:
                test.log(Status.FAIL, "❌ Test Failed: " + methodName);
                test.fail("Reason: " + result.getThrowable());
                test.fail("Screenshot on failure:")
                    .addScreenCaptureFromPath(screenShotPath);
                break;

            case ITestResult.SKIP:
                test.log(Status.SKIP, "⚠️ Test Skipped: " + methodName);
                test.skip("Reason: " + result.getThrowable());
                test.skip("Screenshot on skip:")
                    .addScreenCaptureFromPath(screenShotPath);
                break;

            default:
                test.log(Status.INFO, "ℹ️ Test status unknown for: " + methodName);
        }
    }

    /**
     * Cleans up resources after test execution.
     */
    public void cleanUp() {
        BaseSteps.tearDown();
    }

    /**
     * Finalizes and writes the report to disk.
     */
    public void reportCompletion() {
        extent.flush();
    }
}
