import java.awt.Shape;
import java.awt.geom.Ellipse2D;

/**
 * Simbolo efimero: en cada giro de su rueda va decrementando su tamano hasta
 * quedar convertido en un punto.
 *
 * Se distingue por un circulo (como un reloj) dibujado sobre el triangulo,
 * mientras sea lo bastante grande para verse.
 *
 * @author Yamel Sarmiento - Johan Pinilla
 */
public class EphemeralSymbol extends Symbol
{
    /** Numero de giros necesarios para llegar a ser un punto. */
    public static final int STEPS_TO_POINT = 5;

    private int spinsDone;

    /**
     * Crea un simbolo efimero a tamano completo.
     *
     * @param color color CSS del simbolo
     */
    public EphemeralSymbol(String color)
    {
        super(color);
        spinsDone = 0;
    }

    @Override
    public String getType()
    {
        return "ephemeral";
    }

    /**
     * Cada giro de la rueda encoge el simbolo un paso. Al llegar al paso
     * final se queda como un punto y ya no cambia mas.
     */
    @Override
    public void onSpin()
    {
        if (spinsDone < STEPS_TO_POINT) {
            spinsDone++;
        }
        setScale(1.0 - (double) spinsDone / STEPS_TO_POINT);
    }

    @Override
    public Symbol copy()
    {
        return new EphemeralSymbol(getColor());
    }

    @Override
    protected Shape markerShape(int cx, int cy, int size)
    {
        int d = Math.max(6, size);
        return new Ellipse2D.Double(cx - d / 2.0, cy - d / 2.0, d, d);
    }
}