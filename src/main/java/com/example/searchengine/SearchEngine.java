package com.example.searchengine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


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
	public ResponseEntity<String> search(@RequestParam String q, @RequestHeader("Accept") String accept){
		HttpHeaders httpHeaders = new HttpHeaders();
		if (q.isEmpty()) return new ResponseEntity<>("Query is empty.", HttpStatus.BAD_REQUEST);
		List<String> results = searcher.search(q.toLowerCase(), flippedIndexFileName);
		if (accept.contains("application/json")){
			ArrayList<String> temp = new ArrayList<>();
			for (String link : results) temp.add("\"" + link + "\"");
			String joinedString = "[" + String.join(", ", temp) + "]";
			httpHeaders.put("Content-Type", Collections.singletonList("application/json"));
			return new ResponseEntity<>(joinedString, httpHeaders, HttpStatus.OK);
		}
		return new ResponseEntity<>(results.stream().map(link -> "<a href=\"" + link + "\">" + link + "</a>").collect(Collectors.joining("<br>")), HttpStatus.OK);
	}

	@GetMapping("/lucky")
	public ResponseEntity<String> lucky(@RequestParam String q, @RequestHeader("Accept") String accept){
		if (q.isEmpty()){
			return new ResponseEntity<>("Query is empty.", HttpStatus.BAD_REQUEST);
		}
		List<String> temp = searcher.search(q.toLowerCase(), flippedIndexFileName);
		if (temp.isEmpty()) {
			return new ResponseEntity<>("No Keyword found.", HttpStatus.NOT_FOUND);
		}
		HttpHeaders httpHeaders = new HttpHeaders();
		if (accept.contains("application/json")){
			httpHeaders.put("Content-Type", Collections.singletonList("application/json"));
			return new ResponseEntity<>("\"" + temp.get(0) +"\"", httpHeaders, HttpStatus.OK);
		}
		httpHeaders.put("Location", Collections.singletonList(temp.get(0)));
		return new ResponseEntity<>(httpHeaders, HttpStatus.FOUND);
	}
}
