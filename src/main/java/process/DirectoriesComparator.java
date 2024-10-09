package process;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DirectoriesComparator {
	private static String FILE_DIFF_PATH = "file/diff/";
	private static String ADDED = "[Added    ]";
	private static String MODIFIED = "[Modifiled]";
	private static String DELETED = "[Deleted  ]";

	public static void compareDirectories(StringBuilder diffReport, String newOutputDir, String oldOutputDir,
			Map<String, String> mapNewFile,
			Map<String, String> mapOldFile) throws Exception {
		File newFolder = new File(newOutputDir);
		File oldFolder = new File(oldOutputDir);

		List<String> modifiedFiles = new ArrayList<String>();
		List<String> addedFiles = new ArrayList<String>();
		List<String> deletedFiles = new ArrayList<String>();

		File[] listNewFiles = newFolder.listFiles();
		File[] listOldFiles = oldFolder.listFiles();

		if (listNewFiles != null && listOldFiles != null) {

			getModifiedFiles(listNewFiles, modifiedFiles, addedFiles, oldFolder, diffReport, mapNewFile, mapOldFile);
			getDeletedFiles(listOldFiles, deletedFiles, newFolder, diffReport);
		}

		File modifiedNewDir = new File(FILE_DIFF_PATH + "new");
		if (!modifiedNewDir.exists()) {
			modifiedNewDir.mkdir();
		}

		File modifiedOldDir = new File(FILE_DIFF_PATH + "old");
		if (!modifiedOldDir.exists()) {
			modifiedOldDir.mkdir();
		}

		if (modifiedFiles.size() > 0) {
			DiffExporter.exportDifference(modifiedFiles, newFolder, modifiedNewDir);
			DiffExporter.exportDifference(modifiedFiles, oldFolder, modifiedOldDir);
		}

		if (addedFiles.size() > 0) {
			DiffExporter.exportDifference(modifiedFiles, newFolder, modifiedNewDir);
		}

		if (deletedFiles.size() > 0) {
			DiffExporter.exportDifference(modifiedFiles, oldFolder, modifiedOldDir);
		}

	}

	private static void getModifiedFiles(File[] listNewFiles, List<String> modifiedFiles, List<String> addedFiles,
			File oldFolder, StringBuilder diffReport, Map<String, String> mapNewFile,
			Map<String, String> mapOldFile) throws Exception {

		File fileCompare = null;

		for (File newFile : listNewFiles) {
			fileCompare = new File(oldFolder, newFile.getName());

			if (!fileCompare.exists()) {
				diffReport.append(ADDED + ": " + newFile.getAbsolutePath() + "\n");
				addedFiles.add(newFile.getName());

				continue;
			}

			if ((newFile.isDirectory() || !newFile.getName().contains("."))
					&& (fileCompare.isDirectory() || !fileCompare.getName().contains("."))) {
				compareDirectories(diffReport, newFile.getAbsolutePath(), fileCompare.getAbsolutePath(), mapNewFile,
						mapOldFile);
				continue;
			}

			if (fileCompare.getName().contains(".jar")) {

				if (!mapNewFile.get(fileCompare.getAbsolutePath())
						.equals(mapOldFile.get(fileCompare.getAbsolutePath()))) {
					diffReport.append(MODIFIED + ": " + fileCompare.getAbsolutePath() + "\n");

					continue;
				}
			}

			if (fileCompare.getName().contains(".war") || fileCompare.getName().contains(".ear")) {
				// war file no need to compare
				continue;
			}

			if (FileComparator.createHTMLDiffFile(
					FileComparator.generateDiffContent(fileCompare.getAbsolutePath(), newFile.getAbsolutePath()),
					FILE_DIFF_PATH + newFile.getName() + ".html")) {
				diffReport.append(MODIFIED + "comparedResult/" + ": " + newFile.getAbsolutePath() + "\n");
				modifiedFiles.add(fileCompare.getName());
			}
		}
	}

	private static void getDeletedFiles(File[] listOldFiles, List<String> deletedFiles,
			File newFolder, StringBuilder diffReport) throws Exception {

		File fileCompare = null;
		for (File newFile : listOldFiles) {
			fileCompare = new File(newFolder, newFile.getName());

			if (!fileCompare.exists()) {
				diffReport.append(DELETED + ": " + newFile.getName() + "\n");
				deletedFiles.add(newFile.getName());

				continue;
			}
		}
	}

}
