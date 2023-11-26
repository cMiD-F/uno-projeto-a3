import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Uno
 *
 * Classe Jogo:
 * Define o ponto de entrada do jogo criando o quadro,
 * e preenchê-lo com um GamePanel.
 *
 * @autor Cauet Damasceno
 * @versão 2023
 */
public class Jogo implements KeyListener {
    /**
     * Ponto de entrada para a aplicação criar uma instância da classe Game.
     *
     * @param args Não usado.
     */
    public static void main(String[] args) {
        new Jogo();
    }

    /**
     * Referência ao objeto GamePanel para o qual passar eventos importantes.
     */
    private final PainelJogo gamePanel;

    /**
     * Cria o JFrame com um GamePanel dentro dele, anexa um key listener,
     * e torna tudo visível.
     */
    public Jogo() {
        JFrame frame = new JFrame("Uno");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        gamePanel = new PainelJogo();
        frame.getContentPane().add(gamePanel);

        frame.addKeyListener(this);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Chamado quando a tecla é pressionada. Passa o pressionamento de tecla para o
     * Painel de jogo.
     *
     * @param e Informação sobre qual tecla foi pressionada.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        gamePanel.handleInput(e.getKeyCode());
    }

    /**
     * Não usado.
     *
     * @param e Não usado.
     */
    @Override
    public void keyTyped(KeyEvent e) {
    }

    /**
     * Não usado.
     *
     * @param e Não usado.
     */
    @Override
    public void keyReleased(KeyEvent e) {
    }
}
