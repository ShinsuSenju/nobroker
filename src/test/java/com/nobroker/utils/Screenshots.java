package com.nobroker.utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class Screenshots {
    TakesScreenshot screenshot;

    // Captures screenshot and saves it with timestamp in filename
    public static String captureScreenshot(WebDriver driver, String filename) {
        String timeStamp = new SimpleDateFormat("yyyy-mm-dd HH-mm-ss").format(new Date());
        String path = System.getProperty("user.dir") + "/target/Screenshots/" + filename + "_" + timeStamp + ".png";
        File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

        try {
            FileUtils.copyFile(source, new File(path));
        } catch (IOException e) {
            e.printStackTrace(); // Logs error if file copy fails
        }
        return path;
    }
}
