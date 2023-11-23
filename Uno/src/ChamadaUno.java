import java.awt.*;

/**
 * Uno
 *
 * Classe ChamadaUno:
 * Mostra ONU! para um jogador quando ele chamou UNO!
 *
 * @autor Cauet Damasceno
 * @versão 2023
 */
public class ChamadaUno extends PlayerFlashOverlay {
    /**
     * Configura a sobreposição pronta para exibição.
     *
     * @param position Posição onde colocar esta sobreposição.
     */
    public ChamadaUno(Posicao position) {
        super(position, "UNO!", Color.RED, 40);
        setEnabled(false);
    }

    /**
     * Sorteia a ONU! texto piscando mostrando 75% do tempo.
     *
     * @param g Referência ao objeto Graphics para renderização.
     */
    @Override
    public void paint(Graphics g) {
        if (displayTimer % 200 < 150) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, fontSize));
            g.drawString(message, bounds.position.x, bounds.position.y);
            g.setFont(new Font("Arial", Font.BOLD, fontSize));
            for (int i = 0; i < message.length(); i++) {
                g.setColor(Carta.getColourByID(i % 4));
                g.drawString(message.charAt(i) + "", bounds.position.x + 2 + i * 30, bounds.position.y - 2);
            }
        }
    }
}
