package com.example.searchengine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;



@RestController
@RequestMapping("/")
public class SearchEngine {

	public final String indexFileName = "./src/main/resources/index.csv";

	public final String flippedIndexFileName = "./src/main/resources/index_flipped.csv";

	public final String startUrl = "https://api.interactions.ics.unisg.ch/hypermedia-environment/cc2247b79ac48af0";

	@Autowired
	Searcher searcher;

	@Autowired
	IndexFlipper indexFlipper;

	@Autowired
	SearchEngineProperties properties;
	Crawler crawler;

	@PostConstruct
	public void initialize(){
		if (properties.getCrawler().equals("multithread")){
			this.crawler = new MultithreadCrawler(indexFileName);
		} else {
			this.crawler = new SimpleCrawler(indexFileName);
		}
		if (properties.getCrawl()) {
			crawler.crawl(startUrl);
			indexFlipper.flipIndex(indexFileName, flippedIndexFileName);
		}
	}
	@GetMapping("/search")
	public List<String> search(@RequestParam String q){
		List<String> temp = searcher.search(q.toLowerCase(), flippedIndexFileName);
		if (temp.isEmpty()) {
			temp.add("Keyword does not exist.");
			return temp;
		}
		return temp.stream().map(link -> "<a href='" + link + "'>" + link + "</a>").toList();
	}

	@GetMapping("/lucky")
	public String lucky(@RequestParam String q){
		List<String> temp = searcher.search(q.toLowerCase(), flippedIndexFileName);
		if (temp.isEmpty()) {
			return "Keyword does not exist.";
		}
		return temp.get(0);
	}
}
