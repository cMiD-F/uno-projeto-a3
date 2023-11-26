import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Uno
 *
 * Classe DesafioOverlay :
 * Define a sobreposição usada para escolher Desafiar/Recusar/Acumular contra um
 * +4.
 *
 * @autor Cauet Damasceno
 * @versão 2023
 */
public class DesafioOverlay extends WndInterface implements TurnDecisionOverlayInterface {
    /**
     * Lista de botões que podem ser usados na sobreposição. Inclui um botão Desafio
     * e Recusar.
     */
    private final List<Botao> buttonList;
    /**
     * Referência ao TurnAction que acionou a exibição desta sobreposição.
     */
    private TurnActionFactory.TurnDecisionAction currentAction;
    /**
     * Referência ao jogador a ser usado para seleção de cartas quando o
     * empilhamento for permitido.
     */
    private final Jogador playerReference;
    /**
     * Quando verdadeiro através do RuleSet, as cartas do jogador podem ser
     * empilhadas, se possível.
     */
    private final boolean allowStacking;

    /**
     * Inicializa a sobreposição com um botão Desafio e Recusar e verifica
     * se o RuleSet permite o empilhamento para armazenar em cache o processamento
     * para posterior.
     *
     * @param limites Os limites de toda a área de jogo. Os botões estão deslocados
     *                de
     *                o Centro.
     */
    public DesafioOverlay(Retangulo bounds) {
        super(bounds);
        setEnabled(false);
        buttonList = new ArrayList<>();
        Posicao centre = bounds.getCentre();
        // Se for permitido blefar, inclua o botão de desafio.
        if (!InterfaceJogo.getCurrentGame().getRuleSet().getNoBluffingRule()) {
            buttonList.add(new Botao(new Posicao(centre.x - 150, centre.y + 100), 100, 40, "Challenge", 1));
        }
        buttonList.add(new Botao(new Posicao(centre.x + 50, centre.y + 100), 100, 40, "Decline", 0));

        allowStacking = InterfaceJogo.getCurrentGame().getRuleSet().canStackCards();
        playerReference = InterfaceJogo.getCurrentGame().getBottomPlayer();
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
     * Desenha os botões.
     *
     * @param g Referência ao objeto Graphics para renderização.
     */
    @Override
    public void paint(Graphics g) {
        buttonList.forEach(button -> button.paint(g));
    }

    /**
     * Torna a sobreposição visível.
     *
     * @param currentAction O TurnAction usado para fazer esta sobreposição
     *                      aparecer.
     */
    @Override
    public void showOverlay(TurnActionFactory.TurnDecisionAction currentAction) {
        this.currentAction = currentAction;
        setEnabled(true);
    }

    /**
     * Não faz nada se não estiver habilitado. Atualiza o estado de foco para todos
     * os botões.
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
     * Não faz nada se não estiver habilitado. Verifica se há um clique nos botões.
     * Se um botão for clicado, a ação será registrada para fechar a sobreposição.
     * Caso contrário, se uma carta for clicada, isso será +4 quando o empilhamento
     * estiver ativado
     * o cartão está encadeado.
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
                currentAction.injectProperty("isChaining", 0);
                currentAction.injectFlagProperty(button.getActionID());
                setEnabled(false);
                return;
            }
        }

        if (allowStacking) {
            Carta clickedCard = playerReference.chooseCardFromClick(mousePosition);
            if (clickedCard != null && clickedCard.getFaceValueID() == 13) {
                currentAction.injectProperty("faceValueID", clickedCard.getFaceValueID());
                currentAction.injectProperty("colourID", clickedCard.getColourID());
                currentAction.injectProperty("cardID", clickedCard.getCardID());
                currentAction.injectProperty("isChaining", 1);
                currentAction.injectFlagProperty(0);
                setEnabled(false);
            }
        }
    }
}
