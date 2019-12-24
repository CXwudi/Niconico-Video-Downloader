package com.cxwudi.niconico_videodownloader.solve_tasks.downloader;

import com.cxwudi.niconico_videodownloader.entity.Vsong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;

/**
 * An implementation of {@link AbstractVideoDownloader} that drives IDM to download the Vocaloid PV,
 * it still use youtube-dl to fetch video url.
 *
 * Good that niconico video url currently doesn't require cookies to be watched or download by
 * third-party downloader/browsers.
 * Keep in mind that if one day niconico decide to add cookie validation, this downloading method will no longer working
 *
 * @author CX无敌
 */
public class IDMwithYoutubeDLDownloader extends AbstractVideoDownloader {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * The second honor function that first use youtube-dl to get a url of the video,
     * then use IDM to download the video from the url
     * @param song the Vsong instance
     * @param dir the directory where the PV is saved to
     * @param fileName the name of the PV file in the directory
     * @throws IOException
     */
    @Override
    protected void downloadImpl(Vsong song, File dir, String fileName) throws IOException {

    }
}
