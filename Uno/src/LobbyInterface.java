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
 * LobbyInterface class:
 * Shows a lobby to setup the players and ruleset ready to start a game.
 *
 * @autor Cauet Damasceno
 * @versão 2023
 */

public class LobbyInterface extends WndInterface {
    /**
     * List of the players. Always contains 4. Those that are enabled are considered active.
     */
    private final List<LobbyPlayer> playerList;
    /**
     * List of buttons visible on the lobby interface.
     */
    private final List<Butao> buttonList;
    /**
     * Reference to the GamePanel for callbacks.
     */
    private final GamePainel gamePanel;
    /**
     * The RuleSet being configured.
     */
    private final ConjuntoRegras ruleSet;

    /**
     * String showing the stack rule state.
     */
    private String stackRuleStateStr;
    /**
     * String showing the draw rule state.
     */
    private String drawTillPlayableRuleStateStr;
    /**
     * String showing the state of the two player rule.
     */
    private String twoPlayerRuleStr, twoPlayerPrefixStr;
    /**
     * String showing the seven-zero rule state.
     */
    private String sevenZeroRuleStr;
    /**
     * Forced Play Rule state string.
     */
    private String forcedPlayRuleStr;
    /**
     * Jump-In rule state string.
     */
    private String jumpInRuleStr;
    /**
     * No bluffing rule state string.
     */
    private String noBluffingRuleStr;
    /**
     * Score limit for the game to be started.
     */
    private String scoreLimitStr;

    /**
     * Initialise the interface with bounds and make it enabled.
     *
     * @param bounds Area to display the lobby in.
     */
    public LobbyInterface(Retangulo bounds, GamePainel gamePanel) {
        super(bounds);
        this.gamePanel = gamePanel;
        List<String> aiNames = getRandomAINameList();
        playerList = new ArrayList<>();
        playerList.add(new LobbyPlayer("Jogador", Jogador.PlayerType.ThisPlayer,
                new Retangulo(new Posicao(20, 100), bounds.width / 2, 100)));
        playerList.add(new LobbyPlayer(aiNames.get(0), Jogador.PlayerType.AIPlayer,
                new Retangulo(new Posicao(20, 100 + 120), bounds.width / 2, 100)));
        playerList.add(new LobbyPlayer(aiNames.get(1), Jogador.PlayerType.AIPlayer,
                new Retangulo(new Posicao(20, 100 + 120 * 2), bounds.width / 2, 100)));
        playerList.add(new LobbyPlayer(aiNames.get(2), Jogador.PlayerType.AIPlayer,
                new Retangulo(new Posicao(20, 100 + 120 * 3), bounds.width / 2, 100)));

        buttonList = new ArrayList<>();
        buttonList.add(new Butao(new Posicao(bounds.width / 4 - 150, bounds.height - 100), 300, 60,
                "Alternar número de jogadores", 1));
        buttonList.add(new Butao(new Posicao(bounds.width * 3 / 4 - 150, bounds.height - 100), 300, 60,
                "Começar o jogo", 2));
        ruleSet = new ConjuntoRegras();
        updateAllRuleLabels();
        buttonList.add(new Butao(new Posicao(bounds.width / 2 + 120, 145), 150, 40, "Alternar regra", 3));
        buttonList.add(new Butao(new Posicao(bounds.width / 2 + 120, 195), 150, 40, "Alternar regra", 4));
        buttonList.add(new Butao(new Posicao(bounds.width / 2 + 120, 285), 150, 40, "Alternar regra", 5));
        buttonList.add(new Butao(new Posicao(bounds.width / 2 + 120, 335), 150, 40, "Alternar regra", 6));
        buttonList.add(new Butao(new Posicao(bounds.width / 2 + 120, 385), 150, 40, "Alternar regra", 7));
        buttonList.add(new Butao(new Posicao(bounds.width / 2 + 120, 435), 150, 40, "Alternar regra", 8));
        buttonList.add(new Butao(new Posicao(bounds.width / 2 + 120, 485), 150, 40, "Limite de ciclo", 9));
        buttonList.add(new Butao(new Posicao(bounds.width * 3 / 4 - 100, 535), 200, 40, "Restaurar ao padrão", 10));
    }


    /**
     * Does nothing.
     *
     * @param deltaTime Time since last update.
     */
    @Override
    public void update(int deltaTime) {

    }

