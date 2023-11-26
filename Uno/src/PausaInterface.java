import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Uno
 *
 * Classe PausaInterface :
 * Uma interface de pausa simples que permite controlar alguns botões
 * fluxo do jogo ou apenas faça uma pausa por um momento.
 *
 * @autor Cauet Damasceno
 * @versão 2023
 */
public class PausaInterface extends WndInterface {
    /**
     * Uma lista de todos os botões da interface.
     */
    private final List<Botao> buttonList;
    /**
     * Uma referência ao PainelJogo para retornar a ele.
     */
    private final PainelJogo painelJogo;
    /**
     * Texto mostrando as mensagens com diferentes controles.
     */
    private final List<String> leftMessages, rightMessages;

    /**
     * Inicialize a interface com limites e deixe-a pronta para uso.
     *
     * @param limites A região na qual desenhar esta interface.
     */
    public PausaInterface(Retangulo bounds, PainelJogo gamePanel) {
        super(bounds);
        this.painelJogo = gamePanel;
        buttonList = new ArrayList<>();
        buttonList.add(new Botao(new Posicao(bounds.position.x + 6, bounds.position.y + 6 + 60),
                bounds.width - 12, 30, "Retomar", 1));
        buttonList.add(new Botao(new Posicao(bounds.position.x + 6, bounds.position.y + 6 + 30 + 6 + 60),
                bounds.width - 12, 30, "Voltar ao lobby", 3));
        buttonList.add(new Botao(new Posicao(bounds.position.x + 6, bounds.position.y + 6 + (30 + 6) * 2 + 60),
                bounds.width - 12, 30, "Sair", 2));

        leftMessages = new ArrayList<>();
        rightMessages = new ArrayList<>();

        leftMessages.add("Sair: Pausa");
        leftMessages.add("P: Classificar manualmente");

        rightMessages.add("0: Ative a depuração");
        rightMessages.add("9: Revele todas as mãos");
        rightMessages.add("8: Alternar direção de giro");
        rightMessages.add("7: Mão vazia do jogador");
        rightMessages.add("6: Remova a carta do jogador esquerdo");
        rightMessages.add("5: Alternar Mostrar sequência de ação de turno");
        rightMessages.add("4: Alternar Mostrar ativar árvore de ação ativada");
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
     * Desenha um fundo e todos os botões.
     *
     * @param g Referência ao objeto Graphics para renderização.
     */
    @Override
    public void paint(Graphics g) {
        // esmaece tudo atrás da interface de pausa
        g.setColor(new Color(144, 143, 143, 204));
        g.fillRect(0, 0, PainelJogo.PANEL_WIDTH, PainelJogo.PANEL_HEIGHT);

        g.setColor(new Color(165, 177, 94, 205));
        g.fillRect(bounds.position.x, bounds.position.y, bounds.width, bounds.height);
        g.fillRect(170, 300, 160, 90);
        g.fillRect(790, 220, 410, 300);
        g.setColor(Color.BLACK);
        g.drawRect(bounds.position.x, bounds.position.y, bounds.width, bounds.height);
        g.drawRect(170, 300, 160, 90);
        g.drawRect(790, 220, 410, 300);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        int strWidth = g.getFontMetrics().stringWidth("Pausado");
        g.drawString("Pausado", bounds.position.x + bounds.width / 2 - strWidth / 2, bounds.position.y + 40);
        buttonList.forEach(button -> button.paint(g));

        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Controles", 210, 320);
        for (int y = 0; y < leftMessages.size(); y++) {
            g.drawString(leftMessages.get(y), 180, 350 + y * 30);
        }
        g.drawString("Controles de depuração (0 first)", 880, 260);
        for (int y = 0; y < rightMessages.size(); y++) {
            g.drawString(rightMessages.get(y), 800, 300 + y * 30);
        }
    }

    /**
     * Não faz nada se não estiver habilitado. Atualiza os estados de foco de todos
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
     * Não faz nada se não estiver habilitado. Verifica se um botão foi clicado e
     * responde
     * para isso.
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
                handleButtonAction(button.getActionID());
                break;
            }
        }
    }

    /**
     * Lida com o actionID mapeando cada ID para uma ação relacionada ao botão.
     *
     * @param actionID O actionID a ser mapeado para uma ação no menu de pausa.
     */
    private void handleButtonAction(int actionID) {
        switch (actionID) {
            case 1 -> painelJogo.setPauseState(false);
            case 2 -> painelJogo.quitGame();
            case 3 -> painelJogo.showLobby();
        }
    }
}
