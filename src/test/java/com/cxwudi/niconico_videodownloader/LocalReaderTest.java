/**
 * 
 */
package com.cxwudi.niconico_videodownloader;

import com.cxwudi.niconico_videodownloader.get_tasks.LocalReader;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author CX无敌
 *
 */
class LocalReaderTest {

	@Test
	void testReading() {
		LocalReader localReader = new LocalReader();
		//Map<String, String> map = localRecorder.getIsDownloaded();
		System.out.println(new File(new File("."), "downloaded.txt").getAbsolutePath());
		System.out.println(System.getProperty("user.dir"));
		localReader.readRecord();
		System.out.println(localReader.getCollection());
		assertTrue(true);
		
	}

}
