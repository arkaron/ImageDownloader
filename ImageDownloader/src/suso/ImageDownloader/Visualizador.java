package suso.ImageDownloader;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.apache.log4j.Logger;

public class Visualizador extends JFrame {

	final static Logger logger = Logger.getLogger(Visualizador.class);
	static ImageIcon imagen;
	JLabel etiqueta = new JLabel(imagen);

	public Visualizador() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
		Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();

		this.setTitle("Image Downloader by Arkaron Labs");
		// this.setLocationRelativeTo(null);
		this.setLocation(0, 0);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		/*
		 * COMO LAS IMAGENES, SE DEBEN COLOCAR DENTRO DE COMPONENTES (ETIQUETAS,
		 * BOTONES, ETC..), LO QUE HARÉ SERÁ UNA ETIQUETA (LABEL) SIN TEXTO,
		 * (AUNQUE TAMBIÉN SE LE PUEDE AGREGAR TEXTO) Y EN ELLA COLOCO LA
		 * IMAGEN. PARA COLOCAR LA IMAGEN, LE PASAMOS COMO PARAMETRO A LA
		 * ETIQUETA EL OBJETO QUE CONTIENE LA IMAGEN.
		 */

		// AGREGAMOS LA ETIQUETA QUE CONTIENE LA IMAGEN AL FRAME
		getContentPane().add(etiqueta);

		// ESTABLECEMOS EL TAMAÑO DEL FRAME
		//this.setSize(rect.width, rect.height - 50);
		//this.show();
		getContentPane().setBackground(Color.DARK_GRAY);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setUndecorated(true);
		this.setVisible(true);
	}

	/*
	 * devuelve la foto original, pero si el tamaño es mayor al contenedor, lo
	 * redimensiona
	 */
	private Icon getFoto(ImageIcon imagen) {
		Dimension d = this.getSize();
		// si la foto es mas grande que el contendor -> redimensionar
		if ((imagen.getIconWidth() > d.getWidth()) || (imagen.getIconHeight() > d.getHeight())) {
			float v_w = getValorEscalado(imagen.getIconWidth(), d.width);
			float v_h = getValorEscalado(imagen.getIconHeight(), d.height);
			//logger.info(" d.width: " + d.width + " d.height: " +  d.height);
			logger.info(" imagen.getIconWidth(): " + imagen.getIconWidth() + " imagen.getIconHeight(): " +  imagen.getIconHeight());
			logger.info(" v_w: " + v_w + " v_h: " +  v_h);
			return Disminuir(imagen, Math.min(v_w, v_h));
		} else {
			return imagen;
		}
	}

	/*
	 * redimensiona la imagen en para que ingrese al contendor pero manteniendo
	 * sus proporciones
	 */
	private ImageIcon Disminuir(ImageIcon i, float v) {
		int valEscalaX = (int) (i.getIconWidth() * v);
		int valEscalaY = (int) (i.getIconHeight() * v);
		Image mini = i.getImage().getScaledInstance(valEscalaX, valEscalaY, Image.SCALE_AREA_AVERAGING);
		return new ImageIcon(mini);
	}

	/* devuelve el valor de escalado para redimensionar la imagen */
	private float getValorEscalado(int tamanyo_imagen, int tamanyo_marco) {
		//return Math.abs((a / new Float(b)) - 2f);
		return Math.abs(tamanyo_marco / new Float(tamanyo_imagen));
	}

	public static void main(String H[]) throws InterruptedException {
		// imagen = new ImageIcon("D:\\ARCHIVADO\\Fotografia\\aimage015.jpg");
		Visualizador p = new Visualizador();
		p.show();
		p.setFoto("D:\\ARCHIVADO\\Fotografia\\aimage016.jpg", "aimage016");
		// p.etiqueta.setIcon(new
		// ImageIcon("D:\\ARCHIVADO\\Fotografia\\aimage016.jpg"));

		// Cerrar la ventana
	}

	protected void setFoto(String ruta, String titulo) {
		etiqueta.setIcon(this.getFoto(new ImageIcon(ruta)));
		this.setTitle(titulo);
	}

}