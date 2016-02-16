package suso.ImageDownloader;


import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Scraper {
	public static void main(String[] args) throws IOException {
		Validate.isTrue(args.length == 1, "Por favor, proporciona una URL");
		int pagina = suso.ImageDownloader.ImageDownloader
				.parsearEntero(args[0]);
		String url_base = "http://fuskator.com/page/";

		for (; pagina > 1; pagina--) {
			String url = url_base + pagina+ "/";
			System.out.println("Buscando en: " + url);

			Document doc = Jsoup.connect(url).get();
			Elements links = doc.select("a[href]");

			System.out.println(links.size() + " enlaces encontrados en " + url);
			for (Element link : links) {
				String ruta = link.attr("abs:href");
				if (ruta.startsWith("http://fuskator.com/full/"))
					escribir(ruta);
				else
					System.out.println("Ruta descartada: " + ruta);
			}
		}
		System.out.println("FINAL");
	}
 
	private static void escribir(String link) {
		File f = new File("D:\\Fuskator.txt");
		// Escritura
		try {
			FileWriter w = new FileWriter(f,f.exists());
			BufferedWriter bw = new BufferedWriter(w);
			PrintWriter wr = new PrintWriter(bw);
			wr.println(link);// escribimos en el archivo
			wr.close();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}