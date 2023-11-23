import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Uno
 *
 * CurrentGameInterface class:
 * Defines the main game view controlling a list of players and
 * managing the state of all game elements.
 *
 * @autor Cauet Damasceno
 * @versão 2023
 */
public class InterfaceJogo extends WndInterface {

    /**
     * The deck of cards ready to have cards drawn from it.
     */
    private final Deck deck;
    /**
     * A history of cards that have been played.
     */
    private final List<Carta> recentCards;
    /**
     * The centre of where to place recent cards.
     */
    private final Posicao centredCardPos;
    /**
     * Reference to the player who is playing the game.
     */
    private Jogador bottomPlayer;
    /**
     * A manager controlling the various overlays that are shown based on events during the game.
     */
    private final OverlayGerenciador overlayManager;
    /**
     * TurnActions are triggered via playing cards or drawing cards. This acts as a
     * linked list that performs actions at each step including splitting between choices
     * for TurnDecisionAction objects.
     */
    private TurnActionFactory.TurnAction currentTurnAction;
    /**
     * An action queued up to start. This is to let the current action finish and
     * then after it is done this is started.
     */
    private TurnActionFactory.TurnAction queuedTurnAction;

    /**
     * All the players that are currently playing including their hands and other details.
     */
    private final List<Jogador> players;
    /**
     * The current player who is in control of actions.
     */
    private int currentPlayerID;
    /**
     * Animation to show the direction of turn order.
     */
    private final AnimacaoDirecao playDirectionAnimation;
    /**
     * Turn order increasing (true) means clockwise, or false would be anti-clockwise.
     */
    private boolean isIncreasing;

    /**
     * The rules for what card actions are set and other specific changes to how the game is played.
     */
    private final ConjuntoRegras ruleSet;
    /**
     * Reference to the current instance of this class so that other classes can quickly access it directly.
     */
    private static InterfaceJogo activeSingleton;
    /**
     * Reference to GamePanel for when the game ends.
     */
    private final GamePainel gamePanel;
    /**
     * When GamePanel.DEBUG_MODE and this are true, output is shown for each transition in the TaskAction sequence.
     */
    private boolean debugShowTaskActionNotes;
    /**
     * When GamePanel.DEBUG_MODE and this are true, output is shown with the whole tree TaskAction sequence when setCurrentTurnAction is used.
     */
    private boolean debugShowTreeOnNewAction;

    /**
     * Gets the current single instance of CurrentGameInterface. This is not enforced, but
     * it can be null if not created yet, but it is expected that only one will be run at a time.
     *
     * @return Reference to the current instance of this class.
     */
    public static InterfaceJogo getCurrentGame() {
        return activeSingleton;
    }

    /**
     * Initialise the interface with bounds and make it enabled. Use this version when coming from the Lobby for
     * a new set of rounds.
     *
     * @param bounds The bounds of the interface.
     * @param ruleSet The rules definition for how the game is to be played.
     * @param lobbyPlayers Players to create in the game.
     */
    public InterfaceJogo(Retangulo bounds, ConjuntoRegras ruleSet, List<LobbyPlayer> lobbyPlayers, GamePainel gamePanel) {
        this(bounds, createPlayersFromLobby(lobbyPlayers, bounds), ruleSet, gamePanel);
    }

    /**
     * Initialise the interface with bounds and make it enabled. Use this version when coming from
     * after a game has already been completed and the sequence of games is continuing.
     *
     * @param bounds The bounds of the interface.
     * @param playerList Players to create in the game.
     * @param ruleSet The rules definition for how the game is to be played.
     */
    public InterfaceJogo(Retangulo bounds, List<Jogador> playerList, ConjuntoRegras ruleSet, GamePainel gamePanel) {
        super(bounds);
        activeSingleton = this;
        this.ruleSet = ruleSet;
        this.gamePanel = gamePanel;
        recentCards = new ArrayList<>();
        centredCardPos = new Posicao(bounds.position.x+bounds.width/2-30,bounds.position.y+bounds.height/2-45);
        deck = new Deck(new Posicao(centredCardPos.x-160,centredCardPos.y));

        this.players = playerList;
        for (Jogador player : players) {
            if(player.getPlayerType() == Jogador.PlayerType.ThisPlayer) {
                bottomPlayer = player;
            }
            // Emptying hand is required just in case this is a continued sequence of rounds.
            player.emptyHand();
            for(int i = 0; i < 7; i++) {
                player.addCardToHand(deck.drawCard());
            }
        }
        currentPlayerID = (int) (Math.random()*players.size());
        isIncreasing = (Math.random() * 100 < 50);
        playDirectionAnimation = new AnimacaoDirecao(new Posicao(bounds.width/2,bounds.height/2), 120, 5);
        playDirectionAnimation.setIsIncreasing(isIncreasing);

        overlayManager = new OverlayGerenciador(bounds, players);
        forcePlayCard(deck.drawCard());
        currentTurnAction = null;
        debugShowTaskActionNotes = false;
        debugShowTreeOnNewAction = false;
    }

