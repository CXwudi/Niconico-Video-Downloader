package com.cxwudi.niconico_video_downloader;

import com.cxwudi.niconico_videodownloader.util.Config;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.IntStream;

class OtherTest {

	@Test
	void testStream() {
		Date now = new Date();
		var aList = new ConcurrentSkipListSet<Integer>();
		IntStream.range(0, 10000).parallel().forEach(aList::add);
		Date after = new Date();
		System.out.println(after.getTime() - now.getTime());
		System.out.println(aList.size());

	}

	@Test
	void testScanner() {
		System.out.print("Input something: ");
		var scanner = new Scanner(System.in);
		System.out.println(scanner.nextLine());
		scanner.close();
	}

	@Test
	void testRandom() {
		System.out.println(20000 + new Random().nextInt(5000));
		System.out.println(new Random(20000).nextInt(5000));
		
	}

	@Test
	void testReplaceAll() {
		String string = "/asd\\ yes ? aaa";
		System.out.println(string.replaceAll("/", "-").replaceAll("\\\\", "-").replaceAll("\\?", " "));
		
	}

	@Test
	void testCreateFile() {
		File defaultDir = Config.getRootOutputDir();
		File video = new File(defaultDir, "testVideo.mp4");
		try {
			video.createNewFile();
		} catch (IOException e) {
			System.err.println(e);
		}
	}

}
