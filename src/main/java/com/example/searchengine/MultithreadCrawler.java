package com.example.searchengine;

import com.opencsv.CSVWriter;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class MultithreadCrawler extends Crawler {
    private ThreadPoolTaskExecutor executorService;
    private CopyOnWriteArraySet<String> visited;
    private CopyOnWriteArraySet<String[]> lines;
    private ObserveRunnable observeRunnable;
    private boolean done = false;

    public MultithreadCrawler(String indexFileName) {
        super(indexFileName);
        visited = new CopyOnWriteArraySet<>();
        lines = new CopyOnWriteArraySet<>();
        executorService = new ThreadPoolTaskExecutor();
        executorService.setCorePoolSize(50);
        executorService.setMaxPoolSize(100);
        executorService.initialize();
    }

    public void crawl(String startUrl) {
        double startTime = System.currentTimeMillis();
        executorService.submit(new CrawlerRunnable(this, startUrl));
        new Thread(new ObserveRunnable(this)).run();
        double endTime = System.currentTimeMillis();
        double duration = endTime - startTime;
        System.out.println("duration: " + duration);
    }

    class CrawlerRunnable implements Runnable {
        MultithreadCrawler crawler;
        String startUrl;

        public CrawlerRunnable(MultithreadCrawler crawler, String startUrl) {
            this.crawler = crawler;
            this.startUrl = startUrl;
        }

        @Override
        public void run() {
            if (visited.contains(startUrl)) return;
            List<List<String>> content = getInfo(startUrl);
            String[] keywords = new String[content.get(0).size() + 1];
            keywords[0] = startUrl.substring(startUrl.lastIndexOf('/'));
            System.arraycopy(content.get(0).toArray(String[]::new), 0, keywords, 1, content.get(0).size());
            lines.add(keywords);
            visited.add(startUrl);
            for (String link : content.get(1)) {
                executorService.submit(new CrawlerRunnable(crawler, link));
            }
        }
    }

    class ObserveRunnable implements Runnable {
        private MultithreadCrawler crawler;

        public ObserveRunnable(MultithreadCrawler crawler) {
            this.crawler = crawler;
        }

        @Override
        public void run() {
            while (executorService.getActiveCount() > 0) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            executorService.shutdown();
            try {
                FileWriter fileWriter = new FileWriter(indexFileName);
                CSVWriter writer = new CSVWriter(fileWriter, ',', CSVWriter.NO_QUOTE_CHARACTER, ' ', "\r\n");
                for (String[] line : lines) {
                    writer.writeNext(line);
                }
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}