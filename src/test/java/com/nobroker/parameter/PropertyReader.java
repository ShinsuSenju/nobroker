package com.nobroker.parameter;

import java.util.Properties;
import java.io.FileInputStream;

import java.io.IOException;

public class PropertyReader {
	private static Properties properties = null;
	private static final String DEFAULT_PATH = "resources/PropertyData/profiledata.properties";

	private static void loadProperties(String path) {
		if (properties == null) {
			properties = new Properties();
			try (FileInputStream fis = new FileInputStream(path)) {
				properties.load(fis);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static String getDataFromPropertyFile(String property) {
		loadProperties(DEFAULT_PATH);
		return properties.getProperty(property);
	}

	// Optional: allow custom property file path
	public static String getDataFromPropertyFile(String property, String path) {
		loadProperties(path);
		return properties.getProperty(property);
	}
}
