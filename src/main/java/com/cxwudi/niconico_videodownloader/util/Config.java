package com.cxwudi.niconico_videodownloader.util;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.invoke.MethodHandles;

public class Config {

	private static File systemConfigFile = new File("data/system_config.properties");
	private static Configuration config;
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private static final LazyVar<String> EMAIL = new LazyVar<>(
			() -> config.getString("niconico.email")
	);
	public static String getEmail() { return EMAIL.get();}
	private static final LazyVar<String> PASSWORD = new LazyVar<>(
			() -> config.getString("niconico.password")
	);
	public static String getPassword() { return PASSWORD.get();}

	private static final LazyVar<File> OUTPUT_ROOT_DIR = new LazyVar<>(
			() -> new File(config.getString("output.rootDirectory"))
	);
	public static File getRootOutputDir() {return OUTPUT_ROOT_DIR.get();}

	private static final LazyVar<File> DOWNLOADED_RECORD_FILE = new LazyVar<>(
			() -> new File(config.getString("user.data.downloadedList"))
	);
	public static File getDownloadedList() {return DOWNLOADED_RECORD_FILE.get();}
	
	private static final LazyVar<File> YOUTUBE_DL_FILE = new LazyVar<>(
			() -> new File(config.getString("youtube-dl"))
	);
	public static File getYoutubeDlFile() {return YOUTUBE_DL_FILE.get();}
	
	static {
		Parameters params = new Parameters();
		var builder = new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
				.configure(params.properties()
						.setFileName(systemConfigFile.getPath()));
		try
		{
			config = builder.getConfiguration();
			logger.info("successfully load {}", systemConfigFile);
		}
		catch(ConfigurationException cex)
		{
			logger.error("Can not get configuration\n {}", cex);
		}

		//setup selenium system properties
		var webDriverProperties = config.getKeys("webdriver");
		webDriverProperties.forEachRemaining(keyStr -> {
			var value = config.getString(keyStr);
			logger.debug("Selenium system property set: {} -> {}", keyStr, value);
			System.setProperty(keyStr, value);
		});
	}


	public static final void touch() {/*simply invoke the static block above*/}
	
	private Config() {}
}
