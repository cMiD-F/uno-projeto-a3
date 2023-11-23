import java.awt.*;

/**
 * Uno
 *
 * Classe PlayDirectionAnimation:
 * Representa um par de orbes que circulam no sentido horário ou anti-horário
 * para
 * mostra a direção do jogo para a ordem dos turnos.
 * 
 * @autor Cauet Damasceno
 * @versão 2023
 */
public class AnimacaoDirecao {
    /**
     * Centro da órbita.
     */
    private final Posicao centre;
    /**
     * Distância em que os orbes estão espaçados do centro.
     */
    private final int radiusFromCentre;
    /**
     * A largura e altura das formas ovais.
     */
    private final int indicatorSize;
    /**
     * Os dois orbes posicionados 180 graus em torno da órbita um do outro.
     */
    private final Posicao movingObject1, movingObject2;
    /**
     * O ângulo atual da órbita.
     */
    private double currentAngle;
    /**
     * Direção da órbita.
     */
    private boolean isIncreasing;

    /**
     * Inicializa as formas ovais para representar a direção do jogo.
     *
     * @param center           Centro da órbita.
     * @param radiusFromCentre Distância em que os orbes estão espaçados do centro.
     * @param IndicatorSize    A largura e altura das formas ovais.
     */
    public AnimacaoDirecao(Posicao centre, int radiusFromCentre, int indicatorSize) {
        this.centre = centre;
        this.radiusFromCentre = radiusFromCentre;
        this.indicatorSize = indicatorSize;
        currentAngle = 0;
        movingObject1 = new Posicao(centre.x, centre.y + radiusFromCentre);
        movingObject2 = new Posicao(centre.x, centre.y - radiusFromCentre);
        isIncreasing = true;
    }

    /**
     * Move as duas formas ovais em um movimento circular ao redor do centro.
     *
     * @param deltaTime Tempo desde a última atualização.
     */
    public void update(int deltaTime) {
        if (isIncreasing)
            currentAngle += deltaTime / 1000.0;
        else
            currentAngle -= deltaTime / 1000.0;
        if (currentAngle > Math.PI * 2)
            currentAngle = 0;

        movingObject1.setPosition(centre.x + (int) (radiusFromCentre * Math.cos(currentAngle)),
                centre.y + (int) (radiusFromCentre * Math.sin(currentAngle)));
        movingObject2.setPosition(centre.x - (int) (radiusFromCentre * Math.cos(currentAngle)),
                centre.y - (int) (radiusFromCentre * Math.sin(currentAngle)));
    }

    /**
     * Desenha as duas formas ovais que representam a direção do jogo.
     *
     * @param g Referência ao objeto Graphics para renderização.
     */
    public void paint(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillOval(movingObject1.x, movingObject1.y, indicatorSize, indicatorSize);
        g.fillOval(movingObject2.x, movingObject2.y, indicatorSize, indicatorSize);
    }

    /**
     * Muda a direção do visual.
     *
     * @param isIncreasing Quando verdadeiro, as formas ovais se movem no sentido
     *                     horário, quando falso, elas se movem no sentido
     *                     anti-horário.
     */
    public void setIsIncreasing(boolean isIncreasing) {
        this.isIncreasing = isIncreasing;
    }

}
