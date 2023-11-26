import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Uno
 *
 * Classe AIJogador:
 * Define uma variação específica do Player que é manipulado
 * automaticamente pela IA escolhendo ações a serem executadas durante as
 * atualizações
 * com atrasos aleatórios para dar aos jogadores tempo para observar as ações
 * conforme elas ocorrem.
 *
 * @autor Cauet Damasceno
 * @versão 2023
 */
public class AIJogador extends Jogador {
    /**
     * Definir os diferentes tipos de estratégias que uma IA pode ter.
     * Aleatório: selecione um dos outros três aleatoriamente.
     * Ofensivo: Usa primeiras cartas de baixo valor.
     * Defensivo: Usa as primeiras cartas de alto valor.
     * Caótico: Usa cartas solicitadas de qualquer uma que possa ser jogada.
     */
    public enum AIStrategy {
        Ofensivo, Defensivo, Caotico, Aleatorio
    }

    /**
     * A estratégia a ser usada para selecionar como as cartas serão jogadas.
     */
    private AIStrategy strategy;
    /**
     * Temporizador usado para atrasar entre ações.
     */
    private double delayTimer;
    /**
     * ID do jogador considerado para convocação.
     */
    private int consideringPlayerID;
    /**
     * Aguarde até que seja tomada uma decisão sobre a chamada.
     */
    private double consideringDelayTimer;
    /**
     * Quando verdadeiro, a situação atual permite um salto.
     * A transição de falso para verdadeiro é usada para avaliar considerJumpIn.
     */
    private boolean canJumpIn;
    /**
     * Quando verdadeiro, o AIJogador optou por entrar após o período
     * considerandoJumpInTimer.
     */
    private boolean consideringJumpIn;
    /**
     * Temporizador até que um salto seja executado, se ainda permitido.
     */
    private double consideringJumpInTimer;

    /**
     * Define uma IA em cima de um jogador básico pronto para realizar ações
     * durante a atualização().
     *
     * @param playerNumber       O playerID associado a este player.
     * @param playerName         O nome mostrado para este jogador.
     * @param limites            A região para colocar os cartões.
     * @param strategy           A estratégia que a IA usará para jogar.
     * @param showPlayerNameLeft Quando verdadeiro, o nome do jogador é centralizado
     *                           à esquerda
     *                           lado dos limites, caso contrário, será centralizado
     *                           no
     *                           principal.
     */
    public AIJogador(int playerNumber, String playerName, Retangulo bounds, AIStrategy strategy,
            boolean showPlayerNameLeft) {
        super(playerNumber, playerName, PlayerType.AIJogador, bounds, showPlayerNameLeft);
        if (strategy == AIStrategy.Aleatorio) {
            selectRandomStrategy();
        } else {
            this.strategy = strategy;
        }
        resetDelayTimer();
        consideringDelayTimer = -1;
    }

    /**
     * Escolhe uma estratégia aleatória.
     */
    private void selectRandomStrategy() {
        switch ((int) (Math.random() * 3)) {
            case 0 -> strategy = AIStrategy.Ofensivo;
            case 1 -> strategy = AIStrategy.Defensivo;
            case 2 -> strategy = AIStrategy.Caotico;
        }
    }

    /**
     * Verifica ações válidas que podem ser realizadas por este jogador e
     * executa-os se houver capacidade para isso.
     *
     * @param deltaTime Tempo desde a última atualização.
     */
    @Override
    public void update(int deltaTime) {
        updateAntiUnoCheck(deltaTime);
        updateJumpInCheck(deltaTime);

        // Não faça mais nada se este não for o jogador atual.
        if (InterfaceJogo.getCurrentGame().getCurrentPlayer().getPlayerID() != getPlayerID()) {
            return;
        }

        // Atraso até
        delayTimer -= deltaTime;
        if (delayTimer <= 0) {
            resetDelayTimer();
        } else {
            return;
        }

        // Se não houver nenhuma ação de turno para lidar com isso significa que o
        // jogador está
        // realizando seu turno normal
        if (InterfaceJogo.getCurrentGame().getCurrentTurnAction() == null) {
            performTurn();
        } else {
            // Trata a ação turn se for necessário
            TurnActionFactory.TurnAction currentAction = InterfaceJogo.getCurrentGame().getCurrentTurnAction();
            if (currentAction instanceof TurnActionFactory.TurnDecisionAction) {
                TurnActionFactory.TurnDecisionAction decisionAction = (TurnActionFactory.TurnDecisionAction) currentAction;
                if (decisionAction.timeOut) {
                    handleTurnDecision(decisionAction);
                }
            }
        }
    }

