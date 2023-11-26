import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Uno
 *
 * Classe PainelJogo:
 * Gerencia o jogo principal com ações do mouse, teclas e
 * quaisquer eventos cronometrados para as diferentes partes do jogo.
 *
 * @autor Cauet Damasceno
 * @versão 2023
 */
public class PainelJogo extends JPanel implements MouseListener, MouseMotionListener, ActionListener {
    /**
     * Altura do painel.
     */
    public static final int PANEL_HEIGHT = 720;
    /**
     * Largura do painel.
     */
    public static final int PANEL_WIDTH = 1280;

    /**
     * Referência à janela que aparece quando o jogo é pausado.
     */
    private final PausaInterface pauseWnd;
    /**
     * Referência à interface ativa.
     */
    private WndInterface activeInterface;
    /**
     * Quando o modo de depuração está ativado. Saídas e controles adicionais estão
     * habilitados.
     */
    public static boolean DEBUG_MODE;

    /**
     * Configura o jogo pronto para ser jogado, incluindo a seleção de jogar contra
     * qualquer
     * AI ou outro jogador.
     */
    public PainelJogo() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(new Color(93, 141, 74));

        pauseWnd = new PausaInterface(new Retangulo(PANEL_WIDTH / 2 - 100, PANEL_HEIGHT / 2 - 100, 200, 200), this);
        pauseWnd.setEnabled(false);

        showLobby();

        Timer updateTimer = new Timer(20, this);
        updateTimer.start();

