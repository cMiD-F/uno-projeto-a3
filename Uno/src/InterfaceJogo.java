import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Uno
 *
 * Classe InterfaceJogo:
 * Define a visualização principal do jogo controlando uma lista de jogadores e
 * gerenciar o estado de todos os elementos do jogo.
 *
 * @autor Cauet Damasceno
 * @versão 2023
 */
public class InterfaceJogo extends WndInterface {

    /**
     * O baralho de cartas pronto para receber cartas.
     */
    private final Deck deck;
    /**
     * Um histórico de cartas que foram jogadas.
     */
    private final List<Carta> recentCards;
    /**
     * O centro de onde colocar os cartões recentes.
     */
    private final Posicao centredCardPos;
    /**
     * Referência ao jogador que está jogando.
     */
    private Jogador bottomPlayer;
    /**
     * Um gerente controlando as diversas sobreposições mostradas com base nos
     * eventos
     * durante o jogo.
     */
    private final OverlayGerenciador overlayManager;
    /**
     * TurnActions são acionadas por meio de cartas de baralho ou compra de cartas.
     * Isto funciona como um
     * lista vinculada que executa ações em cada etapa, incluindo divisão entre
     * escolhas
     * para objetos TurnDecisionAction.
     */
    private TurnActionFactory.TurnAction currentTurnAction;
    /**
     * Uma ação na fila para começar. Isso permite que a ação atual termine e
     * então, depois de feito, isso é iniciado.
     */
    private TurnActionFactory.TurnAction queuedTurnAction;

    /**
     * Todos os jogadores que estão jogando atualmente, incluindo suas mãos e outros
     * detalhes.
     */
    private final List<Jogador> players;
    /**
     * O jogador atual que está no controle das ações.
     */
    private int currentPlayerID;
    /**
     * Animação para mostrar a direção da ordem de curva.
     */
    private final AnimacaoDirecao playDirectionAnimation;
    /**
     * Aumentar a ordem de rotação (verdadeiro) significa sentido horário, ou falso
     * seria
     * sentido anti-horário.
     */
    private boolean isIncreasing;
    /**
     * As regras para quais ações das cartas são definidas e outras mudanças
     * específicas em como as ações
     * o jogo é jogado.
     */
    private final ConjuntoRegras ruleSet;
    /**
     * Referência à instância atual desta classe para que outras classes possam
     * acesse-o rapidamente e diretamente.
     */
    private static InterfaceJogo activeSingleton;
    /**
     * Referência ao painelJogo para quando o jogo termina.
     */
    private final PainelJogo painelJogo;
    /**
     * Quando painelJogo.DEBUG_MODE e this são verdadeiros, a saída é mostrada para
     * cada
     * transição na sequência TaskAction.
     */
    private boolean debugShowTaskActionNotes;
    /**
     * Quando painelJogo.DEBUG_MODE e this forem verdadeiros, a saída é mostrada com
     * o inteiro
     * sequência de TaskAction em árvore quando setCurrentTurnAction é usado.
     */
    private boolean debugShowTreeOnNewAction;

    /**
     * Obtém a única instância atual de CurrentGameInterface. Isso não é
     * aplicado, mas
     * pode ser nulo se ainda não foi criado, mas espera-se que apenas um seja
     * corra de cada vez.
     *
     * @return Referência à instância atual desta classe.
     */
    public static InterfaceJogo getCurrentGame() {
        return activeSingleton;
    }

    /**
     * Inicialize a interface com limites e habilite-a. Use esta versão
     * quando vindo do Lobby para
     * um novo conjunto de rodadas.
     *
     * @param limites      Os limites da interface.
     * @param RuleSet      A definição das regras de como o jogo será jogado.
     * @param lobbyPlayers Jogadores para criar no jogo.
     */
    public InterfaceJogo(Retangulo bounds, ConjuntoRegras ruleSet, List<LobbyPlayer> lobbyPlayers,
            PainelJogo painelJogo) {
        this(bounds, createPlayersFromLobby(lobbyPlayers, bounds), ruleSet, painelJogo);
    }

