package process;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.github.difflib.text.DiffRow;
import com.github.difflib.text.DiffRowGenerator;

public class CreateFileDiff {

    public CreateFileDiff() {
    }

    public String generateDiffContent(String oldFile, String newFile) throws IOException {

        List<String> oldFileLines = Files.readAllLines(Paths.get(oldFile));
        List<String> newFileLines = Files.readAllLines(Paths.get(newFile));

        DiffRowGenerator generator = DiffRowGenerator.create()
                .inlineDiffByWord(true)
                .showInlineDiffs(true)
                .lineNormalizer(str -> str.replace("\t", "    ").replace(" ", "&nbsp; "))
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

        return html.toString();
    }

    public void createHTMLDiffFile(String htmlContent, String outputFilePath) throws IOException {
        File htmlFile = new File(outputFilePath);
        FileWriter writer = new FileWriter(htmlFile);
        writer.write(htmlContent);
        writer.close();
    }
}