        addMouseListener(this);
        addMouseMotionListener(this);
        DEBUG_MODE = false;
    }

    /**
     * Define a interface atualmente ativa para o lobby, removendo qualquer
     * interface existente
     * interface.
     * Se isso for acionado pela interface de pausa, ele apenas retoma o atual
     * interface.
     */
    public void showLobby() {
        if (!(activeInterface instanceof LobbyInterface)) {
            activeInterface = new LobbyInterface(new Retangulo(0, 0, PANEL_WIDTH, PANEL_HEIGHT), this);
        }
        setPauseState(false);
    }

    /**
     * Define a interface atualmente ativa para a interface pós-jogo após um jogo
     * terminou.
     *
     * @param playerList Lista dos jogadores que estavam jogando na rodada.
     * @param RuleSet    Regras aplicadas durante a rodada.
     */
    public void showPostGame(List<Jogador> playerList, ConjuntoRegras ruleSet) {
        activeInterface = new InterfacePosJogo(new Retangulo(0, 0, PANEL_WIDTH, PANEL_HEIGHT),
                playerList, ruleSet, this);
    }

    /**
     * Cria um novo jogo com a lista de jogadores especificada. Use isso para vir
     * do lobby.
     *
     * @param playerList A lista de jogadores para iniciar um jogo.
     * @param RuleSet    Definição de como o jogo será jogado.
     */
    public void startGame(List<LobbyPlayer> playerList, ConjuntoRegras ruleSet) {
        activeInterface = new InterfaceJogo(new Retangulo(0, 0, PANEL_WIDTH, PANEL_HEIGHT),
                ruleSet, playerList, this);
    }

    /**
     * Cria um novo jogo com a lista de jogadores especificada. Use isso para vir
     * do pós-jogo.
     *
     * @param playerList A lista de jogadores para iniciar uma nova rodada.
     * @param RuleSet    Definição de como o jogo será jogado.
     */
    public void startNextRound(List<Jogador> playerList, ConjuntoRegras ruleSet) {
        activeInterface = new InterfaceJogo(new Retangulo(0, 0, PANEL_WIDTH, PANEL_HEIGHT),
                playerList, ruleSet, this);
    }

    /**
     * Desenha a grade do jogo e desenha a mensagem na parte inferior mostrando uma
     * string
     * representando o estado do jogo.
     *
     * @param g Referência ao objeto Graphics para renderização.
     */
    public void paint(Graphics g) {
        super.paint(g);
        if (activeInterface != null) {
            activeInterface.paint(g);
        }
        if (pauseWnd.isEnabled()) {
            pauseWnd.paint(g);
        }
        if (DEBUG_MODE) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("DEBUG ON", 10, 20);
        }
    }

    /**
     * Pausa ou retoma o jogo.
     *
     * @param isPaused Quando verdadeiro, o jogo é pausado e a janela de pausa é
     *                 mostrada.
     */
    public void setPauseState(boolean isPaused) {
        if (activeInterface != null) {
            activeInterface.setEnabled(!isPaused);
        }
        pauseWnd.setEnabled(isPaused);
    }

    /**
     * Sai do jogo imediatamente.
     */
    public void quitGame() {
        System.exit(0);
    }

    /**
     * Manipula a entrada da tecla para que Escape abra o menu de pausa.
     *
     * @param keyCode A tecla que foi pressionada.
     */
    public void handleInput(int keyCode) {
        if (keyCode == KeyEvent.VK_ESCAPE) {
            setPauseState(!pauseWnd.isEnabled());
        } else if (keyCode == KeyEvent.VK_0) {
            DEBUG_MODE = !DEBUG_MODE;
        } else {
            activeInterface.handleInput(keyCode);
        }
        repaint();
    }

    /**
     * Passa o evento do mouse para todas as janelas.
     *
     * @param e Informações sobre o evento do mouse.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        Posicao mousePosition = new Posicao(e.getX(), e.getY());
        pauseWnd.handleMousePress(mousePosition, e.getButton() == 1);
        if (activeInterface != null) {
            activeInterface.handleMousePress(mousePosition, e.getButton() == 1);
        }
        repaint();
    }

    /**
     * Sorteia um título com UNO! e texto abaixo para créditos.
     *
     * @param g       Referência ao objeto Graphics para renderização.
     * @param limites Limites da área de jogo.
     */
    public void paintUnoTitle(Graphics g, Retangulo bounds) {
        g.setFont(new Font("Arial", Font.BOLD, 40));
        g.drawString("UNO!", bounds.width / 2 - 40, 50);
        g.setFont(new Font("Arial", Font.BOLD, 10));
        g.drawString("", bounds.width / 2 - 70, 65);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        g.setColor(Carta.getColourByID(0));
        g.drawString("U", bounds.width / 2 - 40 + 2, 48);
        g.setColor(Carta.getColourByID(1));
        g.drawString("N", bounds.width / 2 - 40 + 2 + 30, 48);
        g.setColor(Carta.getColourByID(2));
        g.drawString("O", bounds.width / 2 - 40 + 2 + 60, 48);
        g.setColor(Carta.getColourByID(3));
        g.drawString("!", bounds.width / 2 - 40 + 2 + 90, 48);
    }

    /**
     * Passa o evento do mouse para todas as janelas.
     *
     * @param e Informações sobre o evento do mouse.
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        Posicao mousePosition = new Posicao(e.getX(), e.getY());
        pauseWnd.handleMouseMove(mousePosition);
        if (activeInterface != null) {
            activeInterface.handleMouseMove(mousePosition);
        }
        repaint();
    }

    /**
     * Força a atualização do jogo ativo e força uma repintura.
     *
     * @param e Informações sobre o evento.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (activeInterface != null) {
            activeInterface.update(20);
        }
        repaint();
    }

    /**
     * Não configurado.
     *
     * @param e Não definido.
     */
    @Override
    public void mouseClicked(MouseEvent e) {
    }

    /**
     * Não configurado.
     *
     * @param e Não definido.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
    }

    /**
     * Não configurado.
     *
     * @param e Não definido.
     */
    @Override
    public void mouseEntered(MouseEvent e) {
    }

    /**
     * Não configurado.
     *
     * @param e Não definido.
     */
    @Override
    public void mouseExited(MouseEvent e) {
    }

    /**
     * Não configurado.
     *
     * @param e Não definido.
     */
    @Override
    public void mouseDragged(MouseEvent e) {
    }
}
