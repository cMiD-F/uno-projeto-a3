import java.awt.*;

public class PlayerFlashOverlay extends WndInterface implements GeralOverlayInterface {

    /**
     * Temporizador até que a sobreposição seja ocultada novamente.
     */
    protected double displayTimer;
    /**
     * Mensagem a ser exibida.
     */
    protected String message;
    /**
     * Cor para mostrar.
     */
    private final Color colour;
    /**
     * Tamanho da mensagem.
     */
    protected final int fontSize;

    /**
     * Configura a sobreposição pronta para exibição.
     *
     * @param position Posição onde colocar esta sobreposição.
     * @param mensagem Mensagem a ser exibida.
     * @param color    Cor para mostrar a mensagem.
     */
    public PlayerFlashOverlay(Posicao position, String message, Color colour, int fontSize) {
        super(new Retangulo(position, 40, 40));
        setEnabled(false);
        this.message = message;
        this.colour = colour;
        this.fontSize = fontSize;
    }

    /**
     * Define a mensagem para o novo valor.
     *
     * @param mensagem A mensagem a ser exibida.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Mostra a sobreposição e define um cronômetro para quanto tempo ela aparecerá.
     */
    @Override
    public void showOverlay() {
        setEnabled(true);
        displayTimer = 2000;
    }

    /**
     * Atualiza o cronômetro para ocultar a sobreposição e oculta-a quando atinge 0.
     *
     * @param deltaTime Tempo desde a última atualização.
     */
    @Override
    public void update(int deltaTime) {
        displayTimer -= deltaTime;
        if (displayTimer <= 0) {
            setEnabled(false);
        }
    }

    /**
     * Desenha o texto SKIPPED piscando e mostrando 75% do tempo.
     *
     * @param g Referência ao objeto Graphics para renderização.
     */
    @Override
    public void paint(Graphics g) {
        if (displayTimer % 200 < 150) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, fontSize));
            int strWidth = g.getFontMetrics().stringWidth(message);
            g.drawString(message, bounds.position.x - strWidth / 2 - 2, bounds.position.y - 2);
            g.setColor(colour);
            g.drawString(message, bounds.position.x - strWidth / 2, bounds.position.y);
        }
    }
}