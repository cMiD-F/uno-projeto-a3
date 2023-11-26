import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * Uno
 *
 * Classe OverlayGerenciador:
 * Define um gerenciador para controlar sobreposições para InterfaceJogo.
 * Essas sobreposições incluem aquelas que aguardam a interface do player com
 * elas
 * e alguns que são apenas informativos.
 *
 * @autor Cauet Damasceno
 * @versão 2023
 */
public class OverlayGerenciador extends WndInterface {
    /**
     * Interfaces mapeadas para strings exclusivas.
     */
    private final Map<String, WndInterface> overlays;
    /**
     * Ação atual para um TurnDecisionAction ativo.
     */
    private TurnActionFactory.TurnDecisionAction overlayAction;

    /**
     * Inicialize as interfaces prontas para qualquer coisa que precise ficar
     * visível.
     *
     * @param limites Os limites de toda a área de jogo.
     */
    public OverlayGerenciador(Retangulo bounds, List<Jogador> playerList) {
        super(bounds);
        setEnabled(true);
        overlays = new HashMap<>();
        SeletorCorCuringa wildColourSelectorOverlay = new SeletorCorCuringa(
                new Posicao(bounds.width / 2 - 100, bounds.height / 2 - 100), 200, 200);
        PassarOuJogarOverlay keepOrPlayOverlay = new PassarOuJogarOverlay(
                new Retangulo(new Posicao(0, 0), bounds.width, bounds.height));
        PlayerSelectionOverlay playerSelectionOverlay = new PlayerSelectionOverlay(
                new Retangulo(new Posicao(0, 0), bounds.width, bounds.height), playerList);
        StatusOverlay statusOverlay = new StatusOverlay(new Retangulo(new Posicao(0, 0), bounds.width, bounds.height));
        DesafioOverlay challengeOverlay = new DesafioOverlay(bounds);
        EscolhaDePilha stackChoiceOverlay = new EscolhaDePilha(bounds);
        overlays.put("wildColour", wildColourSelectorOverlay);
        overlays.put("keepOrPlay", keepOrPlayOverlay);
        overlays.put("otherPlayer", playerSelectionOverlay);
        overlays.put("statusOverlay", statusOverlay);
        overlays.put("isChallenging", challengeOverlay);
        overlays.put("isStacking", stackChoiceOverlay);

        BotaoUno unoButton = new BotaoUno(new Posicao(bounds.position.x + bounds.width - BotaoUno.WIDTH - 40,
                bounds.position.y + bounds.height - BotaoUno.HEIGHT - 40));
        AntiUnoButton antiUnoButton = new AntiUnoButton(
                new Posicao(bounds.position.x + bounds.width - BotaoUno.WIDTH - 40 - 100,
                        bounds.position.y + bounds.height - BotaoUno.HEIGHT - 40));
        for (int i = 0; i < playerList.size(); i++) {
            Posicao playerCentre = playerList.get(i).getCentreOfBounds();
            PlayerFlashOverlay skipVisualOverlay = new PlayerFlashOverlay(playerCentre, "Pular", Color.RED, 40);
            overlays.put("SkipVisual" + i, skipVisualOverlay);
            PlayerFlashOverlay drawNMessageOverlay = new PlayerFlashOverlay(playerCentre, "", Color.RED, 40);
            overlays.put("DrawN" + i, drawNMessageOverlay);
            SucessoDesafioOverlay challengeSuccessOverlay = new SucessoDesafioOverlay(
                    new Retangulo(playerCentre, 100, 100));
            overlays.put("VitoriaDesafio" + i, challengeSuccessOverlay);
            FalhaDesafioOverlay challengeFailedOverlay = new FalhaDesafioOverlay(new Retangulo(playerCentre, 100, 100));
            overlays.put("ChallengeFailed" + i, challengeFailedOverlay);
            ChamadaUno unoCalledOverlay = new ChamadaUno(new Posicao(playerCentre.x, playerCentre.y + 20));
            overlays.put("UNOCalled" + i, unoCalledOverlay);
            PlayerFlashOverlay antiUnoOverlay = new PlayerFlashOverlay(new Posicao(playerCentre.x, playerCentre.y + 20),
                    "!", new Color(226, 173, 67), 50);
            overlays.put("AntiUnoCalled" + i, antiUnoOverlay);
            PlayerFlashOverlay jumpInOverlay = new PlayerFlashOverlay(new Posicao(playerCentre.x, playerCentre.y + 20),
                    "PULOU", Color.ORANGE, 40);
            overlays.put("JumpIn" + i, jumpInOverlay);
        }
        overlays.put("UnoButton", unoButton);
        overlays.put("antiUnoButton", antiUnoButton);

    }

