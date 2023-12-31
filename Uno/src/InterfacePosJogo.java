import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Uno
 *
 * Classe InterfacePosJogo:
 * Define uma interface simples que mostra as pontuações da rodada
 * recém-concluída.
 *
 * @autor Cauet Damasceno
 * @versão 2023
 */
public class InterfacePosJogo extends WndInterface {
    /**
     * Lista de botões visíveis na interface.
     */
    private final List<Botao> buttonList;
    /**
     * Referência aos jogadores que estão sendo mostrados para pontuação.
     */
    private final List<Jogador> players;
    /**
     * Referência ao conjunto de regras para verificar os limites de pontuação.
     */
    private final ConjuntoRegras ruleSet;
    /**
     * Referência ao GamePanel para sair da interface.
     */
    private final PainelJogo painelJogo;
    /**
     * Strings para armazenar em cache a geração de Strings para a lista de
     * jogadores.
     */
    private final List<String> playerStrings;
    /**
     * Vencedor desta rodada e possivelmente de toda a partida.
     */
    private String roundWinnerStr;
    /**
     * Quando verdadeiro, o limite de pontuação foi atingido exibindo uma mensagem
     * extra.
     */
    private boolean scoreLimitReached;
    /**
     * Uma String mostrando a regra de limite de pontuação.
     */
    private String scoreLimitStr;

