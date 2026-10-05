import java.awt.Shape;
import java.awt.geom.Rectangle2D;

/**
 * Simbolo timido: cada vez que es seleccionado en su rueda alterna su estado
 * de visible a invisible (y viceversa). Mientras esta invisible no se dibuja,
 * pero sigue contando con su color para la configuracion y el jackpot.
 *
 * Se distingue por una "rendija" horizontal (un ojo cerrado) sobre el triangulo.
 *
 * @author Yamel Sarmiento - Johan Pinilla
 */
public class ShySymbol extends Symbol
{
    /**
     * Crea un simbolo shy, inicialmente visible.
     *
     * @param color color CSS del simbolo
     */
    public ShySymbol(String color)
    {
        super(color);
    }

    @Override
    public String getType()
    {
        return "shy";
    }

    /**
     * Al ser seleccionado alterna visible / invisible.
     */
    @Override
    public void onSelected()
    {
        setConcealed(!isConcealed());
    }

    @Override
    public Symbol copy()
    {
        return new ShySymbol(getColor());
    }

    @Override
    protected Shape markerShape(int cx, int cy, int size)
    {
        int w = Math.max(10, size * 2);
        int h = Math.max(5, size / 2);
        return new Rectangle2D.Double(cx - w / 2.0, cy - h / 2.0, w, h);
    }
}
