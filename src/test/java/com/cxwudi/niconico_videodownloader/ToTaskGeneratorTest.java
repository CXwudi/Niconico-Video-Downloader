package com.cxwudi.niconico_videodownloader;

import com.cxwudi.niconico_videodownloader.entity.Vsong;
import com.cxwudi.niconico_videodownloader.solve_tasks.ToTaskGenerator;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ToTaskGeneratorTest {

    @Test
    void vsongToTask() {
        var generator = new ToTaskGenerator();
        var vsong = new Vsong("sm23379461", "My PV").setSubDir("2019年V家新曲").setProducerName("CXwudi");

        var task = generator.vsongToTask(vsong);
        System.out.println(task.getOutputDir());
        System.out.println(task.getFileName());
        System.out.println(task.getSong());
    }
    @Test
    void testFile() {
        var generator = new ToTaskGenerator();
        File video = new File(generator.getDownloadDir() + "\\" + "a video.mp4");
        System.out.println(video.isFile());
        System.out.println(video);
        video = new File(generator.getDownloadDir(), "");
        System.out.println(new File(generator.getDownloadDir(), "a video.mp4").getParentFile());
        assertTrue(true);
    }
}