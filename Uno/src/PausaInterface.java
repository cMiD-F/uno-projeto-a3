import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Uno
 *
 * PauseInterface class:
 * A simple pause interface that allows a couple of buttons to control
 * game flow or otherwise just pause for a moment.
 *
 * @autor Cauet Damasceno
 * @versão 2023
 */
public class PausaInterface extends WndInterface {
    /**
     * A list of all the buttons in the interface.
     */
    private final List<Butao> buttonList;
    /**
     * A reference to the GamePanel to call back to it.
     */
    private final GamePainel gamePanel;
    /**
     * Text showing the messages with different controls.
     */
    private final List<String> leftMessages, rightMessages;

    /**
     * Initialise the interface with bounds makes it ready to use.
     *
     * @param bounds The region to draw this interface in.
     */
    public PausaInterface(Retangulo bounds, GamePainel gamePanel) {
        super(bounds);
        this.gamePanel = gamePanel;
        buttonList = new ArrayList<>();
        buttonList.add(new Butao(new Posicao(bounds.position.x+6, bounds.position.y+6+60),
                bounds.width-12, 30, "Resume", 1));
        buttonList.add(new Butao(new Posicao(bounds.position.x+6, bounds.position.y+6+30+6+60),
                        bounds.width-12, 30, "Return to Lobby", 3));
        buttonList.add(new Butao(new Posicao(bounds.position.x+6, bounds.position.y+6+(30+6)*2+60),
                bounds.width-12, 30, "Quit", 2));

        leftMessages = new ArrayList<>();
        rightMessages = new ArrayList<>();

        leftMessages.add("Sair: Pausa");
        leftMessages.add("P: Classificar manualmente");

        rightMessages.add("0: Ative a depuração");
        rightMessages.add("9: Revele todas as mãos");
        rightMessages.add("8: Alternar direção de giro");
        rightMessages.add("7: Mão vazia do jogador");
        rightMessages.add("6: Remova a carta do jogador esquerdo");
        rightMessages.add("5: Alternar Mostrar sequência de ação de turno");
        rightMessages.add("4: Alternar Mostrar ativar árvore de ação ativada");
    }

    /**
     * Does nothing.
     *
     * @param deltaTime Time since last update.
     */
    @Override
    public void update(int deltaTime) {

    }

    /**
     * Draws a background and all the buttons.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    @Override
    public void paint(Graphics g) {
        // grey out everything behind the pause interface
        g.setColor(new Color(144, 143, 143, 204));
        g.fillRect(0, 0, GamePainel.PANEL_WIDTH, GamePainel.PANEL_HEIGHT);

        g.setColor(new Color(165, 177, 94, 205));
        g.fillRect(bounds.position.x, bounds.position.y, bounds.width, bounds.height);
        g.fillRect(170, 300, 160, 90);
        g.fillRect(790, 220, 410, 300);
        g.setColor(Color.BLACK);
        g.drawRect(bounds.position.x, bounds.position.y, bounds.width, bounds.height);
        g.drawRect(170, 300, 160, 90);
        g.drawRect(790, 220, 410, 300);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        int strWidth = g.getFontMetrics().stringWidth("Paused");
        g.drawString("Paused", bounds.position.x + bounds.width/2-strWidth/2, bounds.position.y+40);
        buttonList.forEach(button -> button.paint(g));

        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Controls", 210, 320);
        for(int y = 0; y < leftMessages.size(); y++) {
            g.drawString(leftMessages.get(y), 180, 350+y*30);
        }
        g.drawString("Controles de depuração (0 first)", 880, 260);
        for(int y = 0; y < rightMessages.size(); y++) {
            g.drawString(rightMessages.get(y), 800, 300+y*30);
        }
    }

    /**
     * Does nothing if not enabled. Updates hover states of all buttons.
     *
     * @param mousePosition Position of the mouse during this movement.
     */
    @Override
    public void handleMouseMove(Posicao mousePosition) {
        if(!isEnabled()) return;

        for (Butao button : buttonList) {
            button.setHovering(button.isPositionInside(mousePosition));
        }
    }

    /**
     * Does nothing if not enabled. Checks if a button has been clicked and responds to it.
     *
     * @param mousePosition Position of the mouse cursor during the press.
     * @param isLeft        If true, the mouse button is left, otherwise is right.
     */
    @Override
    public void handleMousePress(Posicao mousePosition, boolean isLeft) {
        if(!isEnabled()) return;

        for (Butao button : buttonList) {
            if(button.isPositionInside(mousePosition)) {
                handleButtonAction(button.getActionID());
                break;
            }
        }
    }

    /**
     * Handles the actionID by mapping each ID to an action related to the button.
     *
     * @param actionID The actionID to map to an action from the pause menu.
     */
    private void handleButtonAction(int actionID) {
        switch(actionID) {
            case 1 -> gamePanel.setPauseState(false);
            case 2 -> gamePanel.quitGame();
            case 3 -> gamePanel.showLobby();
        }
    }
}
