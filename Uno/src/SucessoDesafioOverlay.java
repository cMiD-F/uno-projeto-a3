import java.awt.*;

/**
 * Uno
 *
 * Classe SucessoDesafioOverlay:
 * Exibe um tique piscando brevemente para mostrar que o desafio foi
 * bem-sucedido.
 *
 * @autor Cauet Damasceno
 * @versão 2023
 */
public class SucessoDesafioOverlay extends WndInterface implements GeralOverlayInterface {
    /**
     * Temporizador até que a sobreposição seja ocultada novamente.
     */
    private double displayTimer;
    /**
     * Coordenadas X para fazer o gráfico aparecer.
     */
    private final int[] polyXCoords;
    /**
     * Coordenadas Y para fazer o gráfico aparecer.
     */
    private final int[] polyYCoords;

    /**
     * Inicialize a interface com limites e deixe-a pronta para ser habilitada.
     *
     * @param limites Região onde o objeto é mostrado.
     */
    public SucessoDesafioOverlay(Retangulo bounds) {
        super(bounds);
        setEnabled(false);

        int widthDiv6 = bounds.width / 6;
        int x = bounds.position.x;
        int y = bounds.position.y;
        int heightDiv6 = bounds.height / 6;

        polyXCoords = new int[] { x, x + widthDiv6, x + widthDiv6 * 2,
                x + widthDiv6 * 5, x + bounds.width, x + widthDiv6 * 2 };
        polyYCoords = new int[] { y + heightDiv6 * 4, y + heightDiv6 * 3, y + heightDiv6 * 4,
                y + heightDiv6 * 2, y + heightDiv6 * 3, y + bounds.height };
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
     * Desenha o tick piscando mostrando 75% das vezes.
     *
     * @param g Referência ao objeto Graphics para renderização.
     */
    @Override
    public void paint(Graphics g) {
        if (displayTimer % 200 < 150) {
            g.setColor(new Color(106, 163, 22));
            g.fillPolygon(polyXCoords, polyYCoords, polyXCoords.length);
            g.setColor(Color.BLACK);
            g.drawPolygon(polyXCoords, polyYCoords, polyXCoords.length);
        }
    }
}
