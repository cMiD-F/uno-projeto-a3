import java.awt.*;

/**
 * Uno
 *
 * Classe WndInterface :
 * Define uma abstração genérica a ser usada para múltiplas interfaces.
 *
 * @autor Cauet Damasceno
 * @versão 2023
 */
public abstract class WndInterface {
    /**
     * Estado se o objeto está habilitado para que possa ser usado para
     * gerenciamento
     * atualizações.
     */
    private boolean isEnabled;
    /**
     * bounds desta interface.
     */
    protected final Retangulo bounds;

    /**
     * Inicialize a interface com limites e habilite-a.
     *
     * @param bounds Limites da interface.
     */
    public WndInterface(Retangulo bounds) {
        isEnabled = true;
        this.bounds = bounds;
    }

    /**
     * Atualize os elementos da interface.
     *
     * @param deltaTime Tempo desde a última atualização.
     */
    public abstract void update(int deltaTime);

    /**
     * Desenhe todos os elementos da interface.
     *
     * @param g Referência ao objeto Graphics para renderização.
     */
    public abstract void paint(Graphics g);

    /**
     * Lidar com atualizações relacionadas ao pressionamento do mouse na posição
     * especificada.
     *
     * @param mousePosition Posição do cursor do mouse durante o pressionamento.
     * @param isLeft        Se verdadeiro, o botão do mouse está para a esquerda,
     *                      caso contrário, está para a direita.
     */
    public void handleMousePress(Posicao mousePosition, boolean isLeft) {
    }

    /**
     * Lidar com atualizações relacionadas ao mouse sendo movido.
     *
     * @param mousePosition Posição do mouse durante este movimento.
     */
    public void handleMouseMove(Posicao mousePosition) {
    }

    /**
     * Altere o estado habilitado deste objeto.
     *
     * @param enabled Novo estado para definir o estado ativado/desativado deste
     *                objeto.
     */
    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

    /**
     * Obtenha o estado atual ativado do objeto.
     *
     * @return True se o objeto estiver habilitado.
     */
    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * Lida com a entrada de teclas de uma ação do teclado.
     *
     * @param keyCode A tecla que foi pressionada.
     */
    public void handleInput(int keyCode) {
    }
}