    /**
     * Inicialize a interface com limites e habilite-a. Use esta versão
     * quando vindo de
     * depois de um jogo já ter sido concluído e a sequência de jogos ser
     * continuando.
     *
     * @param limites    Os limites da interface.
     * @param playerList Jogadores para criar no jogo.
     * @param RuleSet    A definição das regras de como o jogo será jogado.
     */
    public InterfaceJogo(Retangulo bounds, List<Jogador> playerList, ConjuntoRegras ruleSet, PainelJogo painelJogo) {
        super(bounds);
        activeSingleton = this;
        this.ruleSet = ruleSet;
        this.painelJogo = painelJogo;
        recentCards = new ArrayList<>();
        centredCardPos = new Posicao(bounds.position.x + bounds.width / 2 - 30,
                bounds.position.y + bounds.height / 2 - 45);
        deck = new Deck(new Posicao(centredCardPos.x - 160, centredCardPos.y));

        this.players = playerList;
        for (Jogador player : players) {
            if (player.getPlayerType() == Jogador.PlayerType.UnoJogador) {
                bottomPlayer = player;
            }
            // Esvaziar a mão é necessário caso esta seja uma sequência contínua de
            // rodadas.
            player.emptyHand();
            for (int i = 0; i < 7; i++) {
                player.addCardToHand(deck.drawCard());
            }
        }
        currentPlayerID = (int) (Math.random() * players.size());
        isIncreasing = (Math.random() * 100 < 50);
        playDirectionAnimation = new AnimacaoDirecao(new Posicao(bounds.width / 2, bounds.height / 2), 120, 5);
        playDirectionAnimation.setIsIncreasing(isIncreasing);

        overlayManager = new OverlayGerenciador(bounds, players);
        forcePlayCard(deck.drawCard());
        currentTurnAction = null;
        debugShowTaskActionNotes = false;
        debugShowTreeOnNewAction = false;
    }

    /**
     * Atualiza todos os componentes do jogo que precisam ser atualizados em um
     * cronômetro.
     *
     * @param deltaTime Tempo desde a última atualização.
     */
    @Override
    public void update(int deltaTime) {
        if (!isEnabled())
            return;

        playDirectionAnimation.update(deltaTime);
        overlayManager.update(deltaTime);
        updateTurnAction();
        players.forEach(player -> player.update(deltaTime));
        checkForEndOfRound();
    }

    /**
     * Verifica se existe atualmente um jogador que ganhou o jogo e inicia o final
     * condições do jogo uma vez encontradas.
     */
    private void checkForEndOfRound() {
        for (Jogador player : players) {
            if (player.getHand().size() == 0) {
                int totalScore = 0;
                for (Jogador value : players) {
                    if (value != player) {
                        value.setCurrentRoundScore(0);
                        totalScore += value.getHandTotalScore();
                    }
                }
                player.setCurrentRoundScore(totalScore);
                player.setWon();
                painelJogo.showPostGame(players, ruleSet);
                return;
            }
        }
    }

    /**
     * Atualiza o estado atual da ação de turno executando a ação e depois
     * iterando para o próximo, se possível.
     */
    private void updateTurnAction() {
        if (currentTurnAction != null) {
            // Tree Debug Output
            if (PainelJogo.DEBUG_MODE && debugShowTaskActionNotes) {
                if (currentTurnAction instanceof TurnActionFactory.TurnDecisionAction) {
                    if (!((TurnActionFactory.TurnDecisionAction) currentTurnAction).hasRunOnce) {
                        System.out.println(currentTurnAction.actionDebugText);
                    }
                } else {
                    System.out.println(currentTurnAction.actionDebugText);
                }
            }
            currentTurnAction.performAction();
            currentTurnAction = currentTurnAction.getNext();
            if (queuedTurnAction != null) {
                currentTurnAction = queuedTurnAction;
                queuedTurnAction = null;
            }
        }
    }

