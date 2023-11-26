import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Uno
 *
 * Classe TurnActionFactory :
 * Esta classe é responsável por construir a sequência de ações que ocorrem
 * quando
 * as cartas são compradas ou jogadas para gerenciar as decisões do jogador como
 * uma máquina de estado dinâmica.
 *
 * @autor Cauet Damasceno
 * @versão 2023
 */
public class TurnActionFactory {

    /**
     * Uno
     *
     * Classe TurnAction :
     * Define um TurnAction que atua como uma lista vinculada de ações.
     * Executa uma ação que se espera que seja executada uma vez antes de iterar
     * para um próximo estado.
     *
     * @author Peter Mitchell
     * @version 2021.1
     */
    public static class TurnAction {
        /**
         * Armazena um mapa de dados usados para passar para as ações para manter dados
         * com estado
         * sobre a sequência de ação.
         */
        protected final Map<String, Integer> storedData;
        /**
         * A ação a ser executada via performAction().
         */
        protected final Consumer<Map<String, Integer>> action;
        /**
         * Referência ao próximo TurnAction na sequência da lista vinculada. Isso pode
         * ser null para indicar o fim.
         */
        protected final TurnAction next;
        /**
         * Texto a ser usado para descrever o estado atual na saída de depuração.
         */
        protected final String actionDebugText;

        /**
         * Armazena as propriedades especificadas prontas para uso.
         *
         * @param next            Referência ao próximo TurnAction na lista vinculada
         *                        seqüência. Isso pode ser nulo para indicar o fim.
         * @param storedData      Armazena um mapa de dados usados para passar para as
         *                        ações
         *                        para manter dados com estado sobre a sequência de
         *                        ações.
         * @param action          A ação a ser executada via performAction().
         * @param actionDebugText Texto a ser usado para descrever o estado atual na
         *                        depuração
         *                        saída.
         */
        public TurnAction(TurnAction next, Map<String, Integer> storedData, Consumer<Map<String, Integer>> action,
                String actionDebugText) {
            this.next = next;
            this.storedData = storedData;
            this.action = action;
            this.actionDebugText = actionDebugText;
        }

        /**
         * Chama a ação especificada se ela não for nula, passando armazenadosData para
         * ela.
         */
        public void performAction() {
            if (action != null) {
                action.accept(storedData);
            }
        }

        /**
         * Obtém o próximo elemento da lista vinculada.
         *
         * @return O próximo TurnAction ou null para indicar o fim.
         */
        public TurnAction getNext() {
            return next;
        }

        /**
         * Armazena os dados especificados no mapa de dados armazenados para serem
         * usados no futuro
         * iterações.
         *
         * @param key   Chave para armazenar no mapa storageData.
         * @param value Valor a ser associado à chave.
         */
        public void injectProperty(String key, Integer value) {
            storedData.put(key, value);
        }
    }

    /**
     * Uno
     *
     * Classe TurnDecisionAction :
     * Define um TurnDecisionAction que atua como uma lista vinculada de ações com
     * um
     * dividido em um
     * de duas opções diferentes com base no valor armazenado em uma variável
     * sinalizada.
     * Executa uma ação que se espera que seja executada uma vez antes de iterar
     * para
     * um próximo estado.
     *
     * @autor Cauet Damasceno
     * @versão 2023
     */
    public static class TurnDecisionAction extends TurnAction {
        /**
         * Quando verdadeiro, o TurnDecisionAction tem um limite de tempo para ser
         * concluído.
         */
        protected final boolean timeOut;
        /**
         * A TurnAction alternativa para a qual mover se a variável do sinalizador for
         * diferente de zero.
         */
        protected final TurnAction otherNext;
        /**
         * A variável sinalizadora usada para determinar quando a decisão foi cumprida.
         */
        protected final String flagName;
        /**
         * Um booleano para rastrear se a ação já foi executada.
         */
        protected boolean hasRunOnce;

        /**
         * Define um TurnDecisionAction que escolhe usar o next ou otherNext
         * TurnAction
         * com base no valor armazenado no valor mapeado de flagName armazenado em
         * storageData. 0
         * será acionado em seguida,
         * e 1 acionarão otherNext. getNext() continuará retornando este valor atual
         * objeto até
         * o flagName foi definido com um valor.
         *
         * @param next            Usado quando o valor de flagName é 0. Referência ao
         *                        próximo
         *                        TurnAction na sequência da lista vinculada. Isso pode
         *                        ser
         *                        null para indicar o fim.
         * @param otherNext       Usado quando o valor de flagName não é 0. Referência
         *                        ao
         *                        alternativa próxima TurnAction na lista vinculada
         *                        seqüência. Isso pode ser nulo para indicar o fim.
         * @param timeOut         Quando verdadeiro, TurnDecisionAction tem um limite de
         *                        tempo para
         *                        completá-lo.
         * @param flagName        A variável de sinalização usada para determinar quando
         *                        a decisão
         *                        foi atendido.
         * @param storedData      Armazena um mapa de dados usados para passar para as
         *                        ações
         *                        para manter dados com estado sobre a sequência de
         *                        ações.
         * @param action          A ação a ser executada via performAction().
         * @param actionDebugText Texto a ser usado para descrever o estado atual na
         *                        depuração
         *                        saída.
         */
        public TurnDecisionAction(TurnAction next, TurnAction otherNext, boolean timeOut, String flagName,
                Map<String, Integer> storedData, Consumer<Map<String, Integer>> action, String actionDebugText) {
            super(next, storedData, action, actionDebugText);
            this.otherNext = otherNext;
            this.timeOut = timeOut;
            this.flagName = flagName;
            hasRunOnce = false;
        }

