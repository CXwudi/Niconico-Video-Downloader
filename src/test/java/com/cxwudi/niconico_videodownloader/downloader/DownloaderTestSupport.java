package com.cxwudi.niconico_videodownloader.downloader;

import com.cxwudi.niconico_videodownloader.entity.Vsong;
import com.cxwudi.niconico_videodownloader.entity.VsongDownloadTask;
import com.cxwudi.niconico_videodownloader.solve_tasks.ToTaskGenerator;

public abstract class DownloaderTestSupport {

    protected VsongDownloadTask getSampleVsongDownloadTask() {
        var song = new Vsong("sm35129416").setTitle("【MV】ラッキー☆オーブ feat. 初音ミク by emon(Tes.) 【MIKU EXPO 5th】")
                .setSubDir("").setURL("fake url");
        return new ToTaskGenerator().vsongToTask(song);
    }
}
