import java.awt.*;

/**
 * Uno
 *
 * Classe EscolhaDePilha:
 * Define a sobreposição usada para escolher Recusar/Acumular em relação a +2.
 *
 * @autor Cauet Damasceno
 * @versão 2023
 */
public class EscolhaDePilha extends WndInterface implements TurnDecisionOverlayInterface {
    /**
     * O botão recusar que simplesmente aceita tirar o sorteio da carta.
     */
    private final Botao declineButton;
    /**
     * Referência ao TurnAction que acionou a exibição desta sobreposição.
     */
    private TurnActionFactory.TurnDecisionAction currentAction;
    /**
     * Referência ao último jogador que está fazendo a escolha.
     */
    private final Jogador playerReference;

    /**
     * Inicialize o botão de recusa e consulte o jogador para rastrear suas cartas.
     *
     * @param limites Os limites de toda a área de jogo. Os botões estão deslocados
     *                do centro.
     */
    public EscolhaDePilha(Retangulo bounds) {
        super(bounds);
        setEnabled(false);
        Posicao centre = bounds.getCentre();
        declineButton = new Botao(new Posicao(centre.x - 50, centre.y + 100), 100, 40, "Decline", 0);

        playerReference = InterfaceJogo.getCurrentGame().getBottomPlayer();
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
     * Desenha o botão Recusar.
     *
     * @param g Referência ao objeto Graphics para renderização.
     */
    @Override
    public void paint(Graphics g) {
        declineButton.paint(g);
    }

    /**
     * Mostra a sobreposição.
     *
     * @param currentAction A ação usada para acionar esta interface.
     */
    @Override
    public void showOverlay(TurnActionFactory.TurnDecisionAction currentAction) {
        this.currentAction = currentAction;
        setEnabled(true);
    }

    /**
     * Não faz nada se não estiver habilitado. Atualiza o status de foco do botão
     * recusar.
     *
     * @param mousePosition Posição do mouse durante este movimento.
     */
    @Override
    public void handleMouseMove(Posicao mousePosition) {
        if (!isEnabled())
            return;

        declineButton.setHovering(declineButton.isPositionInside(mousePosition));
    }

    /**
     * Não faz nada se não estiver habilitado. Verifica se há um clique no botão
     * recusar para
     * lidar com isso.
     * E verifica se o jogador clica em suas cartas para permitir o empilhamento.
     * Se esta sobreposição estiver visível, significa que o empilhamento é
     * permitido.
     *
     * @param mousePosition Posição do cursor do mouse durante o pressionamento.
     * @param isLeft        Se verdadeiro, o botão do mouse está para a esquerda,
     *                      caso contrário, está para a direita.
     */
    @Override
    public void handleMousePress(Posicao mousePosition, boolean isLeft) {
        if (!isEnabled())
            return;

        if (declineButton.isPositionInside(mousePosition)) {
            currentAction.injectFlagProperty(0);
            setEnabled(false);
            return;
        }

        Carta clickedCard = playerReference.chooseCardFromClick(mousePosition);
        if (clickedCard != null && clickedCard.getFaceValueID() == 10) {
            currentAction.injectProperty("faceValueID", clickedCard.getFaceValueID());
            currentAction.injectProperty("colourID", clickedCard.getColourID());
            currentAction.injectProperty("cardID", clickedCard.getCardID());
            currentAction.injectFlagProperty(1);
            setEnabled(false);
        }
    }
}
