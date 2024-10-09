package org.example;

import com.github.difflib.text.DiffRowGenerator;
import process.DiffExporter;
import process.ExtractWarFile;
import process.WarComparator;

import java.io.File;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        ExtractWarFile extractWarFile = new ExtractWarFile();
//        extractWarFile.extractFile();
        System.out.println("Hello world!");


        // github source

        File war1 = new File("war1.war");
        File war2 = new File("war2.war");

        File extract1 = new File("path/to/extract/old");
        File extract2 = new File("path/to/extract/new");

        //Step1 : extract the war files
        ExtractWarFile.extractFile(war1,extract1.getAbsolutePath());
        ExtractWarFile.extractFile(war2,extract2.getAbsolutePath());

        //Step2 : compare files
        List<String> diffFiles = WarComparator.compareDirs(extract1,extract2);

        //Step3 : Export diffFiles
        if(!diffFiles.isEmpty()) {
            File outputFileDir = new File("path/to/diff/output");
            DiffExporter.exportDifference(diffFiles, extract2, outputFileDir);
            System.out.println("Differences exported to: " + outputFileDir.getAbsolutePath());
        } else {
            System.out.println("No differences found.");
        }
    }
}