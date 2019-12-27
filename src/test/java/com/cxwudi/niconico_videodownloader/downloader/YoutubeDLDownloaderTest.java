package com.cxwudi.niconico_videodownloader.downloader;

import com.cxwudi.niconico_videodownloader.entity.VsongDownloadTask;
import com.cxwudi.niconico_videodownloader.solve_tasks.downloader.YoutubeDLDownloader;
import com.cxwudi.niconico_videodownloader.util.DownloadStatus;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class YoutubeDLDownloaderTest extends DownloaderTestSupport{
	@Test
	void testDownload() {
		YoutubeDLDownloader v = new YoutubeDLDownloader();
		VsongDownloadTask task = getSampleVsongDownloadTask();
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
