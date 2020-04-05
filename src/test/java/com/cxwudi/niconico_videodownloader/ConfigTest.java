package com.cxwudi.niconico_videodownloader;

import com.cxwudi.niconico_videodownloader.entity.NicoDriver;
import com.cxwudi.niconico_videodownloader.setup.Config;
import com.cxwudi.niconico_videodownloader.solve_tasks.TasksSolver;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class ConfigTest {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Test
    void testSeleniumProperties() {
        Config.touch();
        try {
            var driver = new ChromeDriver();
            driver.quit();
            assertTrue(true);
        } catch (Exception e){
            fail();
        }
    }

    @Test
    void testOtherVariables(){
        logger.debug(Config.getEmail());
        logger.debug(Config.getPassword());
        logger.debug(Config.getRootOutputDir().toString());
        logger.debug("root output exists: {}", Config.getRootOutputDir().exists());
        logger.debug(Config.getDownloadedList().toString());
        logger.debug(Config.getYoutubeDlFile().toString());
        logger.debug(Config.getIncludedListContainStrings().toString());
        logger.debug(Config.getExcludedListContainStrings().toString());
        assertTrue(true);
    }

    @Test
    void getProperDownloader(){
        logger.debug(Config.getIdmFile().toString());
        logger.debug(Config.getDownloadMethod().getName());
        var driver = new NicoDriver(new ChromeDriver());
        new TasksSolver(driver, new TreeSet<>(), new TreeSet<>());
        driver.quit();
    }
}
