package indraep.crawler;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class CrawlerController {
	public static void main (String [] ar) throws Exception {
		String crawlStorageFolder = "data/crawl/cnn";
		int numberOfCrawlers = 7;
		CrawlConfig config = new CrawlConfig();
		
		/** PROXY SETTING */
		config.setProxyHost("proxy.ui.ac.id");
		config.setProxyPort(8080);
		/** END OF PROXY SETTING */
		
		config.setCrawlStorageFolder(crawlStorageFolder);
		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer = new RobotstxtServer(
		robotstxtConfig, pageFetcher);
		CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
		
		//controller.addSeed("http://indeks.kompas.com/");
		//controller.addSeed("http://news.viva.co.id/");
		//controller.addSeed("http://edition.cnn.com/");
		controller.addSeed("http://www.bbc.com/");
		//controller.addSeed("http://www.nytimes.com/");
		//controller.addSeed("http://www.thejakartapost.com/");
		
		//controller.start(CrawlerKompas.class, numberOfCrawlers);
		//controller.start(CrawlerViva.class, numberOfCrawlers);
		//controller.start(CrawlerCnn.class, numberOfCrawlers);
		controller.start(CrawlerBbc.class, numberOfCrawlers);
		//controller.start(CrawlerNYTimes.class, numberOfCrawlers);
		//controller.start(CrawlerTheJakartaPost.class, numberOfCrawlers);
	}
}