    /**
     * Updates all the game components that need to be updated on a timer.
     *
     * @param deltaTime Time since last update.
     */
    @Override
    public void update(int deltaTime) {
        if(!isEnabled()) return;

        playDirectionAnimation.update(deltaTime);
        overlayManager.update(deltaTime);
        updateTurnAction();
        players.forEach(player -> player.update(deltaTime));
        checkForEndOfRound();
    }

    /**
     * Checks if there is currently a player who has won the game and initiates end game conditions once found.
     */
    private void checkForEndOfRound() {
        for(Jogador player : players) {
            if(player.getHand().size() == 0) {
                int totalScore = 0;
                for (Jogador value : players) {
                    if (value != player) {
                        value.setCurrentRoundScore(0);
                        totalScore += value.getHandTotalScore();
                    }
                }
                player.setCurrentRoundScore(totalScore);
                player.setWon();
                gamePanel.showPostGame(players, ruleSet);
                return;
            }
        }
    }

    /**
     * Updates the current turn action state by performing the action and then iterating to the next one if possible.
     */
    private void updateTurnAction() {
        if(currentTurnAction != null) {
            // Tree Debug Output
            if(GamePainel.DEBUG_MODE && debugShowTaskActionNotes) {
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
            if(queuedTurnAction != null) {
                currentTurnAction = queuedTurnAction;
                queuedTurnAction = null;
            }
        }
    }

    /**
     * Draws all the game elements that are available.
     * When not enabled it will overlay with a transparent layer.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    @Override
    public void paint(Graphics g) {
        deck.paint(g);
        recentCards.forEach(card -> card.paint(g));
        players.forEach(player -> {if(player.getPlayerType() != Jogador.PlayerType.ThisPlayer) player.paint(g);});
        bottomPlayer.paint(g);
        overlayManager.paint(g);

        playDirectionAnimation.paint(g);
    }

    /**
     * Does nothing if not enabled. Passes the interaction to the overlay manager,
     * and allows the player to interact with the deck/their cards when it is their turn.
     *
     * @param mousePosition Position of the mouse cursor during the press.
     * @param isLeft        If true, the mouse button is left, otherwise is right.
     */
    @Override
    public void handleMousePress(Posicao mousePosition, boolean isLeft) {
        if(!isEnabled()) return;

        overlayManager.handleMousePress(mousePosition, isLeft);

        if(currentTurnAction == null && currentPlayerID == bottomPlayer.getPlayerID()) {
            if (deck.isPositionInside(mousePosition)) {
                currentTurnAction = TurnActionFactory.drawCardAsAction(currentPlayerID);
            } else {
                Carta cardToPlay = bottomPlayer.chooseCardFromClick(mousePosition);
                Carta topCard = getTopCard();
                if (bottomPlayer.getValidMoves(topCard.getFaceValueID(), topCard.getColourID()).contains(cardToPlay)) {
                    currentTurnAction = TurnActionFactory.playCardAsAction(currentPlayerID, cardToPlay.getCardID(), cardToPlay.getFaceValueID(), cardToPlay.getColourID());
                }
            }
        } else if(currentTurnAction == null && currentPlayerID != bottomPlayer.getPlayerID() &&
                    InterfaceJogo.getCurrentGame().getRuleSet().allowJumpInRule()) {
            Carta cardToPlay = bottomPlayer.chooseCardFromClick(mousePosition);
            if(cardToPlay != null) {
                jumpIn(bottomPlayer.getPlayerID(), cardToPlay);
            }
        }
    }

    /**
     * Does nothing if not enabled. Passes the mouse movement to the overlay manager and bottom player.
     *
     * @param mousePosition Position of the mouse during this movement.
     */
    @Override
    public void handleMouseMove(Posicao mousePosition) {
        if(!isEnabled()) return;

        overlayManager.handleMouseMove(mousePosition);
        bottomPlayer.updateHover(mousePosition);
    }

    /**
     * Handles the key events for this interface.
     *
     * @param keyCode The key that was pressed.
     */
    @Override
    public void handleInput(int keyCode) {
        if(keyCode == KeyEvent.VK_Q) {
            sortHand();
        } else if(GamePainel.DEBUG_MODE && keyCode == KeyEvent.VK_9) {
            revealHands();
        } else if(GamePainel.DEBUG_MODE && keyCode == KeyEvent.VK_8) {
            toggleTurnDirection();
        } else if(GamePainel.DEBUG_MODE && keyCode == KeyEvent.VK_7) {
            bottomPlayer.emptyHand();
        } else if(GamePainel.DEBUG_MODE && keyCode == KeyEvent.VK_6) {
            bottomPlayer.removeCard(bottomPlayer.getHand().get(0));
        } else if(GamePainel.DEBUG_MODE && keyCode == KeyEvent.VK_5) {
            debugShowTreeOnNewAction = !debugShowTreeOnNewAction;
        } else if(GamePainel.DEBUG_MODE && keyCode == KeyEvent.VK_4) {
            debugShowTaskActionNotes = !debugShowTaskActionNotes;
        } else {
            overlayManager.handleInput(keyCode);
        }
    }

    /**
     * Verifies the card can be played as a jump in and then swaps the current player,
     * and initiates the action of the card being played.
     *
     * @param playerID The player trying to jump in.
     * @param cardToPlay The card that is being jumped in with.
     */
    public void jumpIn(int playerID, Carta cardToPlay) {
        Carta topCard = getTopCard();
        if(currentTurnAction == null && currentPlayerID != playerID
                && topCard.getFaceValueID() == cardToPlay.getFaceValueID()
                && topCard.getColourID() == cardToPlay.getColourID()) {
            currentPlayerID = playerID;
            showGeneralOverlay("JumpIn"+playerID);
            currentTurnAction = TurnActionFactory.playCardAsAction(currentPlayerID, cardToPlay.getCardID(),
                    cardToPlay.getFaceValueID(), cardToPlay.getColourID());
        }
    }

    /**
     * Used to show an overlay based on a current decision.
     */
    public void showOverlayForTurnAction() {
        if(currentTurnAction instanceof TurnActionFactory.TurnDecisionAction) {
            overlayManager.showDecisionOverlay((TurnActionFactory.TurnDecisionAction) currentTurnAction);
        }
    }

    /**
     * Shows an overlay based on a String lookup in the overlay manager.
     *
     * @param overlayName Name that maps to a specific overlay.
     */
    public void showGeneralOverlay(String overlayName) {
        overlayManager.showGeneralOverlay(overlayName);
    }

    /**
     * Forces all hands to reveal and stay revealed.
     */
    public void revealHands() {
        players.forEach(player -> player.revealHand(true));
    }

    /**
     * Sorts the player's hand.
     */
    public void sortHand() {
        bottomPlayer.sortHand();
    }

    /**
     * Returns the player who is currently playing the game.
     *
     * @return The player who is playing the game.
     */
    public Jogador getBottomPlayer() {
        return bottomPlayer;
    }

    /**
     * Toggles the turn direction between clockwise and anti-clockwise.
     * Including updating the animation direction.
     */
    public void toggleTurnDirection() {
        isIncreasing = !isIncreasing;
        playDirectionAnimation.setIsIncreasing(isIncreasing);
    }

    /**
     * Moves to the next player depending on whether the direction is clockwise or anti-clockwise.
     */
    public void moveToNextPlayer() {
        updateUNOState();
        if(isIncreasing) {
            currentPlayerID++;
            if (currentPlayerID >= players.size()) {
                currentPlayerID = 0;
            }
        } else {
            currentPlayerID--;
            if(currentPlayerID < 0) {
                currentPlayerID = players.size()-1;
            }
        }
    }

    /**
     * Sets the current player to NotSafe if they have one card or Safe otherwise, all other players are set to Safe.
     */
    private void updateUNOState() {
        players.get(currentPlayerID).setUnoState(players.get(currentPlayerID).getHand().size() == 1 ? Jogador.UNOState.NotSafe : Jogador.UNOState.Safe);
        for(Jogador player : players) {
            if(player.getPlayerID() != currentPlayerID) {
                player.setUnoState(Jogador.UNOState.Safe);
            }
        }
    }

    /**
     * Applies the effect from being called out on not having said UNO.
     * Flashes the message and draws two cards to that player.
     */
    public void applyAntiUno(int playerID) {
        InterfaceJogo.getCurrentGame().showGeneralOverlay("AntiUnoCalled"+playerID);
        // Set to safe to prevent multiple anti-uno callouts.
        players.get(playerID).setUnoState(Jogador.UNOState.Safe);
        players.get(playerID).addCardToHand(deck.drawCard());
        players.get(playerID).addCardToHand(deck.drawCard());
    }

    /**
     * Gets the current direction of play.
     *
     * @return When true the play direction is clockwise.
     */
    public boolean isIncreasing() {
        return isIncreasing;
    }

    /**
     * Changes the top card colour. Used for changing the colour of the wild and +4 cards.
     *
     * @param colourID 0=Red, 1=Blue, 2=Green, 3=Yellow
     */
    public void setTopCardColour(int colourID) {
        recentCards.get(recentCards.size()-1).setColour(colourID);
    }

    /**
     * If there is a current action already active it will be queued to start asap.
     * Otherwise the action is set up immediately.
     *
     * @param turnAction The TurnAction to begin.
     */
    public void setCurrentTurnAction(TurnActionFactory.TurnAction turnAction) {
        if(currentTurnAction != null) {
            queuedTurnAction = turnAction;
            if(GamePainel.DEBUG_MODE && debugShowTreeOnNewAction) {
                System.out.println("Sequência de ação na fila:");
                TurnActionFactory.debugOutputTurnActionTree(turnAction);
            }
        } else {
            currentTurnAction = turnAction;
            if(GamePainel.DEBUG_MODE && debugShowTreeOnNewAction) {
                System.out.println("Definir sequência de ação:");
                TurnActionFactory.debugOutputTurnActionTree(turnAction);
            }
        }
    }

    /**
     * Used to play the first card. This consists of simply placing the card
     * with no action, and if the card is a wild the colour is randomised.
     *
     * @param card Card to place on top of the card pile with no action.
     */
    public void forcePlayCard(Carta card) {
        placeCard(card);

        if(card.getFaceValueID() >= 13) {
            setTopCardColour((int)(Math.random()*4));
        }
    }

    /**
     * Moves the card's position to the card pile with a random offset and adds it
     * to the collection of recentCards. If the number of recent cards is more
     * than the maximum allowed the oldest card is removed.
     *
     * @param card Card to place on top of the card pile.
     */
    public void placeCard(Carta card) {
        card.position.setPosition(centredCardPos.x, centredCardPos.y);
        card.position.add(new Posicao((int)(Math.random()*24-12),(int)(Math.random()*24-12)));
        recentCards.add(card);
        int MAX_CARD_HISTORY = 10;
        if(recentCards.size() > MAX_CARD_HISTORY) {
            recentCards.remove(0);
        }
    }

    /**
     * Gets the current TurnAction if there is one.
     *
     * @return The current action or null.
     */
    public TurnActionFactory.TurnAction getCurrentTurnAction() {
        return currentTurnAction;
    }

    /**
     * Gets the ruleset to easily check and apply any rules.
     *
     * @return The ruleset definition.
     */
    public ConjuntoRegras getRuleSet() {
        return ruleSet;
    }

    /**
     * Gets the currently active player for turn order.
     *
     * @return The player identified by currentPlayerID.
     */
    public Jogador getCurrentPlayer() {
        return players.get(currentPlayerID);
    }

    /**
     * Gets a list of all players.
     *
     * @return A reference to all the players.
     */
    public List<Jogador> getAllPlayers() {
        return players;
    }

    /**
     * Looks up the player with the given ID.
     *
     * @param playerID ID to get from the players collection.
     * @return The player matching the given playerID.
     */
    public Jogador getPlayerByID(int playerID) {
        return players.get(playerID);
    }

    /**
     * Gets the deck to provide access to drawing cards.
     *
     * @return A reference to the Deck.
     */
    public Deck getDeck() {
        return deck;
    }

    /**
     * Gets the collection of recently played cards.
     *
     * @return A reference to the recently played cards.
     */
    public List<Carta> getRecentCards() {
        return recentCards;
    }

    /**
     * Gets the most recently played recentCard.
     *
     * @return The card that appears on top of the played pile.
     */
    public Carta getTopCard() {
        return recentCards.get(recentCards.size()-1);
    }

    /**
     * Generates a list of players using the specified types. Requires a single ThisPlayer and 1 or 3 AIPlayer.
     *
     * @param playerList A list of player data to generate a collection.
     * @param bounds The bounds to use for calculating offsets and regions.
     */
    private static List<Jogador> createPlayersFromLobby(List<LobbyPlayer> playerList, Retangulo bounds) {
        List<Jogador> result = new ArrayList<>();
        List<LobbyPlayer> playersToAdd = playerList.stream().filter(LobbyPlayer::isEnabled).collect(Collectors.toList());
        if(playersToAdd.size() != 2 && playersToAdd.size() != 4) {
            System.out.println("Erro crítico. Somente combinações de 2 ou 4 jogadores são permitidas");
            return result;
        }
        int thisPlayerIndex = -1;
        for(int i = 0; i < playersToAdd.size(); i++) {
            if(playersToAdd.get(i).getPlayerType() == Jogador.PlayerType.ThisPlayer) {
                if(thisPlayerIndex == -1) {
                    thisPlayerIndex = i;
                } else {
                    System.out.println("Erro crítico. Somente um ThisPlayer é permitido.");
                    return result;
                }
            }
        }
        if(thisPlayerIndex == -1) {
            System.out.println("Erro crítico. É necessário um ThisPlayer!");
            return result;
        }

        for (int i = 0; i < playersToAdd.size(); i++) {
            Retangulo playerRegion;
            boolean showNameLeft;
            if(playersToAdd.size() == 4) {
                playerRegion = getPlayerRect((i + 4 - thisPlayerIndex) % 4, bounds);
                showNameLeft = (i + 4 - thisPlayerIndex) % 2 == 0;
            } else {
                playerRegion = getPlayerRect(playersToAdd.get(i).getPlayerType() == Jogador.PlayerType.ThisPlayer ? 0 : 2, bounds);
                showNameLeft = true;
            }
            if(playersToAdd.get(i).getPlayerType() == Jogador.PlayerType.AIPlayer) {
                result.add(new AIPlayer(i, playersToAdd.get(i).getPlayerName(), playerRegion, playersToAdd.get(i).getAIStrategy(), showNameLeft));
            } else {
                result.add(new Jogador(i, playersToAdd.get(i).getPlayerName(), playersToAdd.get(i).getPlayerType(), playerRegion, showNameLeft));
            }
        }
        return result;
    }

    /**
     * Generates bounds for where a player's cards should be placed.
     *
     * @param direction 0=bottom, 1=left, 2=top, 3=right
     * @param bounds The bounds to use for calculating offsets and regions.
     * @return A Rectangle defining where the player should have their cards on the field.
     */
    private static Retangulo getPlayerRect(int direction, Retangulo bounds) {
        return switch (direction) {
            case 1 -> new Retangulo(bounds.position.x,
                    bounds.position.y + bounds.height / 2-150,
                    (Carta.CARD_WIDTH + 4) * 6, bounds.height / 2 - 100 - 10);
            case 2 -> new Retangulo(bounds.position.x + bounds.width / 2 - (Carta.CARD_WIDTH + 4) * 10 / 2,
                    bounds.position.y-30,
                    (Carta.CARD_WIDTH + 4) * 10, bounds.height / 2 - 100 - 10);
            case 3 -> new Retangulo(bounds.position.x +bounds.width- ((Carta.CARD_WIDTH + 4) * 6 + 50)+50,
                    bounds.position.y + bounds.height / 2-150,
                    (Carta.CARD_WIDTH + 4) * 6, bounds.height / 2 - 100 - 10);
            default -> new Retangulo(bounds.position.x + bounds.width / 2 - (Carta.CARD_WIDTH + 4) * 10 / 2,
                    bounds.position.y + bounds.height / 2 + 130,
                    (Carta.CARD_WIDTH + 4) * 10, bounds.height / 2 - 100 - 10);
        };
    }
}
