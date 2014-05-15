package indraep;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public class Helper {
	public static void main(String [] ar) throws IOException {
		rename("cnn");
		rename("kompas");
		rename("viva");
	}
	
	private static void rename(String siteName) throws IOException {
		File dir = new File("dataset/" + siteName + "/origin_unique");
		File newDir = new File("dataset/" + siteName + "/origin_unique_baru");
		
		if (!newDir.exists()) {
			newDir.mkdir();
		}
		int counter = 0;
		for (File file : dir.listFiles()) {
			String name = newDir.getAbsolutePath() + "/" + counter++ + ".HTML";
			
			file.renameTo(new File(name));
		}
	}
	
	private static void hasIdenticalFiles(String siteName) throws IOException {
		File dir = new File("dataset/" + siteName + "/origin");
		
		for (File file : dir.listFiles()) {
			if (!hasContained(siteName, file)) {
				File newFile = new File(file.getAbsolutePath().replaceAll("origin", "origin_unique"));
				file.renameTo(newFile);
			}
		}
	}
	
	private static boolean hasContained(String siteName, File file) throws IOException {
		File dir = new File("dataset/" + siteName + "/origin_unique");
		
		if (!dir.exists()) {
			dir.mkdir();
		}
		
		for (File f : dir.listFiles()) {
			if (readFile(f).equals(readFile(file))) {
				return true;
			}
		}
		
		return false;
	}
	
	private static boolean equals(File f1, File f2) throws IOException {
		return readFile(f1).equals(readFile(f2));
	}
	
	private static String readFile(String path) throws IOException {
		return readFile(new File(path));
	}
	
	private static String readFile(File file) throws IOException {
		FileInputStream stream = new FileInputStream(file);
		try {
			FileChannel fc = stream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
			return Charset.defaultCharset().decode(bb).toString();
		}
		finally {
			stream.close();
		}
	}
}
