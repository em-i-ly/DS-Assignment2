package com.example.searchengine;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;
public abstract class Crawler {

     final String indexFileName;

    private String baseUrl = "https://api.interactions.ics.unisg.ch/hypermedia-environment/";

    /**
     *
     * @param indexFileName the name of the file that is used as index.
     */
    protected Crawler(String indexFileName) {
        this.indexFileName = indexFileName;
    }

    /**
     *
     * @param url the url where the crawling starts
     */
    public abstract void crawl(String url);

    public  List<List<String>> getInfo(String urlString){
        List<String> keywords = new ArrayList<>();
        List<String> hyperlinks = new ArrayList<>();
        List<List<String>> returnList = new ArrayList<>();
        try {
            URL url = new URL(urlString);
            Document doc = Jsoup.connect(url.toString()).get();
            Elements elements = doc.getElementsByTag("p");
            elements.forEach(x->{
                keywords.add(x.text());
                x.remove();
            });
            elements = doc.select("a[href]");
            elements.forEach(x->hyperlinks.add(baseUrl+x.text()));
        } catch (Exception e){
            e.printStackTrace();
        }
        returnList.add(keywords);
        returnList.add(hyperlinks);
        return returnList;
    }
}
