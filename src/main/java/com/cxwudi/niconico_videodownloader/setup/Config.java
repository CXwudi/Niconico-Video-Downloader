package com.cxwudi.niconico_videodownloader.setup;

import com.cxwudi.niconico_videodownloader.util.LazyVar;
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

/**
 * The global class to store any system configuration and user configuration, including selenium system properties,
 * user account, root download directory and etc.
 *
 * @author CX无敌
 */
public class Config {

	private static File systemConfigFile = new File("data/system_config.properties");
	private static Configuration systemConfig, userConfig;
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private static final LazyVar<String> EMAIL = new LazyVar<>(
			() -> userConfig.getString("niconico.email")
	);
	public static String getEmail() { return EMAIL.get();}

	private static final LazyVar<String> PASSWORD = new LazyVar<>(
			() -> userConfig.getString("niconico.password")
	);
	public static String getPassword() { return PASSWORD.get();}

	private static final LazyVar<File> OUTPUT_ROOT_DIR = new LazyVar<>(
			() -> new File(userConfig.getString("output.rootDirectory"))
	);
	public static File getRootOutputDir() {return OUTPUT_ROOT_DIR.get();}

	private static final LazyVar<File> DOWNLOADED_RECORD_FILE = new LazyVar<>(
			() -> new File(userConfig.getString("data.downloadedList"))
	);
	public static File getDownloadedList() {return DOWNLOADED_RECORD_FILE.get();}
	
	private static final LazyVar<File> YOUTUBE_DL_FILE = new LazyVar<>(
			() -> new File(systemConfig.getString("youtube-dl"))
	);
	public static File getYoutubeDlFile() {return YOUTUBE_DL_FILE.get();}

	public static final void touch() {/*simply invoke the static block above*/}

	static {
		Parameters params = new Parameters();

		setupSystemConfig(params);
		setupUserConfig(params);

		//setup selenium system properties
		var webDriverProperties = systemConfig.getKeys("webdriver");
		webDriverProperties.forEachRemaining(keyStr -> {
			var value = systemConfig.getString(keyStr);
			logger.debug("Selenium system property set: {} -> {}", keyStr, value);
			System.setProperty(keyStr, value);
		});
	}

	private static void setupUserConfig(Parameters params) {
		var userConfigFile = systemConfig.getString("user.config");
		var builder = new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
				.configure(params.properties()
						.setFileName(userConfigFile));
		try
		{
			userConfig = builder.getConfiguration();
			logger.info("successfully load {}", userConfigFile);
		}
		catch(ConfigurationException cex)
		{
			logger.error("Can not get configuration", cex);
		}
	}

	private static void setupSystemConfig(Parameters params) {
		var builder = new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
				.configure(params.properties()
						.setFileName(systemConfigFile.getPath()));
		try
		{
			systemConfig = builder.getConfiguration();
			logger.info("successfully load {}", systemConfigFile);
		}
		catch(ConfigurationException cex)
		{
			logger.error("Can not get configuration", cex);
		}
	}

	private Config() {}
}
