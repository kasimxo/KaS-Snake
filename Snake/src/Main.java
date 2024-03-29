import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

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
	private static int framerate = 100;
	private static int speedX = 0;
	private static int speedY = 0;
	private static boolean alive = true;
	private static KeyListener listener;
	private static Timer timer;
	private static ActionListener alistener;
	private static final long serialVersionUID = 5198887656751766342L;
	private static BufferedImage img;
	private static BufferedImage iconImage;
	private static int score = 0;

	public static void main(String[] args) {
		
		inicialize();
		
		timer = new Timer(framerate, alistener);
		
		timer.start();
		
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
			score++;
			int[] coord = randomCoordinate();
			while(!validarCoord(coord)) {
				coord = randomCoordinate();
			}
			manzana.setPosition(randomCoordinate());
		} else if (snake.getCuerpo().size()>2) {
			//Tal y como estan construidas ahora mismo las mec�nicas de juego no resulta posible chocarte contigo mismo si �nicamente
			//tienes dos segmentos corporales (cabeza-cola) por lo que no tiene sentido realizar esta comprobaci�n
			for (BodyPart parte : snake.getCuerpo()) {
				if (parte!=head && parte.getX()==head.getX() && parte.getY()==head.getY()) {
					if ( parte == snake.getCuerpo().get(2)) {
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
		snake.addSpecific(30, 14);
		
		String iconS = new File("").getAbsolutePath()+"/imgs/icon.png";
		iconS = OsPaths.cleanPath(iconS);
		try {
			iconImage = ImageIO.read(new File(iconS));
		} catch (IOException e1) {
			System.err.println("No se encuentra la imgen icono para el mainframe.");
		}
		
		//Iniciamos tanto la ventaana, como sus caracter�sticas principales
		ventana = new JFrame("KaS - Snake");
		
		var panel = new Main();
        panel.setBackground(Color.white);
        ventana.setBackground(Color.white);
        ventana.setIconImage(iconImage);
        ventana.getContentPane().setPreferredSize(new Dimension(width,height));
        ventana.pack();
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.getContentPane().add(panel, BorderLayout.CENTER);
        //.setLocationRelativeTo(null)
        //Hace que la pantalla aparezca centrada, pero hay que poderlo despues de especificar el tama�o y el .pack()
        //Pero antes del .setVisible()
        ventana.setLocationRelativeTo(null);
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
                case KeyEvent.VK_R:
                	if(!alive) {
                		restart();
                	}
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
				if(!alive) {
					timer.stop();
					showScore();
				} else {
					if(speedX!=0 || speedY!=0) {
						snake.move(speedX,speedY);
					}
					colision();
					
					//Al invocar repaint es cuando dibujamos el frame
					ventana.repaint();
				}
			}
        };
	}

	/**
	 * Aqu� reseteamos toda la partida para que se pueda volver a jugar
	 */
	protected static void restart() {
		Point newPos = ventana.getLocation();
		ventana.dispose();
		alive = true;
		speedX = 0;
		speedY = 0;
		inicialize();
		ventana.setLocation(newPos);
		timer.start();
	}

	protected static void showScore() {
		
		String display = "";
		
		int bestScore = HighScore.readBestScore();
		
		
		
		if(bestScore>=score) {
			display = "<html><p style='text-align: center;'>SCORE: "+ score +"<br/>BEST SCORE: " + bestScore + "<br/>Pulsa [R] para volver a jugar<p/>";
		} else {
			HighScore.saveBestScore(score);
			display = "<html><p style='text-align: center;'>NEW BEST SCORE<br/>SCORE: "+ score +"<br/>PREVIOUS BEST SCORE: " + bestScore + "<br/>Pulsa [R] para volver a jugar<p/>";
		}
		JLabel popupScore = new JLabel(); 
		popupScore.setText(display);
		popupScore.setBounds(0, 0, width, height);
		popupScore.setHorizontalAlignment(JLabel.CENTER);
		popupScore.setVerticalAlignment(JLabel.CENTER);
		popupScore.setVisible(true);
		ventana.add(popupScore);
		ventana.repaint();
		
	}

	/**
	 * Aqu� es donde vamos a pintarlo todo cada frame, es el m�todo encargado de pintar los frames
	 */
	public void paintComponent(Graphics g) {
		
		BodyPart parte;
		
		//Iteramos por todo el cuerpo y lo vamos dibujando
		for (int especifico = 0; especifico<snake.getCuerpo().size(); especifico++) {
			
			parte = snake.getCuerpo().get(especifico);
			
			String head = new File("").getAbsolutePath()+"/imgs/Head";
			head = OsPaths.cleanPath(head);
			String body = new File("").getAbsolutePath()+"/imgs/Straight";
			body = OsPaths.cleanPath(body);
			String bodyL = new File("").getAbsolutePath()+"/imgs/Turn";
			bodyL = OsPaths.cleanPath(bodyL);
			String tail = new File("").getAbsolutePath()+"/imgs/Tail";
			tail = OsPaths.cleanPath(tail);
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
				//Aqu� estamos en la cola
				//vamos a compararlo con la posici�n anterior
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
				//Aqu� en teor�a estamos en el cuerpo
				//LA PREV ES LA QUE ESTA MAS CERCA DE LA CABEZA
				BodyPart prev = snake.getCuerpo().get(especifico-1);
				BodyPart post = snake.getCuerpo().get(especifico+1);
				if(prev.getX()==parte.getX() && parte.getX()==post.getX()) {
					//estamos en columna
					def=body+"U.png";
				} else if (prev.getY()==parte.getY() && parte.getY()==post.getY()) {
					def=body+"R.png";
				} else {
					//aqu� estamos en los casos que no son columna o fila
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
		g.drawImage(manzana.getImg(), manzana.getX()*10, manzana.getY()*10, null);
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
