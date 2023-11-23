import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Uno
 *
 * ChallengeOverlay class:
 * Defines the overlay used for choosing to Challenge/Decline/Stack against a +4.
 *
 * @autor Cauet Damasceno
 * @versão 2023
 */
public class DesafioOverlay extends WndInterface implements TurnDecisionOverlayInterface {
    /**
     * List of buttons that can be used in the overlay. Includes a Challenge and Decline button.
     */
    private final List<Butao> buttonList;
    /**
     * Reference to the TurnAction that triggered the display of this overlay.
     */
    private TurnActionFactory.TurnDecisionAction currentAction;
    /**
     * Reference to the player to be used for card selection when stacking is allowed.
     */
    private final Jogador playerReference;
    /**
     * When true via the RuleSet the player's cards can be stacked if possible.
     */
    private final boolean allowStacking;

    /**
     * Initialises the overlay with a Challenge and Decline button, and checks
     * whether the RuleSet allows for stacking to cache processing for later.
     *
     * @param bounds The bounds of the entire game area. The buttons are offset from the centre.
     */
    public DesafioOverlay(Retangulo bounds) {
        super(bounds);
        setEnabled(false);
        buttonList = new ArrayList<>();
        Posicao centre = bounds.getCentre();
        // If bluffing is allowed include the challenge button.
        if(!InterfaceJogo.getCurrentGame().getRuleSet().getNoBluffingRule()) {
            buttonList.add(new Butao(new Posicao(centre.x - 150, centre.y + 100), 100, 40, "Challenge", 1));
        }
        buttonList.add(new Butao(new Posicao(centre.x+50,centre.y+100), 100, 40, "Decline", 0));

        allowStacking = InterfaceJogo.getCurrentGame().getRuleSet().canStackCards();
        playerReference = InterfaceJogo.getCurrentGame().getBottomPlayer();
    }

    /**
     * Not used.
     *
     * @param deltaTime Time since last update.
     */
    @Override
    public void update(int deltaTime) {

    }

    /**
     * Draws the buttons.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    @Override
    public void paint(Graphics g) {
        buttonList.forEach(button -> button.paint(g));
    }

    /**
     * Makes the overlay visible.
     *
     * @param currentAction The TurnAction used to make this overlay appear.
     */
    @Override
    public void showOverlay(TurnActionFactory.TurnDecisionAction currentAction) {
        this.currentAction = currentAction;
        setEnabled(true);
    }

    /**
     * Does nothing if not enabled. Updates the hover state for all buttons.
     *
     * @param mousePosition Position of the mouse during this movement.
     */
    @Override
    public void handleMouseMove(Posicao mousePosition) {
        if(!isEnabled()) return;

        for (Butao button : buttonList) {
            button.setHovering(button.isPositionInside(mousePosition));
        }
    }

    /**
     * Does nothing if not enabled. Checks for a click in the buttons.
     * If a button has been clicked the action is registered to close the overlay.
     * Otherwise if a card has been clicked that is a +4 when stacking is enabled
     * the card is chained.
     *
     * @param mousePosition Position of the mouse cursor during the press.
     * @param isLeft        If true, the mouse button is left, otherwise is right.
     */
    @Override
    public void handleMousePress(Posicao mousePosition, boolean isLeft) {
        if(!isEnabled()) return;

        for (Butao button : buttonList) {
            if(button.isPositionInside(mousePosition)) {
                currentAction.injectProperty("isChaining", 0);
                currentAction.injectFlagProperty(button.getActionID());
                setEnabled(false);
                return;
            }
        }

        if(allowStacking) {
            Carta clickedCard = playerReference.chooseCardFromClick(mousePosition);
            if(clickedCard != null && clickedCard.getFaceValueID() == 13) {
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
