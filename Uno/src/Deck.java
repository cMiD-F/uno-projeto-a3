import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Uno
 *
 * Deck class:
 * Represents a Deck with a collection of cards.
 *
 * @autor Cauet Damasceno
 * @versão 2023
 */
public class Deck extends Retangulo {
    /**
     * The list of cards currently in the deck.
     */
    private final List<Carta> deck;
    /**
     * The variable used to give every card a unique cardID.
     */
    private int nextCardID;

    /**
     * Initialises the deck with no cards initially.
     *
     * @param position Position for the deck to appear in the game.
     */
    public Deck(Posicao position) {
        super(position, Carta.CARD_WIDTH, Carta.CARD_HEIGHT);
        deck = new ArrayList<>();
        nextCardID = 0;
    }

    /**
     * Draws a the back of a card to represent the deck position.
     * With the word "DECK" appearing above it.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    public void paint(Graphics g) {
        Carta.paintCardBack(g, this);

        g.setColor(Color.BLACK);
        int strWidth = g.getFontMetrics().stringWidth("Dealer");
        g.drawString("Dealer", position.x+width/2-strWidth/2, position.y-4);
    }

    /**
     * If the deck is empty it is populated with a new deck.
     * Then a card is removed from the deck and returned.
     *
     * @return A single card drawn from the deck.
     */
    public Carta drawCard() {
        if(deck.isEmpty()) {
            fillDeck();
        }
        Carta drawnCard = deck.get(0);
        deck.remove(0);
        return drawnCard;
    }

    /**
     * Fills the deck by populating the deck with all the different card variations,
     * and then shuffles the cards to create a random order.
     */
    private void fillDeck() {
        deck.clear();
        // for each colour
        for(int colourID = 0; colourID < 4; colourID++) {
            // Only 1x"0"
            deck.add(new Carta(0, colourID, nextCardID++));
            // Two of 1 to 9, Draw Two, Skip, and Reverse
            for(int faceValue = 1; faceValue <= 12; faceValue++) {
                deck.add(new Carta(faceValue, colourID, nextCardID++));
                deck.add(new Carta(faceValue, colourID, nextCardID++));
            }
        }
        // Four of each Wild and Draw 4 Wild.
        for(int i = 0; i < 4; i++) {
            deck.add(new Carta(13, 4, nextCardID++));
            deck.add(new Carta(14, 4, nextCardID++));
        }
        // randomise order
        Collections.shuffle(deck);
    }
}
