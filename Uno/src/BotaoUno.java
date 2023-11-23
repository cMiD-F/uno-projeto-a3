import java.awt.*;

/**
 * Uno
 *
 * Classe UnoButton:
 * Uma variação especial do botão que aparece de forma diferente para a chamada
 * do Uno.
 * Pressionar o botão destina-se quando um jogador atinge 2 ou menos cartas.
 *
 * @autor Cauet Damasceno
 * @versão 2023
 */
public class BotaoUno extends WndInterface implements GeralOverlayInterface {
    /**
     * Largura do botão.
     */
    public static final int WIDTH = 80;
    /**
     * Altura do botão.
     */
    public static final int HEIGHT = 60;
    /**
     * Status atual de foco do botão.
     */
    private boolean isHovered;
    /**
     * Referência ao BottomPlayer.
     */
    protected final Jogador bottomPlayer;
    /**
     * Quando isActive está ativo, o botão pode interagir e fica visível.
     */
    protected boolean isActive;

    /**
     * Inicializa o UnoButton.
     *
     * @param position Posição para colocar o botão Uno.
     */
    public BotaoUno(Posicao position) {
        super(new Retangulo(position, WIDTH, HEIGHT));
        isHovered = false;
        setEnabled(true);
        bottomPlayer = InterfaceJogo.getCurrentGame().getBottomPlayer();
        isActive = false;
    }

    /**
     * Mostra a sobreposição.
     */
    @Override
    public void showOverlay() {
        setEnabled(true);
    }

    /**
     * Habilita o botão quando deveria estar disponível.
     *
     * @param deltaTime Tempo desde a última atualização.
     */
    @Override
    public void update(int deltaTime) {
        isActive = bottomPlayer.getUnoState() == Jogador.UNOState.NotSafe
                || (bottomPlayer.getUnoState() == Jogador.UNOState.Safe
                        && InterfaceJogo.getCurrentGame().getCurrentPlayer() == bottomPlayer
                        && bottomPlayer.getHand().size() == 2);
    }

    /**
     * Desenha o botão Uno com um oval expandido ao passar o mouse com o texto UNO
     * no
     * meio.
     *
     * @param g Referência ao objeto Graphics para renderização.
     */
    @Override
    public void paint(Graphics g) {
        if (!isActive)
            return;

        drawButtonBackground(g);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        int strWidth = g.getFontMetrics().stringWidth("UNO");
        g.drawString("UNO", bounds.position.x + bounds.width / 2 - strWidth / 2 - 2,
                bounds.position.y + bounds.height / 2 + 2 + 10);
        g.setColor(new Color(226, 173, 67));
        g.drawString("UNO", bounds.position.x + bounds.width / 2 - strWidth / 2,
                bounds.position.y + bounds.height / 2 + 10);
    }

    protected void drawButtonBackground(Graphics g) {
        g.setColor(new Color(147, 44, 44));
        int expandAmount = isHovered ? 20 : 0;
        g.fillOval(bounds.position.x - expandAmount / 2, bounds.position.y - expandAmount / 2,
                bounds.width + expandAmount, bounds.height + expandAmount);
    }

    /**
     * Atualiza o estado de foco do botão Uno.
     *
     * @param mousePosition Posição do mouse durante este movimento.
     */
    @Override
    public void handleMouseMove(Posicao mousePosition) {
        isHovered = bounds.isPositionInside(mousePosition);
    }

    /**
     * Quando o botão está disponível e é clicado, o jogador é sinalizado como tendo
     * chamado e o sinal chamado pisca.
     *
     * @param mousePosition Posição do cursor do mouse durante o pressionamento.
     * @param isLeft        Se verdadeiro, o botão do mouse está para a esquerda,
     *                      caso contrário, está para a direita.
     */
    @Override
    public void handleMousePress(Posicao mousePosition, boolean isLeft) {
        if (isActive && bounds.isPositionInside(mousePosition)) {
            bottomPlayer.setUnoState(Jogador.UNOState.Called);
            InterfaceJogo.getCurrentGame().showGeneralOverlay("UNOCalled" + bottomPlayer.getPlayerID());
        }
    }
}
