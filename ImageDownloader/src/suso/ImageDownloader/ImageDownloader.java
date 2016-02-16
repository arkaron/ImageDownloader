package suso.ImageDownloader;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.GregorianCalendar;

import javax.activation.MimetypesFileTypeMap;

import org.apache.tika.Tika;

import java.io.FileNotFoundException;

public class ImageDownloader {

	// archivo de texto con urls escritas linea a línea. Escribir con "\\"
	final static String ficheroDeEntrada = "D:\\Borrame.txt";
	static Visualizador visualiza_imagen;
	static int contador_descargas_fallidas = 0;

	public ImageDownloader() {
		super();
		visualiza_imagen = new Visualizador();
		visualiza_imagen.show();
	}

	public static void main(String[] args) {

		if (args.length == 0)
			usoArgumentos();
		else {
			int fuente = parsearEntero(args[0]);
			// directorio donde se guardarán las imágenes. Debe acabar en "\\"
			final String directorio_base = args[1] + "\\";
			final int offset = parsearEntero(args[2]);

			ImageDownloader image_downloader = new ImageDownloader();

			if (existeDirectorio(directorio_base))
				switch (fuente) {
				case 1:
					image_downloader.procesarFicheroEntrada(ficheroDeEntrada, directorio_base);
					break;
				case 2:
					vipchick(directorio_base);
					break;
				case 3:
					postyourgirls(directorio_base + "postyourgirls\\", offset);
					break;
				case 4:
					vecinitas_candidatas(directorio_base + "vecinitas_FHM\\");
					break;
				case 5:
					vecinitas_finalistas(directorio_base + "vecinitas_FHM\\");
					break;
				default:
					System.out.println("Argumentos inválidos");
					break;
				}

			image_downloader.visualiza_imagen.dispose();
			System.out.println("FINAL");
		}
	}

	private static void postyourgirlsws(String direccion_considerada, String directorio_destino) {
		// Sabemos que el nombre del archivo es del tipo:
		// http://www.postyourgirls.ws/images/523/image10.jpg
		// http://www.postyourgirls.ws/images/911/image6.jpg
		// Objetivo: 880_image7_cute-blonde-student-seduced-to-fuck
		direccion_considerada = direccion_considerada.trim();
		int caracter_corte_inicio = direccion_considerada.indexOf("gallery") + 8;
		String archivo_base = direccion_considerada.substring(caracter_corte_inicio,
				direccion_considerada.length() - 4);
		String carpeta = direccion_considerada.substring(direccion_considerada.length() - 3);
		String archivo = "";
		String archivo_plano = "";
		String url_base = "http://www.postyourgirls.ws/images/";
		System.out.println("url_base " + url_base);
		for (int imagen = 1; imagen <= 10; imagen++) {
			archivo = carpeta + "/image" + imagen + ".jpg";
			archivo_plano = carpeta + "_image" + imagen + "_" + archivo_base + ".jpg";
			System.out.println("archivo " + archivo);
			if (descargar_visualizar(url_base + archivo, directorio_destino + archivo_plano))
				break;
		}
	}

	protected static int parsearEntero(String cadena_caracteres) {
		int resultado = 0;
		try {
			resultado = Integer.parseInt(cadena_caracteres);
		} catch (NumberFormatException e) {
			System.out.println("No entiendo como numero " + cadena_caracteres);
		}
		return resultado;
	}

	private static void usoArgumentos() {
		System.out.println("Uso del programa:");
		System.out.println("1. Fichero de entrada");
		System.out.println("2. Vipchick");
		System.out.println("3. PostYourGirls");
	}

	private static boolean existeDirectorio(String directorio) {
		File fRuta = new File(directorio);
		boolean resultado = fRuta.isDirectory();
		if (resultado)
			System.out.println(directorio + ", es un directorio");
		else
			System.out.println(directorio + ", NO es un directorio válido");
		return resultado;
	}

