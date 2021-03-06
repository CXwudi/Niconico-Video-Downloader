package com.cxwudi.niconico_videodownloader.setup;

import com.cxwudi.niconico_videodownloader.solve_tasks.downloader.DLMethodNamesEnum;
import com.cxwudi.niconico_videodownloader.util.LazyVar;
import io.github.bonigarcia.wdm.WebDriverManager;
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
import java.util.Arrays;
import java.util.List;

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

	public static final void touch() {/*simply invoke the static block above*/}

	static {
		//don't forget to set UTF-8 to support Chinese and Japanese
		FileBasedConfigurationBuilder.setDefaultEncoding(PropertiesConfiguration.class, "UTF-8");
		Parameters params = new Parameters();

		setupSystemConfig(params);
		setupUserConfig(params);
		setupWebDriver();

	}

	private static void setupWebDriver() {
		//setup selenium system properties
		WebDriverManager.chromedriver().setup();
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
			logger.error("Can not get user configuration", cex);
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
			logger.error("Can not get system configuration", cex);
		}
	}


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

	private static final LazyVar<File> IDM_FILE = new LazyVar<>(
			() -> new File(systemConfig.getString("idm"))
	);
	public static File getIdmFile() { return IDM_FILE.get();}

	private static final LazyVar<DLMethodNamesEnum> DOWNLOADER_METHOD = new LazyVar<>(
			() -> DLMethodNamesEnum.getMethodByName(systemConfig.getString("downloadMethod"))
	);
	public static DLMethodNamesEnum getDownloadMethod(){ return DOWNLOADER_METHOD.get(); }

	//input.includeList.containString
	private static final LazyVar<List<String>> INCLUDE_LIST_CONTAIN_STRINGS = new LazyVar<>(
			() -> {
				var flatStr = userConfig.getString("input.includeList.containString");
				return Arrays.asList(flatStr.split(","));
			}
	);
	public static List<String> getIncludedListContainStrings() { return INCLUDE_LIST_CONTAIN_STRINGS.get(); }

	//input.excludeList.containString
	private static final LazyVar<List<String>> EXCLUDE_LIST_CONTAIN_STRINGS = new LazyVar<>(
			() -> {
				var flatStr = userConfig.getString("input.excludeList.containString");
				return Arrays.asList(flatStr.split(","));
			}
	);
	public static List<String> getExcludedListContainStrings() { return EXCLUDE_LIST_CONTAIN_STRINGS.get(); }

	private Config() {}
}
