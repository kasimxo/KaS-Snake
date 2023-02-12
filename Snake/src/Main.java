import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main extends JPanel {
	
	private static Apple manzana;
	//El juego se movera en una pantalla 1/10 de las dimensiones, 
	//porque ser� a trav�s de una cuadricula imaginaria de 10x10
	private static int width = 400;
	private static int height = 400;
	private static SnakeBody snake;
	private static JFrame ventana;
	//El frame rate es la cantidad de milisengundos que esperamos para actualizar cada frame
	//No es preciso porque no tiene en cuenta el tiempo que se haya perdido con los procesos
	//IE: Es un tiempo fijo de espera despu�s de todos los procesos
	private static int framerate = 60;
	private static int speedX = 0;
	private static int speedY = 0;
	private static boolean alive = true;
	private static KeyListener listener;
	private static final long serialVersionUID = 5198887656751766342L;

	public static void main(String[] args) {
		
		inicialize();
		
		//Entramos en el loop de juego
		while(alive) {
			snake.move(speedX,speedY);
			//Antes de actualizar el frame comprobamos colisiones
			colision();
			
			//Al invocar repaint es cuando dibujamos el frame
			ventana.repaint();
			
			frame();
		}
		
	}
	
	/**
	 * Este m�todo comprueba (una vez actualizada la posici�n de la serpiente)
	 * si hemos chocado con algo,a saber: <br/>
	 * Un muro -> muerto <br/>
	 * Tu cuerpo -> muerto <br/>
	 * Tu posici�n anterior -> NO MUERTO, MOVIMIENTO NO VALIDO <br/>
	 * Una manzana -> Se alarga tu cuerpo <br/>
	 * <b> SOLO COMPRUEBA LA POSICION DE LA CABEZA, YA QUE ES LA UNICA RELEVANTE </br>
	 */
	private static void colision() {
		boolean incorrecto = false;
		BodyPart head = snake.getCuerpo().get(0);
		//Primero comprobamos la colisi�n con los muros
		if (head.getX()*10 >= width || head.getX()*10 < 0 || head.getY()*10 >= height ||head.getY()*10 < 0) {
			muerto("Colisi�n con muro");
		} else if (head.getX()==manzana.getX() && head.getY()==manzana.getY()) {
			//Despu�s la colisi�n con la manzana
			snake.alargar();
			int[] coord = randomCoordinate();
			while(!validarCoord(coord)) {
				coord = randomCoordinate();
			}
			manzana.setPosition(randomCoordinate());
		} else {
			for (BodyPart parte : snake.getCuerpo()) {
				if (parte!=head && parte.getX()==head.getX() && parte.getY()==head.getY()) {
					if (parte == snake.getCuerpo().get(2)) {
						//En teor�a aqu� estamos comprobando si hemos chocado en direcci�n contraria
						//Aka, la serpiente va hacia arriba y pulsamos hacia abajo
						System.err.println("Direcci�n incorrecta");
						speedX = speedX * -1;
						speedY = speedY * -1;
						incorrecto = true;
						
					} else {
						muerto("Colisi�n con cuerpo");
					}
				}
			}
			if (incorrecto) {
				snake.move(speedX, speedY);
			}
		}
	}

	/**
	 * Este m�todo comprueba una coordenada para ver si es v�lida. <br/>
	 * Para que una coordenada sea v�lida, no puede coincider con el cuerpo de la serpiente <br/>
	 * @param coord la posici�n escogida para validar
	 * @return
	 * true si la coordenada es v�lida <br/>
	 * false si no lo es
	 */
	private static boolean validarCoord(int[] coord) {
			for (BodyPart parte : snake.getCuerpo()) {
				if(parte.getX()==coord[0] && parte.getY()==coord[1]) {
					System.err.println("Posici�n generada inv�lida");
					return false;
				}
			}
		return true;
	}

	private static void muerto(String s) {
		System.err.println("Has muerto por: " + s);
		alive=false;
	}

	/**
	 * Este m�todo sirve parar esperar un intervalo de milisegundos establecido
	 */
	private static void frame() {
		try {
			Thread.sleep(framerate);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Es el primer m�todo al que llamamos al abrir el juego, genera todo lo necesario.
	 * A saber: la ventana, una primera manzana y el cuerpo con una sola posici�n.
	 * Tambi�n configura todo el keyListener
	 */
	private static void inicialize() {
		
		//Aqu� generamos la manzana, dandole una posici�n aleatoria pero comprobando que 
		//no coincida con la que le vamos a dar despu�s al cuerpo
		manzana = new Apple();
		int[] coord = randomCoordinate();
		while(coord[0]==30 && coord[1]==15) {
			coord = randomCoordinate();
		}
		manzana.setPosition(coord);
		
		//Aqu� generamos el cuerpo, con una posici�n fija
		snake = new SnakeBody(new BodyPart(30,15));
		
		
		//Iniciamos tanto la ventaana, como sus caracter�sticas principales
		ventana = new JFrame("KaS - Snake");
		var panel = new Main();
        panel.setBackground(Color.LIGHT_GRAY);
        
        ventana.getContentPane().setPreferredSize(new Dimension(width,height));
        ventana.pack();
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.getContentPane().add(panel, BorderLayout.CENTER);
        ventana.setVisible(true);
        
        
        //Aqu� configuramos todo el keylistener
        listener = new KeyListener() {

            @Override
            public void keyPressed(KeyEvent e) {
                switch(e.getKeyCode()) {
                case KeyEvent.VK_W:
                	speedY = -1;
                	speedX = 0;
                    break;
                case KeyEvent.VK_S:
                	speedY = 1;
                	speedX = 0;
                    break;
                case KeyEvent.VK_A:
                	speedY = 0;
                	speedX = -1;
                    break;
                case KeyEvent.VK_D:
                	speedY = 0;
                	speedX = 1;
                    break;
                }
            }

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
        };
        
        ventana.addKeyListener(listener);
        ventana.setFocusable(true); // very important
	}

	
	/**
	 * Aqu� es donde vamos a pintarlo todo cada frame, es el m�todo encargado de pintar los frames
	 */
	public void paintComponent(Graphics g) {
		
		//Iteramos por todo el cuerpo y lo vamos dibujando
		for (BodyPart parte  : snake.getCuerpo()) {
			
			g.setColor(parte.getColor());
			g.fillRect(parte.getX()*10, parte.getY()*10, parte.getWidth(), parte.getHeight());
			
		}
		
		g.setColor(manzana.getColor());
		g.fillOval(manzana.getX()*10, manzana.getY()*10, manzana.getRadio(), manzana.getRadio());
	}
	
	/**
	 * Este m�todo genera una coordenada int[x, y] aleatoria.
	 * No comprueba esta coordenada con nada.
	 * @return
	 * Un array de dos posiciones, x, y.
	 */
	public static int[] randomCoordinate() {
		int[] coordenada = {(int) (Math.random()*width/10),(int) (Math.random()*height/10)};
		return coordenada;
		
	}

}
