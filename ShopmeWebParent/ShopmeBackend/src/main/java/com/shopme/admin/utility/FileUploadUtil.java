package com.shopme.admin.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class FileUploadUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadUtil.class);

    public static void saveFile(String uploadDir, String fileName, MultipartFile multipartFile)
            throws IOException {
        Path uploadPath = Path.of(uploadDir);
        System.out.println("uploadPath " + uploadPath.toAbsolutePath());

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        } else { // Delete all previous files
            if (!uploadPath.toString().contains("extras")) // don't remove the existed extra images
                cleanDir(uploadPath);
        }

        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path path = uploadPath.resolve(fileName);
            System.out.println("FilePath: " + path.toAbsolutePath());
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new IOException("Could not save file: " + fileName, ex);
        }
    }

    private static void cleanDir(Path uploadPath) {
        /*int count = 0; count++;  => This approach will get an error: Variable 'count' is accessed from within inner class, needs to be final or effectively final
        The issue here is that the variable count is being accessed from within an inner class (the anonymous SimpleFileVisitor class) in a non-final way.
        In Java, when an inner class accesses a variable from its outer class, that variable must be either final or "effectively final", meaning its value is never changed after initialization.
        In this case, count is not final, and its value is being changed inside the inner class (count++;). This is not allowed, because the inner class may outlive the outer class instance, and the compiler cannot guarantee that the variable count will still exist when the inner class tries to access it.
        To fix this issue, you can either declare count as final, or make it effectively final by not changing its value after initialization. However, since you are trying to change the value of count inside the inner class, declaring it as final is not an option.
        A possible solution is to use an array of size 1 to hold the value of count. This way, you can change the value of count inside the inner class without violating the "effectively final" rule.*/
        final int[] count = {0};
        try {
            Files.walkFileTree(uploadPath, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file); // Delete each file
                    count[0]++;
                    return FileVisitResult.CONTINUE;
                }
            });
//            System.out.println("Deleted " + count[0] + " files");
            LOGGER.info("Deleted {} files", count[0]);
        } catch (IOException e) {
//            System.out.println("Error cleaning directory: " + uploadPath.toAbsolutePath());
            LOGGER.error("Error cleaning directory: {}", uploadPath.toAbsolutePath(), e);
        }
    }

    public static void removeDir(String uploadDir) {
        Path dir = Path.of(uploadDir);

        if (!Files.exists(dir)) // may be `extra` dir and not existed, so not to throw an error
            return;

        cleanDir(dir);

        try {
            Files.delete(dir);
//            System.out.println("Deleted directory: " + dir.toAbsolutePath());
            LOGGER.info("Deleted directory: {}", dir.toAbsolutePath());
        } catch (IOException e) {
//            System.out.println("Error deleting directory: " + dir.toAbsolutePath());
            LOGGER.error("Error deleting directory: {}", dir.toAbsolutePath(), e);
        }
    }

    public static void removeFile(Path name) {
        try {
            Files.delete(name);
//            System.out.println("Deleted file: " + name);
            LOGGER.info("Deleted file: {}", name);
        } catch (IOException e) {
//            System.out.println("Error deleting file: " + name);
            LOGGER.error("Error deleting file: {}", name, e);
        }
    }
}
