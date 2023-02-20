import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Main extends JPanel {
	
	private static Apple manzana;
	//El juego se movera en una pantalla 1/10 de las dimensiones, 
	//porque será a través de una cuadricula imaginaria de 10x10
	private static int width = 400;
	private static int height = 400;
	private static SnakeBody snake;
	private static JFrame ventana;
	//El frame rate es la cantidad de milisengundos que esperamos para actualizar cada frame
	//No es preciso porque no tiene en cuenta el tiempo que se haya perdido con los procesos
	//IE: Es un tiempo fijo de espera después de todos los procesos
	private static int framerate = 100;
	private static int speedX = 0;
	private static int speedY = 0;
	private static boolean alive = true;
	private static KeyListener listener;
	private static Timer timer;
	private static ActionListener alistener;
	private static final long serialVersionUID = 5198887656751766342L;
	private static BufferedImage img;
	private static BufferedImage imgApple;

	public static void main(String[] args) {
		
		File file = new File("C:\\Users\\Andrés\\git\\KaS-Snake\\Snake\\imgs\\Apple.png");
		
		try {
			imgApple =  ImageIO.read(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		
		}
		
		inicialize();
		
		timer = new Timer(framerate, alistener);
		
		timer.start();
		
		
	}
	
	/**
	 * Este método comprueba (una vez actualizada la posición de la serpiente)
	 * si hemos chocado con algo,a saber: <br/>
	 * Un muro -> muerto <br/>
	 * Tu cuerpo -> muerto <br/>
	 * Tu posición anterior -> NO MUERTO, MOVIMIENTO NO VALIDO <br/>
	 * Una manzana -> Se alarga tu cuerpo <br/>
	 * <b> SOLO COMPRUEBA LA POSICION DE LA CABEZA, YA QUE ES LA UNICA RELEVANTE </br>
	 */
	private static void colision() {
		boolean incorrecto = false;
		BodyPart head = snake.getCuerpo().get(0);
		//Primero comprobamos la colisión con los muros
		if (head.getX()*10 >= width || head.getX()*10 < 0 || head.getY()*10 >= height ||head.getY()*10 < 0) {
			muerto("Colisión con muro");
		} else if (head.getX()==manzana.getX() && head.getY()==manzana.getY()) {
			//Después la colisión con la manzana
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
						//En teoría aquí estamos comprobando si hemos chocado en dirección contraria
						//Aka, la serpiente va hacia arriba y pulsamos hacia abajo
						System.err.println("Dirección incorrecta");
						speedX = speedX * -1;
						speedY = speedY * -1;
						incorrecto = true;
						
					} else {
						muerto("Colisión con cuerpo");
					}
				}
			}
			if (incorrecto) {
				snake.move(speedX, speedY);
			}
		}
	}

	/**
	 * Este método comprueba una coordenada para ver si es válida. <br/>
	 * Para que una coordenada sea válida, no puede coincider con el cuerpo de la serpiente <br/>
	 * @param coord la posición escogida para validar
	 * @return
	 * true si la coordenada es válida <br/>
	 * false si no lo es
	 */
	private static boolean validarCoord(int[] coord) {
			for (BodyPart parte : snake.getCuerpo()) {
				if(parte.getX()==coord[0] && parte.getY()==coord[1]) {
					System.err.println("Posición generada inválida");
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
	 * Este método sirve parar esperar un intervalo de milisegundos establecido
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
	 * Es el primer método al que llamamos al abrir el juego, genera todo lo necesario.
	 * A saber: la ventana, una primera manzana y el cuerpo con una sola posición.
	 * También configura todo el keyListener
	 */
	private static void inicialize() {
		
		//Aquí generamos la manzana, dandole una posición aleatoria pero comprobando que 
		//no coincida con la que le vamos a dar después al cuerpo
		manzana = new Apple();
		int[] coord = randomCoordinate();
		while(coord[0]==30 && coord[1]==15) {
			coord = randomCoordinate();
		}
		manzana.setPosition(coord);
		
		//Aquí generamos el cuerpo, con una posición fija
		snake = new SnakeBody(new BodyPart(30,15));
		
		
		//Iniciamos tanto la ventaana, como sus características principales
		ventana = new JFrame("KaS - Snake");
		var panel = new Main();
        panel.setBackground(Color.white);
        ventana.setBackground(Color.white);
        
        ventana.getContentPane().setPreferredSize(new Dimension(width,height));
        ventana.pack();
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.getContentPane().add(panel, BorderLayout.CENTER);
        ventana.setVisible(true);
        
        
        //Aquí configuramos todo el keylistener
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
                case KeyEvent.VK_UP:
                	speedY = -1;
                	speedX = 0;
                    break;
                case KeyEvent.VK_DOWN:
                	speedY = 1;
                	speedX = 0;
                    break;
                case KeyEvent.VK_LEFT:
                	speedY = 0;
                	speedX = -1;
                    break;
                case KeyEvent.VK_RIGHT:
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
        
        alistener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				snake.move(speedX,speedY);
				colision();
				
				//Al invocar repaint es cuando dibujamos el frame
				ventana.repaint();
				
			}
        	
        };
	}

	
	/**
	 * Aquí es donde vamos a pintarlo todo cada frame, es el método encargado de pintar los frames
	 */
	public void paintComponent(Graphics g) {
		
		//Iteramos por todo el cuerpo y lo vamos dibujando
		for (int especifico = 0; especifico<snake.getCuerpo().size(); especifico++) {
			
			BodyPart parte = snake.getCuerpo().get(especifico);
			
			String head = "C:\\Users\\Andrés\\git\\KaS-Snake\\Snake\\imgs\\Head";
			String body = "C:\\Users\\Andrés\\git\\KaS-Snake\\Snake\\imgs\\Straight";
			String bodyL = "C:\\Users\\Andrés\\git\\KaS-Snake\\Snake\\imgs\\Turn";
			String tail = "C:\\Users\\Andrés\\git\\KaS-Snake\\Snake\\imgs\\Tail";
			String def = "";
			
			if (especifico==0) {
				
				if(speedX>0) {
					def=head+"R.png";
				} else if (speedX<0) {
					def=head+"L.png";
				} else if (speedY<0) {
					def=head+"U.png";
				} else {
					def=head+"D.png";
				}
				
			} else if (especifico==snake.getCuerpo().size()-1) {
				//Aquí estamos en la cola
				//vamos a compararlo con la posición anterior
				BodyPart prev = snake.getCuerpo().get(especifico-1);
				if (parte.getX()==prev.getX()) {
					if(parte.getY()>prev.getY()) {
						def=tail+"U.png";
					} else {
						def=tail+"D.png";
					}
				} else {
					if(parte.getX()>prev.getX()) {
						def=tail+"L.png";
					} else {
						def=tail+"R.png";
					}
				}
			} else {
				//Aquí en teoría estamos en el cuerpo
				//LA PREV ES LA QUE ESTA MAS CERCA DE LA CABEZA
				BodyPart prev = snake.getCuerpo().get(especifico-1);
				BodyPart post = snake.getCuerpo().get(especifico+1);
				if(prev.getX()==parte.getX() && parte.getX()==post.getX()) {
					//estamos en columna
					def=body+"U.png";
				} else if (prev.getY()==parte.getY() && parte.getY()==post.getY()) {
					def=body+"R.png";
				} else {
					//aquí estamos en los casos que no son columna o fila
					if(prev.getX()>post.getX() && prev.getY()>post.getY()) {
						//Todo esto esta bien
						if(prev.getY()==parte.getY()) {
							def=bodyL+"R.png";
						} else {
							def=bodyL+"L.png";
						}
					} else if(prev.getX()>post.getX() && prev.getY()<post.getY() ) {
						if(prev.getY()==parte.getY()) {
							def=bodyL+"D.png";
						} else {
							def=bodyL+"U.png";
						}
					} else if(prev.getX()<post.getX() && prev.getY()<post.getY() ) {
						if(prev.getY()==parte.getY()) {
							def=bodyL+"L.png";
						} else {
							def=bodyL+"R.png";
						}
					} else {
						if(prev.getY()==parte.getY()) {
							def=bodyL+"U.png";
						} else {
							def=bodyL+"D.png";
						}
					}
				}
				
			}
			
			
			try {
				
				img = ImageIO.read(new File(def));
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			g.drawImage(img, parte.getX()*10, parte.getY()*10, null);
			
			
		}
		g.drawImage(imgApple, manzana.getX()*10, manzana.getY()*10, null);
	}
	
	/**
	 * Este método genera una coordenada int[x, y] aleatoria.
	 * No comprueba esta coordenada con nada.
	 * @return
	 * Un array de dos posiciones, x, y.
	 */
	public static int[] randomCoordinate() {
		int[] coordenada = {(int) (Math.random()*width/10),(int) (Math.random()*height/10)};
		return coordenada;
		
	}

}
