import java.awt.*;

/**
 * Uno
 *
 * Classe de cartão:
 * Define um cartão incluindo suas propriedades e métodos para mostrar a
 * aparência da frente e do verso.
 *
 * @autor Cauet Damasceno
 * @versão 2023
 */
public class Carta extends Retangulo {
    /**
     * Definição constante da largura de um cartão a ser utilizado para cálculos.
     */
    public static final int CARD_WIDTH = 60;
    /**
     * Definição constante da altura de um cartão a ser utilizado para cálculos.
     */
    public static final int CARD_HEIGHT = 90;

    /**
     * As Strings a serem mostradas para cada faceValueID diferente.
     */
    private static final String[] cardFaceValues = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "+2", "Pular", "Reverter", "+4", "Curinga" };
    /**
     * A etiqueta no centro do cartão.
     */
    private final String cardLabel;
    /**
     * A etiqueta em ambos os cantos do cartão.
     */
    private final String cornerLabel;
    /**
     * O ID usado para determinar qual das quatro cores é (ou 4 se o cartão for
     * um curinga sem cor definida ainda).
     */
    private int colourID;
    /**
     * O faceValue para representar que tipo de número ou outra aparência visual o
     * cartão tem.
     */
    private final int faceValueID;
    /**
     * A cor usada para desenho com base no colorID.
     */
    private Color drawColour;
    /**
     * O ID exclusivo baseado na ordem retirada do baralho.
     */
    private final int cardID;

    public Carta(int faceValueID, int colourID, int cardID) {
        super(new Posicao(0, 0), CARD_WIDTH, CARD_HEIGHT);
        this.faceValueID = faceValueID;
        this.cardLabel = cardFaceValues[faceValueID];
        this.colourID = colourID;
        this.drawColour = getColourByID(colourID);
        this.cardID = cardID;
        if (faceValueID == 10) {
            this.cornerLabel = "+2";
        } else if (faceValueID == 13) {
            this.cornerLabel = "+4";
        } else if (faceValueID == 14) {
            this.cornerLabel = "";
        } else {
            this.cornerLabel = cardLabel;
        }
    }

    /**
     * Desenha a carta voltada para cima com base nas propriedades e no tipo de
     * carta.
     *
     * @param g Referência ao objeto Graphics para renderização.
     */
    public void paint(Graphics g) {
        // Desenha o fundo do cartão com borda branca e cor do cartão
        g.setColor(Color.WHITE);
        g.fillRect(position.x, position.y, width, height);
        g.setColor(drawColour);
        g.fillRect(position.x + 2, position.y + 2, width - 4, height - 4);

        if (colourID != 4) {
            // Desenhe um oval branco para qualquer não-selvagem no meio.
            g.setColor(Color.WHITE);
            g.fillOval(position.x + 4, position.y + height / 2 - ((width - 8) / 4),
                    width - 8, (width - 8) / 2);
        } else {
            // Segmentos vermelhos, azuis, verdes e amarelos para qualquer curinga no meio.
            for (int i = 0; i < 4; i++) {
                g.setColor(getColourByID(i));
                g.fillArc(position.x + 4, position.y + height / 2 - ((width - 8) / 4) - 5,
                        width - 8, (width - 8) / 2 + 10, 270 + 90 * i, 90);
            }
        }

        int fontHeight = (cardLabel.length() > 4) ? 10 : 20;
        g.setFont(new Font("Arial", Font.BOLD, fontHeight));
        int strWidth = g.getFontMetrics().stringWidth(cardLabel);
        // Desenha texto sombreado (preto) para o rótulo central
        if (colourID == 4 || cardLabel.length() <= 4) {
            g.setColor(Color.BLACK);
            g.drawString(cardLabel, position.x + width / 2 - strWidth / 2 - 1,
                    position.y + height / 2 + fontHeight / 2 + 1);
        }
        // Cor para tornar o texto primário visível com base na adição de uma sombra.
        if (colourID == 4) {
            g.setColor(Color.WHITE);
        } else {
            g.setColor(cardLabel.length() <= 4 ? drawColour : Color.BLACK);
        }
        // Desenha o rótulo central
        g.drawString(cardLabel, position.x + width / 2 - strWidth / 2,
                position.y + height / 2 + fontHeight / 2);

        // Desenha rótulos em cada um dos cantos
        fontHeight = (cornerLabel.length() > 2) ? 10 : 20;
        g.setFont(new Font("Arial", Font.BOLD, fontHeight));
        strWidth = g.getFontMetrics().stringWidth(cornerLabel);
        g.setColor(Color.WHITE);
        g.drawString(cornerLabel, position.x + 5, position.y + 5 + fontHeight);
        g.drawString(cornerLabel, position.x + width - strWidth - 5, position.y + height - 5);
    }

    /**
     * @param g       Referência ao objeto Graphics para renderização.
     * @param limites Limites a serem usados para retirar o cartão.
     */
    public static void paintCardBack(Graphics g, Retangulo bounds) {
        g.setColor(Color.WHITE);
        g.fillRect(bounds.position.x, bounds.position.y, bounds.width, bounds.height);
        g.setColor(Color.BLACK);
        g.fillRect(bounds.position.x + 2, bounds.position.y + 2, bounds.width - 4, bounds.height - 4);
        g.setColor(new Color(147, 44, 44));
        g.fillOval(bounds.position.x + 4, bounds.position.y + bounds.height / 2 - ((bounds.width - 8) / 4),
                bounds.width - 8, (bounds.width - 8) / 2);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        int strWidth = g.getFontMetrics().stringWidth("UNO");
        g.drawString("UNO", bounds.position.x + bounds.width / 2 - strWidth / 2 - 2,
                bounds.position.y + bounds.height / 2 - ((bounds.width - 8) / 4) + 2 + 20);
        g.setColor(new Color(226, 173, 67));
        g.drawString("UNO", bounds.position.x + bounds.width / 2 - strWidth / 2,
                bounds.position.y + bounds.height / 2 - ((bounds.width - 8) / 4) + 20);
    }

    /**
     * Define a cor e a cor usada para desenhar.
     *
     * @param colorID A cor para definir o cartão. 0=Vermelho, 1=Azul, 2=Verde,
     *                3=Amarelo, 4=Selvagem
     */
    public void setColour(int colourID) {
        this.colourID = colourID;
        drawColour = getColourByID(colourID);
    }

    /**
     * Obtém a cor atual do cartão.
     *
     * @return O colorID atual deste cartão.
     */
    public int getColourID() {
        return colourID;
    }

    /**
     * Obtém o faceValueID do cartão.
     *
     * @return O faceValueID atual do cartão.
     */
    public int getFaceValueID() {
        return faceValueID;
    }

    /**
     * Obtém o número exclusivo que representa apenas este cartão.
     *
     * @return O cardID exclusivo que identifica este cartão.
     */
    public int getCardID() {
        return cardID;
    }

    /**
     * Obtém uma cor mapeada para o colorID ou Preto.
     *
     * @param colorID O colorID para obter uma cor para desenhar.
     * @return Uma cor mapeada para o colorID. 0=Vermelho, 1=Azul, 2=Verde,
     *         3=Amarelo,
     *         Default=Preto.
     */
    public static Color getColourByID(int colourID) {
        return switch (colourID) {
            case 0 -> new Color(191, 48, 48);
            case 1 -> new Color(36, 94, 160);
            case 2 -> new Color(115, 187, 54);
            case 3 -> new Color(238, 188, 65);
            default -> Color.BLACK;
        };
    }

    /**
     * Obtém a pontuação com base no faceValue do cartão.
     * As cartas numeradas são o seu valor nominal, wild e +4 valem 50,
     * e outros valem 20.
     *
     * @return A pontuação calculada para este cartão.
     */
    public int getScoreValue() {
        if (faceValueID < 10)
            return faceValueID;
        else if (faceValueID == 13 || faceValueID == 14)
            return 50;
        else
            return 20;
    }
}
