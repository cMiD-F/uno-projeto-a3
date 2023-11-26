/**
 * Uno
 *
 * Classe Retangulo:
 * Define um retângulo simples com uma posição para o canto superior esquerdo,
 * e uma largura/altura para representar o tamanho do retângulo.
 *
 * @autor Cauet Damasceno
 * @versão 2023
 */
public class Retangulo {
    /**
     * O canto superior esquerdo do retângulo.
     */
    protected final Posicao position;
    /**
     * Largura do retângulo.
     */
    protected final int width;
    /**
     * Altura do retângulo.
     */
    protected final int height;

    /**
     * Cria o novo retângulo com as propriedades fornecidas.
     *
     * @param position O canto superior esquerdo do retângulo.
     * @param width    Largura do retângulo.
     * @param height   Altura do retângulo.
     */
    public Retangulo(Posicao position, int width, int height) {
        this.position = position;
        this.width = width;
        this.height = height;
    }

    /**
     * @param x      X coordenada do canto superior esquerdo.
     * @param y      Coordenada Y do canto superior esquerdo.
     * @param width  Largura do retângulo.
     * @param height Altura do retângulo.
     */
    public Retangulo(int x, int y, int width, int height) {
        this(new Posicao(x, y), width, height);
    }

    /**
     * Obtém a altura do retângulo.
     *
     * @return Altura do retângulo.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Obtém a largura do retângulo.
     *
     * @return Largura do retângulo.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Obtém o canto superior esquerdo do retângulo.
     *
     * @return Canto superior esquerdo do retângulo.
     */
    public Posicao getPosition() {
        return position;
    }

    /**
     * Obtém o centro do retângulo com base nos valores armazenados.
     *
     * @return Coordenadas centrais do retângulo.
     */
    public Posicao getCentre() {
        return new Posicao(position.x + width / 2, position.y + height / 2);
    }

    /**
     * Testa se targetPosition está dentro do Rectangle.
     *
     * @param targetPosition Posição para testar se está dentro do Retângulo.
     * @return True se targetPosition estiver dentro deste retângulo.
     */
    public boolean isPositionInside(Posicao targetPosition) {
        return targetPosition.x >= position.x && targetPosition.y >= position.y
                && targetPosition.x < position.x + width && targetPosition.y < position.y + height;
    }

    /**
     * Testa se o retângulo está cruzando com algum outro retângulo.
     *
     * @param otherRectangle Outra posição para comparar em uma colisão.
     * @return Verdadeiro se este retângulo estiver cruzando o outroRetângulo.
     */
    public boolean isIntersecting(Retangulo otherRectangle) {
        // quebra se alguma das afirmações a seguir for verdadeira porque significa que
        // elas não se cruzam
        if (position.y + height < otherRectangle.position.y)
            return false;
        if (position.y > otherRectangle.position.y + otherRectangle.height)
            return false;
        if (position.x + width < otherRectangle.position.x)
            return false;
        if (position.x > otherRectangle.position.x + otherRectangle.width)
            return false;

        // as caixas delimitadoras se cruzam
        return true;
    }
}