    /**
     * Encontra a sobreposição correspondente para uma decisão, se necessário, e a
     * mostra.
     * Depois mostra o statusOverlay em todas as situações mesmo que não seja a
     * atual
     * decisão do jogador.
     *
     * @param currentAction Ação a ser usada para determinar qual sobreposição
     *                      mostrar.
     */
    public void showDecisionOverlay(TurnActionFactory.TurnDecisionAction currentAction) {
        if (currentAction.timeOut) {
            setEnabled(true);
            if (InterfaceJogo.getCurrentGame().getCurrentPlayer().getPlayerType() == Jogador.PlayerType.UnoJogador) {
                WndInterface overlayToShow = overlays.get(currentAction.flagName);
                if (overlayToShow instanceof TurnDecisionOverlayInterface) {
                    ((TurnDecisionOverlayInterface) overlayToShow).showOverlay(currentAction);
                }
            }
            overlayAction = currentAction;
            ((TurnDecisionOverlayInterface) overlays.get("statusOverlay")).showOverlay(currentAction);
        }
    }

    /**
     * Encontra a interface correspondente e a torna visível, se possível.
     *
     * @param overlayName Nome que mapeia para uma interface.
     */
    public void showGeneralOverlay(String overlayName) {
        // Dividir para permitir entradas de parâmetros separadas por ;
        String[] splitOverlayName = overlayName.split(";");
        WndInterface overlayToShow = overlays.get(splitOverlayName[0]);
        if (overlayToShow instanceof GeralOverlayInterface) {
            ((GeralOverlayInterface) overlayToShow).showOverlay();
            if (splitOverlayName[0].startsWith("DrawN")) {
                // Define o número a ser exibido.
                ((PlayerFlashOverlay) overlayToShow).setMessage("+" + splitOverlayName[1]);
            }
        }
    }

    /**
     * Oculta todas as sobreposições de decisão chamadas automaticamente quando o
     * TurnAction
     * alterações em atualização().
     */
    public void hideAllDecisionOverlays() {
        overlays.forEach((key, overlay) -> {
            if (overlay instanceof TurnDecisionOverlayInterface) {
                overlay.setEnabled(false);
            }
        });
        setEnabled(false);
    }

    /**
     * Atualiza todas as sobreposições ativas e oculta todas as sobreposições de
     * decisão se o
     * TurnAction alterado.
     *
     * @param deltaTime Tempo desde a última atualização.
     */
    @Override
    public void update(int deltaTime) {
        if (overlayAction != InterfaceJogo.getCurrentGame().getCurrentTurnAction()) {
            overlayAction = null;
            hideAllDecisionOverlays();
        }

        overlays.forEach((key, overlay) -> {
            if (overlay.isEnabled()) {
                overlay.update(deltaTime);
            }
        });
    }

    /**
     * Pinta todas as sobreposições habilitadas.
     *
     * @param g Referência ao objeto Graphics para renderização.
     */
    @Override
    public void paint(Graphics g) {
        overlays.forEach((key, overlay) -> {
            if (overlay.isEnabled()) {
                overlay.paint(g);
            }
        });
    }

    /**
     * Passa o evento mousePress para todas as sobreposições habilitadas.
     *
     * @param mousePosition Posição do cursor do mouse durante o pressionamento.
     * @param isLeft        Se verdadeiro, o botão do mouse está para a esquerda,
     *                      caso contrário, está para a direita.
     */
    @Override
    public void handleMousePress(Posicao mousePosition, boolean isLeft) {
        overlays.forEach((key, overlay) -> {
            if (overlay.isEnabled()) {
                overlay.handleMousePress(mousePosition, isLeft);
            }
        });
    }

    /**
     * Passa o evento mouseMove para todas as sobreposições habilitadas.
     *
     * @param mousePosition Posição do mouse durante este movimento.
     */
    @Override
    public void handleMouseMove(Posicao mousePosition) {
        overlays.forEach((key, overlay) -> {
            if (overlay.isEnabled()) {
                overlay.handleMouseMove(mousePosition);
            }
        });
    }
}