	private static void clasificarDirección(String origen, String directorio_base) {
		if (origen.startsWith("http://fotos.sologatitas.com"))
			sologatitas(origen, directorio_base);
		else if (origen.startsWith("http://fotos.lamaslinda.com"))
			lamaslinda(origen, directorio_base);
		else if (origen.startsWith("http://www.submityourex.com"))
			submityourex1(origen, directorio_base + "submityourex\\");
		else if (origen.startsWith("http://static.cdn.submityourex.com"))
			submityourex2(origen, directorio_base + "submityourex\\");
		else if (origen.startsWith("    <a href=\"/content/"))
			submityourex3(origen, directorio_base + "submityourex\\");
		else if (origen.startsWith("<div class=\"pic\" id="))
			fuskator1(origen, directorio_base + "Fuskator\\");
		else if (origen.startsWith("http://fuskator.com/full/"))
			fuskator2(origen, directorio_base + "Fuskator\\");
		else if (origen.startsWith("http://www.postyourgirls.ws"))
			postyourgirlsws(origen, directorio_base + "postyourgirlsws\\");
		else
			System.out.println("Linea no procesada: " + origen);
	}

	private static boolean descargar_visualizar(String origen, String destino) {
		boolean resultado = false;
		File archivo_destino = new File(destino);

		if (archivo_destino.exists())
			System.out.println("Ya existe el archivo " + destino);
		else {
			if (!descargar(origen, destino))
				contador_descargas_fallidas++;
			else {
				contador_descargas_fallidas = 0;
				visualiza_imagen.setFoto(destino, destino);
			}
			resultado = (contador_descargas_fallidas > 2);
			if (resultado)
				contador_descargas_fallidas = 0;
		}
		return resultado;
	}