    /**
     * Verifica o status atual de quaisquer chamadas anti-uno disponíveis e faz uma
     * decisão de chamá-los.
     *
     * @param deltaTime Tempo desde a última atualização.
     */
    private void updateAntiUnoCheck(int deltaTime) {
        for (Jogador player : InterfaceJogo.getCurrentGame().getAllPlayers()) {
            if (player != this && !player.isSafe() && player.getHand().size() == 1) {
                if (consideringPlayerID != player.getPlayerID()) {
                    consideringDelayTimer = Math.random() * 800 + 200;
                }
                consideringPlayerID = player.getPlayerID();
            }
        }
        if (consideringPlayerID == -1
                || InterfaceJogo.getCurrentGame().getPlayerByID(consideringPlayerID).isSafe()) {
            consideringPlayerID = -1;
        } else {
            consideringDelayTimer -= deltaTime;
            if (consideringDelayTimer <= 0) {
                consideringDelayTimer = Math.random() * 1200 + 300;
                if (Math.random() * 100 < 30) {
                    InterfaceJogo.getCurrentGame().applyAntiUno(consideringPlayerID);
                }
            }
        }
    }

    /**
     * Atualiza o estado do salto se for permitido e possível para isso
     * jogador.
     *
     * @param deltaTime Tempo desde a última atualização.
     */
    private void updateJumpInCheck(int deltaTime) {
        if (InterfaceJogo.getCurrentGame().getRuleSet().allowJumpInRule()
                && InterfaceJogo.getCurrentGame().getCurrentTurnAction() == null
                && InterfaceJogo.getCurrentGame().getCurrentPlayer() != this) {
            Carta topCard = InterfaceJogo.getCurrentGame().getTopCard();
            List<Carta> validCards = getHand().stream()
                    .filter(card -> card.getFaceValueID() == topCard.getFaceValueID()
                            && card.getColourID() == topCard.getColourID())
                    .collect(Collectors.toList());
            if (!validCards.isEmpty()) {
                if (!canJumpIn) {
                    consideringJumpIn = Math.random() * 100 < 80;
                    consideringJumpInTimer = Math.random() * 200 + 100;
                }
                canJumpIn = true;
            } else {
                canJumpIn = false;
                consideringJumpIn = false;
            }
        } else {
            canJumpIn = false;
            consideringJumpIn = false;
        }

        if (consideringJumpIn) {
            consideringJumpInTimer -= deltaTime;
            if (consideringJumpInTimer <= 0) {
                Carta topCard = InterfaceJogo.getCurrentGame().getTopCard();
                List<Carta> validCards = getHand().stream()
                        .filter(card -> card.getFaceValueID() == topCard.getFaceValueID()
                                && card.getColourID() == topCard.getColourID())
                        .collect(Collectors.toList());
                if (!validCards.isEmpty()) {
                    InterfaceJogo.getCurrentGame().jumpIn(getPlayerID(), validCards.get(0));
                }
            }
        }
    }

    /**
     * Realiza o turno verificando se há alguma jogada válida a ser executada.
     * Se não houver movimento válido, uma carta é retirada do baralho.
     * Caso contrário, uma carta é escolhida entre os movimentos válidos e jogada
     * inicializando um
     * TurnAction.
     */
    private void performTurn() {
        Carta topCard = InterfaceJogo.getCurrentGame().getTopCard();
        List<Carta> validMoves = getValidMoves(topCard.getFaceValueID(), topCard.getColourID());
        if (validMoves.isEmpty()) {
            InterfaceJogo.getCurrentGame()
                    .setCurrentTurnAction(TurnActionFactory.drawCardAsAction(getPlayerID()));
        } else {
            Carta cardToPlay = chooseCard(validMoves);
            checkCallUNO();
            InterfaceJogo.getCurrentGame().setCurrentTurnAction(TurnActionFactory.playCardAsAction(
                    getPlayerID(), cardToPlay.getCardID(), cardToPlay.getFaceValueID(), cardToPlay.getColourID()));
        }
    }

    /**
     * Redefine o temporizador de atraso de volta ao padrão.
     */
    private void resetDelayTimer() {
        delayTimer = 1500;
    }

    /**
     * Pega uma lista de cartas que podem ser jogadas e escolhe a carta
     * com base na estratégia selecionada para a IA.
     *
     * @param validCards Uma coleção de cartas válidas para serem jogadas.
     * @return Uma única carta válida selecionada para ser jogada.
     */
    private Carta chooseCard(List<Carta> validCards) {
        if (strategy == AIStrategy.Caotico) {
            return validCards.get((int) (Math.random() * validCards.size()));
        }

        validCards.sort(Comparator.comparingInt(Carta::getScoreValue));

        if (strategy == AIStrategy.Defensivo) {
            return validCards.get(validCards.size() - 1);
        } else { // Ofensivo
            return validCards.get(0);
        }
    }

    /**
     * Verifica o flagName da decisãoAction para determinar um
     * resposta apropriada baseada em outros métodos desta classe.
     *
     * @param DecisionAction Referência à ação atual que requer uma decisão.
     */
    private void handleTurnDecision(TurnActionFactory.TurnDecisionAction decisionAction) {
        switch (decisionAction.flagName) {
            case "wildColour" -> chooseWildColour(decisionAction);
            case "keepOrPlay" -> chooseKeepOrPlay(decisionAction);
            case "otherPlayer" -> choosePlayerToSwapWith(decisionAction);
            case "isChallenging" -> chooseChallengeOrDecline(decisionAction);
            case "isStacking" -> chooseStackPlus2(decisionAction);
        }
    }