    /**
     * Draws all the elements required for the LobbyInterface.
     *
     * @param g Reference to the Graphics object for rendering.
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
     * Draws the background and game title.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    private void drawBackground(Graphics g) {
        g.setColor(new Color(205, 138, 78, 128));
        g.fillRect(10, 80, bounds.width/2+20, 500);
        g.fillRect(bounds.width/2+40, 80, bounds.width/2-60, 500);
        g.setColor(Color.BLACK);
        g.drawRect(10, 80, bounds.width/2+20, 500);
        g.drawRect(bounds.width/2+40, 80, bounds.width/2-60, 500);
    }

    /**
     * Draws the text for all the rule status messages.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    private void drawRuleText(Graphics g) {
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString("Regras", bounds.width/2+280, 120);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString(stackRuleStateStr, bounds.width/2+300, 175);
        g.drawString(drawTillPlayableRuleStateStr, bounds.width/2+300, 225);
        g.drawString(twoPlayerPrefixStr, bounds.width/2+140, 270);
        g.drawString(twoPlayerRuleStr, bounds.width/2+300, 270);
        g.drawString(sevenZeroRuleStr, bounds.width/2+300, 315);
        g.drawString(jumpInRuleStr, bounds.width/2+300, 365);
        g.drawString(forcedPlayRuleStr, bounds.width/2+300, 415);
        g.drawString(noBluffingRuleStr, bounds.width/2+300, 465);
        g.drawString(scoreLimitStr, bounds.width/2+300, 515);
    }

    /**
     * updates the hover status of elements. Does nothing if not enabled.
     *
     * @param mousePosition Position of the mouse during this movement.
     */
    @Override
    public void handleMouseMove(Posicao mousePosition) {
        if(!isEnabled()) return;

        buttonList.forEach(button -> button.setHovering(button.isPositionInside(mousePosition)));
        playerList.forEach(lobbyPlayer -> lobbyPlayer.updateHoverState(mousePosition));
    }

    /**
     * Does nothing if not enabled. Checks for presses on the buttons and players
     * with methods to handle the interactions as necessary.
     *
     * @param mousePosition Position of the mouse cursor during the press.
     * @param isLeft        If true, the mouse button is left, otherwise is right.
     */
    @Override
    public void handleMousePress(Posicao mousePosition, boolean isLeft) {
        if(!isEnabled()) return;

        buttonList.forEach(button -> {
            if(button.isPositionInside(mousePosition))
                handleButtonPress(button.getActionID());
        });
        playerList.forEach(lobbyPlayer -> {
            if (lobbyPlayer.isPositionInside(mousePosition))
                lobbyPlayer.handleClick();
        });
    }

    /**
     * Handles the button actions by mapping the IDs to actions.
     *
     * @param actionID ID mapped to an action relevant to the button.
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
     * Toggles player 2 and 3 between enabled and disabled states.
     */
    private void toggleNumberOfPlayers() {
        ruleSet.setTwoPlayers(!ruleSet.getOnlyTwoPlayers());
        updatePlayerNumberStatus();
    }

    /**
     * Updates the visual status based on the number of players.
     */
    private void updatePlayerNumberStatus() {
        playerList.get(2).setEnabled(!ruleSet.getOnlyTwoPlayers());
        playerList.get(3).setEnabled(!ruleSet.getOnlyTwoPlayers());
        twoPlayerPrefixStr = ruleSet.getOnlyTwoPlayers() ? "Dois jogadores:" : "Quatro jogadores:";
        twoPlayerRuleStr = ruleSet.getOnlyTwoPlayers() ? "O reverso é pular" : "Regras normais para reversão";
    }

    /**
     * Toggles the stacking rule and updates the message.
     */
    private void toggleStackRule() {
        ruleSet.setCanStackCards(!ruleSet.canStackCards());
        updateStackRuleLabel();
    }

    /**
     * Updates the text for the stack rule.
     */
    private void updateStackRuleLabel() {
        stackRuleStateStr = "Empilhamento +2/+4: " + (ruleSet.canStackCards() ? "Ligado" : "Desligado");
    }

    /**
     * Toggles the draw rule and updates the message.
     */
    private void toggleDrawTillCanPlayRule() {
        ruleSet.setDrawnTillCanPlay(!ruleSet.shouldDrawnTillCanPlay());
        updateDrawTillCanPlayRuleLabel();
    }

