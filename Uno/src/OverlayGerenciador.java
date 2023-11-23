import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * Uno
 *
 * OverlayManager class:
 * Defines a manager to control overlays for the CurrentGameInterface.
 * These overlays include those that wait for the player to interface with them
 * and some that are just informational.
 *
 * @autor Cauet Damasceno
 * @versão 2023
 */
public class OverlayGerenciador extends WndInterface {
    /**
     * Interfaces mapped to unique strings.
     */
    private final Map<String, WndInterface> overlays;
    /**
     * Current action for an active TurnDecisionAction.
     */
    private TurnActionFactory.TurnDecisionAction overlayAction;

    /**
     * Initialise the interfaces all ready for any that needs to be made visible.
     *
     * @param bounds The bounds of the entire game area.
     */
    public OverlayGerenciador(Retangulo bounds, List<Jogador> playerList) {
        super(bounds);
        setEnabled(true);
        overlays = new HashMap<>();
        SeletorCorCuringa wildColourSelectorOverlay = new SeletorCorCuringa(new Posicao(bounds.width/2-100,bounds.height/2-100),200,200);
        PassarOuJogarOverlay keepOrPlayOverlay = new PassarOuJogarOverlay(new Retangulo(new Posicao(0,0), bounds.width, bounds.height));
        PlayerSelectionOverlay playerSelectionOverlay = new PlayerSelectionOverlay(new Retangulo(new Posicao(0,0), bounds.width, bounds.height), playerList);
        StatusOverlay statusOverlay = new StatusOverlay(new Retangulo(new Posicao(0,0), bounds.width, bounds.height));
        DesafioOverlay challengeOverlay = new DesafioOverlay(bounds);
        EscolhaDePilha stackChoiceOverlay = new EscolhaDePilha(bounds);
        overlays.put("wildColour", wildColourSelectorOverlay);
        overlays.put("keepOrPlay", keepOrPlayOverlay);
        overlays.put("otherPlayer", playerSelectionOverlay);
        overlays.put("statusOverlay", statusOverlay);
        overlays.put("isChallenging", challengeOverlay);
        overlays.put("isStacking", stackChoiceOverlay);

        BotaoUno unoButton = new BotaoUno(new Posicao(bounds.position.x + bounds.width - BotaoUno.WIDTH-40,
                bounds.position.y + bounds.height - BotaoUno.HEIGHT-40));
        AntiUnoButton antiUnoButton = new AntiUnoButton(new Posicao(bounds.position.x + bounds.width - BotaoUno.WIDTH-40-100,
                bounds.position.y + bounds.height - BotaoUno.HEIGHT-40));
        for(int i = 0; i < playerList.size(); i++) {
            Posicao playerCentre = playerList.get(i).getCentreOfBounds();
            PlayerFlashOverlay skipVisualOverlay = new PlayerFlashOverlay(playerCentre, "Pular", Color.RED, 40);
            overlays.put("SkipVisual"+i,skipVisualOverlay);
            PlayerFlashOverlay drawNMessageOverlay = new PlayerFlashOverlay(playerCentre, "", Color.RED, 40);
            overlays.put("DrawN"+i,drawNMessageOverlay);
            SucessoDesafioOverlay challengeSuccessOverlay = new SucessoDesafioOverlay(new Retangulo(playerCentre, 100,100));
            overlays.put("ChallengeSuccess"+i,challengeSuccessOverlay);
            FalhaDesafioOverlay challengeFailedOverlay = new FalhaDesafioOverlay(new Retangulo(playerCentre, 100,100));
            overlays.put("ChallengeFailed"+i,challengeFailedOverlay);
            ChamadaUno unoCalledOverlay = new ChamadaUno(new Posicao(playerCentre.x,playerCentre.y+20));
            overlays.put("UNOCalled"+i,unoCalledOverlay);
            PlayerFlashOverlay antiUnoOverlay = new PlayerFlashOverlay(new Posicao(playerCentre.x,playerCentre.y+20),
                    "!", new Color(226, 173, 67), 50);
            overlays.put("AntiUnoCalled"+i,antiUnoOverlay);
            PlayerFlashOverlay jumpInOverlay = new PlayerFlashOverlay(new Posicao(playerCentre.x,playerCentre.y+20),
                    "PULOU", Color.ORANGE, 40);
            overlays.put("JumpIn"+i, jumpInOverlay);
        }
        overlays.put("UnoButton", unoButton);
        overlays.put("antiUnoButton", antiUnoButton);

    }

