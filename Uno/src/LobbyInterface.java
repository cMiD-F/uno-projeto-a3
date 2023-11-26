import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * Uno
 *
 * Classe LobbyInterface:
 * Mostra um lobby para configurar os jogadores e o conjunto de regras para
 * iniciar um jogo.
 *
 * @autor Cauet Damasceno
 * @versão 2023
 */

public class LobbyInterface extends WndInterface {
    /**
     * Lista dos jogadores. Sempre contém 4. Aqueles que estão habilitados são
     * considerados
     * ativo.
     */
    private final List<LobbyPlayer> playerList;
    /**
     * Lista de botões visíveis na interface do lobby.
     */
    private final List<Botao> buttonList;
    /**
     * Referência ao GamePanel para retornos de chamada.
     */
    private final PainelJogo gamePanel;
    /**
     * O RuleSet sendo configurado.
     */
    private final ConjuntoRegras ruleSet;
    /**
     * String mostrando o estado da regra da pilha.
     */
    private String stackRuleStateStr;
    /**
     * String mostrando o estado da regra de sorteio.
     */
    private String drawTillPlayableRuleStateStr;
    /**
     * String mostrando o estado da regra de dois jogadores.
     */
    private String twoPlayerRuleStr, twoPlayerPrefixStr;
    /**
     * String mostrando o estado da regra sete-zero.
     */
    private String sevenZeroRuleStr;
    /**
     * String de estado da regra de jogo forçado.
     */
    private String forcedPlayRuleStr;
    /**
     * String de estado da regra Jump-In.
     */
    private String jumpInRuleStr;
    /**
     * Nenhuma sequência de estado de regra de blefe.
     */
    private String noBluffingRuleStr;
    /**
     * Limite de pontuação para o jogo ser iniciado.
     */
    private String scoreLimitStr;