    /**
     * Updates the text for the draw till can play rule.
     */
    private void updateDrawTillCanPlayRuleLabel() {
        drawTillPlayableRuleStateStr = "Empate até poder jogar: " + (ruleSet.shouldDrawnTillCanPlay() ? "Ligado" : "Desligado");
    }

    /**
     * Toggles the seven-zero rule and updates the message.
     */
    private void toggleSevenZeroRule() {
        ruleSet.setSevenZeroRule(!ruleSet.getSevenZeroRule());
        updateSevenZeroRuleLabel();
    }

    /**
     * Updates the text for the seven-zero rule.
     */
    private void updateSevenZeroRuleLabel() {
        sevenZeroRuleStr = "Sete-0: " + (ruleSet.getSevenZeroRule() ? "Ligado (7=Swap, 0=Pass All)" : "Desligado");
    }

    /**
     * Toggles the forced play rule and updates the message.
     */
    private void toggleForcedPlayRule() {
        ruleSet.setForcedPlayRule(!ruleSet.getForcedPlayRule());
        updateForcedPlayRuleLabel();
    }

    /**
     * Updates the text for the forced play rule.
     */
    private void updateForcedPlayRuleLabel() {
        forcedPlayRuleStr = "Jogo forçado: " + (ruleSet.getForcedPlayRule() ? "Ligado" : "Desligado");
    }

    /**
     * Toggles the Jump-In rule and updates the message.
     */
    private void toggleJumpInRule() {
        ruleSet.setAllowJumpInRule(!ruleSet.allowJumpInRule());
        updateJumpInRuleLabel();
    }

    /**
     * Updates the text for the jump in rule.
     */
    private void updateJumpInRuleLabel() {
        jumpInRuleStr = "Pule dentro: " + (ruleSet.allowJumpInRule() ? "Ligado" : "Desligado");
    }

    /**
     * Toggles the No Bluffing rule and updates the message.
     */
    private void toggleNoBluffingRule() {
        ruleSet.setNoBuffingRule(!ruleSet.getNoBluffingRule());
        updateNoBuffingRuleLabel();
    }

    private void updateNoBuffingRuleLabel() {
        noBluffingRuleStr = "Sem blefe: " + (ruleSet.getNoBluffingRule() ? "Ligado" : "Desligado");
    }

    /**
     * Cycles between the options for score limit
     */
    private void cycleScoreLimit() {
        switch(ruleSet.getScoreLimitType()) {
            case OneRound -> ruleSet.setScoreLimitType(ConjuntoRegras.ScoreLimitType.Score200);
            case Score200 -> ruleSet.setScoreLimitType(ConjuntoRegras.ScoreLimitType.Score300);
            case Score300 -> ruleSet.setScoreLimitType(ConjuntoRegras.ScoreLimitType.Score500);
            case Score500 -> ruleSet.setScoreLimitType(ConjuntoRegras.ScoreLimitType.Unlimited);
            case Unlimited -> ruleSet.setScoreLimitType(ConjuntoRegras.ScoreLimitType.OneRound);
        }
        updateScoreLimitLabel();
    }

    /**
     * Updates the label to show a message related to the score.
     */
    private void updateScoreLimitLabel() {
        String scoreLabelMessage = "";
        switch(ruleSet.getScoreLimitType()) {
            case OneRound -> scoreLabelMessage = "Uma rodada";
            case Score200 -> scoreLabelMessage = "200 pontos";
            case Score300 -> scoreLabelMessage = "300 pontos";
            case Score500 -> scoreLabelMessage = "500 pontos";
            case Unlimited -> scoreLabelMessage = "Ilimitado";
        }
        scoreLimitStr = "Limite de pontuação: " + scoreLabelMessage;
    }

    /**
     * Resets all rules to defaults.
     */
    private void resetRulesToDefault() {
        ruleSet.setToDefaults();
        updateAllRuleLabels();
    }

    /**
     * Updates the state of all labels.
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
     * Loads a list of names from AINameList.txt and randomly sorts it ready for use.
     *
     * @return A list of names read in from the file.
     */
    private List<String> getRandomAINameList() {
        List<String> names = new ArrayList<>();
        try {
            Scanner scan = new Scanner(new File("C:\\Users\\Cauet\\Desktop\\Uno-main\\Uno\\AINameList.txt"));

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
