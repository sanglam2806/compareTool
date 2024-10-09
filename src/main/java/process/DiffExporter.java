package process;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class DiffExporter {
	public static void exportDifference(List<String> diffFiles, File sourceDir, File targetDir) throws IOException {
		for (String diffFile : diffFiles) {
			File srcDir = new File(sourceDir, diffFile);
			File destination = new File(targetDir, diffFile);

			if (srcDir.exists()) {
				destination.getParentFile().mkdirs();
				Files.copy(srcDir.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}
		}
	}

}
