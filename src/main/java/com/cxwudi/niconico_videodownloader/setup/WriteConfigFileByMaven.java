package com.cxwudi.niconico_videodownloader.setup;

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
 * A scripting class just for writing our selenium drivers location
 * @author CX无敌
 */
public class WriteConfigFileByMaven {

    private static File systemConfigFile = new File("data/system_config.properties");

    private FileBasedConfigurationBuilder<FileBasedConfiguration> builder;
    private Configuration config;

    public WriteConfigFileByMaven() {
        Parameters params = new Parameters();
        builder = new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
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
    }

    public void writeSeleniumDriverLocation(){
        var sysProp = System.getProperties();
        var currentDir = sysProp.getProperty("user.dir");
        for (var property: sysProp.entrySet()) {
            var keyStr = String.valueOf(property.getKey());
            var valueStr = String.valueOf(property.getValue());
//            logger.debug("get property: {} = {}", keyStr, valueStr);
            if (keyStr.contains("driver")){
                var resolvedStr = valueStr.replace(currentDir + "\\", "");
                config.setProperty(keyStr, resolvedStr);
                logger.info("saving new selenium property: {} = {}", keyStr, resolvedStr);
            }
        }

        try {
            builder.save();
        } catch (ConfigurationException e) {
            logger.error("Can not save config file\n{}", e);
        }
    }

    public static void main(String[] args) {
        var obj = new WriteConfigFileByMaven();
        obj.writeSeleniumDriverLocation();
    }

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

}
