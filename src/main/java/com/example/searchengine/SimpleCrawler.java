package com.example.searchengine;

import com.opencsv.CSVWriter;

import java.io.*;
import java.util.*;

public class SimpleCrawler extends Crawler {


    protected SimpleCrawler(String indexFileName) {
        super(indexFileName);
    }

    public void crawl(String startUrl){
        try {
            long duration = System.currentTimeMillis();
            Set<String[]> lines = explore(startUrl, new HashSet<>(), new HashSet<>());
            FileWriter fileWriter = new FileWriter(indexFileName);
            CSVWriter writer = new CSVWriter(fileWriter,',', CSVWriter.NO_QUOTE_CHARACTER,' ',"\n");
            for (String[] line : lines) {
                writer.writeNext(line);
            }
            writer.close();
            System.out.println("duration simple crawler: "+(System.currentTimeMillis() - duration));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     *
     * @param startUrl the url where the crawling operation starts
     * @param lines stores the lines to print on the index file
     * @param visited stores the urls that the program has already visited
     * @return the set of lines to print on the index file
     */
    public Set<String[]> explore(String startUrl, Set<String[]> lines, Set<String> visited){
        if (visited.contains(startUrl)) {
            return lines;
        }
        List<List<String>> content = getInfo(startUrl);
        String[] keywords = new String[content.get(0).size() + 1];
        keywords[0] = startUrl.substring(startUrl.lastIndexOf('/'));
        System.arraycopy(content.get(0).toArray(String[]::new), 0, keywords, 1, content.get(0).size());
        lines.add(keywords);
        visited.add(startUrl);
        for (String link : content.get(1)){
            lines = explore(link, lines, visited);
        }
        return lines;
    }
}