    /**
     * Desenha todos os elementos do jogo disponíveis.
     * Quando não ativado, ele se sobreporá a uma camada transparente.
     *
     * @param g Referência ao objeto Graphics para renderização.
     */
    @Override
    public void paint(Graphics g) {
        deck.paint(g);
        recentCards.forEach(card -> card.paint(g));
        players.forEach(player -> {
            if (player.getPlayerType() != Jogador.PlayerType.UnoJogador)
                player.paint(g);
        });
        bottomPlayer.paint(g);
        overlayManager.paint(g);

        playDirectionAnimation.paint(g);
    }

    /**
     * Não faz nada se não estiver habilitado. Passa a interação para o gerenciador
     * de sobreposição,
     * e permite que o jogador interaja com o baralho/suas cartas quando for seu
     * vez.
     *
     * @param mousePosition Posição do cursor do mouse durante o pressionamento.
     * @param isLeft        Se verdadeiro, o botão do mouse está para a esquerda,
     *                      caso contrário, está para a direita.
     */
    @Override
    public void handleMousePress(Posicao mousePosition, boolean isLeft) {
        if (!isEnabled())
            return;

        overlayManager.handleMousePress(mousePosition, isLeft);

        if (currentTurnAction == null && currentPlayerID == bottomPlayer.getPlayerID()) {
            if (deck.isPositionInside(mousePosition)) {
                currentTurnAction = TurnActionFactory.drawCardAsAction(currentPlayerID);
            } else {
                Carta cardToPlay = bottomPlayer.chooseCardFromClick(mousePosition);
                Carta topCard = getTopCard();
                if (bottomPlayer.getValidMoves(topCard.getFaceValueID(), topCard.getColourID()).contains(cardToPlay)) {
                    currentTurnAction = TurnActionFactory.playCardAsAction(currentPlayerID, cardToPlay.getCardID(),
                            cardToPlay.getFaceValueID(), cardToPlay.getColourID());
                }
            }
        } else if (currentTurnAction == null && currentPlayerID != bottomPlayer.getPlayerID() &&
                InterfaceJogo.getCurrentGame().getRuleSet().allowJumpInRule()) {
            Carta cardToPlay = bottomPlayer.chooseCardFromClick(mousePosition);
            if (cardToPlay != null) {
                jumpIn(bottomPlayer.getPlayerID(), cardToPlay);
            }
        }
    }

    /**
     * Não faz nada se não estiver habilitado. Passa o movimento do mouse para o
     * gerenciador de sobreposição
     * e jogador inferior.
     *
     * @param mousePosition Posição do mouse durante este movimento.
     */
    @Override
    public void handleMouseMove(Posicao mousePosition) {
        if (!isEnabled())
            return;

        overlayManager.handleMouseMove(mousePosition);
        bottomPlayer.updateHover(mousePosition);
    }

    /**
     * Lida com os principais eventos desta interface.
     *
     * @param keyCode A tecla que foi pressionada.
     */
    @Override
    public void handleInput(int keyCode) {
        if (keyCode == KeyEvent.VK_Q) {
            sortHand();
        } else if (PainelJogo.DEBUG_MODE && keyCode == KeyEvent.VK_9) {
            revealHands();
        } else if (PainelJogo.DEBUG_MODE && keyCode == KeyEvent.VK_8) {
            toggleTurnDirection();
        } else if (PainelJogo.DEBUG_MODE && keyCode == KeyEvent.VK_7) {
            bottomPlayer.emptyHand();
        } else if (PainelJogo.DEBUG_MODE && keyCode == KeyEvent.VK_6) {
            bottomPlayer.removeCard(bottomPlayer.getHand().get(0));
        } else if (PainelJogo.DEBUG_MODE && keyCode == KeyEvent.VK_5) {
            debugShowTreeOnNewAction = !debugShowTreeOnNewAction;
        } else if (PainelJogo.DEBUG_MODE && keyCode == KeyEvent.VK_4) {
            debugShowTaskActionNotes = !debugShowTaskActionNotes;
        } else {
            overlayManager.handleInput(keyCode);
        }
    }

