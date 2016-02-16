package suso.ImageDownloader;

import java.awt.*;

import javax.swing.*;

import java.awt.event.*;

public class Visualizador extends JFrame
{
	
	static ImageIcon imagen;

	public Visualizador()
	{
		super("Ultima imagen descargada");
		
		/*	COMO LAS IMAGENES, SE DEBEN COLOCAR DENTRO DE COMPONENTES
		(ETIQUETAS, BOTONES, ETC..), LO QUE HARÉ SERÁ UNA ETIQUETA (LABEL)
		SIN TEXTO, (AUNQUE TAMBIÉN SE LE PUEDE AGREGAR TEXTO) Y EN ELLA
		COLOCO LA IMAGEN.
		PARA COLOCAR LA IMAGEN, LE PASAMOS COMO PARAMETRO A LA ETIQUETA
		EL OBJETO QUE CONTIENE LA IMAGEN.
	 */
		JLabel etiqueta = new JLabel(imagen);

		//AGREGAMOS LA ETIQUETA QUE CONTIENE LA IMAGEN AL FRAME
		getContentPane().add(etiqueta);

		//ESTABLECEMOS EL TAMAÑO DEL FRAME
		this.setSize(500, 500);

	}


	public static void main(String H[]) throws InterruptedException
	{
		imagen = new ImageIcon("D:\\Downloads\\BORRAR\\media_thumb-comment-16416013.jpeg");
		Visualizador p = new Visualizador();
		p.show();
		Thread.sleep(1000);
		p.imagen = new ImageIcon("D:\\Downloads\\BORRAR\\meneame-media-comment-16675501.jpeg");

		//Cerrar la ventana
		p.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent evt)
			{
				System.exit(0);
			}
		});
	}

}