import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Uno
 *
 * Classe Deck:
 * Representa um Deck com uma coleção de cartas.
 *
 * @autor Cauet Damasceno
 * @versão 2023
 */
public class Deck extends Retangulo {
    /**
     * A lista de cartas atualmente no baralho.
     */
    private final List<Carta> deck;
    /**
     * A variável usada para dar a cada cartão um cardID exclusivo.
     */
    private int nextCardID;

    /**
     * Inicializa o baralho inicialmente sem cartas.
     *
     * @param position Posição para o deck aparecer no jogo.
     */
    public Deck(Posicao position) {
        super(position, Carta.CARD_WIDTH, Carta.CARD_HEIGHT);
        deck = new ArrayList<>();
        nextCardID = 0;
    }

    /**
     * Desenha o verso de uma carta para representar a posição do baralho.
     * Com a palavra "DECK" aparecendo acima dele.
     *
     * @param g Referência ao objeto Graphics para renderização.
     */
    public void paint(Graphics g) {
        Carta.paintCardBack(g, this);

        g.setColor(Color.BLACK);
        int strWidth = g.getFontMetrics().stringWidth("Dealer");
        g.drawString("Dealer", position.x + width / 2 - strWidth / 2, position.y - 4);
    }

    /**
     * Se o baralho estiver vazio, ele será preenchido com um novo baralho.
     * Em seguida, uma carta é retirada do baralho e devolvida.
     *
     * @return Uma única carta retirada do baralho.
     */
    public Carta drawCard() {
        if (deck.isEmpty()) {
            fillDeck();
        }
        Carta drawnCard = deck.get(0);
        deck.remove(0);
        return drawnCard;
    }

    /**
     * Preenche o baralho preenchendo-o com todas as diferentes variações de cartas,
     * e depois embaralha as cartas para criar uma ordem aleatória.
     */
    private void fillDeck() {
        deck.clear();
        // para cada cor
        for (int colourID = 0; colourID < 4; colourID++) {
            // Apenas 1x"0"
            deck.add(new Carta(0, colourID, nextCardID++));
            // Dois de 1 a 9, Desenhar Dois, Pular e Reverter
            for (int faceValue = 1; faceValue <= 12; faceValue++) {
                deck.add(new Carta(faceValue, colourID, nextCardID++));
                deck.add(new Carta(faceValue, colourID, nextCardID++));
            }
        }
        // Quatro de cada Wild e sorteio de 4 Wild.
        for (int i = 0; i < 4; i++) {
            deck.add(new Carta(13, 4, nextCardID++));
            deck.add(new Carta(14, 4, nextCardID++));
        }
        // randomiza a ordem
        Collections.shuffle(deck);
    }
}
