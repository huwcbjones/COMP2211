package t16.utils;

import java.io.File;
import java.io.IOException;

/**
 * {DESCRIPTION}
 *
 * @author Huw Jones
 * @since 06/03/2017
 */
public class FileUtils {

    /**
	 * Delete a file or a directory and its children.
	 * @param file The directory to delete.
	 * @throws IOException Exception when problem occurs during deleting the directory.
     * @url http://roufid.com/how-to-delete-folder-recursively-in-java/
     * @modified By Huw Jones 06/05/2017
	 */
    public static void delete(File file) throws IOException {

        File[] filesList = file.listFiles();
        if(filesList == null) return;
        for (File childFile : filesList) {

            if (childFile.isDirectory()) {
                delete(childFile);
            } else {
                if (!childFile.delete()) {
                    throw new IOException();
                }
            }
        }

        if (!file.delete()) {
            throw new IOException();
        }
    }

}