	private static boolean descargar(String origen, String destino) {
		boolean resultado = false;
		final Tika tika = new Tika();
		try {
			File archivo_destino = new File(destino);
			//Lo hemos comprobado antes, igual podemos quitarlo si no necesitamos el File
			if (archivo_destino.exists())
				System.out.println("Ya existe el archivo " + destino);
			else {
				File archivo_temporal = new File(destino + ".tmp");
				// Cargamos un objeto URL con la direccion de la imagen que
				// quieras descargar.
				URL url = new URL(origen);
				System.out.println("url: " + url);
				// Abrimos una conexion a esa URL
				URLConnection urlConnection = url.openConnection();
				// Abrimos un stream de entrada para descargarnos la imagen como
				// array de bytes
				DataInputStream bufferentrada = new DataInputStream(urlConnection.getInputStream());
				System.out.println("conectado...");
				int numBytes = 0;
				byte[] byteBuff = new byte[2048];
				// Stream de salida para guardar el fichero en el equipo local
				FileOutputStream dOut = new FileOutputStream(archivo_temporal);
				while (-1 != (numBytes = bufferentrada.read(byteBuff))) {
					dOut.write(byteBuff, 0, numBytes);
				}
				// Lo guardamos.
				dOut.flush();
				dOut.close();
				bufferentrada.close();
				System.out.println("Tamano archivo: " + archivo_temporal.length());
				//System.out.println("MIME1: " + urlConnection.guessContentTypeFromStream(bufferentrada));
				//System.out.println("MIME2: " + Files.probeContentType(archivo_temporal.toPath()));
				String mimetype = tika.detect(archivo_temporal);
				System.out.println("MIME3: " + new MimetypesFileTypeMap().getContentType(archivo_temporal));
				System.out.println("MIME4: " + mimetype);
				//resultado = ((archivo_temporal.length() > 450) & !(archivo_temporal.length() == 75915));
				resultado = (mimetype.contains("image"));
				if (resultado)
					archivo_temporal.renameTo(archivo_destino);
				else
					archivo_temporal.delete();
			}
		} catch (FileNotFoundException e) {
			System.out.println("Error archivo: " + e.getLocalizedMessage());
		} catch (UnknownHostException e) {
			System.out.println("No se ha encontrado el servidor: " + e.getLocalizedMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultado;
	}

	private static boolean descargar_prueba(String origen, String destino) {
		boolean resultado = false;
		BufferedInputStream in = null;
		FileOutputStream fout = null;
		try {
			in = new BufferedInputStream(new URL(origen).openStream());
			fout = new FileOutputStream(destino);

			final byte data[] = new byte[2048];
			int count;
			while ((count = in.read(data, 0, 1024)) != -1) {
				System.out.print(count + ".");
				fout.write(data, 0, count);
			}

			if (in != null) {
				in.close();
			}
			if (fout != null) {
				fout.close();
			}

			resultado = true;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return resultado;
	}

	private static boolean descargar_new(String origen, String destino) {
		boolean resultado = false;
		System.out.println(origen + " -*- " + destino);
		File archivo_destino = new File(destino);

		if (archivo_destino.exists())
			System.out.println("Ya existe el archivo " + destino);
		else {
			try {
				URL website = new URL(origen);
				String tipo_fichero = website.openConnection().getContentType();
				System.out.println("Tipo fichero: " + tipo_fichero);
				if (!tipo_fichero.equalsIgnoreCase("text/html")) {
					ReadableByteChannel rbc = Channels.newChannel(website.openStream());
					FileOutputStream fos = new FileOutputStream(destino);
					fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
					fos.close();
					resultado = true;
				}

			} catch (FileNotFoundException e) {
				System.out.println("Error archivo: " + e.getLocalizedMessage());
			} catch (UnknownHostException e) {
				System.out.println("No se ha encontrado el servidor: " + e.getLocalizedMessage());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return resultado;
	}

	private static void vecinitas_candidatas(String directorio_destino) {
		// http://especiales.fhm.es/vecinitas2012/9/normal/normal_01.jpg
		DecimalFormat df = new DecimalFormat("00");
		for (int ano = 2010; ano <= 2012; ano++) {
			for (int candidata = 1; candidata <= 300; candidata++) { // a partir
																		// de
																		// 100,
																		// dificil
				for (int picint = 1; picint <= 20; picint++) {
					String picstr = df.format(picint);
					String archivo = ano + "_candidata_" + candidata + "_" + picstr + ".jpg";
					String url_base = "http://especiales.fhm.es/vecinitas" + ano + "/" + candidata + "/normal/normal_"
							+ picstr + ".jpg";
					System.out.println("archivo " + archivo);
					System.out.println("url_base " + url_base);
					if (descargar_visualizar(url_base, directorio_destino + archivo))
						break;
				}
			}
		}
	}

	private static void vecinitas_finalistas(String directorio_destino) {
		// http://especiales.fhm.es/vecinitas2012/10finalistas/fotos/92_04.jpg
		DecimalFormat df = new DecimalFormat("00");
		for (int ano = 2011; ano <= 2013; ano++) {
			for (int candidata = 1; candidata <= 100; candidata++) {
				for (int picint = 1; picint <= 20; picint++) {
					String picstr = df.format(picint);
					String archivo = ano + "_finalista_" + candidata + "_" + picstr + ".jpg";
					String url_base = "http://especiales.fhm.es/vecinitas" + ano + "/10finalistas/fotos/" + candidata
							+ "_" + picstr + ".jpg";
					System.out.println("archivo " + archivo);
					System.out.println("url_base " + url_base);
					if (descargar_visualizar(url_base, directorio_destino + archivo))
						break;
				}
			}
		}
	}

	private static void sologatitas(String direccion_hoy, String directorio_destino) {
		// Sabemos que el nombre del archivo son los últimos 13 caracteres
		String archivo = direccion_hoy.substring(direccion_hoy.length() - 13);
		String url_base = direccion_hoy.substring(0, direccion_hoy.length() - 13);
		System.out.println("archivo " + archivo);
		System.out.println("url_base " + url_base);
		for (int ano = 2000; ano <= 2010; ano++) {
			archivo = ano + archivo.substring(4);
			if (descargar_visualizar(url_base + archivo, directorio_destino + archivo))
				break;
		}
	}

	private static void lamaslinda(String direccion_hoy, String directorio_destino) {
		// Sabemos que el nombre del archivo son los últimos 11 caracteres
		String archivo = direccion_hoy.substring(direccion_hoy.length() - 11);
		String url_base = direccion_hoy.substring(0, direccion_hoy.length() - 11);
		System.out.println("archivo " + archivo);
		System.out.println("url_base " + url_base);
		DecimalFormat df = new DecimalFormat("00");
		for (int ano = 0; ano <= 10; ano++) {
			archivo = df.format(ano) + archivo.substring(2);
			if (descargar_visualizar(url_base + archivo, directorio_destino + archivo))
				break;
		}
	}

	private static void vipchick(String directorio_destino) {
		// Sabemos que el nombre del archivo es del tipo:
		// http://www.vipchick.com/a/2007/02-feb/vipchick.com_22.jpg
		String archivo = "";
		String archivo_plano = "";
		String url_base = "http://www.vipchick.com/a/";
		System.out.println("url_base " + url_base);
		String[] meses = { "01-jan", "02-feb", "03-mar", "04-apr", "05-may", "06-jun", "07-jul", "08-aug", "09-sep",
				"10-oct", "11-nov", "12-dec" };
		for (int ano = 2008; ano <= 2009; ano++) {
			for (int mes = 0; mes <= 11; mes++) {// es el indice de un array,
													// asi que empieza por 0
				String mes_actual = ano + "/" + meses[mes] + "/";
				String mes_actual_plano = ano + "_" + meses[mes] + "_";
				for (int dia = 1; dia <= 31; dia++) {
					String diaString = Integer.toString(dia);
					if (dia < 10)
						diaString = "0" + diaString;
					archivo = mes_actual + "vipchick.com_" + diaString + ".jpg";
					archivo_plano = mes_actual_plano + "vipchick.com_" + diaString + ".jpg";
					System.out.println("archivo " + archivo);
					if (descargar_visualizar(url_base + archivo, directorio_destino + archivo_plano))
						break;
				}
			}
		}
	}

	private static void submityourex1(String direccion_hoy, String directorio_destino) {
		// Sabemos que el nombre del archivo es del tipo:
		// http://static.cdn.submityourex.com//5/5/8/a/31020/pic6.jpg
		// a partir de la página de miniaturas
		// http://www.submityourex.com/preview/5/5/8/a/31020
		// http://www.submityourex.com/content/0/9/2/1/30850
		int caracter_corte = Math.max(direccion_hoy.indexOf("preview"), direccion_hoy.indexOf("content"));
		String archivo_base = direccion_hoy.substring(caracter_corte + 7);
		submityourex(archivo_base, directorio_destino);
	}

	private static void submityourex2(String direccion_hoy, String directorio_destino) {
		// Sabemos que el nombre del archivo es del tipo:
		// http://static.cdn.submityourex.com//5/5/8/a/31020/pic6.jpg
		// a partir de la página de miniaturas
		// http://static.cdn.submityourex.com/8/4/4/d/36744//mini_pic1.jpg
		int caracter_corte = direccion_hoy.indexOf("/mini_pic");
		String archivo_base = direccion_hoy.substring(caracter_corte - 15, caracter_corte);
		submityourex(archivo_base, directorio_destino);
	}

	private static void submityourex3(String direccion_hoy, String directorio_destino) {
		// Sabemos que el nombre del archivo es del tipo:
		// http://static.cdn.submityourex.com//5/5/8/a/31020/pic6.jpg
		// a partir de la direccion
		// <a href="/content/0/4/3/b/38697/" title=
		int caracter_corte_inicio = direccion_hoy.indexOf("content") + 7;
		int caracter_corte_fin = direccion_hoy.indexOf("title") - 2;
		String archivo_base = direccion_hoy.substring(caracter_corte_inicio, caracter_corte_fin);
		submityourex(archivo_base, directorio_destino);
	}

	private static void submityourex(String archivo_base, String directorio_destino) {
		if (!archivo_base.endsWith("/"))
			archivo_base += "/";
		String url_base = "http://static.cdn.submityourex.com/" + archivo_base;
		String archivo = "";
		archivo_base = archivo_base.replace('/', '_').substring(9);
		System.out.println("url_base " + url_base);
		for (int picnumber = 1; picnumber <= 20; picnumber++) {
			archivo = "pic" + picnumber + ".jpg";
			if (descargar_visualizar(url_base + archivo, directorio_destino + archivo_base + archivo))
				break;
		}
	}

	private static void fuskator1(String direccion_hoy, String directorio_destino) {
		// Sabemos que el nombre del archivo es del tipo:
		// http://images.fuskator.com/large/hZGb31IUfez/amateur_homemade_latina_shaved_solo_xlogs_1.jpg
		// http://images.fuskator.com/large/lp6YwOwMpqD/5254_erobliss1.jpg
		// a partir de la direccion
		// <div class="pic" id="divpic_9d620f9de4c0719b"><div class="pic_pad"><a
		// title="amateur homemade latina shaved solo xlogs"
		// href="/full/hZGb31IUfez/amateur_homemade_latina_shaved_solo_xlogs.html"><img
		// src="/small/hZGb31IUfez/amateur_homemade_latina_shaved_solo_xlogs_4.jpg"
		int caracter_corte_inicio = direccion_hoy.indexOf("full") + 5;
		int caracter_corte_fin = direccion_hoy.indexOf("img src") - 8;
		String archivo_base = direccion_hoy.substring(caracter_corte_inicio, caracter_corte_fin);
		fuskator(archivo_base, directorio_destino);
	}

	private static void fuskator2(String direccion_hoy, String directorio_destino) {
		// Sabemos que el nombre del archivo es del tipo:
		// http://images.fuskator.com/large/hZGb31IUfez/amateur_homemade_latina_shaved_solo_xlogs_1.jpg
		// http://images.fuskator.com/large/lp6YwOwMpqD/5254_erobliss1.jpg
		// a partir de la direccion
		// <div class="pic" id="divpic_9d620f9de4c0719b"><div class="pic_pad"><a
		// title="amateur homemade latina shaved solo xlogs"
		// href="/full/hZGb31IUfez/amateur_homemade_latina_shaved_solo_xlogs.html"><img
		// src="/small/hZGb31IUfez/amateur_homemade_latina_shaved_solo_xlogs_4.jpg"
		int caracter_corte_inicio = direccion_hoy.indexOf("full") + 5;
		int caracter_corte_fin = direccion_hoy.indexOf(".html");
		String archivo_base = direccion_hoy.substring(caracter_corte_inicio, caracter_corte_fin);
		fuskator(archivo_base, directorio_destino);
	}

	private static void fuskator(String archivo_base, String directorio_destino) {
		// Sabemos que el nombre del archivo es del tipo:
		// http://images.fuskator.com/large/hZGb31IUfez/amateur_homemade_latina_shaved_solo_xlogs_1.jpg
		// http://images.fuskator.com/large/lp6YwOwMpqD/5254_erobliss1.jpg
		// a partir de la direccion
		// <div class="pic" id="divpic_9d620f9de4c0719b"><div class="pic_pad"><a
		// title="amateur homemade latina shaved solo xlogs"
		// href="/full/hZGb31IUfez/amateur_homemade_latina_shaved_solo_xlogs.html"><img
		// src="/small/hZGb31IUfez/amateur_homemade_latina_shaved_solo_xlogs_4.jpg"
		String url_base = "http://images.fuskator.com/large/" + archivo_base;
		String archivo = "";
		archivo_base = archivo_base.replace('/', '_');
		System.out.println("url_base " + url_base);
		for (int picnumber = 1; picnumber <= 30; picnumber++) {
			archivo = "_" + picnumber + ".jpg";
			if (descargar_visualizar(url_base + archivo, directorio_destino + archivo_base + archivo))
				break;
		}
	}

	private static void postyourgirls(String directorio_destino, int offset) {
		// Sabemos que el nombre del archivo es del tipo:
		// http://www.postyourgirls.com/abr10/26/05/10.jpg
		// Nuevo: http://postyourgirls.com/enero14/20/02/01.jpg
		String url_base = "http://www.postyourexgirls.com/";
		System.out.println("url_base: " + url_base);

		// String[] meses = {"ene", "feb", "mar", "abr", "may", "jun", "jul",
		// "ago", "sep", "oct", "nov", "dic" };
		String[] meses = { "enero", "febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "ago", "sep", "Octubre",
				"Noviembre", "Diciembre" };

		GregorianCalendar calendario = new GregorianCalendar();

		calendario.add(GregorianCalendar.DATE, -offset);

		// calendario.add(GregorianCalendar.DATE, -40);

		for (int dias_antes = 0; dias_antes < 10; dias_antes++) {
			boolean finDeSemana = (calendario.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SATURDAY)
					|| (calendario.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SUNDAY);
			if (!finDeSemana) {
				String diaString = intToFormatString(calendario.get(GregorianCalendar.DATE), 2);
				int mesInt = calendario.get(GregorianCalendar.MONTH);
				String anoString = Integer.toString(calendario.get(GregorianCalendar.YEAR)).substring(2);
				// String anoString ="10";

				for (int pagina = 1; pagina < 11; pagina++) {
					String directorio = meses[mesInt] + anoString + "/" + diaString + "/" + intToFormatString(pagina, 2)
							+ "/";
					// File f_directorio = new File(directorio_destino +
					// directorio);
					// f_directorio.mkdirs();

					System.out.println("directorio " + directorio);

					for (int foto = 1; foto < 15; foto++) {
						String archivo = directorio + intToFormatString(foto, 2) + ".jpg";
						String archivo_plano = "postyourgirls-" + archivo.replace('/', '-');
						if (descargar_visualizar(url_base + archivo, directorio_destino + archivo_plano))
							break;
						// descargar
						// (url_base+archivo,directorio_destino+archivo_plano);
						// System.out.println("Origen: " + url_base + archivo);
						// System.out.println("Destino:" + directorio_destino +
						// archivo);
					}
				}
			}
			calendario.add(GregorianCalendar.DATE, -1);
		}
	}

	private static String intToFormatString(int numero, int caracteres) {
		String resultado = Integer.toString(numero);
		while (resultado.length() < caracteres) {
			resultado = "0" + resultado;
		}
		return resultado;
	}

	private static void procesarFicheroEntrada(String fichero_entrada, String directorio_base) {
		try {
			// así se declara el fichero para leer de él
			BufferedReader fichero = new BufferedReader(new FileReader(fichero_entrada));
			String linea = fichero.readLine();
			// cuando ya no se puede leer del fichero readLine devuelve null
			while (linea != null) {
				// procesar como se quiera la línea.
				clasificarDirección(linea, directorio_base);
				linea = fichero.readLine();
			}
			// al acabar siempre hay que cerrar el fichero
			fichero.close();
		} catch (IOException e) {
			// aquí se pondrá el tratamiento de errores por si no se puede leer
			// (por ejemplo si el fichero no existe)
			System.out.println("¡¡No puedo abrir el fichero!!");
		}
	}
}
