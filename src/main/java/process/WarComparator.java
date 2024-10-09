package process;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class WarComparator {

    public static List<String> compareDirs (File dir1 , File dir2) throws Exception {
        List<String> diffFiles = new ArrayList<String>();
        compareDirectoryContent(dir1, dir2, diffFiles);
        return diffFiles;
    }

    private static void compareDirectoryContent(File dir1, File dir2, List<String> diffFiles) throws Exception {
        Path dir1Path = dir1.toPath();
        Files.walkFileTree(dir1Path, new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult visitFile(Path filePath, BasicFileAttributes attr){
                Path relativePath = dir1Path.relativize(filePath);
                Path fileInDir2 = dir2.toPath().resolve(relativePath);
                if(Files.exists(fileInDir2)) {
                    try {
                        if(!filesAreEqual(filePath, fileInDir2)) {
                            diffFiles.add(relativePath.toString() + "Differs");
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    diffFiles.add(relativePath.toString() + " only in " + dir1.getName());
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private static boolean filesAreEqual(Path file1, Path file2) throws Exception {
        byte[] f1Hash = calculateChecksum(file1);
        byte[] f2Hash = calculateChecksum(file2);
        return MessageDigest.isEqual(f1Hash, f2Hash);
    }

    private static byte[] calculateChecksum(Path file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try(InputStream ips = Files.newInputStream(file)) {
            byte[] buffer = new byte[1024];
            int n;
            while((n = ips.read(buffer)) > 0){
                digest.update(buffer, 0, n);
            }
            return digest.digest();
        }
    }
}
