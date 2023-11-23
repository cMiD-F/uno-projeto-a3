/**
 * Uno
 *
 * Classe Conjunto de Regras:
 * Refina a classe que armazena as regras ativas usadas para determinar como os
 * cartões são mapeados para ações e quais ações são permitidas.
 *
 * @autor Cauet Damasceno
 * @versão 2023
 */
public class ConjuntoRegras {
    /**
     * Define os diferentes CardActions exclusivos que podem ser mapeados para
     * faceValueIDs.
     */
    public enum CardAction {
        Nothing, Plus2, Plus4, Wild, Skip, Reverse, Swap, PassAll
    }

    /**
     * Limites de pontuação que determinam quanto tempo o jogo durará.
     */
    public enum ScoreLimitType {
        OneRound, Score200, Score300, Score500, Unlimited
    }

    /**
     * 0 a 14 mapeados com CardActions para representar cada uma das diferentes
     * cartas Uno.
     */
    private final CardAction[] faceValueToActionMap;
    /**
     * Verdadeiro se cartas +2 e +4 puderem ser empilhadas em resposta.
     */
    private boolean canStackCards;
    /**
     * Verdadeiro se, ao comprar do baralho para o turn, as cartas devem continuar a
     * ser compradas
     * até que uma carta jogável seja encontrada.
     */
    private boolean drawnTillCanPlay;
    /**
     * O tempo que o jogador tem que fazer sua escolha durante as ações.
     */
    private final int defaultTimeOut;
    /**
     * Usado para determinar se Reverse se torna um salto quando verdadeiro.
     */
    private boolean onlyTwoPlayers;
    /**
     * Quando verdadeiro, 7 se torna uma ação de troca e 0 se torna uma ação de
     * aprovação total.
     */
    private boolean sevenZeroRule;
    /**
     * Quando nenhum blefe é verdadeiro, o +4 não pode ser desafiado.
     */
    private boolean noBluffingRule;
    /**
     * Quando verdadeiro, os jogadores podem entrar fora da ordem do turno com
     * cartas mostrando a mesma
     * valor nominal.
     */
    private boolean allowJumpInRule;
    /**
     * Quando verdadeiro, não há escolha de manter ou jogar, é um jogo forçado.
     */
    private boolean forcedPlayRule;
    /**
     * Armazena o tipo de limite de pontuação a ser usado para gerenciar o final do
     * jogo.
     */
    private ScoreLimitType scoreLimitType;

    /**
     * Inicializa um RuleSet padrão.
     */
    public ConjuntoRegras() {
        faceValueToActionMap = new CardAction[15];
        for (int i = 0; i <= 9; i++) {
            faceValueToActionMap[i] = CardAction.Nothing;
        }
        faceValueToActionMap[10] = CardAction.Plus2;
        faceValueToActionMap[11] = CardAction.Skip;
        faceValueToActionMap[12] = CardAction.Reverse;
        faceValueToActionMap[13] = CardAction.Plus4;
        faceValueToActionMap[14] = CardAction.Wild;
        defaultTimeOut = 25;
        setToDefaults();
    }

    public void setToDefaults() {
        setCanStackCards(true);
        setDrawnTillCanPlay(true);
        setTwoPlayers(false);
        setSevenZeroRule(false);
        setForcedPlayRule(false);
        setAllowJumpInRule(false);
        setNoBuffingRule(false);
        setScoreLimitType(ScoreLimitType.OneRound);
    }

    /**
     * Procura o CardAction que deve ser ativado em relação a um jogo jogado
     * cartão.
     *
     * @param faceValueID O faceValue a ser procurado no mapa de ação.
     * @return O CardAction mapeado associado ao faceValueID especificado.
     */
    public CardAction getActionForCard(int faceValueID) {
        return faceValueToActionMap[faceValueID];
    }

    /**
     * Verifica se as cartas +2 e +4 podem ser jogadas em resposta a outras cartas
     * +2 e +4
     * cartões.
     *
     * @return Verdadeiro se cartas +2 e +4 puderem ser empilhadas em resposta.
     */
    public boolean canStackCards() {
        return canStackCards;
    }

    /**
     * Altera o estado do empilhamento das cartas.
     *
     * @param canStackCards Quando cartas verdadeiras +2 e +4 podem ser empilhadas
     *                      em resposta.
     */
    public void setCanStackCards(boolean canStackCards) {
        this.canStackCards = canStackCards;
    }

