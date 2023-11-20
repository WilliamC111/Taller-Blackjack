import java.util.Random;


public class Deck{

	private Card[] deck;
	private int counter;
	private static final int NUMBER_OF_CARDS = 52;
	private static final Random rand = new Random();

	public Deck(){
		String [] suit = {"T","C","D","P"};
		deck = new Card[NUMBER_OF_CARDS];
		int position = 0;
		for (int i = 0; i < suit.length; i++) {
			for (int j = 1; j <= 13; j++) {
				deck[position] = new Card(j,suit[i]);	
				position++;
			}
		}
	}//end constructor

	public void shuffle(){
		counter=0;

		for(int i=0; i<deck.length; i++){
			int random = rand.nextInt(NUMBER_OF_CARDS);
			Card t = deck[i];
			deck[i] = deck[random];
			deck[random]=t;
		}
	}
	
	public Card dealCard(){
		
		if(counter<deck.length){
			return deck[counter++];
		}
		else{
			return null;
		}
	}



}//end Class Deck