    /**
     * Verifica se a carta pode ser jogada como um salto e depois troca a carta
     * atual
     * jogador,
     * e inicia a ação da carta que está sendo jogada.
     *
     * @param playerID   O jogador tentando entrar.
     * @param cardToPlay A carta que está sendo usada.
     */
    public void jumpIn(int playerID, Carta cardToPlay) {
        Carta topCard = getTopCard();
        if (currentTurnAction == null && currentPlayerID != playerID
                && topCard.getFaceValueID() == cardToPlay.getFaceValueID()
                && topCard.getColourID() == cardToPlay.getColourID()) {
            currentPlayerID = playerID;
            showGeneralOverlay("JumpIn" + playerID);
            currentTurnAction = TurnActionFactory.playCardAsAction(currentPlayerID, cardToPlay.getCardID(),
                    cardToPlay.getFaceValueID(), cardToPlay.getColourID());
        }
    }

    /**
     * Usado para mostrar uma sobreposição baseada em uma decisão atual.
     */
    public void showOverlayForTurnAction() {
        if (currentTurnAction instanceof TurnActionFactory.TurnDecisionAction) {
            overlayManager.showDecisionOverlay((TurnActionFactory.TurnDecisionAction) currentTurnAction);
        }
    }

    /**
     * Mostra uma sobreposição baseada em uma pesquisa de String no gerenciador de
     * sobreposições.
     *
     * @param overlayName Nome que mapeia para uma sobreposição específica.
     */
    public void showGeneralOverlay(String overlayName) {
        overlayManager.showGeneralOverlay(overlayName);
    }

    /**
     * Força todas as mãos a revelarem e permanecerem reveladas.
     */
    public void revealHands() {
        players.forEach(player -> player.revealHand(true));
    }

    /**
     * Classifica a mão do jogador.
     */
    public void sortHand() {
        bottomPlayer.sortHand();
    }

    /**
     * Retorna o jogador que está jogando atualmente.
     *
     * @return O jogador que está jogando.
     */
    public Jogador getBottomPlayer() {
        return bottomPlayer;
    }

    /**
     * Alterna o sentido de rotação entre sentido horário e anti-horário.
     * Incluindo atualização da direção da animação.
     */
    public void toggleTurnDirection() {
        isIncreasing = !isIncreasing;
        playDirectionAnimation.setIsIncreasing(isIncreasing);
    }

    /**
     * Move para o próximo jogador dependendo se a direção é no sentido horário ou
     * sentido anti-horário.
     */
    public void moveToNextPlayer() {
        updateUNOState();
        if (isIncreasing) {
            currentPlayerID++;
            if (currentPlayerID >= players.size()) {
                currentPlayerID = 0;
            }
        } else {
            currentPlayerID--;
            if (currentPlayerID < 0) {
                currentPlayerID = players.size() - 1;
            }
        }
    }

    /**
     * Define o jogador atual como NotSafe se ele tiver uma carta ou Safe caso
     * contrário,
     * todos os outros jogadores estão definidos como Seguros.
     */
    private void updateUNOState() {
        players.get(currentPlayerID).setUnoState(
                players.get(currentPlayerID).getHand().size() == 1 ? Jogador.UNOState.NotSafe : Jogador.UNOState.Safe);
        for (Jogador player : players) {
            if (player.getPlayerID() != currentPlayerID) {
                player.setUnoState(Jogador.UNOState.Safe);
            }
        }
    }

    /**
     * Aplica o efeito de ser chamado por não ter dito UNO.
     * Mostra a mensagem e compra duas cartas para aquele jogador.
     */
    public void applyAntiUno(int playerID) {
        InterfaceJogo.getCurrentGame().showGeneralOverlay("AntiUnoCalled" + playerID);
        // Defina como seguro para evitar vários callouts anti-uno.
        players.get(playerID).setUnoState(Jogador.UNOState.Safe);
        players.get(playerID).addCardToHand(deck.drawCard());
        players.get(playerID).addCardToHand(deck.drawCard());
    }

    /**
     * Obtém a direção atual do jogo.
     *
     * @return Quando verdadeiro, a direção da reprodução é no sentido horário.
     */
    public boolean isIncreasing() {
        return isIncreasing;
    }

    /**
     * Muda a cor do cartão superior. Usado para mudar a cor do wild e +4
     * cartões.
     *
     * @param colorID 0=Vermelho, 1=Azul, 2=Verde, 3=Amarelo
     */
    public void setTopCardColour(int colourID) {
        recentCards.get(recentCards.size() - 1).setColour(colourID);
    }