    /**
     * Inicialize a interface com limites e habilite-a.
     *
     * @param limites Área para exibir o lobby.
     */
    public LobbyInterface(Retangulo bounds, PainelJogo gamePanel) {
        super(bounds);
        this.gamePanel = gamePanel;
        List<String> aiNames = getRandomAINameList();
        playerList = new ArrayList<>();
        playerList.add(new LobbyPlayer("Jogador", Jogador.PlayerType.UnoJogador,
                new Retangulo(new Posicao(20, 100), bounds.width / 2, 100)));
        playerList.add(new LobbyPlayer(aiNames.get(0), Jogador.PlayerType.AIJogador,
                new Retangulo(new Posicao(20, 100 + 120), bounds.width / 2, 100)));
        playerList.add(new LobbyPlayer(aiNames.get(1), Jogador.PlayerType.AIJogador,
                new Retangulo(new Posicao(20, 100 + 120 * 2), bounds.width / 2, 100)));
        playerList.add(new LobbyPlayer(aiNames.get(2), Jogador.PlayerType.AIJogador,
                new Retangulo(new Posicao(20, 100 + 120 * 3), bounds.width / 2, 100)));

        buttonList = new ArrayList<>();
        buttonList.add(new Botao(new Posicao(bounds.width / 4 - 150, bounds.height - 100), 300, 60,
                "Alternar número de jogadores", 1));
        buttonList.add(new Botao(new Posicao(bounds.width * 3 / 4 - 150, bounds.height - 100), 300, 60,
                "Começar o jogo", 2));
        ruleSet = new ConjuntoRegras();
        updateAllRuleLabels();
        buttonList.add(new Botao(new Posicao(bounds.width / 2 + 120, 145), 150, 40, "Alternar regra", 3));
        buttonList.add(new Botao(new Posicao(bounds.width / 2 + 120, 195), 150, 40, "Alternar regra", 4));
        buttonList.add(new Botao(new Posicao(bounds.width / 2 + 120, 285), 150, 40, "Alternar regra", 5));
        buttonList.add(new Botao(new Posicao(bounds.width / 2 + 120, 335), 150, 40, "Alternar regra", 6));
        buttonList.add(new Botao(new Posicao(bounds.width / 2 + 120, 385), 150, 40, "Alternar regra", 7));
        buttonList.add(new Botao(new Posicao(bounds.width / 2 + 120, 435), 150, 40, "Alternar regra", 8));
        buttonList.add(new Botao(new Posicao(bounds.width / 2 + 120, 485), 150, 40, "Limite de ciclo", 9));
        buttonList.add(new Botao(new Posicao(bounds.width * 3 / 4 - 100, 535), 200, 40, "Restaurar ao padrão", 10));
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
     * Desenha todos os elementos necessários para o LobbyInterface.
     *
     * @param g Referência ao objeto Graphics para renderização.
     */
    @Override
    public void paint(Graphics g) {
        drawBackground(g);
        gamePanel.paintUnoTitle(g, bounds);

        // Draw interaction elements
        buttonList.forEach(button -> button.paint(g));
        playerList.forEach(lobbyPlayer -> lobbyPlayer.paint(g));

        drawRuleText(g);
    }

    /**
     * Desenha o plano de fundo e o título do jogo.
     *
     * @param g Referência ao objeto Graphics para renderização.
     */
    private void drawBackground(Graphics g) {
        g.setColor(new Color(205, 138, 78, 128));
        g.fillRect(10, 80, bounds.width / 2 + 20, 500);
        g.fillRect(bounds.width / 2 + 40, 80, bounds.width / 2 - 60, 500);
        g.setColor(Color.BLACK);
        g.drawRect(10, 80, bounds.width / 2 + 20, 500);
        g.drawRect(bounds.width / 2 + 40, 80, bounds.width / 2 - 60, 500);
    }

    /**
     * Desenha o texto de todas as mensagens de status das regras.
     *
     * @param g Referência ao objeto Graphics para renderização.
     */
    private void drawRuleText(Graphics g) {
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString("Regras", bounds.width / 2 + 280, 120);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString(stackRuleStateStr, bounds.width / 2 + 300, 175);
        g.drawString(drawTillPlayableRuleStateStr, bounds.width / 2 + 300, 225);
        g.drawString(twoPlayerPrefixStr, bounds.width / 2 + 140, 270);
        g.drawString(twoPlayerRuleStr, bounds.width / 2 + 300, 270);
        g.drawString(sevenZeroRuleStr, bounds.width / 2 + 300, 315);
        g.drawString(jumpInRuleStr, bounds.width / 2 + 300, 365);
        g.drawString(forcedPlayRuleStr, bounds.width / 2 + 300, 415);
        g.drawString(noBluffingRuleStr, bounds.width / 2 + 300, 465);
        g.drawString(scoreLimitStr, bounds.width / 2 + 300, 515);
    }

    /**
     * atualiza o status de foco dos elementos. Não faz nada se não estiver ativado.
     *
     * @param mousePosition Posição do mouse durante este movimento.
     */
    @Override
    public void handleMouseMove(Posicao mousePosition) {
        if (!isEnabled())
            return;

        buttonList.forEach(button -> button.setHovering(button.isPositionInside(mousePosition)));
        playerList.forEach(lobbyPlayer -> lobbyPlayer.updateHoverState(mousePosition));
    }

    /**
     * Não faz nada se não estiver habilitado. Verifica se há pressionamentos de
     * botões e players
     * com métodos para lidar com as interações conforme necessário.
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
        playerList.forEach(lobbyPlayer -> {
            if (lobbyPlayer.isPositionInside(mousePosition))
                lobbyPlayer.handleClick();
        });
    }

    /**
     * Lida com as ações dos botões mapeando os IDs para as ações.
     *
     * @param actionID ID mapeado para uma ação relevante para o botão.
     */
    private void handleButtonPress(int actionID) {
        switch (actionID) {
            case 1 -> toggleNumberOfPlayers();
            case 2 -> gamePanel.startGame(playerList, ruleSet);
            case 3 -> toggleStackRule();
            case 4 -> toggleDrawTillCanPlayRule();
            case 5 -> toggleSevenZeroRule();
            case 6 -> toggleJumpInRule();
            case 7 -> toggleForcedPlayRule();
            case 8 -> toggleNoBluffingRule();
            case 9 -> cycleScoreLimit();
            case 10 -> resetRulesToDefault();
        }
    }

    /**
     * Alterna os jogadores 2 e 3 entre os estados ativado e desativado.
     */
    private void toggleNumberOfPlayers() {
        ruleSet.setTwoPlayers(!ruleSet.getOnlyTwoPlayers());
        updatePlayerNumberStatus();
    }

    /**
     * Atualiza o status visual com base no número de jogadores.
     */
    private void updatePlayerNumberStatus() {
        playerList.get(2).setEnabled(!ruleSet.getOnlyTwoPlayers());
        playerList.get(3).setEnabled(!ruleSet.getOnlyTwoPlayers());
        twoPlayerPrefixStr = ruleSet.getOnlyTwoPlayers() ? "Dois jogadores:" : "Quatro jogadores:";
        twoPlayerRuleStr = ruleSet.getOnlyTwoPlayers() ? "O reverso é pular" : "Regras normais para reversão";
    }

    /**
     * Alterna a regra de empilhamento e atualiza a mensagem.
     */
    private void toggleStackRule() {
        ruleSet.setCanStackCards(!ruleSet.canStackCards());
        updateStackRuleLabel();
    }

    /**
     * Atualiza o texto da regra da pilha.
     */
    private void updateStackRuleLabel() {
        stackRuleStateStr = "Empilhamento +2/+4: " + (ruleSet.canStackCards() ? "Ligado" : "Desligado");
    }

    /**
     * Alterna a regra de sorteio e atualiza a mensagem.
     */
    private void toggleDrawTillCanPlayRule() {
        ruleSet.setDrawnTillCanPlay(!ruleSet.shouldDrawnTillCanPlay());
        updateDrawTillCanPlayRuleLabel();
    }

    /**
     * Atualiza o texto do sorteio até poder jogar.
     */
    private void updateDrawTillCanPlayRuleLabel() {
        drawTillPlayableRuleStateStr = "Empate até poder jogar: "
                + (ruleSet.shouldDrawnTillCanPlay() ? "Ligado" : "Desligado");
    }

    /**
     * Alterna a regra sete-zero e atualiza a mensagem.
     */
    private void toggleSevenZeroRule() {
        ruleSet.setSevenZeroRule(!ruleSet.getSevenZeroRule());
        updateSevenZeroRuleLabel();
    }

    /**
     * Atualiza o texto da regra dos sete zeros.
     */
    private void updateSevenZeroRuleLabel() {
        sevenZeroRuleStr = "Sete-0: " + (ruleSet.getSevenZeroRule() ? "Ligado (7=Swap, 0=Pass All)" : "Desligado");
    }

    /**
     * Alterna a regra de jogo forçado e atualiza a mensagem.
     */
    private void toggleForcedPlayRule() {
        ruleSet.setForcedPlayRule(!ruleSet.getForcedPlayRule());
        updateForcedPlayRuleLabel();
    }

    /**
     * Atualiza o texto da regra de jogo forçado.
     */
    private void updateForcedPlayRuleLabel() {
        forcedPlayRuleStr = "Jogo forçado: " + (ruleSet.getForcedPlayRule() ? "Ligado" : "Desligado");
    }

    /**
     * Alterna a regra Jump-In e atualiza a mensagem.
     */
    private void toggleJumpInRule() {
        ruleSet.setAllowJumpInRule(!ruleSet.allowJumpInRule());
        updateJumpInRuleLabel();
    }

    /**
     * Atualiza o texto da regra de entrada.
     */
    private void updateJumpInRuleLabel() {
        jumpInRuleStr = "Pule dentro: " + (ruleSet.allowJumpInRule() ? "Ligado" : "Desligado");
    }

    /**
     * Alterna a regra Não Blefar e atualiza a mensagem.
     */
    private void toggleNoBluffingRule() {
        ruleSet.setNoBuffingRule(!ruleSet.getNoBluffingRule());
        updateNoBuffingRuleLabel();
    }

    private void updateNoBuffingRuleLabel() {
        noBluffingRuleStr = "Sem blefe: " + (ruleSet.getNoBluffingRule() ? "Ligado" : "Desligado");
    }

    /**
     * Cicla entre as opções de limite de pontuação
     */
    private void cycleScoreLimit() {
        switch (ruleSet.getScoreLimitType()) {
            case OneRound -> ruleSet.setScoreLimitType(ConjuntoRegras.ScoreLimitType.Score200);
            case Score200 -> ruleSet.setScoreLimitType(ConjuntoRegras.ScoreLimitType.Score300);
            case Score300 -> ruleSet.setScoreLimitType(ConjuntoRegras.ScoreLimitType.Score500);
            case Score500 -> ruleSet.setScoreLimitType(ConjuntoRegras.ScoreLimitType.Unlimited);
            case Unlimited -> ruleSet.setScoreLimitType(ConjuntoRegras.ScoreLimitType.OneRound);
        }
        updateScoreLimitLabel();
    }

    /**
     * Atualiza o rótulo para mostrar uma mensagem relacionada à pontuação.
     */
    private void updateScoreLimitLabel() {
        String scoreLabelMessage = "";
        switch (ruleSet.getScoreLimitType()) {
            case OneRound -> scoreLabelMessage = "Uma rodada";
            case Score200 -> scoreLabelMessage = "200 pontos";
            case Score300 -> scoreLabelMessage = "300 pontos";
            case Score500 -> scoreLabelMessage = "500 pontos";
            case Unlimited -> scoreLabelMessage = "Ilimitado";
        }
        scoreLimitStr = "Limite de pontuação: " + scoreLabelMessage;
    }

    /**
     * Redefine todas as regras para os padrões.
     */
    private void resetRulesToDefault() {
        ruleSet.setToDefaults();
        updateAllRuleLabels();
    }

    /**
     * Atualiza o estado de todos os rótulos.
     */
    private void updateAllRuleLabels() {
        updateStackRuleLabel();
        updateDrawTillCanPlayRuleLabel();
        updatePlayerNumberStatus();
        updateSevenZeroRuleLabel();
        updateJumpInRuleLabel();
        updateForcedPlayRuleLabel();
        updateNoBuffingRuleLabel();
        updateScoreLimitLabel();
    }

    /**
      * Carrega uma lista de nomes de AINameList.txt e a classifica aleatoriamente, pronta para
      * usar.
      *
      * @return Uma lista de nomes lidos do arquivo.
      */
    private List<String> getRandomAINameList() {
        List<String> names = new ArrayList<>();
        try {
            Scanner scan = new Scanner(new File("C:\\Users\\Cauet\\Desktop\\uno-projeto-a3\\Uno\\AINameList.txt"));

            while (scan.hasNextLine()) {
                names.add(scan.nextLine().trim());
            }
            Collections.shuffle(names);
        } catch (FileNotFoundException e) {
            System.out.println("Falha ao ler a lista de nomes. Arquivo não encontrado.");
            e.printStackTrace();
        }
        return names;
    }
}
