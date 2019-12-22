package com.cxwudi.niconico_videodownloader;

import com.cxwudi.niconico_videodownloader.setup.Config;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class OtherTest {

	@Test
	void testStream() {
		Date now = new Date();
		var aList = new ConcurrentSkipListSet<Integer>();
		IntStream.range(0, 10000).parallel().forEach(aList::add);
		Date after = new Date();
		System.out.println(after.getTime() - now.getTime());
		System.out.println(aList.size());
		assertTrue(true);
	}

	@Test
	void testScanner() {
		System.out.print("Input something: ");
		var scanner = new Scanner(System.in);
		System.out.println(scanner.nextLine());
		scanner.close();
		assertTrue(true);
	}

	@Test
	void testRandom() {
		System.out.println(20000 + new Random().nextInt(5000));
		System.out.println(new Random(20000).nextInt(5000));
		assertTrue(true);
	}

	@Test
	void testReplaceAll() {
		String string = "/asd\\ yes ? aaa";
		System.out.println(string.replaceAll("/", "-").replaceAll("\\\\", "-").replaceAll("\\?", " "));
		assertTrue(true);
	}

	@Test
	void testCreateFile() {
		File defaultDir = Config.getRootOutputDir();
		File video = new File(defaultDir, "testVideo.mp4");
		try {
			video.createNewFile();
			video.deleteOnExit();
		} catch (IOException e) {
			System.err.println(e);
		}
		assertTrue(video.exists());
	}

}
