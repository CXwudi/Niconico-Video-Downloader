package com.cxwudi.niconico_videodownloader;

import com.cxwudi.niconico_videodownloader.entity.Vsong;
import com.cxwudi.niconico_videodownloader.solve_tasks.ToTaskGenerator;
import com.cxwudi.niconico_videodownloader.solve_tasks.downloader.YoutubeDLDownloader;
import com.cxwudi.niconico_videodownloader.util.DownloadStatus;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class YoutubeDLDownloaderTest {
	@Test
	void testDownload() {
		YoutubeDLDownloader v = new YoutubeDLDownloader();
		var song = new Vsong("sm35129416").setTitle("【MV】ラッキー☆オーブ feat. 初音ミク by emon(Tes.) 【MIKU EXPO 5th】")
				.setSubDir("").setURL("fake url");
		var task = new ToTaskGenerator().vsongToTask(song);
		var status = v.downloadVocaloidPV(task);
		assertTrue(status == DownloadStatus.SUCCESS);
	}
	@Test
	void testStream() {
		var stdout = new PrintWriter(System.out, true);
		stdout.println("print from print writer");
		assertTrue(true);
	}
	
}