    /**
     * Se já houver uma ação atual ativa, ela será colocada na fila para iniciar o
     * mais rápido possível.
     * Caso contrário, a ação será configurada imediatamente.
     *
     * @param turnAction O TurnAction para começar.
     */
    public void setCurrentTurnAction(TurnActionFactory.TurnAction turnAction) {
        if (currentTurnAction != null) {
            queuedTurnAction = turnAction;
            if (PainelJogo.DEBUG_MODE && debugShowTreeOnNewAction) {
                System.out.println("Sequência de ação na fila:");
                TurnActionFactory.debugOutputTurnActionTree(turnAction);
            }
        } else {
            currentTurnAction = turnAction;
            if (PainelJogo.DEBUG_MODE && debugShowTreeOnNewAction) {
                System.out.println("Definir sequência de ação:");
                TurnActionFactory.debugOutputTurnActionTree(turnAction);
            }
        }
    }

    /**
     * Usado para jogar a primeira carta. Isto consiste em simplesmente colocar o
     * cartão
     * sem ação, e se a carta for curinga, a cor será aleatória.
     *
     * @param card Cartão para colocar no topo da pilha de cartas sem ação.
     */
    public void forcePlayCard(Carta card) {
        placeCard(card);

        if (card.getFaceValueID() >= 13) {
            setTopCardColour((int) (Math.random() * 4));
        }
    }

    /**
     * Move a posição da carta para a pilha de cartas com um deslocamento aleatório
     * e a adiciona
     * à coleção de recentCards. Se o número de cartões recentes for maior
     * que o máximo permitido o cartão mais antigo é removido.
     *
     * @param card Cartão para colocar no topo da pilha de cartas.
     */
    public void placeCard(Carta card) {
        card.position.setPosition(centredCardPos.x, centredCardPos.y);
        card.position.add(new Posicao((int) (Math.random() * 24 - 12), (int) (Math.random() * 24 - 12)));
        recentCards.add(card);
        int MAX_CARD_HISTORY = 10;
        if (recentCards.size() > MAX_CARD_HISTORY) {
            recentCards.remove(0);
        }
    }

    /**
     * Obtém o TurnAction atual, se houver.
     *
     * @return A ação atual ou nula.
     */
    public TurnActionFactory.TurnAction getCurrentTurnAction() {
        return currentTurnAction;
    }

    /**
     * Obtém o conjunto de regras para verificar e aplicar facilmente quaisquer
     * regras.
     *
     * @return A definição do conjunto de regras.
     */
    public ConjuntoRegras getRuleSet() {
        return ruleSet;
    }

    /**
     * Obtém o jogador atualmente ativo por ordem de turno.
     *
     * @return O jogador identificado por currentPlayerID.
     */
    public Jogador getCurrentPlayer() {
        return players.get(currentPlayerID);
    }

    /**
     * Obtém uma lista de todos os jogadores.
     *
     * @return Uma referência para todos os jogadores.
     */
    public List<Jogador> getAllPlayers() {
        return players;
    }

    /**
     * Procura o jogador com o ID fornecido.
     *
     * @param playerID ID obtido na coleção de jogadores.
     * @return O jogador que corresponde ao playerID fornecido.
     */
    public Jogador getPlayerByID(int playerID) {
        return players.get(playerID);
    }

    /**
     * Faz com que o baralho forneça acesso a cartas de compra.
     *
     * @return Uma referência ao Deck.
     */
    public Deck getDeck() {
        return deck;
    }

    /**
     * Obtém a coleção de cartas jogadas recentemente.
     *
     * @return Uma referência às cartas jogadas recentemente.
     */
    public List<Carta> getRecentCards() {
        return recentCards;
    }

    /**
     * Obtém o cartão recente jogado mais recentemente.
     *
     * @return A carta que aparece no topo da pilha jogada.
     */
    public Carta getTopCard() {
        return recentCards.get(recentCards.size() - 1);
    }