    /**
     * Verifica se as cartas devem ser compradas até que uma possa ser jogada.
     *
     * @return Verdadeiro se, ao comprar cartas do baralho, as cartas do turn devem
     *         continuar a ser
     *         sorteado até que uma carta jogável seja encontrada.
     */
    public boolean shouldDrawnTillCanPlay() {
        return drawnTillCanPlay;
    }

    /**
     * Altera o estado de compra para que uma carta possa ser jogada.
     *
     * @param drawTillCanPlay Quando cartas verdadeiras devem ser compradas até que
     *                        uma possa ser
     *                        jogado.
     */
    public void setDrawnTillCanPlay(boolean drawnTillCanPlay) {
        this.drawnTillCanPlay = drawnTillCanPlay;
    }

    /**
     * Obtém o tempo em segundos que pode ser gasto no máximo para qualquer ação
     * individual.
     *
     * @return O tempo que o jogador tem para fazer sua escolha durante as ações.
     */
    public int getDefaultTimeOut() {
        return defaultTimeOut;
    }

    /**
     * Define se há apenas dois jogadores.
     *
     * @param onlyTwoPlayers Quando verdadeiro, Reverse se torna um salto.
     */
    public void setTwoPlayers(boolean onlyTwoPlayers) {
        this.onlyTwoPlayers = onlyTwoPlayers;
        faceValueToActionMap[12] = onlyTwoPlayers ? CardAction.Skip : CardAction.Reverse;
    }

    /**
     * Obtém o estado atual dos dois jogadores.
     *
     * @return True se as únicas regras para dois jogadores estiverem ativas com
     *         Reverse definido como
     *         pular.
     */
    public boolean getOnlyTwoPlayers() {
        return onlyTwoPlayers;
    }

    /**
     * Altera o estado de ativação da regra sete-zero.
     *
     * @param sevenZeroRule Quando verdadeiro 7 é uma ação de troca e 0 é uma ação
     *                      de aprovação total.
     */
    public void setSevenZeroRule(boolean sevenZeroRule) {
        this.sevenZeroRule = sevenZeroRule;
        faceValueToActionMap[0] = sevenZeroRule ? CardAction.Swap : CardAction.Nothing;
        faceValueToActionMap[7] = sevenZeroRule ? CardAction.PassAll : CardAction.Nothing;
    }

    /**
     * Verifica se a regra Sete-Zero está ativa.
     *
     * @return Quando verdadeiro, 7 é uma ação de troca e 0 é uma ação de aprovação
     *         total.
     */
    public boolean getSevenZeroRule() {
        return sevenZeroRule;
    }

    /**
     * Define a regra Sem Buffing.
     *
     * @param noBluffingRule Quando nenhum blefe é verdadeiro, o +4 não pode ser
     *                       desafiado.
     */
    public void setNoBuffingRule(boolean noBluffingRule) {
        this.noBluffingRule = noBluffingRule;
    }

    /**
     * Obtém o estado atual da regra de não blefar.
     *
     * @return Quando nenhum blefe é verdadeiro, o +4 não pode ser desafiado.
     */
    public boolean getNoBluffingRule() {
        return noBluffingRule;
    }

    /**
     * Sets the current state of the allowing jump in rule.
     *
     * @param allowJumpInRule When true players can jump in with cards of the same
     *                        face value.
     */
    public void setAllowJumpInRule(boolean allowJumpInRule) {
        this.allowJumpInRule = allowJumpInRule;
    }

    /**
     * Gets the current state of the jump in rule.
     *
     * @return When true players can jump in with cards of the same face value.
     */
    public boolean allowJumpInRule() {
        return allowJumpInRule;
    }

    /**
     * Sets the current state of the forced play rule.
     *
     * @param forcedPlayRule When forced play is true, there should be no keep or
     *                       play choice.
     */
    public void setForcedPlayRule(boolean forcedPlayRule) {
        this.forcedPlayRule = forcedPlayRule;
    }

    /**
     * Gets the current state of the forced play rule.
     *
     * @return When forced play is true, there should be no keep or play choice.
     */
    public boolean getForcedPlayRule() {
        return forcedPlayRule;
    }

    /**
     * Sets the score limit to wind the rounds.
     *
     * @param scoreLimitType The new score limit to apply.
     */
    public void setScoreLimitType(ScoreLimitType scoreLimitType) {
        this.scoreLimitType = scoreLimitType;
    }

    /**
     * Gets the current score limit setting.
     *
     * @return The current score limit to win all the rounds.
     */
    public ScoreLimitType getScoreLimitType() {
        return scoreLimitType;
    }
}
