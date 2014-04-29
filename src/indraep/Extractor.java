package indraep;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Extractor {
	private void makeOriginViva() throws IOException {
		File dir = new File("dataset/viva/origin/");
		
		for (File childFile : dir.listFiles()) {
			Document doc = Jsoup.parse(childFile, "UTF-8");
		
			
		}
	}
}