        /**
         * Verifica se flagName foi definido em storageData. Se tiver sido definido o
         * o valor é avaliado de forma que 0 retorne em seguida ou qualquer outro valor
         * retorne
         * outroPróximo.
         * Quando ainda não tiver sido definido o método continuará retornando uma
         * referência
         * para a turma atual.
         *
         * @return O objeto atual ou o próximo TurnAction a ser usado.
         */
        @Override
        public TurnAction getNext() {
            if (storedData.containsKey(flagName)) {
                return (storedData.get(flagName) == 0) ? next : otherNext;
            }
            return this;
        }

        /**
         * Verifica se a ação já foi executada. Então executa
         * a ação se não for nula com base na definição em TurnAction.
         */
        @Override
        public void performAction() {
            if (hasRunOnce)
                return;
            hasRunOnce = true;
            super.performAction();
        }

        /**
         * Um método de atalho para armazenar um valor diretamente no flagName associado
         * com esta TurnDecisionAction.
         *
         * @param value O valor a ser definido em storageData usando flagName.
         */
        public void injectFlagProperty(Integer value) {
            injectProperty(flagName, value);
        }
    }

    /**
     * Filas colocando o cartão especificado seguido da sequência de ações que
     * resultado do
     * tipo de carta que foi jogada ao chamar este método.
     *
     * @param playerID    O jogador que controla a carta.
     * @param cardID      O ID exclusivo associado à carta a ser jogada.
     * @param faceValueID A referência ao que aparece na carta a ser jogada.
     * @param colorID     A cor da carta a ser jogada.
     * @return Uma sequência de ações baseadas na carta que está sendo jogada.
     */
    public static TurnAction playCardAsAction(int playerID, int cardID, int faceValueID, int colourID) {
        Map<String, Integer> storedData = new HashMap<>();
        storedData.put("playerID", playerID);
        storedData.put("cardID", cardID);
        storedData.put("faceValueID", faceValueID);
        storedData.put("colourID", colourID);
        TurnAction nextSequence = cardIDToTurnAction(faceValueID, storedData);
        return new TurnAction(nextSequence, storedData, TurnActionFactory::placeCard, "Place Card");
    }

    /**
     * Itera recursivamente sobre a árvore TurnAction fornecida e a gera para
     * fins de depuração para o console.
     *
     * @param headNode Nó para gerar recursivamente uma saída de árvore.
     */
    public static void debugOutputTurnActionTree(TurnAction headNode) {
        debugRecursiveNodeOutput(headNode, 0);
    }

    /**
     * Imprime a árvore do nó especificado, dividindo para baixo em qualquer
     * TurnDecisionAção.
     *
     * @param currentNode O nó a ser impresso neste nível da iteração.
     * @param indentLevel Indica até que ponto recuar o texto para a saída e para
     *                    numeração.
     */
    private static void debugRecursiveNodeOutput(TurnAction currentNode, int indentLevel) {
        if (currentNode == null)
            return;
        if (currentNode instanceof TurnDecisionAction) {
            TurnDecisionAction currentSplitNode = (TurnDecisionAction) currentNode;
            System.out.println("\t".repeat(indentLevel) + "? " + (indentLevel + 1) + ". " + currentSplitNode.flagName
                    + " Timeout: " + currentSplitNode.timeOut + " " + currentSplitNode.actionDebugText);
            debugRecursiveNodeOutput(currentSplitNode.next, indentLevel + 1);
            if (currentSplitNode.next != currentSplitNode.otherNext) {
                debugRecursiveNodeOutput(currentSplitNode.otherNext, indentLevel + 1);
            }
        } else {
            System.out
                    .println("\t".repeat(indentLevel) + "- " + (indentLevel + 1) + ". " + currentNode.actionDebugText);
            debugRecursiveNodeOutput(currentNode.next, indentLevel + 1);
        }
    }

    /**
     * Este método deve ser usado quando o jogador estiver usando sua ação de turno
     * para comprar
     * uma carta do baralho.
     * A árvore de decisão gerada por este método segue a sequência mostrada abaixo.
     * É construído ao contrário.
     * Comprar Carta -> carta Jogável? -> (verdadeiro) -> keepOrPlay? -> Manter ->
     * MoveToNextTurn
     * -> Jogar -> Começar o Cartão de Jogo de Ação
     * -> (falso) -> drawTillCanPlay? -> (verdadeiro) -> Começar a carta de compra
     * de ação
     * -> (falso) -> MoveToNextTurn
     *
     * @param playerID O jogador que está realizando a ação de desenho.
     * @return A sequência da árvore de decisão de TurnActions conforme descrito,
     *         pronta para
     *         iteração.
     */
    public static TurnAction drawCardAsAction(int playerID) {
        Map<String, Integer> storedData = new HashMap<>();
        storedData.put("playerID", playerID);
        TurnAction moveToNextTurn = new TurnAction(null, storedData, TurnActionFactory::moveNextTurn,
                "Mover para a próxima pessoa");
        TurnAction playCard = new TurnAction(null, storedData, TurnActionFactory::playCardAsActionFromData,
                "Jogue a carta retirada");
        TurnDecisionAction keepOrPlay = new TurnDecisionAction(moveToNextTurn, playCard, true,
                "keepOrPlay", storedData, TurnActionFactory::beginChoiceOverlay, "Escolha manter ou jogar");
        TurnDecisionAction isForcedPlay = new TurnDecisionAction(keepOrPlay, playCard, false,
                "isForcedPlay", storedData, TurnActionFactory::checkForcedPlayRule,
                "Verifique se o Jogo Forçado está habilitado e force o jogo se estiver.");
        TurnAction keepDrawing = new TurnAction(null, storedData, TurnActionFactory::drawCardAsActionFromData,
                "Compre outra carta (árvore recursiva)");
        TurnDecisionAction drawTillCanPlay = new TurnDecisionAction(moveToNextTurn, keepDrawing, false,
                "drawTillCanPlay?", storedData, TurnActionFactory::checkDrawTillCanPlayRule,
                "Verifique a regra do empate até poder jogar");
        TurnDecisionAction canPlayCard = new TurnDecisionAction(drawTillCanPlay, isForcedPlay, false,
                "cardPlayable", storedData, TurnActionFactory::isCardPlayable, "Verifique se a carta pode ser jogada");
        return new TurnAction(canPlayCard, storedData, TurnActionFactory::drawCard, "Compre uma carta");
    }

