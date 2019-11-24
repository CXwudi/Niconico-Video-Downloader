package com.cxwudi.niconico_videodownloader.setup;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.stream.Collectors;

public class CopyFilesForRelease {

    public static void main(String[] args) {
        final var targetDir = new File(args[0]);
        final var array = Arrays.copyOfRange(args, 1, args.length);
        logger.info("start copying {} into {}", Arrays.toString(array), targetDir);

        var listOfInputDir = Arrays.stream(array)
                .map(File::new).collect(Collectors.toList());
        listOfInputDir.parallelStream().forEach(dir -> {
            var destDir = new File(targetDir, dir.getName());
            try {
                FileUtils.copyDirectory(dir, destDir);
            } catch (IOException e) {
                logger.error("Fail to copy {} to {}, \nwith exception {}", dir, destDir, e);
            }
        });
    }

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
}
