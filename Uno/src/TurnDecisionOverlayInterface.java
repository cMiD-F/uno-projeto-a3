/**
 * Uno
 *
 * TurnDecisionOverlayInterface interface:
 * Defines an interface to be used for defining overlays that can be
 * made to appear using a TurnDecisionAction.
 *
 * @autor Cauet Damasceno
 * @versão 2023
 */
public interface TurnDecisionOverlayInterface {
    /**
     * Show the overlay.
     *
     * @param currentAction The action used to trigger this interface.
     */
    void showOverlay(TurnActionFactory.TurnDecisionAction currentAction);
}
