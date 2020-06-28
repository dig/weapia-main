package net.sunken.common.util;

import lombok.experimental.UtilityClass;

import java.io.File;

@UtilityClass
public class FileUtil {

    public static boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }
}