    /**
     * Requer que os dados armazenados contenham (playerID, cardID, faceValueID,
     * colorID)
     * Se drawCount foi definido, ele será transferido. Todas as outras propriedades
     * são
     * descartado.
     * A sequência TurnAction resultante não é retornada, ela é colocada na fila
     * diretamente
     * no jogo atual para iniciar uma nova sequência de jogo da carta.
     * Este método deve ser usado para sequenciar o jogo de uma carta como parte
     * de outras ações de efeitos de cartas.
     *
     * @param storageData Referência aos dados compartilhados para uma sequência de
     *                    ações.
     */
    private static void playCardAsActionFromData(Map<String, Integer> storedData) {
        TurnAction playCard = playCardAsAction(storedData.get("playerID"), storedData.get("cardID"),
                storedData.get("faceValueID"), storedData.get("colourID"));
        playCard.injectProperty("drawCount", storedData.get("drawCount"));
        InterfaceJogo.getCurrentGame().setCurrentTurnAction(playCard);
    }

    /**
     * Requer que os dados armazenados contenham um playerID. A sequência TurnAction
     * resultante
     * é
     * não retornado, ele é colocado na fila diretamente no jogo atual para iniciar
     * uma sequência
     * de
     * desenhando o cartão. Isso só deve ser usado para sequenciar sorteios
     * adicionais
     * quando drawTillCanPlay? é verdadeiro e desencadeia um empate recursivo via
     * drawCardAsAction().
     *
     * @param storageData Referência aos dados compartilhados para uma sequência de
     *                    ações.
     */
    private static void drawCardAsActionFromData(Map<String, Integer> storedData) {
        TurnAction drawCardSequence = drawCardAsAction(storedData.get("playerID"));
        InterfaceJogo.getCurrentGame().setCurrentTurnAction(drawCardSequence);
    }

    /**
     * Gera uma sequência de TurnActions para lidar com os eventos necessários
     * quando um +2
     * carta é jogada.
     * O seguinte mostra a sequência que pode ocorrer. É construído em
     * reverter.
     *
     * MoveToNextTurn -> Aumentar contagem de sorteios +2 ->
     * hasPlus2AndResponseAllowed?
     * -> (verdadeiro) -> isStacking? -> (verdadeiro) -> Iniciar Carta de Jogo de
     * Ação
     * -> (falso) -> Comprar Carta * Contagem de Compras + Redefinir Contagem de
     * Compras para 0 ->
     * MoveToNextTurn
     * -> (falso) -> Comprar Carta * Contagem de Compras + Redefinir Contagem de
     * Compras para 0 ->
     * MoveToNextTurn
     *
     * @param storageData Referência aos dados compartilhados para uma sequência de
     *                    ações.
     * @return A sequência da árvore de decisão de TurnActions conforme descrito,
     *         pronta para
     *         iteração.
     */
    private static TurnAction playPlus2Action(Map<String, Integer> storedData) {
        TurnAction moveToNextTurn = new TurnAction(null, storedData, TurnActionFactory::moveNextTurn,
                "Mover para a próxima curva");
        TurnAction dealPenalty = new TurnAction(moveToNextTurn, storedData, TurnActionFactory::drawNCards,
                "Desenhe N Cartas Numéricas");
        TurnAction playCard = new TurnAction(null, storedData, TurnActionFactory::playCardAsActionFromData,
                "Jogue outro +2 (recursivo)");
        TurnDecisionAction waitForPlay2OrCancel = new TurnDecisionAction(dealPenalty, playCard, true,
                "isStacking", storedData, TurnActionFactory::beginChoiceOverlay,
                "Verifique se há +2 ou opção Cancelar");
        TurnDecisionAction checkCanRespond = new TurnDecisionAction(dealPenalty, waitForPlay2OrCancel, false,
                "hasPlus2AndResponseAllowed", storedData, TurnActionFactory::hasPlus2AndResponseAllowed,
                "Pode empilhar e tem +2");
        TurnAction increaseDrawCount = new TurnAction(checkCanRespond, storedData,
                TurnActionFactory::increaseDrawCountBy2, "Aumentar N (Compre duas cartas) em 2");
        return new TurnAction(increaseDrawCount, storedData, TurnActionFactory::moveNextTurn,
                "Mover para a próxima curva");
    }

