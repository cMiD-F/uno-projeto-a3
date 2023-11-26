/**
 * Uno
 *
 * Classe Posicao:
 * Usado para representar uma única posição x,y.
 *
 * @autor Cauet Damasceno
 * @versão 2023
 */
public class Posicao {
    /**
     * Vetor unitário descendente.
     */
    public static final Posicao DOWN = new Posicao(0, 1);
    /**
     * Vetor unitário móvel ascendente.
     */
    public static final Posicao UP = new Posicao(0, -1);
    /**
     * Vetor unitário móvel à esquerda.
     */
    public static final Posicao LEFT = new Posicao(-1, 0);
    /**
     * Vetor unitário móvel à direita.
     */
    public static final Posicao RIGHT = new Posicao(1, 0);
    /**
     * Vetor unitário zero.
     */
    public static final Posicao ZERO = new Posicao(0, 0);

    /**
     * Coordenada X.
     */
    public int x;
    /**
     * Coordenada Y.
     */
    public int y;

    /**
     * Define o valor da Posição.
     *
     * @param x coordenada X.
     * @param e coordenada Y.
     */
    public Posicao(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Copie o construtor para criar uma nova Posição usando os valores de outra.
     *
     * @param positionToCopy Posição da qual copiar valores.
     */
    public Posicao(Posicao positionToCopy) {
        this.x = positionToCopy.x;
        this.y = positionToCopy.y;
    }

    /**
     * Define a posição para as coordenadas x e y especificadas.
     *
     * @param x coordenada X.
     * @param e coordenada Y.
     */
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Atualiza esta posição adicionando os valores de otherPosition.
     *
     * @param otherPosition Outra posição a ser adicionada a esta.
     */
    public void add(Posicao otherPosition) {
        this.x += otherPosition.x;
        this.y += otherPosition.y;
    }

    /**
     * Calcule a distância desta posição até a outra posição.
     *
     * @param otherPosition Posição para verificar a distância.
     * @return Distância entre esta posição e a outra posição.
     */
    public double distanceTo(Posicao otherPosition) {
        return Math.sqrt(Math.pow(x - otherPosition.x, 2) + Math.pow(y - otherPosition.y, 2));
    }

    /**
     * Multiplica ambos os componentes da posição por um valor.
     *
     * @param amount Quantidade pela qual multiplicar o vetor.
     */
    public void multiply(int amount) {
        x *= amount;
        y *= amount;
    }

    /**
     * Atualiza esta posição subtraindo os valores de otherPosition.
     *
     * @param otherPosition Outra posição a ser adicionada a esta.
     */
    public void subtract(Posicao otherPosition) {
        this.x -= otherPosition.x;
        this.y -= otherPosition.y;
    }

    /**
     * Compara o objeto Position com outro objeto.
     * Qualquer objeto que não seja Posição retornará falso. Caso contrário, compare
     * x e y para
     * igualdade.
     *
     * @param o Objeto para comparar esta Posição.
     * @return Verdadeiro se o objeto o for igual a esta posição para x e y.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Posicao position = (Posicao) o;
        return x == position.x && y == position.y;
    }

    /**
     * Obtém uma versão em string da Posição.
     *
     * @return Uma string no formato (x, y)
     */
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
