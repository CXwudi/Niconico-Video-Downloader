package com.cxwudi.niconico_videodownloader.solve_tasks.downloader;

import com.cxwudi.niconico_videodownloader.entity.Vsong;
import com.cxwudi.niconico_videodownloader.setup.Config;
import com.cxwudi.niconico_videodownloader.util.DownloadStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;

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

    private static final String idmProcessName = "IDMan.exe";

    /**
     * The second honor function that first use youtube-dl to get a url of the video,
     * then use IDM to download the video from the url
     * @param song the Vsong instance
     * @param dir the directory where the PV is saved to
     * @param fileName the name of the PV file in the directory
     * @return {@link DownloadStatus#SUCCESS} if success
     * @throws IOException
     */
    @Override
    protected DownloadStatus downloadImpl(Vsong song, File dir, String fileName) throws IOException {
        var urlStrBuilder = new StringBuilder();
        if (getUrl(song.getId(), urlStrBuilder)){
            // Call IDM to download url
            return downloadUsingIdm(urlStrBuilder.toString(), dir, fileName);
        } else {
            return DownloadStatus.FAIL_INITIAL;
        }
    }

    /**
     * Get the video url using youtube-dl --get-url command tag
     * @param smId the sm-id of this vsong
     * @param urlStrBuilder to store the url gotten from youtube-dl
     * @return {@code true} if success
     * @throws IOException if youtube-dl process throws exception
     */
    private boolean getUrl(String smId, StringBuilder urlStrBuilder) throws IOException {
        logger.info("start getting video URL for {}", smId);
        var youtubeDlGetUrlPb = new ProcessBuilder(
                Config.getYoutubeDlFile().getAbsolutePath(),
                "-v",
                "--username", '"' + Config.getEmail() + '"',
                "--password", '"' + Config.getPassword() + '"',
                "https://www.nicovideo.jp/watch/" + smId,
                "--get-url",
                "-f", "\"best[height<=1080]\""); //for IDM, we trust it to download 1080p video for real fast
        youtubeDlGetUrlPb.redirectError(ProcessBuilder.Redirect.INHERIT);
        var getUrlProcess = youtubeDlGetUrlPb.start();

        try (var output = new BufferedReader(new InputStreamReader(getUrlProcess.getInputStream()))){
            var url = output.readLine();
            logger.info("URL get✔: {}", url);
            urlStrBuilder.append(url);
            return true;
        } catch (Exception e){
            logger.error("Oh no, CXwudi and Miku fail to get URL, downloading fail 😭", e);
            return false;
        }
    }

    /**
     * Once URL is gotten, use IDM to download it
     * @param url the URL of the vsong, just gotten from youtube-dl
     * @param dir where to store the video file
     * @param fileName the name of the video file to store
     * @return {@link DownloadStatus#SUCCESS} if download success
     * @throws IOException if IDM process throw exception, or fail to delete existing file
     */
    private DownloadStatus downloadUsingIdm(String url, File dir, String fileName) throws IOException {
        var targetFile = new File(dir, fileName);
        if (targetFile.exists()){
            logger.warn("{} already exists, now deleted and re-download", targetFile);
            Files.delete(targetFile.toPath());
        }
        var idmPath = Config.getIdmFile().toString();

        if (!terminateExistingIdmProcess(idmPath)) {
            return DownloadStatus.FAIL_INITIAL;
        }

        logger.info("CXwudi and Miku are invoking IDM to download it 😂");
        var idmPB = new ProcessBuilder(
            idmPath,
                "/d", url,
                "/p", dir.getAbsolutePath(),
                "/f", fileName,
                "/n", "/q"); //no question and quite after download
        var idmProcess = idmPB.inheritIO().start();
        try {
            idmProcess.waitFor();
        } catch (InterruptedException e) {
            idmProcess.destroy();
            logger.error("Interrupted in IDMwithYoutubeDLDownloader.downloadUsingIdm", e);
            System.exit(-1);
        }
        if (Files.exists(targetFile.toPath())){
            return DownloadStatus.SUCCESS;
        } else {
            return DownloadStatus.FAIL_DOWNLOAD;
        }

    }

    /**
     * To terminate IDM,
     * used for make sure we are synchronized when calling IDM, the existing IDM process must be terminated
     * @param idmPath path of IDM
     * @return {@code true} if termination success
     */
    private boolean terminateExistingIdmProcess(String idmPath) {
        try {
            var taskKillPb = new ProcessBuilder("taskkill", "/F", "/FI", String.format("\"imagename eq %s\"", idmProcessName));
            logger.info("killing IDM with {}", taskKillPb.command());
            var p = taskKillPb.inheritIO().start();
            p.waitFor();
            return true;
        } catch (IOException e) {
            logger.error("Fail to kill IDM process", e);
            return false;
        } catch (InterruptedException e) {
            logger.error("Interrupted in IDMwithYoutubeDLDownloader.terminateExistingIdmProcess", e);
            System.exit(-1);
            return false; // let compiler happy
        }
    }

    /**
     * Since there is no way for Java project to know the progress and status of IDM process,
     * and the IDM temp file is in a secret folder with hashed folder name. (It's still possible to get that temp file,
     * but it's not worth to spend that effort to do it)
     *
     * So we can only check if targetFile is existed periodically. It will do check once per second, until 1 min 40 se gone
     * @param targetFile
     * @throws InterruptedException
     */
    private DownloadStatus waitUntilFileIsHere(File targetFile) throws InterruptedException {
        logger.info("waiting for IDM ...");
        final long before = System.currentTimeMillis();
        final var maxTime = 1000L * 100; //1 m 40 s
        while (!targetFile.exists()){
            if (System.currentTimeMillis() - before > maxTime) {
                logger.warn("Oh no, CXwudi's IDM failed to download {} in 1 min 40 second, download progress may fail", targetFile);
                return DownloadStatus.UNKNOWN_STATUS;
            }
            Thread.sleep(1000L); //1s
        }
        logger.info("IDM process done");
        return DownloadStatus.SUCCESS;
    }

    /**
     * Pause the current project to wait until the video is downloaded
     *
     * IDM process will return immediately, but its sub-process which is doing the actual downloading was still on-going.
     * Unfortunately there is no way to get the file size, so this method is simply just checking if file size increased
     *
     * @param targetFile the file to be monitor on
     * @deprecated doesn't work
     */
    @Deprecated()
    private void returnWhenFinished(File targetFile) throws InterruptedException {
        final var initialSleep = 2000L; //2s
        final var timeToSleep = 1000L; //1s
        final var afterSleep = 3000L; //3s
        final var maxCount = 5; //maximum if not increased 5 times, then function returns
        var previousSize = -1L;
        var sizeNotIncreaseCount = 0;

        Thread.sleep(initialSleep); //initial sleep, to let IDM warm up

        while (sizeNotIncreaseCount < maxCount){
            var currentSize = targetFile.length();
            if (previousSize == currentSize) {
                sizeNotIncreaseCount++;
            } else {
                previousSize = currentSize;
                sizeNotIncreaseCount = 0;
            }
            logger.debug("has download {} bytes", currentSize);
            Thread.sleep(timeToSleep);
        }
        Thread.sleep(afterSleep);
    }
}
