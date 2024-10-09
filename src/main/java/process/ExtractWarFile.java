package process;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ExtractWarFile {

	private Map<String, String> fileMapModified;

	public Map<String, String> extractFile(String warFilePath, String outputDir) throws IOException {
		File destDir = new File(outputDir);

		if (fileMapModified == null) {
			fileMapModified = new HashMap<String, String>();
		}

		if (!destDir.exists()) {
			destDir.mkdir();
		}

		try (ZipFile zipFile = new ZipFile(warFilePath)) {
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();

				if (!entry.getName().contains("classes") && !entry.getName().contains(".class")) {
					File newFile = new File(outputDir, entry.getName());

					fileMapModified.put(newFile.getAbsolutePath(), entry.getLastModifiedTime().toString());

					if (newFile.isDirectory() || !newFile.getName().contains(".")) {
						// create new folder when directory
						newFile.mkdir();

					} else {
						new File(newFile.getParent()).mkdir();

						try (InputStream fis = zipFile.getInputStream(entry)) {
							FileOutputStream fos = new FileOutputStream(newFile);

							byte[] buffer = new byte[1024];
							int len;

							while ((len = fis.read(buffer)) > 0) {
								fos.write(buffer, 0, len);
							}

							fos.close();
							fis.close();
						}

						if (newFile.getName().endsWith(".war") || newFile.getName().endsWith("service.jar")
								|| newFile.getName().endsWith("core.jar")) {
							extractFile(newFile.getAbsolutePath(),
									newFile.getAbsolutePath().substring(0, newFile.getAbsolutePath().indexOf(".")));
						}
					}
				}

			}
		}

		return fileMapModified;
	}
}
