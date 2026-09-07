import java.util.ArrayList;

/**
 * Clase para la creacion de las ruedas necesarias para hacer la slot machine
 * @author Yamel Sarmiento - Johan Pinilla
 */
public class Wheel {
    private static final int WINDOW_LEFT = 60;
    private static final int WINDOW_TOP = 100;
    private static final int WINDOW_GAP = 30;
    private static final int FRAME_MARGIN = 15;
    private static final int FRAME_WIDTH = Symbol.WIDTH + FRAME_MARGIN * 2;
    private static final int FRAME_HEIGHT = Symbol.HEIGHT + FRAME_MARGIN * 2;

    private ArrayList<Symbol> symbols;
    private int currentIndex;
    private int position;
    private boolean locked;        // ← NUEVO Ciclo 2
    private final Object frame;
    /**
     * Crea una rueda vacia en relacion con la posicion dentro de la maquina.
     * @param position posicion 1-based de la rueda en la maquina
     */
    public Wheel(int position) {
        symbols = new ArrayList<>();
        currentIndex = 0;
        this.position = position;
        locked = false;            // ← NUEVO Ciclo 2
        frame = new Object();
    }

    /**
     * Agrega un nuevo symbol al color dado en cada una de las ruedas.
     */
    public void addSymbol(String color) {
        addSymbol(symbols.size(), color);
    }

    /**
     * Mantener cada uno de los symbols en secuencia en el mismo orden
     * establecido.
     */
    void addSymbol(int index, String color) {
        int target = Math.max(0, Math.min(index, symbols.size()));
        symbols.add(target, new Symbol(color));
        if (symbols.size() == 1) {
            currentIndex = 0;
        }
    }

    /**
     * Quita el symbol del color dado, si se presenta se elimina del canvas.
     * @param color color del simbolo a eliminar
     * @return true si se elimino, false si no existia
     */
    public boolean delSymbol(String color) {
        int idx = indexOf(color);
        if (idx == -1) return false;
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
     * Hace que el symbol del color dado sea el visible en la rueda.
     * @param color color del simbolo a mostrar
     * @return true si se encontro y se posiciono, false si no existia
     */
    public boolean placeSymbol(String color) {
        int idx = indexOf(color);
        if (idx == -1) return false;
        currentIndex = idx;
        return true;
    }

    /**
     * Rota la rueda un numero de pasos. Soporta pasos negativos.
     * Usa floorMod para garantizar indice positivo siempre.
     * @param steps numero de pasos a girar
     */
    public void spin(int steps) {
        if (!symbols.isEmpty()) {
            currentIndex = Math.floorMod(currentIndex + steps, symbols.size());
        }
    }

    /**
     * Fija la rueda para que no pueda girar.
     */
    public void lock() {                         // Ciclo 2
        locked = true;
    }

    /**
     * Suelta la rueda para que pueda girar nuevamente.
     */
    public void unlock() {                       // Ciclo 2
        locked = false;
    }

    /**
     * Indica si la rueda esta fijada.
     * @return true si la rueda esta bloqueada
     */
    public boolean isLocked() {                  // Ciclo 2
        return locked;
    }

    /**
     * Devuelve el symbol visible actualmente.
     * @return symbol actual, null si la rueda esta vacia
     */
    public Symbol currentSymbol() {
        return symbols.isEmpty() ? null : symbols.get(currentIndex);
    }

    /**
     * Retorna la lista completa de simbolos de la rueda.
     * @return lista de simbolos
     */
    public ArrayList<Symbol> getSymbols() {
        return symbols;
    }

    /**
     * @return posicion 1-based de la rueda dentro de la maquina
     */
    public int getPosition() {
        return position;
    }

    void setPosition(int position) {
        this.position = position;
    }

        /**
         * Redibuja la rueda: esconde todos los simbolos y muestra solo el actual.
         */
    void drawWheel() {
        for (Symbol s : symbols) {
            s.erase();
        }
    
        int x = WINDOW_LEFT
            + (position - 1) * (Symbol.WIDTH + WINDOW_GAP);
    
        int frameX = x - FRAME_MARGIN;
        int frameY = WINDOW_TOP - FRAME_MARGIN;
    
        Canvas.getCanvas().drawOutline(
            frame,
            java.awt.Color.BLACK,
            new java.awt.Rectangle(
                frameX,
                frameY,
                FRAME_WIDTH,
                FRAME_HEIGHT
            )
        );
    
        Symbol current = currentSymbol();
    
        if (current != null) {
            current.moveTo(x, WINDOW_TOP);
            current.draw();
        }
    }

    /**
     * Elimina visualmente toda la rueda.
     */
    void eraseWheel() {
        for (Symbol s : symbols) {
            s.erase();
        }
    
        Canvas.getCanvas().erase(frame);
    }

    private int indexOf(String color) {
        for (int i = 0; i < symbols.size(); i++) {
            if (symbols.get(i).getColor().equals(color)) return i;
        }
        return -1;
    }
}