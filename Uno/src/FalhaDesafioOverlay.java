import java.awt.*;

/**
 * Uno
 *
 * Classe FalhaDesafioOverlay :
 * Exibe uma cruz piscando brevemente para mostrar que o desafio falhou.
 *
 * @autor Cauet Damasceno
 * @versão 2023
 */
public class FalhaDesafioOverlay extends WndInterface implements GeralOverlayInterface {
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
    public FalhaDesafioOverlay(Retangulo bounds) {
        super(bounds);
        setEnabled(false);

        int x = bounds.position.x;
        int y = bounds.position.y;
        int sValX = bounds.width / 8;
        int sValY = bounds.height / 8;

        polyXCoords = new int[] { x + sValX, x + 2 * sValX, x + 4 * sValX, x + 6 * sValX, x + 7 * sValX,
                x + 5 * sValX, x + 7 * sValX, x + 6 * sValX, x + 4 * sValX, x + 2 * sValX, x + sValX, x + 3 * sValX };
        polyYCoords = new int[] { y + 2 * sValY, y + sValY, y + 3 * sValY, y + sValY, y + 2 * sValY, y + 4 * sValY,
                y + 6 * sValY, y + 7 * sValY, y + 5 * sValY, y + 7 * sValY, y + 6 * sValY, y + 4 * sValY };
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
            g.setColor(new Color(179, 50, 38));
            g.fillPolygon(polyXCoords, polyYCoords, polyXCoords.length);
            g.setColor(Color.BLACK);
            g.drawPolygon(polyXCoords, polyYCoords, polyXCoords.length);
        }
    }
}
