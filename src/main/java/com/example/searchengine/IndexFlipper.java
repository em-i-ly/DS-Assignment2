package com.example.searchengine;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;
@Component
public class IndexFlipper {

    public void flipIndex(String indexFileName, String flippedIndexFileName){
        try {
            CSVReader csvReader = new CSVReader(new FileReader(indexFileName));
            List<String[]> csvLines = csvReader.readAll();
            Set<String[]> lines = new HashSet<>();
            for(String[] line : csvLines){
                for (int i = 1; i < 4; i++) {
                    boolean isNotInSet = true;
                    for (String[] temp : lines) {
                        if (line[i].equals(temp[0])) {
                            String[] newArr = new String[temp.length + 1];
                            System.arraycopy(temp, 0, newArr, 0, newArr.length - 1);
                            newArr[newArr.length - 1] = line[0];
                            lines.remove(temp);
                            lines.add(newArr);
                            isNotInSet = false;
                            break;
                        }
                    }
                    if (isNotInSet) lines.add(new String[]{line[i], line[0]});
                }
            }
            CSVWriter writer = new CSVWriter(new FileWriter(flippedIndexFileName),',', CSVWriter.NO_QUOTE_CHARACTER,' ',"\r\n");
            for (String[] line : lines) {
                writer.writeNext(line);
            }
            writer.close();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

}
