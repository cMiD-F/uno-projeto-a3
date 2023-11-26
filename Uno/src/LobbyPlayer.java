import javax.swing.*;
import java.awt.*;

/**
 * Uno
 *
 * Classe LobbyPlayer:
 * Define um jogador no menu Lobby com funções para modificar suas configurações
 * prontas antes do início do jogo.
 *
 * @autor Cauet Damasceno
 * @versão 2023
 */
public class LobbyPlayer extends Retangulo {
    /**
     * O nome mostrado para o jogador.
     */
    private String playerName;
    /**
     * O tipo de player (UnoJogador, AIJogador ou NetworkPlayer).
     */
    private final Jogador.PlayerType playerType;
    /**
     * A estratégia a ser utilizada para o tipo AIJogador.
     */
    private AIJogador.AIStrategy aiStrategy;
    /**
     * Uma String mostrando a versão em texto da estratégia.
     */
    private String strategyStr;
    /**
     * Visível e incluído na coleção de jogadores quando verdadeiro.
     */
    private boolean isEnabled;
    /**
     * Verdadeiro quando o mouse está sobre o player.
     */
    private boolean isHovered;
    /**
     * String representando o tipo de jogador.
     */
    private final String playerTypeStr;

    /**
     * Inicializa o objeto pronto para mostrar informações sobre o jogador.
     *
     * @param playerName O nome mostrado para o jogador.
     * @param playerType O tipo de jogador (UnoJogador, AIJogador ou
     *                   RedePlayer).
     * @param limites    Região para interagir com este objeto de jogador no menu.
     */
    public LobbyPlayer(String playerName, Jogador.PlayerType playerType, Retangulo bounds) {
        super(bounds.position, bounds.width, bounds.height);
        this.playerName = playerName;
        this.playerType = playerType;
        aiStrategy = AIJogador.AIStrategy.Aleatorio;
        strategyStr = "Estratégia: " + aiStrategy.toString();
        isEnabled = true;
        playerTypeStr = playerType == Jogador.PlayerType.UnoJogador ? "Você:" : "Player IA:";
    }

    /**
     * Muda o nome do jogador.
     *
     * @param playerName Nome para o qual alterar o player.
     */
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
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
     * Obtém o tipo de jogador. (UnoJogador, AIJogador ou NetworkPlayer).
     *
     * @return O tipo de jogador.
     */
    public Jogador.PlayerType getPlayerType() {
        return playerType;
    }

    /**
     * Define o estado habilitado do player.
     *
     * @param isEnabled Quando verdadeiro, o jogador é incluído na lista de
     *                  jogadores para
     *                  o jogo.
     */
    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    /**
     * Obtém o status atual ativado.
     *
     * @return Quando verdadeiro, o jogador deve estar visível e incluído como
     *         jogador.
     */
    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * Obtém a estratégia deste jogador. Isso só é relevante para os tipos
     * AIJogador.
     *
     * @return A estratégia a ser usada para a IA.
     */
    public AIJogador.AIStrategy getAIStrategy() {
        return aiStrategy;
    }

    /**
     * Lida com o clique para iterar a estratégia de IA ou escolher um novo nome
     * para o jogador.
     */
    public void handleClick() {
        if (playerType == Jogador.PlayerType.AIJogador) {
            iterateStrategy();
        } else {
            chooseNewName();
        }
    }

    /**
     * Fornece uma entrada JOptionPane para inserir uma string de até 12 caracteres.
     * A String corta os espaços em branco antes de avaliar e limita o comprimento
     * máximo
     * com 12 caracteres. Não fará nada se não houver pelo menos 1 válido
     * personagem.
     */
    private void chooseNewName() {
        String newName = JOptionPane.showInputDialog(null, "Digite um nome com até 12 caracteres!");
        if (newName != null) {
            newName = newName.trim();
            if (newName.length() > 12) {
                newName = newName.substring(0, 12);
            }
            if (newName.length() > 0) {
                setPlayerName(newName);
            }
        }
    }

    /**
     * Itera através da lista de estratégias de IA para a próxima.
     */
    private void iterateStrategy() {
        switch (aiStrategy) {
            case Aleatorio -> aiStrategy = AIJogador.AIStrategy.Ofensivo;
            case Ofensivo -> aiStrategy = AIJogador.AIStrategy.Defensivo;
            case Defensivo -> aiStrategy = AIJogador.AIStrategy.Caotico;
            case Caotico -> aiStrategy = AIJogador.AIStrategy.Aleatorio;
        }
        strategyStr = "Estratégia: " + aiStrategy.toString();
    }

    /**
     * Não faz nada se não estiver habilitado. Desenha o conteúdo mostrando a
     * identidade deste jogador
     * Informação.
     *
     * @param g Referência ao objeto Graphics para renderização.
     */
    public void paint(Graphics g) {
        if (!isEnabled)
            return;

        if (isHovered) {
            g.setColor(new Color(115, 156, 58, 204));
        } else {
            g.setColor(new Color(118, 94, 57, 204));
        }
        g.fillRect(position.x, position.y, width, height);
        g.setColor(Color.BLACK);
        g.drawRect(position.x, position.y, width, height);

        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString(playerTypeStr, position.x + 20, position.y + 50);
        g.drawString(playerName, position.x + 120, position.y + 50);

        if (playerType == Jogador.PlayerType.AIJogador) {
            g.drawString(strategyStr, position.x + 300, position.y + 50);
            g.drawString("(Estratégias de clique para pedalar)", position.x + 300, position.y + 75);
        } else {
            g.drawString("(Clique para mudar seu nome)", position.x + 300, position.y + 50);
        }
    }

    /**
     * Atualiza o estado pairado do objeto de botão com base em onde o mouse está.
     *
     * @param mousePosition Posição do mouse.
     */
    public void updateHoverState(Posicao mousePosition) {
        isHovered = isPositionInside(mousePosition);
    }
}
