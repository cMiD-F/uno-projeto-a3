import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Uno
 *
 * Classe PlayerSelectionOverlay:
 * Define a sobreposição usada para escolher um jogador para trocar.
 *
 * @autor Cauet Damasceno
 * @versão 2023
 */
public class PlayerSelectionOverlay extends WndInterface implements TurnDecisionOverlayInterface {
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
     * Inicializa a sobreposição com um botão para cada um dos outros jogadores.
     * Os botões estão centralizados nas regiões do jogador.
     *
     * @param bounds Os (bounds) de toda a área de jogo. Os botões estão deslocados
     *               de
     *               o Centro.
     */
    public PlayerSelectionOverlay(Retangulo bounds, List<Jogador> playerList) {
        super(bounds);
        setEnabled(false);
        buttonList = new ArrayList<>();
        for (int i = 0; i < playerList.size(); i++) {
            if (playerList.get(i).getPlayerType() != Jogador.PlayerType.UnoJogador) {
                Posicao centre = playerList.get(i).getCentreOfBounds();
                buttonList.add(new Botao(new Posicao(centre.x - 100, centre.y - 20), 200, 40, "Choose Player", i));
            }
        }
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
     * Desenha todos os botões.
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
     * @param currentAction A ação usada para acionar esta interface.
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
