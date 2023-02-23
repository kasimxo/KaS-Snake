import java.util.ArrayList;
import java.util.List;

public class SnakeBody {

	private List<BodyPart> cuerpo;
	//Este buffer es el que nos guarda la �ltima posici�n eliminada, por si necesitamos rescatarla en caso de comer una manzana
	private BodyPart buffer;

	/** 
	 * Es el constructor al que llamamos al iniciar el juego. Nos crear� el cuerpo con el segmento inicial.
	 * @param head
	 */
	public SnakeBody(BodyPart head) {
		super();
		this.cuerpo = new ArrayList();
		cuerpo.add(head);
	}
	
	public void alargar() {
		cuerpo.add(buffer);
	}
	
	/**
	 * Aqu� vamos a mover la serpiente, en un principio a�adimos una nueva parte en las posiciones
	 * y eliminamos la �ltima posici�n.
	 * @param speedX
	 * @param speedY
	 */
	public void move(int speedX, int speedY) {
		cuerpo.add(0, new BodyPart(cuerpo.get(0).getX()+speedX,cuerpo.get(0).getY()+speedY));
		buffer = cuerpo.get(cuerpo.size()-1);
		cuerpo.remove(cuerpo.size()-1);
	}

	public List<BodyPart> getCuerpo() {
		return cuerpo;
	}

	public void setCuerpo(List<BodyPart> cuerpo) {
		this.cuerpo = cuerpo;
	}
	/**
	 * A�ade un segmento de cuerpo en la posici�n indicada
	 * Se usa en el inicio para que el cuerpo se genere con dos segmentos, cabeza y cola
	 * @param x
	 * @param y
	 */
	public void addSpecific(int x, int y) {
		this.cuerpo.add(new BodyPart(x,y));
	}
}
