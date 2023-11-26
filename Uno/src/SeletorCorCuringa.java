import java.awt.*;

/**
 * Uno
 *
 * Classe WildColourSelectorOverlay:
 * Define a sobreposição para escolha de cores para cartas curinga e +4.
 *
 * @autor Cauet Damasceno
 * @versão 2023
 */
public class SeletorCorCuringa extends WndInterface implements TurnDecisionOverlayInterface {
    /**
     * Rastreamento da área pairada com a posição e região atuais da grade
     * número.
     */
    private int hoveredRegion, hoverX, hoverY;
    /**
     * Referência ao TurnAction que acionou a exibição desta sobreposição.
     */
    private TurnActionFactory.TurnDecisionAction controllingTurnAction;

    /**
     * Inicializa a sobreposição usando a região especificada.
     *
     * @param position Posição para colocar a sobreposição.
     * @param width    Largura da sobreposição.
     * @param height   Altura da sobreposição.
     */
    public SeletorCorCuringa(Posicao position, int width, int height) {
        super(new Retangulo(position, width, height));
        setEnabled(false);
    }

    /**
     * Faz nada.
     *
     * @param deltaTime Tempo desde a última atualização.
     */
    @Override
    public void update(int deltaTime) {

    }

    /**
     * Desenha uma interface de seleção para escolher uma cor.
     *
     * @param g Referência ao objeto Graphics para renderização.
     */
    public void paint(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(bounds.position.x - 20, bounds.position.y - 40, bounds.width + 40, bounds.height + 60);
        // Segmentos vermelhos, azuis, verdes e amarelos para qualquer curinga no meio.
        for (int i = 0; i < 4; i++) {
            g.setColor(Carta.getColourByID(i));
            if (i == hoveredRegion) {
                int offsetX = (hoverX == 0) ? -1 : 1;
                int offsetY = (hoverY == 0) ? -1 : 1;
                g.fillArc(bounds.position.x + offsetX * 10, bounds.position.y + offsetY * 10,
                        bounds.width, bounds.height, 270 + 90 * i, 90);
            } else {
                g.fillArc(bounds.position.x, bounds.position.y, bounds.width, bounds.height,
                        270 + 90 * i, 90);
            }
        }
        g.setColor(Color.WHITE);
        g.drawRect(bounds.position.x - 20, bounds.position.y - 40, bounds.width + 40, bounds.height + 60);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        String message = "Escolha uma cor";
        int strWidth = g.getFontMetrics().stringWidth(message);
        g.drawString(message, bounds.position.x + bounds.width / 2 - strWidth / 2, bounds.position.y - 5);
    }

    /**
     * Atualiza a região atualmente pairada na roda de cores.
     *
     * @param mousePosition Posição do mouse durante este movimento.
     */
    @Override
    public void handleMouseMove(Posicao mousePosition) {
        hoveredRegion = -1;
        if (bounds.isPositionInside(mousePosition)) {
            hoverX = (mousePosition.x - bounds.position.x) / (bounds.width / 2);
            hoverY = (mousePosition.y - bounds.position.y) / (bounds.height / 2);
            if (hoverX == 0 && hoverY == 0)
                hoveredRegion = 2;
            else if (hoverX == 1 && hoverY == 0)
                hoveredRegion = 1;
            else if (hoverX == 1 && hoverY == 1)
                hoveredRegion = 0;
            else if (hoverX == 0 && hoverY == 1)
                hoveredRegion = 3;
        }
    }

    /**
     * Verifica se a região clicada é válida para seleção de cores e
     * aplica a cor como uma ação, se apropriado.
     *
     * @param mousePosition Posição do cursor do mouse durante o pressionamento.
     * @param isLeft        Se verdadeiro, o botão do mouse está para a esquerda,
     *                      caso contrário, está para a direita.
     */
    @Override
    public void handleMousePress(Posicao mousePosition, boolean isLeft) {
        handleMouseMove(mousePosition);
        if (hoveredRegion != -1) {
            controllingTurnAction.injectProperty("colourID", hoveredRegion);
            controllingTurnAction.injectFlagProperty(1);
            setEnabled(false);
        }
    }

    /**
     * Mostra a sobreposição.
     *
     * @param currentAction A ação usada para acionar esta interface.
     */
    @Override
    public void showOverlay(TurnActionFactory.TurnDecisionAction currentAction) {
        this.controllingTurnAction = currentAction;
        setEnabled(true);
    }
}
