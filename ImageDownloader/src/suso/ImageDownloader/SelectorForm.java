package suso.ImageDownloader;

import java.awt.*;
import java.awt.event.*;
import java.io.File;

import javax.swing.*;

public class SelectorForm extends JPanel implements ActionListener {
	private static String fuskatorStr = "Fuskator";
	private static String postYourGirlsWSStr = "PostyourgirlsWS";
	File listado_enlaces = new File("D:\\Borrame.txt");
	File directorio_base = new File("D:\\TEMPORAL\\");
	String web_seleccionada = fuskatorStr;
	
	Checkbox scraperChk = new Checkbox("Escanear primero");
	Checkbox downloadChk = new Checkbox("Descargar");

	// JLabel picture;

	public SelectorForm() {
		super(new BorderLayout());

		// Create the radio buttons.
		JRadioButton fuskatorBtn = new JRadioButton(fuskatorStr);
		fuskatorBtn.setMnemonic(KeyEvent.VK_B);
		fuskatorBtn.setActionCommand(fuskatorStr);
		fuskatorBtn.setSelected(true);

		JRadioButton postyourgwsBtn = new JRadioButton(postYourGirlsWSStr);
		postyourgwsBtn.setMnemonic(KeyEvent.VK_C);
		postyourgwsBtn.setActionCommand(postYourGirlsWSStr);

		// Group the radio buttons.
		ButtonGroup rabiobtngroup = new ButtonGroup();
		rabiobtngroup.add(fuskatorBtn);
		rabiobtngroup.add(postyourgwsBtn);

		// Register a listener for the radio buttons.
		fuskatorBtn.addActionListener(this);
		postyourgwsBtn.addActionListener(this);

		// Put the radio buttons in a column in a panel.
		JPanel radioPanel = new JPanel(new GridLayout(0, 1));
		radioPanel.add(fuskatorBtn);
		radioPanel.add(postyourgwsBtn);

		// Añado el botón Aceptar
		Button aceptarBtn = new Button("Aceptar");
		aceptarBtn.addActionListener(this);
		Button seleccionarFicheroBtn = new Button("Selecciona Fichero de Enlaces");
		seleccionarFicheroBtn.setActionCommand("SeleccionarFicheroEnlaces"); //Realmente no se necesita
		seleccionarFicheroBtn.addActionListener(this);
		Button seleccionarDirectorioBtn = new Button("Selecciona Directorio Descarga");
		seleccionarDirectorioBtn.setActionCommand("SeleccionarDirectorioBase"); //Realmente no se necesita
		seleccionarDirectorioBtn.addActionListener(this);

		// add(picture, BorderLayout.CENTER);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(radioPanel);
		add(scraperChk);
		add(downloadChk);
		add(seleccionarFicheroBtn);
		add(seleccionarDirectorioBtn);
		add(aceptarBtn);
		setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
	}

	/** Listens to the radio buttons and buttons */
	public void actionPerformed(ActionEvent evento) {
		System.out.println(evento.getActionCommand());
		// picture.setIcon(createImageIcon("images/" + evento.getActionCommand()
		// + ".gif"));

		if (evento.getActionCommand() == "SeleccionarFicheroEnlaces") {
			listado_enlaces = seleccionaArchivo(listado_enlaces);
		}
		if (evento.getActionCommand() == "SeleccionarDirectorioBase") {
			directorio_base = seleccionaArchivo(directorio_base);
		}
		if (evento.getActionCommand() == "Aceptar") {
			if (scraperChk.getState()) {
				System.out.println(web_seleccionada);
				Scraper.procesar_pagina_web(web_seleccionada, 1);
			}

			if (downloadChk.getState()) {
				ImageDownloader image_downloader = new ImageDownloader();
				image_downloader.procesar(listado_enlaces.getAbsolutePath(),
						directorio_base.getAbsolutePath(), 1, 0);
			}
		}
		if (evento.getActionCommand().equalsIgnoreCase(postYourGirlsWSStr)
				|| evento.getActionCommand().equalsIgnoreCase(fuskatorStr)) {
			web_seleccionada = evento.getActionCommand();
		}
	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	protected static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = SelectorForm.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	private File seleccionaArchivo(File fichero) {
		JFileChooser fileChooser = new JFileChooser();
		System.out.println(fichero.getAbsolutePath());

		/* llamamos el metodo que permite cargar la ventana */
		if (fichero.isDirectory()) {
			System.out.println("directorio");
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fileChooser.setAcceptAllFileFilterUsed(false);
		} else
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		fileChooser.setCurrentDirectory(fichero.getParentFile());
		/* abrimos el archivo seleccionado */
		if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
			fichero = fileChooser.getSelectedFile();
		System.out.println(fichero.getAbsolutePath());
		return fichero;
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	private static void createAndShowGUI() {
		// Create and set up the window.
		JFrame frame = new JFrame("Image Downloader Selector");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create and set up the content pane.
		JComponent newContentPane = new SelectorForm();
		newContentPane.setOpaque(true); // content panes must be opaque
		frame.setContentPane(newContentPane);

		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
}
