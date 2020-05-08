package com.cxwudi.niconico_videodownloader.downloader;

import com.cxwudi.niconico_videodownloader.entity.VsongDownloadTask;
import com.cxwudi.niconico_videodownloader.solve_tasks.downloader.IDMwithYoutubeDLDownloader;
import com.cxwudi.niconico_videodownloader.util.DownloadStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IDMwithYoutubeDLDownloaderTest extends DownloaderTestSupport{

    @Test
    void testDownload(){
        var downloader = new IDMwithYoutubeDLDownloader();
        VsongDownloadTask task = getSampleVsongDownloadTask();
        var status = downloader.downloadVocaloidPV(task);
        assertEquals(status, DownloadStatus.SUCCESS);
    }

}