/**
 * Uno
 *
 * Interface TurnDecisionOverlayInterface:
 * Define uma interface a ser usada para definir sobreposições que podem ser
 * feito para aparecer usando um TurnDecisionAction.
 *
 * @autor Cauet Damasceno
 * @versão 2023
 */
public interface TurnDecisionOverlayInterface {
    /**
     * Mostre a sobreposição.
     *
     * @param currentAction A ação usada para acionar esta interface.
     */
    void showOverlay(TurnActionFactory.TurnDecisionAction currentAction);
}
