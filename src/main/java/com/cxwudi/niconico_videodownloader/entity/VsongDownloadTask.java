package com.cxwudi.niconico_videodownloader.entity;

import java.io.File;
import java.util.Objects;

public class VsongDownloadTask {
    private Vsong song;
    private String fileName;
    private File outputDir;

    public VsongDownloadTask(Vsong song, String fileName, File outputDir) {
        Objects.requireNonNull(song);
        Objects.requireNonNull(fileName);
        Objects.requireNonNull(outputDir);
        this.song = song;
        this.fileName = fileName;
        this.outputDir = outputDir;
    }

    public Vsong getSong() {
        return song;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public File getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(File outputDir) {
        this.outputDir = outputDir;
    }

    /**
     * @return the target output file, does not guarantee that the song is been download or not
     */
    public File getVsongFile(){
        return new File(outputDir, fileName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VsongDownloadTask that = (VsongDownloadTask) o;
        return song.equals(that.song);
    }

    @Override
    public int hashCode() {
        return Objects.hash(song);
    }
}
