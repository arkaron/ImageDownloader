package suso.ImageDownloader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Scraper {

	final static Logger logger = Logger.getLogger(Scraper.class);

	static File fichero_escitura = new File("D:\\Borrame.txt");
	static String url_base;
	static String url_base_imagen;

	public static void main(String[] args) {
		Validate.isTrue(args.length == 2, "Por favor, proporciona una URL y un número de página");
		int pagina = suso.ImageDownloader.ImageDownloader.parsearEntero(args[1]);
		procesar_pagina_web(args[0], pagina);
	}

	public static void procesar_pagina_web(String web, int pagina) {
		// Solo en Java 7 el switch soporta cadenas (String)
		switch (web) {
		case "Fuskator":
		case "fuskator":
			url_base = "http://fuskator.com/page/";
			url_base_imagen = "http://fuskator.com/full/";
			break;
		case "PostyourgirlsWS":
		case "postyourgirlsws":
			url_base = "http://postyourgirls.ws/";
			url_base_imagen = "http://www.postyourgirls.ws";
			break;
		default:
			logger.info("Proporcione web y número de página");
		}

		if (!url_base.isEmpty()) {
			for (; pagina >= 1; pagina--) {
				String url = url_base + pagina + "/";
				logger.info("Buscando en: " + url);

				try {
					Document doc = Jsoup.connect(url).get();
					Elements links = doc.select("a[href]");

					logger.info(links.size() + " enlaces encontrados en " + url);
					for (Element link : links) {
						String ruta = link.attr("abs:href");
						if (ruta.startsWith(url_base_imagen))
							escribir(ruta, fichero_escitura);
						else
							logger.info("Ruta descartada: " + ruta);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			logger.info("FINAL");
		}
	}

	private static void escribir(String link, File f) {
		// Escritura
		try {
			FileWriter w = new FileWriter(f, f.exists());
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