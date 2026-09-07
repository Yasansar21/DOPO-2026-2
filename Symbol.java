/**
 * @author Yamel Sarmiento - Johan Pinilla
 */
public class Symbol extends Triangle
{
    /** Tamaño usado para dibujar cada simbolo. */
    public static final int WIDTH = 50;
    public static final int HEIGHT = 70;

    private String color;
    private int currentX;
    private int currentY;

    /**
     * Crea un nuevo simbolo triangular con el color indicado.
     */
    public Symbol(String color)
    {
        super();

        this.color = color;
        this.currentX = 0;
        this.currentY = 0;

        changeSize(HEIGHT, WIDTH);
        changeColor(CssColors.toColor(color));
    }

    /**
     * Regresa el nombre exacto del color CSS de este simbolo.
     */
    public String getColor()
    {
        return color;
    }

    /**
     * Mueve el simbolo a las coordenadas indicadas.
     */
    void moveTo(int targetX, int targetY)
    {
        moveHorizontal(targetX - currentX);
        moveVertical(targetY - currentY);

        currentX = targetX;
        currentY = targetY;
    }

    /**
     * Muestra el simbolo.
     */
    public void draw()
    {
        makeVisible();
    }

    /**
     * Esconde el simbolo.
     */
    public void erase()
    {
        makeInvisible();
    }
}