    /**
     * Gera uma sequência de TurnActions para lidar com os eventos necessários
     * quando um +4
     * carta é jogada.
     * O seguinte mostra a sequência que pode ocorrer. É construído em
     * reverter.
     *
     * couldPreviousPlayCard PreCheck -> WildColourSelection -> Definir cor da pilha
     * superior
     * -> MoveToNextTurn
     * -> é desafiador? -> (verdadeiro) -> poderiaPreviousPlayCard? -> (verdadeiro)
     * ->
     * MoveToPreviousTurn -> Compre 6 cartas -> MoveToNextPlayer -> Draw * Draw
     * Count +
     * reiniciar
     * -> (falso) -> Aumentar drawCount em 4 -> Draw * Draw Count + redefinir
     * contagem de sorteios
     * -> (falso) -> isChaining? -> (verdadeiro) -> Iniciar Carta de Jogo de Ação
     * -> (falso) -> Aumentar drawCount em 4 -> Draw * Draw Count + redefinir
     * contagem de sorteios
     *
     * @param storageData Referência aos dados compartilhados para uma sequência de
     *                    ações.
     * @return A sequência da árvore de decisão de TurnActions conforme descrito,
     *         pronta para
     *         iteração.
     */
    private static TurnAction playPlus4Action(Map<String, Integer> storedData) {
        TurnAction moveToNextSkipDamagedPlayer = new TurnAction(null, storedData, TurnActionFactory::moveNextTurn,
                "Mover para a próxima curva");
        TurnAction drawNCards = new TurnAction(moveToNextSkipDamagedPlayer, storedData, TurnActionFactory::drawNCards,
                "Compre N Cartas Numéricas");
        TurnAction increaseDrawBy4 = new TurnAction(drawNCards, storedData, TurnActionFactory::increaseDrawCountBy4,
                "Aumente N (drawCount) em 4");
        TurnAction playCardAsResponse = new TurnAction(null, storedData, TurnActionFactory::playCardAsActionFromData,
                "Compre +4 no anterior (recursivo)");
        TurnAction increaseDrawBy4ThenStack = new TurnAction(playCardAsResponse, storedData,
                TurnActionFactory::increaseDrawCountBy4, "Aumentar N (drawCount) em 4");
        TurnDecisionAction isChainingCard = new TurnDecisionAction(increaseDrawBy4, increaseDrawBy4ThenStack,
                false, "isChaining", storedData, null, "Nenhuma ação");
        TurnAction drawNCardsAndDoNothing = new TurnAction(null, storedData, TurnActionFactory::drawNCards,
                "Desenhe N Cartas Numéricas");
        TurnAction moveBackToNext = new TurnAction(drawNCardsAndDoNothing, storedData, TurnActionFactory::moveNextTurn,
                "Mover para a próxima curva");
        TurnAction applyPenalty = new TurnAction(moveBackToNext, storedData, TurnActionFactory::draw4ChallengeSuccess,
                "Aplicar penalidade (+4) ao jogador");
        TurnAction moveToPreviousPlayer = new TurnAction(applyPenalty, storedData, TurnActionFactory::movePrevious,
                "Mover para o jogador anterior");
        TurnAction increaseDrawBy2 = new TurnAction(increaseDrawBy4, storedData,
                TurnActionFactory::increaseDrawCountBy2, "Aumentar N (drawCount) em 2");
        TurnDecisionAction couldPreviousPlayCard = new TurnDecisionAction(increaseDrawBy2, moveToPreviousPlayer,
                false, "couldPreviousPlayCard", storedData, TurnActionFactory::showChallengeResult,
                "O jogador anterior poderia ter jogado uma carta? (Sem ação)");
        TurnDecisionAction isChallenging = new TurnDecisionAction(isChainingCard, couldPreviousPlayCard, true,
                "isChallenging", storedData, TurnActionFactory::beginChoiceOverlay,
                "Pergunte se o jogador quer desafiar, empilhar ou não fazer nada");
        TurnDecisionAction canChallengeOrStack = new TurnDecisionAction(increaseDrawBy4, isChallenging, false,
                "canChallenge", storedData, TurnActionFactory::checkNoBluffingRule,
                "Verifique se um Desafio é permitido ou se há uma carta para Empilhar");
        TurnAction moveToNextTurn = new TurnAction(canChallengeOrStack, storedData, TurnActionFactory::moveNextTurn,
                "Mover para a próxima curva");
        TurnAction setTopOfPileColour = new TurnAction(moveToNextTurn, storedData, TurnActionFactory::setTopPileColour,
                "Alterar a cor no topo da pilha");
        TurnDecisionAction chooseWildColour = new TurnDecisionAction(setTopOfPileColour, setTopOfPileColour,
                true, "wildColour", storedData, TurnActionFactory::beginChoiceOverlay,
                "Peça ao jogador uma escolha de cor");
        return new TurnAction(chooseWildColour, storedData, TurnActionFactory::checkCouldPlayCard,
                "Verifique se uma carta pode ter sido jogada");
    }

    /**
     * Gera uma sequência de TurnActions para lidar com os eventos necessários
     * quando um Wild
     * carta é jogada.
     * O seguinte mostra a sequência que pode ocorrer. É construído em
     * reverter.
     *
     * WildColourSelection -> Definir cor da pilha superior -> MoveToNextTurn
     *
     * @param storageData Referência aos dados compartilhados para uma sequência de
     *                    ações.
     * @return A sequência da árvore de decisão de TurnActions conforme descrito,
     *         pronta para
     *         iteração.
     */
    private static TurnAction playWildAction(Map<String, Integer> storedData) {
        TurnAction moveToNextTurn = new TurnAction(null, storedData, TurnActionFactory::moveNextTurn,
                "Mover para a próxima curva");
        TurnAction setTopOfPileColour = new TurnAction(moveToNextTurn, storedData, TurnActionFactory::setTopPileColour,
                "Alterar a cor no topo da pilha");
        return new TurnDecisionAction(setTopOfPileColour, setTopOfPileColour,
                true, "wildColour", storedData, TurnActionFactory::beginChoiceOverlay,
                "Peça ao jogador uma escolha de cor");
    }

