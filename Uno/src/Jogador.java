import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Uno
 *
 * Classe Jogador:
 * Define um jogador com todas as informações sobre um único jogador.
 *
 * @autor Cauet Damasceno
 * @versão 2023
 */
public class Jogador {
    /**
     * Os tipos de jogadores incluem:
     * UnoJogador: Apenas um é permitido, este é o jogador que está jogando este
     * jogo.
     * AIJogador: Controlado por uma IA (deveria estar usando uma classe AIJogador).
     */
    public enum PlayerType {
        UnoJogador, AIJogador
    }

    /**
     * Seguro indica que o jogador não está vulnerável a contra-ataques.
     * Chamado indica o jogador chamado antes de terminar seu turno com uma carta.
     * NotSafe indica que os jogadores podem contra-atacar o uno do jogador e
     * fazê-lo comprar cartas.
     */
    public enum UNOState {
        Safe, Called, NotSafe
    }

    /**
     * O ID exclusivo deste jogador.
     */
    private final int playerID;
    /**
     * O nome deste jogador.
     */
    private final String playerName;
    /**
     * O tipo de jogador. (UnoJogador, AIJogador ou NetworkPlayer).
     */
    private final PlayerType playerType;
    /**
     * A região para retirar as cartas do jogador.
     */
    private final Retangulo bounds;

    /**
     * A coleção de cartas contidas na mão do jogador.
     */
    private final List<Carta> hand;
    /**
     * A carta sobre a qual o jogador está passando o mouse.
     */
    private Carta hoveredCard;
    /**
     * Quando verdadeiro, as cartas deste jogador são reveladas com a face para
     * cima.
     */
    private boolean showCards;
    /**
     * A pontuação total entre múltiplas rodadas para este jogador.
     */
    private int totalScore;
    /**
     * A pontuação de uma única rodada para este jogador.
     */
    private int currentRoundScore;
    /**
     * Quando verdadeiro, este jogador venceu a rodada atual.
     * Necessário armazenar isso porque a pontuação pode ser 0, apenas para todos os
     * outros jogadores
     * tem 0s nas mãos.
     */
    private boolean wonRound;
    /**
     * Quando verdadeiro, o nome do jogador fica centralizado no lado esquerdo dos
     * limites,
     * caso contrário, será centralizado na parte superior.
     */
    private final boolean showPlayerNameLeft;
    /**
     * O UNOState atual que pode ser Seguro, Chamado ou NotSafe.
     */
    private UNOState unoState;

    /**
     * Inicializa o jogador com a mão vazia e o padrão é mostrar cartas se
     * o player é definido com o tipo UnoJogador.
     *
     * @param playerID           O ID exclusivo deste player.
     * @param playerName         O nome deste jogador.
     * @param playerType         O tipo de jogador. (UnoJogador, AIJogador ou
     *                           RedePlayer).
     * @param limites            A região para a compra das cartas do jogador.
     * @param showPlayerNameLeft Quando verdadeiro, o nome do jogador é centralizado
     *                           à esquerda
     *                           lado dos limites, caso contrário, será centralizado
     *                           no
     *                           principal.
     */
    public Jogador(int playerID, String playerName, PlayerType playerType, Retangulo bounds,
            boolean showPlayerNameLeft) {
        this.playerName = playerName;
        this.playerID = playerID;
        this.playerType = playerType;
        this.bounds = bounds;
        this.showPlayerNameLeft = showPlayerNameLeft;
        hand = new ArrayList<>();
        showCards = playerType == PlayerType.UnoJogador;
        wonRound = false;
        totalScore = currentRoundScore = 0;
        unoState = UNOState.Safe;
    }

    /**
     * Faz nada.
     *
     * @param deltaTime Tempo desde a última atualização.
     */
    public void update(int deltaTime) {

    }

    /**
     * Compra as cartas do jogador com verso ou frente. Depois desenha o
     * nome do jogador próximo.
     *
     * @param g Referência ao objeto Graphics para renderização.
     */
    public void paint(Graphics g) {
        if (showCards) {
            hand.forEach(card -> card.paint(g));
        } else {
            hand.forEach(card -> Carta.paintCardBack(g, card));
        }
        g.setFont(new Font("Arial", Font.BOLD, 20));
        int strWidth = g.getFontMetrics().stringWidth(playerName);
        g.setColor(new Color(1, 1, 1, 204));
        int nameXOffset = bounds.position.x
                + (showPlayerNameLeft ? -(strWidth - 50) : (bounds.width / 2 - (strWidth + 30) / 2));
        int nameYOffset = bounds.position.y + (showPlayerNameLeft ? (bounds.height / 2 - 20) : -10);
        g.fillRect(nameXOffset, nameYOffset, strWidth + 30, 40);
        g.setColor(InterfaceJogo.getCurrentGame().getCurrentPlayer().getPlayerID() == getPlayerID()
                ? Color.ORANGE
                : Color.WHITE);
        g.drawString(playerName, nameXOffset + 15, nameYOffset + 25);
    }

