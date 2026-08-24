import java.util.ArrayList;

/**
 * Clase para la creacion de las ruedas necesarias para hacer la slot machine
 * @author Yamel Sarmiento - Johan Pinilla
 */
public class Wheel
{
    private static final int WINDOW_LEFT = 60;
    private static final int WINDOW_TOP = 100;
    private static final int WINDOW_GAP = 20;

    private ArrayList<Symbol> symbols;
    private int currentIndex;  
    private int position;      

    /**
     * Crea una rueda vacia en relacion con la posicion dentro de la maquina
     */
    public Wheel(int position)
    {
        symbols = new ArrayList<>();
        currentIndex = 0;
        this.position = position;
    }

    /**
     * Agrega un nuevo symbol al color dado en cada una de las ruedas
     */
    public void addSymbol(String color)
    {
        addSymbol(symbols.size(), color);
    }

    /**
     * Mantener cada uno de los symbols en secuencia en el mismo orden 
     * establecido
     */
    void addSymbol(int index, String color)
    {
        int target = Math.max(0, Math.min(index, symbols.size()));
        symbols.add(target, new Symbol(color));
        if (symbols.size() == 1) {
            currentIndex = 0;
        }
    }

    /**
     * Quita el symbol del color dado si se presenta se elimina del canvas
     */
    public boolean delSymbol(String color)
    {
        int idx = indexOf(color);
        if (idx == -1) {
            return false;
        }
        Symbol removed = symbols.remove(idx);
        removed.erase();
        if (symbols.isEmpty()) {
            currentIndex = 0;
        } else if (currentIndex >= symbols.size()) {
            currentIndex = symbols.size() - 1;
        } else if (idx < currentIndex) {
            currentIndex--;
        }
        return true;
    }

    /**
     * Hace que el symbol del color dado sea el indicado a mostras en la rueda 
     * asignada
     */
    public boolean placeSymbol(String color)
    {
        int idx = indexOf(color);
        if (idx == -1) {
            return false;
        }
        currentIndex = idx;
        return true;
    }

    /**
     * Rota cada una de las ruedas en un numero asignado de pasos
     */
    public void spin(int steps)
    {
        if (!symbols.isEmpty()) {
            currentIndex = Math.floorMod(currentIndex + steps, symbols.size());
        }
    }

    /**
     * devuelve el symbol que realmente se muestra , en caso de ser null se debe
     * a que la rueda no tiene symbol de por si 
     */
    public Symbol currentSymbol()
    {
        return symbols.isEmpty() ? null : symbols.get(currentIndex);
    }

    /**
     * Se puedever la lista completa ordenada de la lista de simbolos dentro de
     * la rueda
     */
    public ArrayList<Symbol> getSymbols()
    {
        return symbols;
    }

    /**
     * @return this wheel's 1-based position inside the machine
     */
    public int getPosition()
    {
        return position;
    }

    void setPosition(int position)
    {
        this.position = position;
    }

    /**
     * Re dibuja la rueda , esconde cada symbol y solo muestra la actual, 
     * verifica si la maquina es visible
     */
    void drawWheel()
    {
        for (Symbol s : symbols) {
            s.erase();
        }
        Symbol current = currentSymbol();
        if (current != null) {
            int x = WINDOW_LEFT + (position - 1) * (Symbol.WIDTH + WINDOW_GAP);
            current.moveTo(x, WINDOW_TOP);
            current.draw();
        }
    }

    /**
     * Esconde cualquier symbol creado en el momento en que la maquina se
     * vuelve invisible
     */
    void eraseCurrent()
    {
        Symbol current = currentSymbol();
        if (current != null) {
            current.erase();
        }
    }

    private int indexOf(String color)
    {
        for (int i = 0; i < symbols.size(); i++) {
            if (symbols.get(i).getColor().equals(color)) {
                return i;
            }
        }
        return -1;
    }
}