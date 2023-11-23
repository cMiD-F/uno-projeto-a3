import java.awt.*;

/**
 * Uno
 *
 * Classe AntiUnoButton:
 * Um botão especial usado para emparelhar com o botão Uno para chamar
 * jogadores que não chamaram seu Uno.
 *
 * @autor Cauet Damasceno
 * @versão 2023
 */
public class AntiUnoButton extends BotaoUno implements GeralOverlayInterface {
   /**
      * Inicializa o AntiUnoButton.
      *
      * @param position Posição para colocar o botão Uno.
      */
    public AntiUnoButton(Posicao position) {
        super(position);
    }

    /**
      * Atualizações para determinar se há um jogador vulnerável a ser chamado para fora
      * não dizendo "UNO".
      * Eles são vulneráveis se tiverem apenas uma carta, não forem o último jogador
      * (porque é ele quem o controla),
      * e o jogador ainda não ligou para UNO.
      *
      * @param deltaTime Tempo desde a última atualização.
      */
    @Override
    public void update(int deltaTime) {
        isActive = false;
        for (Jogador player : InterfaceJogo.getCurrentGame().getAllPlayers()) {
            if (player != bottomPlayer && !player.isSafe() && player.getHand().size() == 1) {
                isActive = true;
            }
        }
    }

    /**
      * Desenha o botão AntiUno.
      *
      * @param g Referência ao objeto Graphics para renderização.
      */
    @Override
    public void paint(Graphics g) {
        if (!isActive)
            return;

        drawButtonBackground(g);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 50));
        int strWidth = g.getFontMetrics().stringWidth("!");
        g.drawString("!", bounds.position.x + bounds.width / 2 - strWidth / 2 - 2,
                bounds.position.y + bounds.height / 2 + 2 + 10 + 10);
        g.setColor(new Color(226, 173, 67));
        g.drawString("!", bounds.position.x + bounds.width / 2 - strWidth / 2,
                bounds.position.y + bounds.height / 2 + 10 + 10);
    }

    /**
      * Quando o botão está ativo significa que há pelo menos um jogador que pode ser
      *gritou.
      * Este método verifica o botão que está sendo pressionado e determina qual jogador
      * precisa ser chamado.
      *
      * @param mousePosition Posição do cursor do mouse durante o pressionamento.
      * @param isLeft Se verdadeiro, o botão do mouse está para a esquerda, caso contrário, está para a direita.
      */
    @Override
    public void handleMousePress(Posicao mousePosition, boolean isLeft) {
        if (isActive && bounds.isPositionInside(mousePosition)) {
            for (Jogador player : InterfaceJogo.getCurrentGame().getAllPlayers()) {
                if (player != bottomPlayer && !player.isSafe() && player.getHand().size() == 1) {
                    InterfaceJogo.getCurrentGame().applyAntiUno(player.getPlayerID());
                }
            }
        }
    }
}