    /**
     * Gera uma sequência de TurnActions para lidar com os eventos necessários
     * quando um Skip
     * carta é jogada.
     * O seguinte mostra a sequência que pode ocorrer. É construído em
     * reverter.
     *
     * MoveToNextTurn -> Mostrar Pular -> MoveToNextTurn
     *
     * @param storageData Referência aos dados compartilhados para uma sequência de
     *                    ações.
     * @return A sequência da árvore de decisão de TurnActions conforme descrito,
     *         pronta para
     *         iteração.
     */
    private static TurnAction playSkipAction(Map<String, Integer> storedData) {
        TurnAction moveToNextTurnAtEnd = new TurnAction(null, storedData, TurnActionFactory::moveNextTurn,
                "Mover para a próxima pessoa");
        TurnAction showSkip = new TurnAction(moveToNextTurnAtEnd, storedData, TurnActionFactory::showSkip,
                "Mostrar um ícone de pular no player");
        return new TurnAction(showSkip, storedData, TurnActionFactory::moveNextTurn, "Mover para a próxima pessoa");
    }

    /**
     * Gera uma sequência de TurnActions para lidar com os eventos necessários
     * quando um
     * A carta reversa é jogada.
     * O seguinte mostra a sequência que pode ocorrer. É construído em
     * reverter.
     *
     * Alternar ordem de direção da curva -> MoveToNextTurn
     *
     * @param storageData Referência aos dados compartilhados para uma sequência de
     *                    ações.
     * @return A sequência da árvore de decisão de TurnActions conforme descrito,
     *         pronta para
     *         iteração.
     */
    private static TurnAction playReverseAction(Map<String, Integer> storedData) {
        TurnAction moveToNextTurn = new TurnAction(null, storedData, TurnActionFactory::moveNextTurn,
                "Mover para a próxima pessoa");
        return new TurnAction(moveToNextTurn, storedData, TurnActionFactory::togglePlayDirection,
                "Alternar direção de jogo");
    }

    /**
     * Gera uma sequência de TurnActions para tratar os eventos necessários quando
     * um Swap
     * carta é jogada.
     * Isto é para um modo de jogo diferente com a seleção de um jogador para trocar
     * de mãos.
     * O seguinte mostra a sequência que pode ocorrer. É construído em
     * reverter.
     *
     * outro jogador? Seleção -> Trocar mãos (atual, selecionado) -> MoveToNextTurn
     *
     * @param storageData Referência aos dados compartilhados para uma sequência de
     *                    ações.
     * @return A sequência da árvore de decisão de TurnActions conforme descrito,
     *         pronta para
     *         iteração.
     */
    private static TurnAction playSwapAction(Map<String, Integer> storedData) {
        TurnAction moveToNextTurn = new TurnAction(null, storedData, TurnActionFactory::moveNextTurn,
                "Mover para a próxima pessoa");
        TurnAction swapHands = new TurnAction(moveToNextTurn, storedData, TurnActionFactory::swapHandWithOther,
                "Troque de mãos com o jogador selecionado");
        return new TurnDecisionAction(swapHands, swapHands, true,
                "otherPlayer", storedData, TurnActionFactory::beginChoiceOverlay, "Escolha outro jogador para trocar");
    }

    /**
     * Gera uma sequência de TurnActions para lidar com os eventos necessários
     * quando um Passe
     * Todas as cartas são jogadas.
     * Isto é para um modo de jogo diferente, com a mudança de todas as mãos com
     * base em
     * virar ordem.
     * O seguinte mostra a sequência que pode ocorrer. É construído em
     * reverter.
     *
     * Passe todos os cartões -> MoveToNextTurn
     *
     * @param storageData Referência aos dados compartilhados para uma sequência de
     *                    ações.
     * @return A sequência da árvore de decisão de TurnActions conforme descrito,
     *         pronta para
     *         iteração.
     */
    private static TurnAction playPassAllAction(Map<String, Integer> storedData) {
        TurnAction moveToNextTurn = new TurnAction(null, storedData, TurnActionFactory::moveNextTurn,
                "Mover para a próxima pessoa");
        return new TurnAction(moveToNextTurn, storedData, TurnActionFactory::passAllHands, "Passe todas as mãos");
    }

    /**
     * Procura uma ação relevante a ser aplicada com base no valor facial do cartão.
     * Se
     * não há correspondência
     * ação associada para gerar uma sequência TurnAction a partir de então o padrão
     * é
     * para passar para o próximo turno.
     *
     * @param faceValueID O valor nominal da carta que está sendo jogada.
     * @param storageData Referência aos dados armazenados compartilhados a serem
     *                    usados para passar
     *                    em toda a sequência TurnAction.
     * @return Uma sequência de TurnActions baseada no faceValue da carta sendo
     *         jogado.
     */
    private static TurnAction cardIDToTurnAction(int faceValueID, Map<String, Integer> storedData) {
        return switch (InterfaceJogo.getCurrentGame().getRuleSet().getActionForCard(faceValueID)) {
            case Plus2 -> playPlus2Action(storedData);
            case Plus4 -> playPlus4Action(storedData);
            case Wild -> playWildAction(storedData);
            case Skip -> playSkipAction(storedData);
            case Reverse -> playReverseAction(storedData);
            case Swap -> playSwapAction(storedData);
            case PassAll -> playPassAllAction(storedData);
            case Nothing ->
                new TurnAction(null, storedData, TurnActionFactory::moveNextTurn, "Mover para a próxima pessoa");
        };
    }

