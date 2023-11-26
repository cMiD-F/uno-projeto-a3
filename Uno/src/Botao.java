import java.awt.*;

/**
 * Uno
 *
 * Classe de botão:
 * Define um botão simples composto por uma região retangular,
 * algum texto para centralizar nele, um estado de foco e um
 * actionID que está disponível para fornecer contexto ao botão.
 *
 * @autor Cauet Damasceno
 * @versão 2023
 */
public class Botao extends Retangulo {
    /**
     * Um número que pode ser usado para contextualizar quando o botão foi clicado.
     */
    private final int actionID;
    /**
     * Verdadeiro quando o mouse está sobre o retângulo causando uma mudança de cor.
     */
    private boolean isHovered;
    /**
     * Texto para centralizar no botão.
     */
    private final String text;

    /**
     * Configura o botão pronto para exibição e interação.
     *
     * @param position Canto superior esquerdo do botão.
     * @param width    Largura do botão.
     * @param height   Altura do botão.
     * @param text     Texto para centralizar o botão.
     * @param actionID Um número que pode ser usado para fornecer contexto para
     *                 quando o botão foi clicado.
     */
    public Botao(Posicao position, int width, int height, String text, int actionID) {
        super(position, width, height);
        this.actionID = actionID;
        isHovered = false;
        this.text = text;
    }

    /**
     * Desenha um plano de fundo, borda e texto. As cores mudam dependendo se
     * está pairado.
     *
     * @param g Referência ao objeto Graphics para renderização.
     */
    public void paint(Graphics g) {
        if (isHovered) {
            g.setColor(new Color(63, 78, 123));
            g.fillRect(position.x - 3, position.y - 3, width + 6, height + 6);
        } else {
            g.setColor(new Color(123, 133, 163));
            g.fillRect(position.x, position.y, width, height);
        }

        if (isHovered) {
            g.setColor(Color.WHITE);
            g.drawRect(position.x - 3, position.y - 3, width + 6, height + 6);
        } else {
            g.setColor(Color.BLACK);
            g.drawRect(position.x, position.y, width, height);
        }
        g.setFont(new Font("Arial", Font.BOLD, 20));
        int strWidth = g.getFontMetrics().stringWidth(text);
        g.drawString(text, position.x + width / 2 - strWidth / 2, position.y + height / 2 + 8);
    }

    /**
     * Pode ser chamado para obter um actionID personalizado para determinar o
     * resultado de um botão
     * imprensa.
     *
     * @return O actionID a ser usado para fornecer contexto ao botão.
     */
    public int getActionID() {
        return actionID;
    }

    /**
     * Atualiza o estado de foco para o valor especificado.
     *
     * @param isHovering Quando verdadeiro as cores mudam no botão.
     */
    public void setHovering(boolean isHovering) {
        this.isHovered = isHovering;
    }
}
