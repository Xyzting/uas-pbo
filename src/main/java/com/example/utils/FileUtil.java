package com.example.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class FileUtil {

    // SAVE GENERIC
    public static <T extends FileSerializable> void save(String fileName, List<T> list) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
            for (T obj : list) {
                pw.println(obj.toCSV());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // LOAD GENERIC
    public static <T> List<T> load(String fileName, Function<String, T> parser) {
        List<T> list = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) {
            return list;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                list.add(parser.apply(line));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
