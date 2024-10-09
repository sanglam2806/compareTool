package process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.github.difflib.text.DiffRow;
import com.github.difflib.text.DiffRowGenerator;

public class FileComparator {

	public FileComparator() {
	}

	public static String generateDiffContent(String oldFile, String newFile) throws Exception {

		List<String> oldFileLines = readAllLines(oldFile);
		List<String> newFileLines = readAllLines(newFile);

		DiffRowGenerator generator = DiffRowGenerator.create()
				.inlineDiffByWord(true)
				.showInlineDiffs(true)
				.lineNormalizer(str -> str.replace("\t", " &nbsp; &nbsp; &nbsp; &nbsp; "))
				.oldTag(f -> "<span style = 'background-color: #FFCCBB;'>")
				.newTag(f -> "<span style = 'background-color: #CCFFCC;'>")
				.build();

		List<DiffRow> rows = generator.generateDiffRows(oldFileLines, newFileLines);

		StringBuilder html = new StringBuilder("<html><body><table>");
		html.append("<tr><th>File 1</th><th>File 2</th></tr>");

		for (DiffRow row : rows) {
			html.append("<td>").append(row.getOldLine()).append("</td>");
			html.append("<td>").append(row.getNewLine()).append("</td>");
			html.append("</tr>");
		}

		html.append("</table></body></html>");

		if (html.toString().contains("background-color: #FFCCBB;")) {
			return html.toString();
		}

		return null;
	}

	public static boolean createHTMLDiffFile(String htmlContent, String outputFilePath) throws IOException {
		if (htmlContent == null) {
			return false;

		}
		File htmlFile = new File(outputFilePath);
		FileWriter writer = new FileWriter(htmlFile);
		writer.write(htmlContent);
		writer.close();
		return true;
	}

	private static List<String> readAllLines(String filePath) throws Exception {
		List<String> lines = new ArrayList<String>();

		File file = new File(filePath);

		@SuppressWarnings("resource")
		BufferedReader bufReader = new BufferedReader(
				new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
		String line;
		while ((line = bufReader.readLine()) != null) {
			lines.add(line.replaceAll("<", "<span>&lt;</span>").replaceAll(">", "<span>&gt;</span>"));
		}

		return lines;
	}
}
