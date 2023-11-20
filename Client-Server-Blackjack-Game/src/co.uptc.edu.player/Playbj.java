import java.util.ArrayList;

public class Playbj {

	private boolean bust = false;
	private int cardTotal = 0;

	private ArrayList<Card> cards;

	public Playbj(Card c1, Card c2) {
		cards = new ArrayList();
		cards.add(c1);
		cards.add(c2);
	}// end Constructor

	public int GetCardTotal() {
		int suma = 0;
		int aces = 0;
		boolean estate = false;
		for (int i = 0; i < cards.size(); i++) {
			if (cards.get(i).getValue() == 11 || cards.get(i).getValue() == 12 || cards.get(i).getValue() == 13) {
				suma += 10;
			} else if (cards.get(i).getValue() == 1) {
				suma += 11;
				estate = true;
				aces++;
			} else {
				suma += cards.get(i).getValue();
			}

			if (suma > 21 && cards.get(i).getValue() == 1) {
				suma -= 10;
			}
			if (suma > 21 && estate == true) {
				suma -= 10;
				estate = false;
			}
		}
		cardTotal = suma;
		return suma;
	}
	
	public void addCard(Card card) {
		cards.add(card);
	}

	public boolean CheckBust() {
		if (cardTotal > 21) {
			return true;
		} else {
			return bust;
		}
	}

}
