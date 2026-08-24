/**
 * @author Yamel Sarmiento - Johan Pinilla
 */
public class Symbol extends Rectangle
{
    /** Size, in pixels, used to draw every symbol. */
    public static final int WIDTH = 50;
    public static final int HEIGHT = 70;

    private String color;   
    private int currentX;
    private int currentY;

    /**
     * Crea un nuevo symbol al color dado , el symbol no se expresa hasta que
     * el canvas es llamado
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
     * regresa el nombre exacto del color del CSS a este symbol
     */
    public String getColor()
    {
        return color;
    }

    /**
     * Parametriza el movimiento de las coordenadas del symbol usando shapes
     */
    void moveTo(int targetX, int targetY)
    {
        moveHorizontal(targetX - currentX);
        moveVertical(targetY - currentY);
        currentX = targetX;
        currentY = targetY;
    }

    /**
     * Muestra el resultado del canvas al momento de hacer la maquina visible 
     */
    public void draw()
    {
        makeVisible();
    }

    /**
     * Esconde el symbol al momento de que la maquina se hace invisible
     */
    public void erase()
    {
        makeInvisible();
    }
}