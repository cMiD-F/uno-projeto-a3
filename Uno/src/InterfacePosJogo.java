import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Uno
 *
 * PostGameInterface class:
 * Defines a simple interface that shows the scores from the just completed round.
 *
 * @autor Cauet Damasceno
 * @versão 2023
 */
public class InterfacePosJogo extends WndInterface {
    /**
     * List of buttons visible on the interface.
     */
    private final List<Butao> buttonList;
    /**
     * Reference to the players that are being shown for scores.
     */
    private final List<Jogador> players;
    /**
     * Reference to the Ruleset to check for score limits.
     */
    private final ConjuntoRegras ruleSet;
    /**
     * Reference to the GamePanel for exiting the interface.
     */
    private final GamePainel gamePanel;
    /**
     * Strings to cache the String generation for the player list.
     */
    private final List<String> playerStrings;
    /**
     * Winner for this round, and possibly the entire match.
     */
    private String roundWinnerStr;
    /**
     * When true, the score limit has been reached showing an extra message.
     */
    private boolean scoreLimitReached;
    /**
     * A String showing the score limit rule.
     */
    private String scoreLimitStr;

    /**
     * Initialise the interface with bounds and make it enabled.
     *
     * @param bounds Bounds of the interface.
     */
    public InterfacePosJogo(Retangulo bounds, List<Jogador> playerList, ConjuntoRegras ruleSet, GamePainel gamePanel) {
        super(bounds);
        this.players = playerList;
        this.ruleSet = ruleSet;
        this.gamePanel = gamePanel;

        playerStrings = new ArrayList<>();
        for(Jogador player : playerList) {
            playerStrings.add((player.getPlayerType() == Jogador.PlayerType.ThisPlayer ? "You: " : "AI: ")
                    + player.getPlayerName());
            if(player.getWon()) {
                roundWinnerStr = player.getPlayerName();
                switch(ruleSet.getScoreLimitType()) {
                    case OneRound -> scoreLimitReached = true;
                    case Score200 -> scoreLimitReached = player.getTotalScore() >= 200;
                    case Score300 -> scoreLimitReached = player.getTotalScore() >= 300;
                    case Score500 -> scoreLimitReached = player.getTotalScore() >= 500;
                    case Unlimited -> scoreLimitReached = false;
                }
            }
        }

        scoreLimitStr = "Score Limit: ";
        switch(ruleSet.getScoreLimitType()) {
            case OneRound -> scoreLimitStr += "Uma rodada";
            case Score200 -> scoreLimitStr += "200 Pontos";
            case Score300 -> scoreLimitStr += "300 Pontos";
            case Score500 -> scoreLimitStr += "500 Pontos";
            case Unlimited -> scoreLimitStr += "Ilimitado";
        }

        buttonList = new ArrayList<>();
        if(scoreLimitReached) {
            buttonList.add(new Butao(new Posicao(bounds.width / 2 - 250 - 10, 620), 250, 40,
                    "Voltar ao lobby", 1));
            buttonList.add(new Butao(new Posicao(bounds.width / 2 + 10, 620), 250, 40,
                    "Novo jogo com as mesmas configurações", 3));
        } else {
            buttonList.add(new Butao(new Posicao(bounds.width / 2 - 125 - 250 - 20, 620), 250, 40,
                    "Voltar ao lobby", 1));
            buttonList.add(new Butao(new Posicao(bounds.width / 2 - 125, 620), 250, 40,
                    "Continuar na próxima rodada", 2));
            buttonList.add(new Butao(new Posicao(bounds.width / 2 + 125 + 20, 620), 250, 40,
                    "Novo jogo com as mesmas configurações", 3));
        }
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
     * Draws all the elements of the interface.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    @Override
    public void paint(Graphics g) {
        drawBackground(g);
        gamePanel.paintUnoTitle(g, bounds);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        int strWidth = g.getFontMetrics().stringWidth("Resumo pós-jogo");
        g.drawString("Resumo pós-jogo", bounds.width/2-strWidth/2, 120);
        drawPlayers(g);

        buttonList.forEach(button -> button.paint(g));
    }

    /**
     * Draws the background and game title.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    private void drawBackground(Graphics g) {
        g.setColor(new Color(205, 138, 78, 128));
        g.fillRect(bounds.width/4, 80, bounds.width/2, 500);
        g.setColor(Color.BLACK);
        g.drawRect(bounds.width/4, 80, bounds.width/2, 500);
    }

    /**
     * Draws all the player related elements including lines for the grid showing player stats.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    private void drawPlayers(Graphics g) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawLine(bounds.width/4 + 10, 200, bounds.width*3/4-10, 200);
        g.drawString("Round Score", bounds.width/2-10, 180);
        g.drawString("Total Score", bounds.width/2+170, 180);
        for(int i = 0; i < players.size(); i++) {
            g.drawLine(bounds.width/4 + 10, 260+i*60, bounds.width*3/4-10, 260+i*60);
            g.drawString(playerStrings.get(i), bounds.width/4 + 50, 240+i*60);
            g.drawString(players.get(i).getCurrentRoundScore()+"", bounds.width/2, 240+i*60);
            g.drawString(players.get(i).getTotalScore()+"", bounds.width/2+180, 240+i*60);
        }
        g.drawLine(bounds.width/4 + 10, 200, bounds.width/4 + 10, 200+players.size()*60);
        g.drawLine(bounds.width/2 -40, 150, bounds.width/2 -40, 200+players.size()*60);
        g.drawLine(bounds.width/2+130, 150, bounds.width/2+130, 200+players.size()*60);
        g.drawLine(bounds.width*3/4-10, 150, bounds.width*3/4-10, 200+players.size()*60);
        g.drawLine(bounds.width/2 -40, 150, bounds.width*3/4-10, 150);

        g.drawString("Round Winner: ", bounds.width/4 + 25, 490);
        g.drawString(roundWinnerStr, bounds.width/4 + 175, 490);
        g.drawString(scoreLimitStr, bounds.width/2+20, 490);
        if(scoreLimitReached) {
            g.drawString("Score limit reached!", bounds.width/2+40, 530);
        }
    }

    /**
     * Does nothing if not enabled. Checks all the buttons for presses and handles actions if necessary.
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
    }

    /**
     * Does nothing if not enabled. Updates the hover state of all buttons.
     *
     * @param mousePosition Position of the mouse during this movement.
     */
    @Override
    public void handleMouseMove(Posicao mousePosition) {
        if(!isEnabled()) return;
        buttonList.forEach(button -> button.setHovering(button.isPositionInside(mousePosition)));
    }

    /**
     * Looks up the action to apply based on an actionID for a button.
     *
     * @param actionID The actionID for a button that was pressed.
     */
    private void handleButtonPress(int actionID) {
        switch(actionID) {
            case 1 -> gamePanel.showLobby();
            case 2 -> gamePanel.startNextRound(players, ruleSet);
            case 3 -> startNewGameWithSameSettings();
        }
    }

    /**
     * Wipes the score of all players and then starts a new game.
     */
    private void startNewGameWithSameSettings() {
        players.forEach(Jogador::resetScore);
        gamePanel.startNextRound(players, ruleSet);
    }
}