    /**
     * Finds the matching overlay for a decision if necessary, and then shows it.
     * Then shows the statusOverlay in all situations even if it is not the current player's decision.
     *
     * @param currentAction Action to use for determining which overlay to show.
     */
    public void showDecisionOverlay(TurnActionFactory.TurnDecisionAction currentAction) {
        if(currentAction.timeOut) {
            setEnabled(true);
            if(InterfaceJogo.getCurrentGame().getCurrentPlayer().getPlayerType() == Jogador.PlayerType.ThisPlayer) {
                WndInterface overlayToShow = overlays.get(currentAction.flagName);
                if (overlayToShow instanceof TurnDecisionOverlayInterface) {
                   ((TurnDecisionOverlayInterface)overlayToShow).showOverlay(currentAction);
                }
            }
            overlayAction = currentAction;
            ((TurnDecisionOverlayInterface)overlays.get("statusOverlay")).showOverlay(currentAction);
        }
    }

    /**
     * Finds the matching interface and makes it visible if possible.
     *
     * @param overlayName Name that maps to an interface.
     */
    public void showGeneralOverlay(String overlayName) {
        // Split to allow for parameter inputs separated by ;
        String[] splitOverlayName = overlayName.split(";");
        WndInterface overlayToShow = overlays.get(splitOverlayName[0]);
        if(overlayToShow instanceof GeralOverlayInterface) {
            ((GeralOverlayInterface)overlayToShow).showOverlay();
            if(splitOverlayName[0].startsWith("DrawN")) {
                // Sets the number to be displayed.
                ((PlayerFlashOverlay)overlayToShow).setMessage("+"+splitOverlayName[1]);
            }
        }
    }

    /**
     * Hides all the decision overlays automatically called when the TurnAction changes in update().
     */
    public void hideAllDecisionOverlays() {
        overlays.forEach((key, overlay) -> {
            if(overlay instanceof TurnDecisionOverlayInterface) {
                overlay.setEnabled(false);
            }
        });
        setEnabled(false);
    }

    /**
     * Updates all the active overlays and hides all the decision overlays if the TurnAction changed.
     *
     * @param deltaTime Time since last update.
     */
    @Override
    public void update(int deltaTime) {
        if(overlayAction != InterfaceJogo.getCurrentGame().getCurrentTurnAction()) {
            overlayAction = null;
            hideAllDecisionOverlays();
        }

        overlays.forEach((key, overlay) -> {
            if(overlay.isEnabled()) {
                overlay.update(deltaTime);
            }
        });
    }

    /**
     * Paints all enabled overlays.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    @Override
    public void paint(Graphics g) {
        overlays.forEach((key, overlay) -> {
            if(overlay.isEnabled()) {
                overlay.paint(g);
            }
        });
    }

    /**
     * Passes the mousePress event on to all enabled overlays.
     *
     * @param mousePosition Position of the mouse cursor during the press.
     * @param isLeft        If true, the mouse button is left, otherwise is right.
     */
    @Override
    public void handleMousePress(Posicao mousePosition, boolean isLeft) {
        overlays.forEach((key, overlay) -> {
            if(overlay.isEnabled()) {
                overlay.handleMousePress(mousePosition, isLeft);
            }
        });
    }

    /**
     * Passes the mouseMove event on to all enabled overlays.
     *
     * @param mousePosition Position of the mouse during this movement.
     */
    @Override
    public void handleMouseMove(Posicao mousePosition) {
        overlays.forEach((key, overlay) -> {
            if(overlay.isEnabled()) {
                overlay.handleMouseMove(mousePosition);
            }
        });
    }
}