    /**
     * Compra uma carta do baralho, armazena o (cardID, faceValueID e colorID) em
     * dados armazenados,
     * e então adiciona a carta à mão do jogador atual.
     *
     * @param storageData Referência aos dados armazenados compartilhados a serem
     *                    usados para passar
     *                    em toda a sequência TurnAction.
     */
    private static void drawCard(Map<String, Integer> storedData) {
        // Draw card from deck
        Deck deck = InterfaceJogo.getCurrentGame().getDeck();
        Carta drawnCard = deck.drawCard();
        // store ID into storedData
        storedData.put("cardID", drawnCard.getCardID());
        storedData.put("faceValueID", drawnCard.getFaceValueID());
        storedData.put("colourID", drawnCard.getColourID());
        // Add card to hand
        InterfaceJogo.getCurrentGame().getCurrentPlayer().addCardToHand(drawnCard);
    }

    /**
     * Requer que um cardID esteja definido em storageData. Obtém o cartão
     * referenciado por cardID em
     * mão do currentPlayer,
     * então remove a carta da mão e adiciona a carta à pilha de
     * cartas jogadas recentemente.
     *
     * @param storageData Referência aos dados armazenados compartilhados a serem
     *                    usados para passar
     *                    em toda a sequência TurnAction.
     */
    private static void placeCard(Map<String, Integer> storedData) {
        // Pega a carta da mão
        Jogador currentPlayer = InterfaceJogo.getCurrentGame().getCurrentPlayer();
        Carta cardToPlace = currentPlayer.getCardByID(storedData.get("cardID"));
        // Retira a carta da mão
        currentPlayer.removeCard(cardToPlace);
        // Adiciona carta à pilha
        InterfaceJogo.getCurrentGame().placeCard(cardToPlace);
    }

    /**
     * Passa para o próximo turno movendo um jogador na direção atual do jogo.
     *
     * @param storageData Referência aos dados armazenados compartilhados a serem
     *                    usados para passar
     *                    em toda a sequência TurnAction.
     */
    private static void moveNextTurn(Map<String, Integer> storedData) {
        InterfaceJogo.getCurrentGame().moveToNextPlayer();
    }

    /**
     * Usa boostDrawCountByN para aumentar drawCount em 2.
     *
     * @param storageData Referência aos dados armazenados compartilhados a serem
     *                    usados para passar
     *                    em toda a sequência TurnAction.
     */
    private static void increaseDrawCountBy2(Map<String, Integer> storedData) {
        increaseDrawCountByN(2, storedData);
    }

    /**
     * Usa boostDrawCountByN para aumentar drawCount em 4.
     *
     * @param storageData Referência aos dados armazenados compartilhados a serem
     *                    usados para passar
     *                    em toda a sequência TurnAction.
     */
    private static void increaseDrawCountBy4(Map<String, Integer> storedData) {
        increaseDrawCountByN(4, storedData);
    }

    /**
     * Obtém o valor atual armazenado em drawCount em storageData se existir e
     * adiciona o valor a N
     * antes de armazenar o resultado novamente em drawCount.
     *
     * @param N           O número a ser adicionado ao drawCount.
     * @param storageData Referência aos dados armazenados compartilhados a serem
     *                    usados para passar
     *                    em toda a sequência TurnAction.
     */
    private static void increaseDrawCountByN(int N, Map<String, Integer> storedData) {
        int result = N;
        if (storedData.containsKey("drawCount") && storedData.get("drawCount") != null) {
            result += storedData.get("drawCount");
        }
        storedData.put("drawCount", result);
    }

    /**
     * Requer que drawCount esteja definido em storageData. O valor é obtido e um
     * loop é
     * realizado drawCount
     * número de vezes para ligar para drawCard. Depois que todas as cartas forem
     * sorteadas, o
     * drawCount foi removido
     * de storageData para limpar, pronto para qualquer uso futuro.
     *
     * @param storageData Referência aos dados armazenados compartilhados a serem
     *                    usados para passar
     *                    em toda a sequência TurnAction.
     */
    private static void drawNCards(Map<String, Integer> storedData) {
        if (storedData.containsKey("drawCount") && storedData.get("drawCount") != null
                && storedData.get("drawCount") > 0) {
            int count = storedData.get("drawCount");
            for (int i = 0; i < count; i++) {
                drawCard(storedData);
            }
            InterfaceJogo.getCurrentGame().showGeneralOverlay(
                    "DrawN" + InterfaceJogo.getCurrentGame().getCurrentPlayer().getPlayerID()
                            + ";" + count);
            storedData.remove("drawCount");
        }
    }

    /**
     * Requer que os dados armazenados contenham faceValueID e colorID.
     * Obtém a carta do topo da pilha e verifica se a carta armazenada em
     * storageData é
     * jogável.
     * A carta é considerada jogável se corresponder ao faceValueID de
     * a carta do topo,
     * a cor da carta do topo, ou a carta é curinga ou +4.
     * O resultado é armazenado em cardPlayable em storageData como 1 se for
     * jogável ou 0 se não for.
     *
     * @param storageData Referência aos dados armazenados compartilhados a serem
     *                    usados para passar
     *                    em toda a sequência TurnAction.
     */
    private static void isCardPlayable(Map<String, Integer> storedData) {
        Carta latestCard = InterfaceJogo.getCurrentGame().getTopCard();
        boolean isPlayable = storedData.get("faceValueID") == latestCard.getFaceValueID()
                || storedData.get("colourID") == latestCard.getColourID()
                || storedData.get("faceValueID") >= 13;
        storedData.put("cardPlayable", isPlayable ? 1 : 0);
    }

