package com.example.searchengine;

import com.opencsv.CSVReader;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class Searcher {
    /**
     *
     * @param keyword to search
     * @param flippedIndexFileName the file where the search is performed.
     * @return the list of urls
     */
    public List<String> search(String keyword, String flippedIndexFileName){
        long duration = System.currentTimeMillis();
        List<String> urls = new ArrayList<>();
        try {
            CSVReader reader = new CSVReader(new FileReader(flippedIndexFileName));
            List<String[]> csvLines = reader.readAll();
            for(String[] line : csvLines){
                if (line[0].equals(keyword)){
                    for (int i = 1; i < line.length ; i++) {
                        urls.add("https://api.interactions.ics.unisg.ch/hypermedia-environment"+line[i]);
                    }
                }
            }
            } catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("duration searcher flipped: " + (System.currentTimeMillis() - duration));
        return urls;
    }
}
