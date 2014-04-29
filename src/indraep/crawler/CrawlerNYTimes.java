package indraep.crawler;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class CrawlerNYTimes extends WebCrawler {
	
	public HashMap<String,Integer> hash = new HashMap<String,Integer>();
	static int pageNumber = 0;
	private final static Pattern FILTERS =
			Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g"
					+ "|png|tiff?|mid|mp2|mp3|mp4"
					+ "|wav|avi|mov|mpeg|ram|m4v|pdf"
					+ "|rm|smil|wmv|swf|wma|zip|rar|gz))$");
	@Override
	public boolean shouldVisit(WebURL url) {
		String href = url.getURL().toLowerCase();
		return !FILTERS.matcher(href).matches() &&
				href.contains("nytimes.com/");
	}

	@Override
	public void visit(Page page) {
		String url = page.getWebURL().getURL();
		
		if (url.startsWith("http://www.nytimes.com/2014/") && page.getParseData() instanceof HtmlParseData
				&& !hash.containsKey(url)) {
			
			System.out.println("Url = " + url);
			
			HtmlParseData HTMLParseData =
					(HtmlParseData) page.getParseData();
			String text = HTMLParseData.getText();
			String HTML = HTMLParseData.getHtml();
			List<WebURL> links =
					HTMLParseData.getOutgoingUrls();
			PrintStream out = null;
			try {
				out = new PrintStream(new FileOutputStream("dataset/ny_times/origin/" + pageNumber + ".HTML"));
				out.print(HTML);
				System.out.println("PRINTED as " + "origin/" + pageNumber + ".HTML");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("PRINT EXCEPTION");
			}
			finally {
				if (out != null) out.close();
			}
			
			hash.put(url, 0);
			pageNumber++;
			//System.out.println("Text length: " + text.length());
			//System.out.println("Html length: " + HTML.length());
			//System.out.println("Number of outgoing links: " + links.size());
		}
	}
}
