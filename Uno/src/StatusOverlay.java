import java.awt.*;

/**
 * Uno
 *
 * StatusOverlay class:
 * Defines the overlay used for showing information status about a TurnDecisionAction.
 *
 * @autor Cauet Damasceno
 * @versão 2023
 */
public class StatusOverlay extends WndInterface implements TurnDecisionOverlayInterface {
    /**
     * Text to display showing the current status.
     */
    private String statusText;
    /**
     * The font to render the status text with.
     */
    private final Font statusFont = new Font("Arial", Font.BOLD, 20);
    /**
     * Centre of the bounds to draw the text at.
     */
    private final Posicao centre;
    /**
     * Timeout representing time remaining to complete the action.
     */
    private double timeOut;
    /**
     * String showing the number representing the time remaining.
     */
    private String timeOutStr;

    /**
     * Initialise the interface ready to show a status.
     *
     * @param bounds The bounds of the entire game area.
     */
    public StatusOverlay(Retangulo bounds) {
        super(bounds);
        setEnabled(false);
        centre = bounds.getCentre();
        timeOutStr = "";
    }

    /**
     * Updates the timeOut remaining.
     *
     * @param deltaTime Time since last update.
     */
    @Override
    public void update(int deltaTime) {
        timeOut -= deltaTime / 1000.0;
        if(timeOut < 0) timeOut = 0;
        timeOutStr = (int)timeOut + "s";
    }

    /**
     * Draws the text for the status and timer.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    @Override
    public void paint(Graphics g) {
        g.setFont(statusFont);
        int strWidth = g.getFontMetrics().stringWidth(statusText);
        g.setColor(new Color(184, 154, 143, 204));
        g.fillRect(centre.x-strWidth/2-10, centre.y-65, strWidth+20, 60);
        g.setColor(Color.BLACK);
        g.drawString(statusText, centre.x-strWidth/2, centre.y-20);
        strWidth = g.getFontMetrics().stringWidth(timeOutStr);
        g.drawString(timeOutStr, centre.x-strWidth/2-2, centre.y-40+2);
        g.setColor(timeOut < 6 ? Color.RED : Color.YELLOW);
        g.drawString(timeOutStr, centre.x-strWidth/2, centre.y-40);
    }

    /**
     * Shows the overlay by generating a status depending on the currentAction.
     *
     * @param currentAction The action used to trigger this interface.
     */
    @Override
    public void showOverlay(TurnActionFactory.TurnDecisionAction currentAction) {
        setEnabled(true);
        statusText = createContextString(currentAction);
        timeOut = InterfaceJogo.getCurrentGame().getRuleSet().getDefaultTimeOut();
        timeOutStr = (int)timeOut + "s";
    }

    /**
     * Checks whether the action is one the player has to do or if it is someone else,
     * and constructs a message relevant to the current situation.
     *
     * @param currentAction The action to use for context.
     * @return A String representing the status message to be displayed.
     */
    private String createContextString(TurnActionFactory.TurnDecisionAction currentAction) {
        String playerName = InterfaceJogo.getCurrentGame().getCurrentPlayer().getPlayerName();
        String result;
        switch(currentAction.flagName) {
            case "keepOrPlay" -> result = "escolhendo Manter ou Jogar.";
            case "wildColour" -> result = "escolhendo a cor do curinga.";
            case "isChallenging" -> result = "escolhendo Resposta a +4.";
            case "otherPlayer" -> result = "escolhendo outro jogador para trocar.";
            default -> result = "pensamento...";
        }
        if(InterfaceJogo.getCurrentGame().getCurrentPlayer().getPlayerType() == Jogador.PlayerType.UnoJogador) {
            return "Você é " + result;
        }
        return playerName + " é " + result;
    }
}