    /**
     * Adiciona a carta à mão e recalcula as posições onde todas as cartas
     * deve ser posicionado.
     *
     * @param card A carta a ser adicionada à mão.
     */
    public void addCardToHand(Carta card) {
        hand.add(card);
        recalculateCardPositions();
    }

    /**
     * Esvazia a mão.
     */
    public void emptyHand() {
        hand.clear();
    }

    /**
     * Altera a visibilidade das cartas do Jogador.
     *
     * @paramrevel True mostra a frente do cartão, false mostra o verso do cartão.
     */
    public void revealHand(boolean reveal) {
        showCards = reveal;
    }

    /**
     * Obtém o tipo de Player.
     *
     * @return O tipo de jogador.
     */
    public PlayerType getPlayerType() {
        return playerType;
    }

    /**
     * Obtém o ID exclusivo do jogador.
     *
     * @return O playerID exclusivo.
     */
    public int getPlayerID() {
        return playerID;
    }

    /**
     * Recebe um possível faceValue e colorValue que normalmente seria o
     * cores do topo da pilha. E verifica cada carta na mão para encontrar uma lista
     * de todas as cartas que podem ser jogadas e a devolve.
     *
     * @param curFaceValue   O faceValue a ser verificado.
     * @param curColourValue O colorID a ser verificado.
     * @return Uma lista de cartas válidas para serem jogadas neste contexto.
     */
    public List<Carta> getValidMoves(int curFaceValue, int curColourValue) {
        List<Carta> result = new ArrayList<>();
        for (Carta card : hand) {
            if (card.getFaceValueID() == curFaceValue || card.getColourID() == curColourValue
                    || card.getFaceValueID() == 13 || card.getFaceValueID() == 14) {
                result.add(card);
            }
        }
        return result;
    }

    /**
     * Classifica a mão e recalcula as posições de todas as cartas.
     * Os cartões são classificados primeiro por cor e depois por valores faciais.
     */
    public void sortHand() {
        Comparator<Carta> compareByCard = Comparator
                .comparing(Carta::getColourID)
                .thenComparing(Carta::getFaceValueID);
        hand.sort(compareByCard);
        recalculateCardPositions();
    }

    /**
     * Atualiza o foco para verificar qual cartão está pairando e, em seguida,
     * atualiza o
     * posições de todos os cartões a serem compensadas com base na passagem do
     * mouse.
     *
     * @param mousePosition Posição do cursor do mouse.
     */
    public void updateHover(Posicao mousePosition) {
        if (hoveredCard != null && !hoveredCard.isPositionInside(mousePosition)) {
            hoveredCard = null;
        }
        for (Carta card : hand) {
            if (card.isPositionInside(mousePosition)) {
                hoveredCard = card;
                break;
            }
        }
        recalculateCardPositions();
    }

    /**
     * Retira a carta da mão e recalcula a posição de todas as cartas.
     *
     * @param card Cartão a ser removido.
     */
    public void removeCard(Carta card) {
        hand.remove(card);
        recalculateCardPositions();
    }

    /**
     * Pesquisa para encontrar o cardID.
     *
     * @param cardID cardID a ser pesquisado.
     * @return O cartão com cardID ou nulo.
     */
    public Carta getCardByID(int cardID) {
        for (Carta card : hand) {
            if (card.getCardID() == cardID) {
                return card;
            }
        }
        return null;
    }

    /**
     * Atualiza a posição flutuante. Em seguida, retorna qualquer cartão atualmente
     * pairado.
     *
     * @param mousePosition Posição do mouse.
     * @return O cartão atualmente pairado (pode ser nulo se não houver).
     */
    public Carta chooseCardFromClick(Posicao mousePosition) {
        updateHover(mousePosition);
        return hoveredCard;
    }

    /**
     * Obtém todas as cartas da mão do jogador.
     *
     * @return A lista de cartas na mão deste jogador.
     */
    public List<Carta> getHand() {
        return hand;
    }

    /**
     * Obtém o nome do jogador.
     *
     * @return O nome do jogador.
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Soma a pontuação de todas as cartas da mão atual.
     *
     * @return Uma pontuação total para todas as cartas da mão.
     */
    public int getHandTotalScore() {
        int score = 0;
        for (Carta card : hand) {
            score += card.getScoreValue();
        }
        return score;
    }

