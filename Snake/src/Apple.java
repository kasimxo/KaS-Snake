import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Apple {
	
	private int x,y;
	private Color color = Color.red;
	private int radio = 10;
	
	private BufferedImage img;
	
	
	
	public Apple() {
		super();
		File file = new File("C:\\Users\\Andrés\\git\\KaS-Snake\\Snake\\imgs\\Apple.png");
		try {
			this.img =  ImageIO.read(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Apple(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public void setPosition(int[] posicion) {
		this.x = posicion[0];
		this.y = posicion[1];
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public int getRadio() {
		return radio;
	}

	public void setRadio(int radio) {
		this.radio = radio;
	}

	public BufferedImage getImg() {
		return img;
	}

	
}
