import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class BlackjackTable  extends JPanel{

	ArrayList<ImageIcon> cardIcons;
	int newWidth = 50; 
	int newHeight = 75;
	JPanel panel1;
	public BlackjackTable() {
		 cardIcons = new ArrayList<>();
		// TODO Auto-generated constructor stub
		 setLayout(null);
		 panel1 = new JPanel();
		 panel1.setBounds(360,500 , 200, 200);
		 //panel1.setBackground(null);
		 panel1.setOpaque(false);
		 this.add(panel1);
	}
	
	 @Override
     protected void paintComponent(Graphics g) {
         super.paintComponent(g);
         ImageIcon imagenFondo = new ImageIcon("Client-Server-Blackjack-Game\\Images\\mesa.jpeg");
         Image image = imagenFondo.getImage();

         // Dibujar la imagen de fondo en el panel
         g.drawImage(image, 0, 0, 924, 683, this);
     }
	 
	 public void addImageCards(String card) {
		 	if(card.length()<=3) {
	        ImageIcon originalIcon = new ImageIcon("Client-Server-Blackjack-Game\\Images\\Cards\\" + card + ".png");
	        Image originalImage = originalIcon.getImage();
	        Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
	        ImageIcon scaledIcon = new ImageIcon(scaledImage);
	        cardIcons.add(scaledIcon);
		 	}
	        dibujarCartas();
	    }

	 
	 public void dibujarCartas() {
		 panel1.removeAll(); 

		    for (int i = 0; i < cardIcons.size(); i++) {
		        JLabel label = new JLabel(cardIcons.get(i));
		        panel1.add(label);
		       
		    }

		    panel1.revalidate(); // Actualizar el layout
		    panel1.repaint(); // Repintar el panel
		 }
	 
}