    /**
     * Usado para exibir automaticamente uma sobreposição de escolha contextual com
     * base no
     * TurnDecisionAction atual.
     * Chamar este método pressupõe que o TurnAction atual é um
     * TurnDecisionAction e vontade
     * inicialize quaisquer elementos da interface para aguardar uma entrada
     * necessária.
     *
     * @param storageData Referência aos dados armazenados compartilhados a serem
     *                    usados para passar
     *                    em toda a sequência TurnAction.
     */
    private static void beginChoiceOverlay(Map<String, Integer> storedData) {
        InterfaceJogo.getCurrentGame().showOverlayForTurnAction();
    }

    /**
     * Verifica o RuleSet para verificar se as cartas devem continuar a ser
     * sorteadas até um
     * é jogável.
     * O resultado é armazenado em drawTillCanPlay? como 1 se as cartas continuarem
     * a
     * ser sorteado, ou
     * 0 se as cartas não devem ser compradas até que algo possa ser jogado.
     *
     * @param storageData Referência aos dados armazenados compartilhados a serem
     *                    usados para passar
     *                    em toda a sequência TurnAction.
     */
    private static void checkDrawTillCanPlayRule(Map<String, Integer> storedData) {
        storedData.put("drawTillCanPlay?",
                InterfaceJogo.getCurrentGame().getRuleSet().shouldDrawnTillCanPlay() ? 1 : 0);
    }

    /**
     * Verifica o conjunto de regras para verificar se as cartas podem ser
     * empilhadas. Se eles puderem ser empilhados,
     * e o jogador atual
     * tem qualquer carta +2 na mão. O resultado é armazenado em
     * hasPlus2AndResponseAllowed em storageData.
     * Se uma resposta for permitida nesta situação, um 1 será armazenado, caso
     * contrário, um 0.
     *
     * @param storageData Referência aos dados armazenados compartilhados a serem
     *                    usados para passar
     *                    em toda a sequência TurnAction.
     */
    private static void hasPlus2AndResponseAllowed(Map<String, Integer> storedData) {
        if (InterfaceJogo.getCurrentGame().getRuleSet().canStackCards() &&
                InterfaceJogo.getCurrentGame().getCurrentPlayer().getHand().stream()
                        .anyMatch(card -> card.getFaceValueID() == 10)) {
            storedData.put("hasPlus2AndResponseAllowed", 1);
        } else {
            storedData.put("hasPlus2AndResponseAllowed", 0);
        }
    }

    /**
     * Aciona uma sobreposição SkipVisual sobre o player atual.
     *
     * @param storageData Referência aos dados armazenados compartilhados a serem
     *                    usados para passar
     *                    em toda a sequência TurnAction.
     */
    private static void showSkip(Map<String, Integer> storedData) {
        InterfaceJogo.getCurrentGame().showGeneralOverlay("SkipVisual"
                + InterfaceJogo.getCurrentGame().getCurrentPlayer().getPlayerID());
    }

    /**
     * Alterna a direção de giro entre sentido horário para anti-horário e
     * vice-versa
     * vice-versa.
     *
     * @param storageData Referência aos dados armazenados compartilhados a serem
     *                    usados para passar
     *                    em toda a sequência TurnAction.
     */
    private static void togglePlayDirection(Map<String, Integer> storedData) {
        InterfaceJogo.getCurrentGame().toggleTurnDirection();
    }

    /**
     * Requer que o colorID esteja definido em storageData. O colorID é usado para
     * definir o topo
     * cor do cartão.
     * Este método pressupõe que a ação está sendo aplicada como parte de um Wild
     * escolha de cor (não obrigatória).
     *
     * @param storageData Referência aos dados armazenados compartilhados a serem
     *                    usados para passar
     *                    em toda a sequência TurnAction.
     */
    private static void setTopPileColour(Map<String, Integer> storedData) {
        InterfaceJogo.getCurrentGame().setTopCardColour(storedData.get("colourID"));
    }

    /**
     * Obtém a carta jogada antes da carta atual do topo e verifica se houve
     * quaisquer movimentos válidos no
     * tempo que poderia ter sido jogado como uma carta colorida. Se houvesse
     * couldPreviousPlayCard está definido como 1.
     * Caso contrário, couldPreviousPlayCard será definido como 0.
     *
     * @param storageData Referência aos dados armazenados compartilhados a serem
     *                    usados para passar
     *                    em toda a sequência TurnAction.
     */
    private static void checkCouldPlayCard(Map<String, Integer> storedData) {
        List<Carta> recentCards = InterfaceJogo.getCurrentGame().getRecentCards();
        Carta cardBeforeLast = recentCards.get(recentCards.size() - 2);
        List<Carta> validMoves = InterfaceJogo.getCurrentGame().getCurrentPlayer().getValidMoves(
                cardBeforeLast.getFaceValueID(), cardBeforeLast.getColourID());
        for (Carta card : validMoves) {
            if (card.getFaceValueID() < 13) {
                storedData.put("couldPreviousPlayCard", 1);
                return;
            }
        }
        storedData.put("couldPreviousPlayCard", 0);
    }

