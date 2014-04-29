package indraep;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Parser {
	public static void main (String [] ar) throws IOException {
		Document doc = Jsoup.connect("http://localhost/Data/home.html").get();
		Element root = doc.body();

		Elements elementsOfBody = root.select("*");

		for(Element e : elementsOfBody) {
			System.out.println("=>" + e.toString() + " tag_number = " + (e.getAllElements().size() - 1));
		}
	}

	static String getIndent(int tab) {
		String ret = "";
		for (int i = 0; i < tab; i++)
			ret += "\t";
		return ret;
	}

	static void preorder(Element root, int tab) {
		System.out.println(getIndent(tab) + "<" + root.tagName() + ">");

		System.out.println(getIndent(tab + 1) + root.ownText());

		for(Element child : root.children()) {
			preorder(child, tab + 1);
		}

		System.out.println(getIndent(tab) + "</" + root.tagName() + ">");
	}

	private static void CETD(String inputPath, String outputPath, int mode){
		/*==============BAGIAN PREPROCESSING=================*/
		//---Parsing input dokumen HTML menjadi struktur DOM Tree--//
		Document doc = null;
		File in = new File(inputPath);
		try {
			doc = Jsoup.parse(in, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		//--Penghapusan comments--//
		//removeComments(doc);

		//--Penghapusan javascript--//
		doc.select("script, jscript").remove();

		//--Penghapusan style/css--//
		doc.select("style").remove();

		Element body = doc.body();
		Elements elementsOfBody = body.select("*");

		//--Inisialisasi variabel-variabel yang digunakan dalam penentuan threshold--//
		Element globalElementmaxDS = null;
		Double globalMaximumDS = 0.0;
		Double threshold = 0.0;

		/*==============BAGIAN NODE PROCESSING=================*/
		for (Element element : elementsOfBody) {
			//--Penghitungan jumlah tag untuk setiap node--//5
			element.attr("TAG_NUMBER", String.valueOf(element.getAllElements().size() -1));

			//--Penghitungan jumlah karakter untuk setiap node--//
			element.attr("CHAR_NUMBER", String.valueOf(element.text().length()));

			element.attr("MARKED_AS_CONTENT", "0");
			element.attr("PRINTED", "0");
		}
		//--Penghitungan jumlah karakter hyperlink untuk setiap node--//
		//CountHyperlinkCharacter(body);
		//--Penghitungan jumlah tag hyperlink untuk setiap node--//
		//CountHyperlinkTag(body);
		double bodyCharNumber =
				Double.parseDouble(body.attr("CHAR_NUMBER"));
		double bodyLinkCharNumber =
				Double.parseDouble(body.attr("LINKCHAR_NUMBER"));
		double bodyLinkCharPerChar = bodyLinkCharNumber /
				bodyCharNumber;
		//--Penghitungan text density untuk setiap node--//
		//--mode 2 untuk composite text density--//
		//--mode 1 untuk text density biasa--//
		if(mode == 2){
			for (Element element : elementsOfBody) {
				//ComputeCompositeTextDensity(element, bodyLinkCharPerChar);
			}
		} else if(mode == 1) {
			for (Element element : elementsOfBody) {
				long tagnum =
						Long.parseLong(element.attr("TAG_NUMBER"));
				if(tagnum == 0) { tagnum = 1; }
				long charnum =
						Long.parseLong(element.attr("CHAR_NUMBER"));
				double td = (double) charnum/tagnum;
				element.attr("TEXTDENSITY", String.valueOf(td));
			}
		}
		//--Penghitungan density sum untuk setiap node--//
		for (Element element : elementsOfBody) {
			Double densitySum =
					Double.parseDouble(element.attr("TEXTDENSITY"));
			Elements elementChilds = element.children();
			for (Element eachChild : elementChilds) {
				densitySum += Double.parseDouble(eachChild.attr("TEXTDENSITY"));
			}
			element.attr("DENSITY SUM", densitySum.toString());
			//--Pencarian node dengan density sum tertinggi--//
			//--node ini akan digunakan dalam proses penentuan threshold--//
			if(densitySum > globalMaximumDS){
				globalMaximumDS = densitySum;
				globalElementmaxDS = element;
			}
		}

		/*==============BAGIAN PENENTUAN THRESHOLD=================*/
		threshold =
				Double.parseDouble(globalElementmaxDS.attr("TEXTDENSITY"));
		Elements parentsOfMaxDS = globalElementmaxDS.parents();
		//--Mencari node dengan text density terkecil
		//di sepanjang path dari node dengan densitymax hingga node body--//
		if(!globalElementmaxDS.tagName().equalsIgnoreCase("body")){
			for (Element parentMaxDS : parentsOfMaxDS) {
				Double parentTD =
						Double.parseDouble(parentMaxDS.attr("TEXTDENSITY"));
				//--Text density terkecil yang ditemukan akan digunakan sebagai threshold--//
				if(parentTD < threshold){
					threshold = parentTD;
				}
				if(parentMaxDS.tagName().equalsIgnoreCase("body")) break;
			}
		}
		/*==============BAGIAN EKSTRAKSI ISI=================*/
		//--Proses menandai bagian-bagian yang merupakan isi terdapat pada method ExtractContent--//
		//ExtractContent(body, threshold);
		String contentextracted = "";
		for (Element elem : elementsOfBody) {
			Elements childsOfelem = elem.select("*");
			if(elem.attr("MARKED_AS_CONTENT") == "1" &&
					elem.attr("PRINTED") == "0"){
				contentextracted =
						contentextracted.concat(elem.text() + "\n");
				for (Element childOfElem : childsOfelem) {
					childOfElem.attr("PRINTED","1");
				}
			}
		}
		//--Proses mencetak isi yang telah ditandai ke file output--//
		PrintStream out = null;
		try {
			out = new PrintStream(new FileOutputStream(outputPath), true, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (out != null) out.close();
		}
	}

	/*
	 * CountHyperlinkCharacter
	 *
	 * Method ini berfungsi untuk penghitungan karakter hyperlink pada setiap node
	 * Method ini modifikasi dari method CountLinkChar yang diimplementasikan oleh Fei Sun, dkk (2011)
	 */
	private static void CountHyperlinkCharacter(Element element) {
		long linkCharNumber = 0;
		String name = element.tagName();
		long charNumber = Long.parseLong(element.attr("CHAR_NUMBER"));
		if(element.children().size() == 0) {
			if(name.equalsIgnoreCase("a") || name.equalsIgnoreCase("input") || name.equalsIgnoreCase("button")
					|| name.equalsIgnoreCase("option")) {
				element.attr("LINKCHAR_NUMBER", String.valueOf(charNumber));
			}
			else {
				element.attr("LINKCHAR_NUMBER","0");
			}
		}
		else {
			for(Element child = element.child(0); child != null; child = child.nextElementSibling()) {
				CountHyperlinkCharacter(child);
			}
			//if a tag is <a>, then the characters under this tag are all link characters
			if(name.equalsIgnoreCase("a") || name.equalsIgnoreCase("input") || name.equalsIgnoreCase("button")
					|| name.equalsIgnoreCase("option")) {

				linkCharNumber = Long.parseLong(element.attr("CHAR_NUMBER"));
				UpdateHyperlinkCharacter(element);
			}
			//to common tags, their link characters are sum of its child's link characters
			else {
				for(Element child = element.child(0);
						child != null; child = child.nextElementSibling()) {
					linkCharNumber +=
							Long.parseLong(child.attr("LINKCHAR_NUMBER"));
				}
			}
			if(linkCharNumber < charNumber) {
				element.attr("LINKCHAR_NUMBER",
						String.valueOf(linkCharNumber));
			}
			else {
				element.attr("LINKCHAR_NUMBER",
						String.valueOf(charNumber));
			}
		}
	}

	/*
	 * UpdateHyperlinkCharacter
	 *
	 * Method ini berfungsi sebagai penghitungan lanjut karakter
	hyperlink pada setiap node
	 * Method ini modifikasi dari method UpdateLinkChar yang
	diimplementasikan oleh Fei Sun, dkk (2011)
	 */
	private static void UpdateHyperlinkCharacter(Element element) {
		long linkCharNumber = 0;
		if(element.children().size() == 0) {
			return;
		}

		for(Element child = element.child(0); child != null; child = child.nextElementSibling()) {
			linkCharNumber = Long.parseLong(child.attr("CHAR_NUMBER"));
			child.attr("LINKCHAR_NUMBER", String.valueOf(linkCharNumber));
		}
		for(Element child = element.child(0); child != null; child = child.nextElementSibling()) {
			UpdateHyperlinkCharacter(child);
		}
	}

	/*
	 * CountHyperlinkTag
	 *
	 * Method ini berfungsi untuk penghitungan tag hyperlink
	pada setiap node
	 * Method ini modifikasi dari method CountAnchor yang
	diimplementasikan oleh Fei Sun, dkk (2011)
	 */
	private static void CountHyperlinkTag(Element element) {
		long anchorNumber = 0;
		String name = element.tagName();
		if(element.children().size() == 0) {
			if(name.equalsIgnoreCase("a") || name.equalsIgnoreCase("input") || 
					name.equalsIgnoreCase("button") || name.equalsIgnoreCase("option")) {
				element.attr("ANCHOR_NUMBER", element.attr("TAG_NUMBER"));
			} else {
				element.attr("ANCHOR_NUMBER","0");
			}
		}
		else {
			for(Element child = element.child(0); child != null; child = child.nextElementSibling()) {
				CountHyperlinkTag(child);
			}

			//if a tag is <a>, then the tags under this tag are all link tags
			if(name.equalsIgnoreCase("a") || name.equalsIgnoreCase("input") || name.equalsIgnoreCase("button")
					|| name.equalsIgnoreCase("option")) {
				anchorNumber = Long.parseLong(element.attr("TAG_NUMBER"));
				//update the child's anchor number of this node
				UpdateHyperlinkTag(element);
			}
			//to common tags, their link tags are sum of its child's link tags
			else {
				for(Element child = element.child(0);
						child != null; child = child.nextElementSibling()) {
					name = child.tagName();
					anchorNumber += Long.parseLong(child.attr("ANCHOR_NUMBER"));
					//if a tag is <a>, then anchor number add 1
					if(name.equalsIgnoreCase("a") || name.equalsIgnoreCase("input") || 
							name.equalsIgnoreCase("button") || name.equalsIgnoreCase("option")) {
						anchorNumber++;
					}
					//if a node is not a anchor but it's children are all anchors, then we this node as a anchor
					else {
						long chil_anchorNumber= Long.parseLong(child.attr("ANCHOR_NUMBER"));
						long chil_tagNumber = Long.parseLong(child.attr("TAG_NUMBER"));
						long chil_charNumber = Long.parseLong(child.attr("CHAR_NUMBER"));
						long chil_linkCharNumber = Long.parseLong(child.attr("LINKCHAR_NUMBER"));
						//chil_anchorNumber != 0: there are some anchor under this child
						if(chil_anchorNumber == chil_tagNumber && chil_charNumber == chil_linkCharNumber &&
								chil_anchorNumber != 0) {
							anchorNumber++;
						}
					}
				}
			}
			element.attr("ANCHOR_NUMBER", String.valueOf(anchorNumber));
		}
	}

	/*
	 * UpdateHyperlinkTag
	 *
	 * Method ini berfungsi sebagai penghitungan lanjut tag
	hyperlink pada setiap node
	 * Method ini modifikasi dari method UpdateAnchor yang
	diimplementasikan oleh Fei Sun, dkk (2011)
	 */
	private static void UpdateHyperlinkTag(Element element)
	{
		if(element.children().size() == 0) {
			return;
		}

		long anchorNumber = 0;
		for(Element child = element.child(0); child != null; child = child.nextElementSibling()) {
			anchorNumber = Long.parseLong(child.attr("TAG_NUMBER"));
			child.attr("ANCHOR_NUMBER", String.valueOf(anchorNumber));
		}

		for(Element child = element.child(0); child != null; child = child.nextElementSibling())
		{
			UpdateHyperlinkTag(child);
		}
	}
}
