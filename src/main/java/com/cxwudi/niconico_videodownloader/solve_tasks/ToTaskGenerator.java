package com.cxwudi.niconico_videodownloader.solve_tasks;

import com.cxwudi.niconico_videodownloader.entity.Vsong;
import com.cxwudi.niconico_videodownloader.entity.VsongDownloadTask;
import com.cxwudi.niconico_videodownloader.setup.Config;
import com.cxwudi.niconico_videodownloader.util.NicoStringTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.invoke.MethodHandles;

/**
 * This class receive a Vsong instance, and package it into a DownloadTask.
 * It will setup the folder that the song will be download to, and the proper filename that
 * is used to store the song
 * @author CX无敌
 */
public class ToTaskGenerator {
    private File rootDLdir;  //user defined download dir

    public ToTaskGenerator(){this(Config.getRootOutputDir().toString());}

    public ToTaskGenerator(String rootDLdir) {
        this.rootDLdir = new File(rootDLdir);
    }

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public VsongDownloadTask vsongToTask(Vsong song){
        String properFileName = generateFileName(song);
        File dir = makeDirForDownloadingVideoFile(song);
        var downloadTask = new VsongDownloadTask(song, properFileName, dir);
        logger.info("CXwudi and Miku will download Vong {} into {} at {}", song, properFileName, dir.toString());
        return downloadTask;
    }

    /**
     * Create a proper directory for downloading the video, the last folder is named
     * as the same name of the subDir of this vsong.
     * @param song the Vsong to download.
     * @return the file that contains the directory.
     */
    private File makeDirForDownloadingVideoFile(Vsong song) {
        String subDir = NicoStringTool.fixFileName(song.getSubDir());
        //var fileNameString = generateFileName(song);
        //make sure the subfolder is created, otherwise downloading PV might cause problem
        File dir = new File(rootDLdir, subDir);
        try {
            if (!dir.exists())
                dir.mkdirs();//Learn java: even enough your File is xxx/xxx.mp4, mkdirs() will still create folders, but folder is named as "xxx.mp4"
            if (!dir.isDirectory())
                throw new SecurityException("Such path name is not a directory");//a fake exception
        } catch (SecurityException e) {
            logger.warn(e + "\nCXwudi and miku found that this directory" + dir +
                    "is not avaliable, default directory is set, as " + Config.getRootOutputDir());
            dir = Config.getRootOutputDir();
        }

        return dir;
    }
    /**
     * generate an error-free string of the file name. e.g. xxxx.mp4
     * @param song the Vsong object
     * @return the error-free name of the file
     */
    private String generateFileName(Vsong song) {
        String title = song.getTitle();
        if (title.equals("")) return song.getId() + ".mp4";

        var fileNameBuilder = new StringBuilder(title);
        if (!song.getProducerName().equals("") && !title.contains(song.getProducerName())) {
            fileNameBuilder.append("【")
                    .append(song.getProducerName())
                    .append("】");
        }

        var fileNameString = fileNameBuilder.append(".mp4").toString()
                .replace("オリジナル曲", "").replace("オリジナル", "")
                .replace("アニメ", "").replace("MV", "").replace("PV", "")
                .replace("[]", "").replace("【】", "");
        fileNameString = NicoStringTool.fixFileName(fileNameString);
        return fileNameString;
    }

    /**
     * @return the current root directory that set to download
     */
    public File getDownloadDir() {
        return rootDLdir;
    }

    /**
     * @param downloadDir the rootDLdir to set. if error happens, then default dir will be used instead.
     */
    public void setDownloadDir(String downloadDir) {
        File dir = new File(downloadDir);
        try {
            if (dir.isDirectory())
                if (!dir.mkdirs()) throw new SecurityException("fail to make directory");
                else throw new SecurityException("Such path name is not a directory");
            this.rootDLdir = dir;
        } catch (SecurityException e) {
            e.printStackTrace();
            logger.info("CXwudi and miku found that this directory " + dir + " is not avaliable, plz try again.\n" +
                    "A default directroy " + Config.getRootOutputDir() + " has been set instead.");
            this.rootDLdir = Config.getRootOutputDir();
        }

    }
}
