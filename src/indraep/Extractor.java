package indraep;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Extractor {
	public static void main(String [] ar) throws IOException {
		makeGoldTheJakartaPost();
	}
	

	private static void makeGoldKompas() throws IOException {
		File dir = new File("dataset/kompas/origin/");
		
		for (File web : dir.listFiles()) {
			String outputPath = web.toString().replace("origin", "gold").replace("HTML", "txt");
		
			Document doc = Jsoup.parse(web, "UTF-8");
			
			Element body = doc.body();
			Element isiBerita = body.select(".kcm-read-content-text .nml span").first();
			
			try {
				writeToFile(isiBerita.text(), outputPath);
			}
			catch (Exception e) {
				System.out.println(web + " - " + e.getMessage());
			}
		}
	}
	
	private static void makeGoldViva() throws IOException {
		File dir = new File("dataset/viva/origin/");
		
		for (File web : dir.listFiles()) {
			String outputPath = web.toString().replace("origin", "gold").replace("HTML", "txt");
		
			Document doc = Jsoup.parse(web, "UTF-8");
			
			Element body = doc.body();
			Element isiBerita = body.select(".isiberita").first();
			
			writeToFile(isiBerita.text(), outputPath);
		}
	}
	
	private static void makeGoldCNN() throws IOException {
		File dir = new File("dataset/cnn/origin/");
		
		for (File web : dir.listFiles()) {
			String outputPath = web.toString().replace("origin", "gold").replace("HTML", "txt");
			String content = "";
			
			Document doc = Jsoup.parse(web, "UTF-8");
			
			Element body = doc.body();
			Elements p = body.select(".cnn_strycntntlft p");
			
			for (Element par : p) {
				Element anchor = par.select("a").first();
				if (anchor != null && anchor.text().equals(par.text())) {
					continue;
				}
				
				content += par.text();
			}
			
			try {
				writeToFile(content, outputPath);
			}
			catch (Exception e) {
				System.out.println(web + " - " + e.getMessage());
			}
		}
	}
	
	private static void makeGoldBBC() throws IOException {
		File dir = new File("dataset/bbc/origin/");
		
		for (File web : dir.listFiles()) {
			String outputPath = web.toString().replace("origin", "gold").replace("HTML", "txt");
			String content = "";
			
			Document doc = Jsoup.parse(web, "UTF-8");
			
			Element body = doc.body();
			Elements p = body.select(".story-body p");
			
			for (Element par : p) {
				content += par.text();
			}
			
			try {
				writeToFile(content, outputPath);
			}
			catch (Exception e) {
				System.out.println(web + " - " + e.getMessage());
			}
		}
	}
	
	private static void makeGoldTheJakartaPost() throws IOException {
		File dir = new File("dataset/thejakartapost/origin/");
		
		for (File web : dir.listFiles()) {
			String outputPath = web.toString().replace("origin", "gold").replace("HTML", "txt");
			String content = "";
			
			Document doc = Jsoup.parse(web, "UTF-8");
			
			Element body = doc.body();
			Elements p = body.select("div.teaser div.span-13.last p");
			
			for (Element par : p) {
				content += par.text();
			}
			
			try {
				writeToFile(content, outputPath);
			}
			catch (Exception e) {
				System.out.println(web + " - " + e.getMessage());
			}
		}
	}
	
	private static void writeToFile(String content, String outputPath) {
		PrintStream out = null;
		try {
			out = new PrintStream(new FileOutputStream(outputPath));
			out.print(content);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			if (out != null) out.close();
		}
	}
}