    /**
     * Obtém o centro da região do jogador.
     *
     * @return Centro dos limites onde as cartas são sorteadas.
     */
    public Posicao getCentreOfBounds() {
        return bounds.getCentre();
    }

    /**
     * Recalcula as posições de todos os cartões calculando o número de
     * linhas e colunas então centralizando dentro da região e aplicando
     * posições para todas as cartas da mão.
     */
    private void recalculateCardPositions() {
        int paddingX = -15;
        int paddingY = (playerType == PlayerType.UnoJogador) ? 10 : -Carta.CARD_HEIGHT / 2 - 10;
        int elementsPerRow = (bounds.width + paddingX) / Carta.CARD_WIDTH;
        int rows = (int) Math.ceil(hand.size() / (double) elementsPerRow);
        int startY = bounds.position.y + bounds.height / 2 - rows * (Carta.CARD_HEIGHT + paddingY) / 2;
        int x = 0;
        int y = 0;
        int remainingElements = hand.size();
        int rowXOffset = bounds.width / 2 - (int) (elementsPerRow * (Carta.CARD_WIDTH + paddingX) / 2.0);

        // Verdadeiro quando há apenas uma linha não completa (usada para centralizar
        // nessa linha).
        if (remainingElements < elementsPerRow) {
            rowXOffset = bounds.width / 2 - (int) (remainingElements * (Carta.CARD_WIDTH + paddingX) / 2.0);
        }
        for (Carta card : hand) {
            // Aplica um deslocamento visual ao cartão pairado
            int hoverOffset = (card == hoveredCard) ? -10 : 0;
            card.position.setPosition(bounds.position.x + rowXOffset + x * (Carta.CARD_WIDTH + paddingX),
                    startY + y * (Carta.CARD_HEIGHT + paddingY) + hoverOffset);
            x++;
            remainingElements--;
            // Verifica a iteração para a próxima linha.
            if (x >= elementsPerRow) {
                x = 0;
                y++;
                rowXOffset = bounds.width / 2 - (int) (elementsPerRow * (Carta.CARD_WIDTH + paddingX) / 2.0);
                // Assim que uma linha não completa for encontrada (usado para centralizar essa
                // linha).
                if (remainingElements < elementsPerRow) {
                    rowXOffset = bounds.width / 2 - (int) (remainingElements * (Carta.CARD_WIDTH + paddingX) / 2.0);
                }
            }
        }
    }

    /**
     * Define o currentRoundScore e aumenta o totalScore nesse valor.
     *
     * @param newCurrentRoundScore Nova pontuação para este jogador.
     */
    public void setCurrentRoundScore(int newCurrentRoundScore) {
        this.currentRoundScore = newCurrentRoundScore;
        totalScore += currentRoundScore;
    }

    /**
     * Define o estado ganho como verdadeiro.
     */
    public void setWon() {
        wonRound = true;
    }

    /**
     * Isso retorna verdadeiro quando este jogador vence.
     *
     * @return O estado atual ganho.
     */
    public boolean getWon() {
        return wonRound;
    }

    /**
     * A pontuação total entre várias rodadas.
     *
     * @return A pontuação total atual deste jogador.
     */
    public int getTotalScore() {
        return totalScore;
    }

    /**
     * Obtém a pontuação da rodada atual deste jogador.
     *
     * @return A pontuação atual deste jogador na rodada atual.
     */
    public int getCurrentRoundScore() {
        return currentRoundScore;
    }

    /**
     * Redefine a pontuação para nada.
     */
    public void resetScore() {
        totalScore = 0;
        currentRoundScore = 0;
        wonRound = false;
        unoState = UNOState.Safe;
    }

    /**
     * Pode fazer a transição de Safe->Called (NotSafe->Called não deve ocorrer).
     * Pode fazer a transição de Safe->NotSafe, NotSafe->Safe e Called->Safe.
     * Ignorará a tentativa de transição de Called->NotSafe. Este comportamento
     * lida com o ignorar de uma transição para NotSafe quando o turno termina.
     *
     * @param unoState O novo estado a ser definido.
     */
    public void setUnoState(UNOState unoState) {
        if (this.unoState == UNOState.Called && unoState == UNOState.NotSafe) {
            return;
        }
        this.unoState = unoState;
    }

    /**
     * Verifica o unoState atual deste jogador para verificar se ele está protegido
     * contra
     * sendo chamado.
     *
     * @return Verdadeiro se o UNOState for Seguro ou Chamado, e falso se o UNOState
     *         for
     */
    public boolean isSafe() {
        return unoState != UNOState.NotSafe;
    }

    /**
     * Obtém o UNOState atual que pode ser Safe, Called ou NotSafe.
     *
     * @return O UNOState atual.
     */
    public UNOState getUnoState() {
        return unoState;
    }
}
