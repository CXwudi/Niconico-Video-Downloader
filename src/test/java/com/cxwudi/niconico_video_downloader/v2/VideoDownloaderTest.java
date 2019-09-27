package com.cxwudi.niconico_video_downloader.v2;

import static org.junit.Assert.*;

import java.io.File;
import java.io.PrintWriter;

import org.junit.Test;

public class VideoDownloaderTest {
	@Test
	public void testDownload() {
		VideoDownloader v = new VideoDownloader();
		v.downloadVocaloidPV(new Vsong("sm34200478").setTitle("ビューティフルなフィクション / 初音ミク").setSubDir("").setURL("fake url"));
		
	}
	@Test
	public void testStream() {
		var stdout = new PrintWriter(System.out, true);
		stdout.println("print from print writer");
	}
	@Test
	public void testFile() {
		VideoDownloader v = new VideoDownloader();
		File video = new File(v.getDownloadDir() + "\\" + "a video.mp4");
		System.out.println(video.isFile());
		System.out.println(video);
		video = new File(v.getDownloadDir(), "");
		System.out.println(new File(v.getDownloadDir(), "a video.mp4").getParentFile());
	}
	
}
