package com.cxwudi.niconico_videodownloader.util;

import java.io.File;

public class Config {
	
	public static final String EMAIL = "1113421658@qq.com";
	public static final String PASSWORD = "2010017980502";
	
	public static final File OUTPUT_ROOT_DIR = new File("D:\\11134\\Videos");
	
	public static final File DATA = new File("data/");
	
	public static final File DOWNLOADED_RECORD_FILE = new File(DATA, "downloaded.txt");
	
	public static final File LIBRARY = new File("lib/");
	
	public static final File YOUTUBE_DL_FILE = new File(LIBRARY, "youtube-dl.exe");
	
	static {
		System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/lib/selenium_drivers/bin/windows/googlechrome/64bit/chromedriver.exe");
	}
	
	public static final void touch() {/*simply invoke the static block above*/}
	
	private Config() {}
}