    /**
     * Inicialize a interface com limites e habilite-a.
     *
     * @param limites Limites da interface.
     */
    public InterfacePosJogo(Retangulo bounds, List<Jogador> playerList, ConjuntoRegras ruleSet, PainelJogo painelJogo) {
        super(bounds);
        this.players = playerList;
        this.ruleSet = ruleSet;
        this.painelJogo = painelJogo;

        playerStrings = new ArrayList<>();
        for (Jogador player : playerList) {
            playerStrings.add((player.getPlayerType() == Jogador.PlayerType.UnoJogador ? "You: " : "AI: ")
                    + player.getPlayerName());
            if (player.getWon()) {
                roundWinnerStr = player.getPlayerName();
                switch (ruleSet.getScoreLimitType()) {
                    case OneRound -> scoreLimitReached = true;
                    case Score200 -> scoreLimitReached = player.getTotalScore() >= 200;
                    case Score300 -> scoreLimitReached = player.getTotalScore() >= 300;
                    case Score500 -> scoreLimitReached = player.getTotalScore() >= 500;
                    case Unlimited -> scoreLimitReached = false;
                }
            }
        }

        scoreLimitStr = "Score Limit: ";
        switch (ruleSet.getScoreLimitType()) {
            case OneRound -> scoreLimitStr += "Uma rodada";
            case Score200 -> scoreLimitStr += "200 Pontos";
            case Score300 -> scoreLimitStr += "300 Pontos";
            case Score500 -> scoreLimitStr += "500 Pontos";
            case Unlimited -> scoreLimitStr += "Ilimitado";
        }

        buttonList = new ArrayList<>();
        if (scoreLimitReached) {
            buttonList.add(new Botao(new Posicao(bounds.width / 2 - 250 - 10, 620), 250, 40,
                    "Voltar ao lobby", 1));
            buttonList.add(new Botao(new Posicao(bounds.width / 2 + 10, 620), 250, 40,
                    "Novo jogo com as mesmas configurações", 3));
        } else {
            buttonList.add(new Botao(new Posicao(bounds.width / 2 - 125 - 250 - 20, 620), 250, 40,
                    "Voltar ao lobby", 1));
            buttonList.add(new Botao(new Posicao(bounds.width / 2 - 125, 620), 250, 40,
                    "Continuar na próxima rodada", 2));
            buttonList.add(new Botao(new Posicao(bounds.width / 2 + 125 + 20, 620), 250, 40,
                    "Novo jogo com as mesmas configurações", 3));
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
     * Desenha todos os elementos da interface.
     *
     * @param g Referência ao objeto Graphics para renderização.
     */
    @Override
    public void paint(Graphics g) {
        drawBackground(g);
        painelJogo.paintUnoTitle(g, bounds);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        int strWidth = g.getFontMetrics().stringWidth("Resumo pós-jogo");
        g.drawString("Resumo pós-jogo", bounds.width / 2 - strWidth / 2, 120);
        drawPlayers(g);

        buttonList.forEach(button -> button.paint(g));
    }

    /**
     * Desenha o plano de fundo e o título do jogo.
     *
     * @param g Referência ao objeto Graphics para renderização.
     */
    private void drawBackground(Graphics g) {
        g.setColor(new Color(205, 138, 78, 128));
        g.fillRect(bounds.width / 4, 80, bounds.width / 2, 500);
        g.setColor(Color.BLACK);
        g.drawRect(bounds.width / 4, 80, bounds.width / 2, 500);
    }

    /**
     * Desenha todos os elementos relacionados ao jogador, incluindo linhas para a
     * grade mostrando
     * estatísticas do jogador.
     *
     * @param g Referência ao objeto Graphics para renderização.
     */
    private void drawPlayers(Graphics g) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawLine(bounds.width / 4 + 10, 200, bounds.width * 3 / 4 - 10, 200);
        g.drawString("Round Score", bounds.width / 2 - 10, 180);
        g.drawString("Total Score", bounds.width / 2 + 170, 180);
        for (int i = 0; i < players.size(); i++) {
            g.drawLine(bounds.width / 4 + 10, 260 + i * 60, bounds.width * 3 / 4 - 10, 260 + i * 60);
            g.drawString(playerStrings.get(i), bounds.width / 4 + 50, 240 + i * 60);
            g.drawString(players.get(i).getCurrentRoundScore() + "", bounds.width / 2, 240 + i * 60);
            g.drawString(players.get(i).getTotalScore() + "", bounds.width / 2 + 180, 240 + i * 60);
        }
        g.drawLine(bounds.width / 4 + 10, 200, bounds.width / 4 + 10, 200 + players.size() * 60);
        g.drawLine(bounds.width / 2 - 40, 150, bounds.width / 2 - 40, 200 + players.size() * 60);
        g.drawLine(bounds.width / 2 + 130, 150, bounds.width / 2 + 130, 200 + players.size() * 60);
        g.drawLine(bounds.width * 3 / 4 - 10, 150, bounds.width * 3 / 4 - 10, 200 + players.size() * 60);
        g.drawLine(bounds.width / 2 - 40, 150, bounds.width * 3 / 4 - 10, 150);

        g.drawString("Round Winner: ", bounds.width / 4 + 25, 490);
        g.drawString(roundWinnerStr, bounds.width / 4 + 175, 490);
        g.drawString(scoreLimitStr, bounds.width / 2 + 20, 490);
        if (scoreLimitReached) {
            g.drawString("Score limit reached!", bounds.width / 2 + 40, 530);
        }
    }

    /**
     * Não faz nada se não estiver habilitado. Verifica todos os botões quanto a
     * prensas e alças
     * ações se necessário.
     *
     * @param mousePosition Posição do cursor do mouse durante o pressionamento.
     * @param isLeft        Se verdadeiro, o botão do mouse está para a esquerda,
     *                      caso contrário, está para a direita.
     */
    @Override
    public void handleMousePress(Posicao mousePosition, boolean isLeft) {
        if (!isEnabled())
            return;
        buttonList.forEach(button -> {
            if (button.isPositionInside(mousePosition))
                handleButtonPress(button.getActionID());
        });
    }

    /**
     * Não faz nada se não estiver habilitado. Atualiza o estado de foco de todos os
     * botões.
     *
     * @param mousePosition Posição do mouse durante este movimento.
     */
    @Override
    public void handleMouseMove(Posicao mousePosition) {
        if (!isEnabled())
            return;
        buttonList.forEach(button -> button.setHovering(button.isPositionInside(mousePosition)));
    }

    /**
     * Procura a ação a ser aplicada com base em um actionID de um botão.
     *
     * @param actionID O actionID de um botão que foi pressionado.
     */
    private void handleButtonPress(int actionID) {
        switch (actionID) {
            case 1 -> painelJogo.showLobby();
            case 2 -> painelJogo.startNextRound(players, ruleSet);
            case 3 -> startNewGameWithSameSettings();
        }
    }

    /**
     * Limpa a pontuação de todos os jogadores e inicia um novo jogo.
     */
    private void startNewGameWithSameSettings() {
        players.forEach(Jogador::resetScore);
        painelJogo.startNextRound(players, ruleSet);
    }
}