    /**
     * Obtém uma lista de cartas coloridas na mão do AIJogador. Se não houver nenhum,
     * ou
     * aleatoriamente
     * possibilidade de a cor ser escolhida aleatoriamente. Caso contrário, a
     * primeira carta da lista será
     * selecionado
     * conforme a cor a ser aplicada.
     *
     * @param DecisionAction Referência à ação atual que requer uma decisão.
     */
    private void chooseWildColour(TurnActionFactory.TurnDecisionAction decisionAction) {
        List<Carta> colouredHandCards = getHand().stream().filter(card -> card.getColourID() != 4)
                .collect(Collectors.toList());

        // Sem cartas, ou apenas curingas, ou chance rara de 10%: escolha a cor
        // aleatoriamente
        if (colouredHandCards.isEmpty() || Math.random() * 100 > 90) {
            decisionAction.injectProperty("colourID", (int) (Math.random() * 4));
        } else { // Use o primeiro cartão colorido
            decisionAction.injectProperty("colourID", colouredHandCards.get(0).getColourID());
        }
        decisionAction.injectFlagProperty(1);
    }

    /**
     * Sempre opta por jogar cartas que foram compradas.
     *
     * @param DecisionAction Referência à ação atual que requer uma decisão.
     */
    private void chooseKeepOrPlay(TurnActionFactory.TurnDecisionAction decisionAction) {
        checkCallUNO();
        decisionAction.injectFlagProperty(1);
    }

    /**
     * Encontra a mão com o menor número de cartas além das suas e
     * swaps indica uma preferência de troca com esse alvo.
     *
     * @param DecisionAction Referência à ação atual que requer uma decisão.
     */
    private void choosePlayerToSwapWith(TurnActionFactory.TurnDecisionAction decisionAction) {
        Jogador chosenPlayer = this;
        int cardCount = 9999;
        for (Jogador player : InterfaceJogo.getCurrentGame().getAllPlayers()) {
            if (player.getHand().size() < cardCount && player != this) {
                chosenPlayer = player;
                cardCount = chosenPlayer.getHand().size();
            }
        }
        decisionAction.injectFlagProperty(chosenPlayer.getPlayerID());
    }

    /**
     * Verifica se as cartas podem ser empilhadas e sempre encadeia se podem ser com
     * uma carta válida
     * cartão.
     * Caso contrário, decidirá aleatoriamente se desafiará ou recusará.
     *
     * @param DecisionAction Referência à ação atual que requer uma decisão.
     */
    private void chooseChallengeOrDecline(TurnActionFactory.TurnDecisionAction decisionAction) {
        // Sempre empilhe uma carta se ela for permitida e estiver disponível.
        if (InterfaceJogo.getCurrentGame().getRuleSet().canStackCards()) {
            Carta validCard = getHand().stream().filter(card -> card.getFaceValueID() == 13).findFirst().orElse(null);
            if (validCard != null) {
                checkCallUNO();
                decisionAction.injectProperty("faceValueID", validCard.getFaceValueID());
                decisionAction.injectProperty("colourID", validCard.getColourID());
                decisionAction.injectProperty("cardID", validCard.getCardID());
                decisionAction.injectProperty("isChaining", 1);
                decisionAction.injectFlagProperty(0);
                return;
            }
        }
        decisionAction.injectProperty("isChaining", 0);
        // Escolha aleatoriamente 50-50 entre desafiar ou recusar
        // Não é necessário verificar a regra de não blefar porque este método só é
        // chamado
        // se uma escolha válida estiver disponível
        // E a IA SEMPRE escolherá empilhar uma carta, o que significa que isso nunca
        // executará o
        // chance aleatória de desafio nesses casos.
        decisionAction.injectFlagProperty((int) (Math.random() * 2));
    }

    /**
     * Verifica se as cartas podem ser empilhadas e então joga um +2 válido se
     * estiver disponível
     * e permitido.
     * Caso contrário indica que não está sendo feito.
     *
     * @param DecisionAction Referência à ação atual que requer uma decisão.
     */
    private void chooseStackPlus2(TurnActionFactory.TurnDecisionAction decisionAction) {
        if (InterfaceJogo.getCurrentGame().getRuleSet().canStackCards()) {
            Carta validCard = getHand().stream().filter(card -> card.getFaceValueID() == 10).findFirst().orElse(null);
            if (validCard != null) {
                checkCallUNO();
                decisionAction.injectProperty("faceValueID", validCard.getFaceValueID());
                decisionAction.injectProperty("colourID", validCard.getColourID());
                decisionAction.injectProperty("cardID", validCard.getCardID());
                decisionAction.injectFlagProperty(1);
                return;
            }
        }
        decisionAction.injectFlagProperty(0);
    }

    /**
     * Avalia se deve ligar para UNO para tornar a IA segura.
     */
    private void checkCallUNO() {
        if (getHand().size() != 2)
            return;
        if (Math.random() * 100 < 70) {
            setUnoState(UNOState.Called);
            InterfaceJogo.getCurrentGame().showGeneralOverlay("UNOCalled" + getPlayerID());
        }
    }
}