    /**
     * Gera uma lista de jogadores usando os tipos especificados. Requer um único
     * UnoJogador e 1 ou 3 AIJogador.
     *
     * @param playerList Uma lista de dados do jogador para gerar uma coleção.
     * @param limites    Os limites a serem usados para calcular deslocamentos e
     *                   regiões.
     */
    private static List<Jogador> createPlayersFromLobby(List<LobbyPlayer> playerList, Retangulo bounds) {
        List<Jogador> result = new ArrayList<>();
        List<LobbyPlayer> playersToAdd = playerList.stream().filter(LobbyPlayer::isEnabled)
                .collect(Collectors.toList());
        if (playersToAdd.size() != 2 && playersToAdd.size() != 4) {
            System.out.println("Erro crítico. Somente combinações de 2 ou 4 jogadores são permitidas");
            return result;
        }
        int UnoJogadorIndex = -1;
        for (int i = 0; i < playersToAdd.size(); i++) {
            if (playersToAdd.get(i).getPlayerType() == Jogador.PlayerType.UnoJogador) {
                if (UnoJogadorIndex == -1) {
                    UnoJogadorIndex = i;
                } else {
                    System.out.println("Erro crítico. Somente um UnoJogador é permitido.");
                    return result;
                }
            }
        }
        if (UnoJogadorIndex == -1) {
            System.out.println("Erro crítico. É necessário um UnoJogador!");
            return result;
        }

        for (int i = 0; i < playersToAdd.size(); i++) {
            Retangulo playerRegion;
            boolean showNameLeft;
            if (playersToAdd.size() == 4) {
                playerRegion = getPlayerRect((i + 4 - UnoJogadorIndex) % 4, bounds);
                showNameLeft = (i + 4 - UnoJogadorIndex) % 2 == 0;
            } else {
                playerRegion = getPlayerRect(
                        playersToAdd.get(i).getPlayerType() == Jogador.PlayerType.UnoJogador ? 0 : 2, bounds);
                showNameLeft = true;
            }
            if (playersToAdd.get(i).getPlayerType() == Jogador.PlayerType.AIJogador) {
                result.add(new AIJogador(i, playersToAdd.get(i).getPlayerName(), playerRegion,
                        playersToAdd.get(i).getAIStrategy(), showNameLeft));
            } else {
                result.add(new Jogador(i, playersToAdd.get(i).getPlayerName(), playersToAdd.get(i).getPlayerType(),
                        playerRegion, showNameLeft));
            }
        }
        return result;
    }

    /**
     * Gera limites para onde as cartas de um jogador devem ser colocadas.
     *
     * @param direção 0=inferior, 1=esquerda, 2=superior, 3=direita
     * @param limites Os limites a serem usados para calcular deslocamentos e
     *                regiões.
     * @return Um retângulo definindo onde o jogador deve colocar suas cartas no
     *         campo.
     */
    private static Retangulo getPlayerRect(int direction, Retangulo bounds) {
        return switch (direction) {
            case 1 -> new Retangulo(bounds.position.x,
                    bounds.position.y + bounds.height / 2 - 150,
                    (Carta.CARD_WIDTH + 4) * 6, bounds.height / 2 - 100 - 10);
            case 2 -> new Retangulo(bounds.position.x + bounds.width / 2 - (Carta.CARD_WIDTH + 4) * 10 / 2,
                    bounds.position.y - 30,
                    (Carta.CARD_WIDTH + 4) * 10, bounds.height / 2 - 100 - 10);
            case 3 -> new Retangulo(bounds.position.x + bounds.width - ((Carta.CARD_WIDTH + 4) * 6 + 50) + 50,
                    bounds.position.y + bounds.height / 2 - 150,
                    (Carta.CARD_WIDTH + 4) * 6, bounds.height / 2 - 100 - 10);
            default -> new Retangulo(bounds.position.x + bounds.width / 2 - (Carta.CARD_WIDTH + 4) * 10 / 2,
                    bounds.position.y + bounds.height / 2 + 130,
                    (Carta.CARD_WIDTH + 4) * 10, bounds.height / 2 - 100 - 10);
        };
    }
}
