
import java.io.Serializable;

public class Card implements Serializable {

	private static final long serialVersionUID = 710299217036441763L;
	private String suit;
	private int value;

	public Card(int value, String suit) {
		// TODO Auto-generated constructor stub
		this.value = value;
		this.suit = suit;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return value + suit;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

}
