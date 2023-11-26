import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Uno
 *
 * Classe PassarOuJogarOverlay:
 * Usado quando o jogador tem que optar por manter ou jogar uma carta que foi
 * comprada.
 *
 * @autor Cauet Damasceno
 * @versão 2023
 */
public class PassarOuJogarOverlay extends WndInterface implements TurnDecisionOverlayInterface {
    /**
     * Lista de botões que consiste nos botões Manter e Reproduzir.
     */
    private final List<Botao> buttonList;
    /**
     * Referência ao TurnAction que acionou a exibição desta sobreposição.
     */
    private TurnActionFactory.TurnDecisionAction currentAction;
    /**
     * Referência ao cartão fictício sobre o qual a sobreposição está tomando uma
     * decisão.
     */
    private Carta cardForChoice;
    /**
     * Posição onde será colocada a carta em questão.
     */
    private final Posicao cardPosition;

    /**
     * Inicialize os botões Keep e Play e o local onde o cartão fictício
     * tem que ser colocado.
     *
     * @param limites Os limites de toda a área de jogo. Os botões estão deslocados
     *                de
     *                o Centro.
     */
    public PassarOuJogarOverlay(Retangulo bounds) {
        super(bounds);
        setEnabled(false);
        buttonList = new ArrayList<>();
        Posicao centre = bounds.getCentre();
        buttonList.add(new Botao(new Posicao(centre.x - 150, centre.y + 100), 100, 40, "Manter", 0));
        buttonList.add(new Botao(new Posicao(centre.x + 50, centre.y + 100), 100, 40, "Jogar", 1));

        cardPosition = new Posicao(centre.x - Carta.CARD_WIDTH / 2, centre.y + 100 + 20 - Carta.CARD_HEIGHT / 2);
    }

    /**
     * Não usado.
     *
     * @param deltaTime Tempo desde a última atualização.
     */
    @Override
    public void update(int deltaTime) {

    }

    /**
     * Desenha todos os botões e o cartão relacionado à escolha.
     *
     * @param g Referência ao objeto Graphics para renderização.
     */
    @Override
    public void paint(Graphics g) {
        buttonList.forEach(button -> button.paint(g));
        cardForChoice.paint(g);
    }

    /**
     * Cria uma versão fictícia do cartão para ser mostrada como parte da
     * sobreposição
     * e faz a sobreposição aparecer.
     *
     * @param currentAction A ação usada para acionar esta interface.
     */
    @Override
    public void showOverlay(TurnActionFactory.TurnDecisionAction currentAction) {
        this.currentAction = currentAction;
        cardForChoice = new Carta(currentAction.storedData.get("faceValueID"),
                currentAction.storedData.get("colourID"),
                currentAction.storedData.get("cardID"));
        cardForChoice.position.setPosition(cardPosition.x, cardPosition.y);
        setEnabled(true);
    }

    /**
     * Não faz nada quando não está ativado. Atualiza o estado de foco de todos os
     * botões.
     *
     * @param mousePosition Posição do mouse durante este movimento.
     */
    @Override
    public void handleMouseMove(Posicao mousePosition) {
        if (!isEnabled())
            return;

        for (Botao button : buttonList) {
            button.setHovering(button.isPositionInside(mousePosition));
        }
    }

    /**
     * Não faz nada quando não está ativado. Verifica cliques nos botões e
     * aciona o evento correto quando um botão é interagido.
     *
     * @param mousePosition Posição do cursor do mouse durante o pressionamento.
     * @param isLeft        Se verdadeiro, o botão do mouse está para a esquerda,
     *                      caso contrário, está para a direita.
     */
    @Override
    public void handleMousePress(Posicao mousePosition, boolean isLeft) {
        if (!isEnabled())
            return;

        for (Botao button : buttonList) {
            if (button.isPositionInside(mousePosition)) {
                setEnabled(false);
                currentAction.injectFlagProperty(button.getActionID());
                break;
            }
        }
    }
}
