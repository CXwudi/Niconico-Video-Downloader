package com.cxwudi.niconico_videodownloader.solve_tasks.downloader;

import com.cxwudi.niconico_videodownloader.entity.Vsong;
import com.cxwudi.niconico_videodownloader.entity.VsongDownloadTask;
import com.cxwudi.niconico_videodownloader.setup.Config;
import com.cxwudi.niconico_videodownloader.util.DownloadStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Objects;

import static com.cxwudi.niconico_videodownloader.util.DownloadStatus.FAIL_INITIAL;

/**
 * The video downloader is the main class of downloading Vocaloid PV,
 * and stores them in the correspond folder.
 *
 * please extends this class to finish the implementation of how to download the video
 *
 * @author CX无敌
 */
public abstract class AbstractVideoDownloader {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * @return the default downloader, which is {@link YoutubeDLDownloader} currently
     */
    public static AbstractVideoDownloader getDefaultDownloader(){
        return new YoutubeDLDownloader();
    }

    /**
     * Download the Vocaloid Song and rename it properly.
     * it's a warper function of {@link #downloadImpl}
     * @param task the download task to be performed
     * @return {@link DownloadStatus#SUCCESS} if the Vocaloid PV file is downloaded and renamed, otherwise, return others {@link DownloadStatus}.
     */
    public DownloadStatus downloadVocaloidPV(VsongDownloadTask task) {
        Objects.requireNonNull(task);
        if (task.getSong().getId().equals("")) return FAIL_INITIAL;
        var song = task.getSong();
        var file = task.getOutputDir();
        var songFileName = task.getFileName();

        logger.info("It's show time for {}", Config.getDownloadMethod().getName());
        try {
            var status = downloadImpl(song, file, songFileName);
            return status;
//            if (task.getVsongFile().exists()){
//                if (status == SUCCESS) logger.info("download success");
//                else logger.warn("Oh no, CXwudi and Miku may failed to complete downloading {}", task.getVsongFile());
//                return status;
//            } else {
//                logger.warn("Oh no, CXwudi and Miku fail to find {}, download process may fail", task.getVsongFile());
//                return FAIL_DOWNLOAD;
//            }

        }  catch (IOException e) {
            logger.error("IOException: ", e);
            return FAIL_INITIAL;
        }
    }

    /**
     * The implementation of downloading the Vocaloid PV, by given parameters:
     * @param song the Vsong instance
     * @param dir the directory where the PV is saved to
     * @param fileName the name of the PV file in the directory
     * @throws IOException
     */
    protected abstract DownloadStatus downloadImpl(Vsong song, File dir, String fileName) throws IOException;

}
