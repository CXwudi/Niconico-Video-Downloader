package com.cxwudi.niconico_videodownloader;

import com.cxwudi.niconico_videodownloader.entity.Vsong;
import com.cxwudi.niconico_videodownloader.solve_tasks.ToTaskGenerator;
import com.cxwudi.niconico_videodownloader.solve_tasks.downloader.IDMwithYoutubeDLDownloader;
import com.cxwudi.niconico_videodownloader.util.DownloadStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class IDMwithYoutubeDLDownloaderTest {

    @Test
    void testDownload(){
        var downloader = new IDMwithYoutubeDLDownloader();
        var song = new Vsong("sm35129416").setTitle("【MV】ラッキー☆オーブ feat. 初音ミク by emon(Tes.) 【MIKU EXPO 5th】")
                .setSubDir("").setURL("fake url");
        var task = new ToTaskGenerator().vsongToTask(song);
        var status = downloader.downloadVocaloidPV(task);
        assertTrue(status == DownloadStatus.SUCCESS);
    }
}