    /**
     * Compra 4 cartas para o jogador atual. Use para aplicar a penalidade quando um
     * +4
     * desafio foi bem-sucedido.
     *
     * @param storageData Referência aos dados armazenados compartilhados a serem
     *                    usados para passar
     *                    em toda a sequência TurnAction.
     */
    private static void draw4ChallengeSuccess(Map<String, Integer> storedData) {
        for (int i = 0; i < 4; i++) {
            drawCard(storedData);
        }
        InterfaceJogo.getCurrentGame().showGeneralOverlay(
                "DrawN" + InterfaceJogo.getCurrentGame().getCurrentPlayer().getPlayerID() + ";4");
    }

    /**
     * Move para o jogador anterior. Isto é conseguido invertendo o jogo
     * direção,
     * em seguida, passando para o próximo jogador e, em seguida, movendo a direção
     * para trás.
     *
     * @param storageData Referência aos dados armazenados compartilhados a serem
     *                    usados para passar
     *                    em toda a sequência TurnAction.
     */
    private static void movePrevious(Map<String, Integer> storedData) {
        togglePlayDirection(storedData);
        moveNextTurn(storedData);
        togglePlayDirection(storedData);
    }

    /**
     * Requer que otherPlayer esteja definido em storageData. Pega as cartas das
     * mãos de
     * outro jogador,
     * e o jogador atual. Remove as cartas de ambos os jogadores e depois adiciona
     * todas as cartas
     * para a mão do jogador oposto para completar a troca.
     *
     * @param storageData Referência aos dados armazenados compartilhados a serem
     *                    usados para passar
     *                    em toda a sequência TurnAction.
     */
    private static void swapHandWithOther(Map<String, Integer> storedData) {
        int targetPlayerID = storedData.get("otherPlayer");
        Jogador targetPlayer = InterfaceJogo.getCurrentGame().getPlayerByID(targetPlayerID);
        Object[] targetPlayerHand = targetPlayer.getHand().toArray();
        targetPlayer.emptyHand();
        Jogador currentPlayer = InterfaceJogo.getCurrentGame().getCurrentPlayer();
        Object[] currentPlayerHand = currentPlayer.getHand().toArray();
        currentPlayer.emptyHand();
        for (Object card : targetPlayerHand) {
            currentPlayer.addCardToHand((Carta) card);
        }
        for (Object card : currentPlayerHand) {
            targetPlayer.addCardToHand((Carta) card);
        }
    }

    /**
     * Esvazia as mãos de todos os jogadores em uma série de mãos. Então muda o
     * mãos baseadas na direção do jogo.
     * As mãos são então armazenadas de volta nos jogadores em relação à ordem
     * movida.
     *
     * @param storageData Referência aos dados armazenados compartilhados a serem
     *                    usados para passar
     *                    em toda a sequência TurnAction.
     */
    private static void passAllHands(Map<String, Integer> storedData) {
        List<Object[]> hands = new ArrayList<>();
        List<Jogador> players = InterfaceJogo.getCurrentGame().getAllPlayers();
        for (Jogador player : players) {
            hands.add(player.getHand().toArray());
            player.emptyHand();
        }

        // Shuffle the hands
        if (InterfaceJogo.getCurrentGame().isIncreasing()) {
            Object[] movedHand = hands.get(0);
            hands.remove(0);
            hands.add(movedHand);
        } else {
            Object[] movedHand = hands.get(hands.size() - 1);
            hands.remove(hands.size() - 1);
            hands.add(0, movedHand);
        }

        // put all the cards into the hands again
        for (int playerID = 0; playerID < players.size(); playerID++) {
            for (Object card : hands.get(playerID)) {
                players.get(playerID).addCardToHand((Carta) card);
            }
        }
    }

    /**
     * Mostra uma marca ou uma sobreposição cruzada no jogador que desafiou.
     *
     * @param storageData Referência aos dados armazenados compartilhados a serem
     *                    usados para passar
     *                    em toda a sequência TurnAction.
     */
    private static void showChallengeResult(Map<String, Integer> storedData) {
        if (storedData.get("couldPreviousPlayCard") == 0) {
            InterfaceJogo.getCurrentGame().showGeneralOverlay(
                    "ChallengeFailed" + InterfaceJogo.getCurrentGame().getCurrentPlayer().getPlayerID());
        } else {
            InterfaceJogo.getCurrentGame().showGeneralOverlay(
                    "ChallengeSuccess" + InterfaceJogo.getCurrentGame().getCurrentPlayer().getPlayerID());
        }
    }

    /**
     * Verifica as condições para saber se um desafio é permitido ou se também há
     * uma opção de pilha permitida +4 também.
     *
     * @param storageData Referência aos dados armazenados compartilhados a serem
     *                    usados para passar
     *                    em toda a sequência TurnAction.
     */
    private static void checkNoBluffingRule(Map<String, Integer> storedData) {
        boolean canStack = InterfaceJogo.getCurrentGame().getRuleSet().canStackCards();
        boolean hasAPlus4 = InterfaceJogo.getCurrentGame().getCurrentPlayer().getHand().stream()
                .anyMatch(card -> card.getFaceValueID() == 13);
        boolean canBluff = !InterfaceJogo.getCurrentGame().getRuleSet().getNoBluffingRule();

        boolean canChallenge = canBluff || (canStack && hasAPlus4);

        storedData.put("canChallenge", canChallenge ? 1 : 0);
    }

    /**
     * Verifica a regra de jogo forçado.
     *
     * @param storageData Referência aos dados armazenados compartilhados a serem
     *                    usados para passar
     *                    em toda a sequência TurnAction.
     */
    private static void checkForcedPlayRule(Map<String, Integer> storedData) {
        storedData.put("isForcedPlay", InterfaceJogo.getCurrentGame().getRuleSet().getForcedPlayRule() ? 1 : 0);
    }
}
