package com.cxwudi.niconico_video_downloader;

import com.cxwudi.niconico_videodownloader.entity.Vsong;
import com.cxwudi.niconico_videodownloader.solve_tasks.VideoDownloader;
import com.cxwudi.niconico_videodownloader.util.DownloadStatus;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class VideoDownloaderTest {
	@Test
	public void testDownload() {
		VideoDownloader v = new VideoDownloader();
		var status = v.downloadVocaloidPV(new Vsong("sm35129416")
				.setTitle("【MV】ラッキー☆オーブ feat. 初音ミク by emon(Tes.) 【MIKU EXPO 5th】").setSubDir("").setURL("fake url"));
		assertTrue(status == DownloadStatus.SUCCESS);
	}
	@Test
	public void testStream() {
		var stdout = new PrintWriter(System.out, true);
		stdout.println("print from print writer");
		assertTrue(true);
	}
	@Test
	public void testFile() {
		VideoDownloader v = new VideoDownloader();
		File video = new File(v.getDownloadDir() + "\\" + "a video.mp4");
		System.out.println(video.isFile());
		System.out.println(video);
		video = new File(v.getDownloadDir(), "");
		System.out.println(new File(v.getDownloadDir(), "a video.mp4").getParentFile());
		assertTrue(true);
	}
	
}
