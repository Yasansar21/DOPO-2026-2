import java.util.ArrayList;

/**
 * Clase para la creacion de las ruedas necesarias para hacer la slot machine
 * @author Yamel Sarmiento - Johan Pinilla
 */
public class Wheel {
    private static final int WINDOW_LEFT = 60;
    private static final int WINDOW_TOP = 100;
    private static final int WINDOW_GAP = 20;
    private static final int CONTOUR_MARGIN = 6;
    private static final int CONTOUR_THICKNESS = 3;

    private ArrayList<Symbol> symbols;
    private int currentIndex;
    private int position;
    private boolean locked;

    /**
     * Crea una rueda vacia en relacion con la posicion dentro de la maquina.
     * @param position posicion 1-based de la rueda en la maquina
     */
    public Wheel(int position) {
        symbols = new ArrayList<>();
        currentIndex = 0;
        this.position = position;
        locked = false;
    }

    /**
     * Agrega un nuevo symbol al color dado.
     */
    public void addSymbol(String color) {
        addSymbol(symbols.size(), color);
    }

    /**
     * Mantiene los simbolos en el orden establecido.
     */
    void addSymbol(int index, String color) {
        int target = Math.max(0, Math.min(index, symbols.size()));
        symbols.add(target, new Symbol(color));
        if (symbols.size() == 1) {
            currentIndex = 0;
        }
    }

    /**
     * Quita el symbol del color dado.
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
     * @return true si se encontro, false si no existia
     */
    public boolean placeSymbol(String color) {
        int idx = indexOf(color);
        if (idx == -1) return false;

        currentIndex = idx;
        return true;
    }

    /**
     * Rota la rueda un numero de pasos.
     * @param steps numero de pasos a girar
     */
    public void spin(int steps) {
        if (!symbols.isEmpty()) {
            currentIndex = Math.floorMod(
                currentIndex + steps,
                symbols.size()
            );
        }
    }

    /** Fija la rueda. */
    public void lock() {
        locked = true;
    }

    /** Suelta la rueda. */
    public void unlock() {
        locked = false;
    }

    /**
     * Indica si la rueda esta fijada.
     * @return true si esta bloqueada
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * Devuelve el simbolo visible actualmente.
     * @return simbolo actual, null si esta vacia
     */
    public Symbol currentSymbol() {
        return symbols.isEmpty() ? null : symbols.get(currentIndex);
    }

    /**
     * Retorna la lista completa de simbolos.
     * @return lista de simbolos
     */
    public ArrayList<Symbol> getSymbols() {
        return symbols;
    }

    /**
     * @return posicion 1-based de la rueda
     */
    public int getPosition() {
        return position;
    }

    /**
     * Cambia la posicion de la rueda.
     */
    void setPosition(int position) {
        this.position = position;
    }

    /**
     * Redibuja la rueda: marco, simbolos y simbolo actual.
     */
    void drawWheel() {
        drawContour();

        for (Symbol s : symbols) {
            s.erase();
        }

        Symbol current = currentSymbol();

        if (current != null) {
            int x = WINDOW_LEFT
                + (position - 1) * (Symbol.WIDTH + WINDOW_GAP);

            current.moveTo(x, WINDOW_TOP);
            current.draw();
        }
    }

    /**
     * Dibuja el contorno negro que delimita la rueda.
     */
    private void drawContour() {
        Canvas canvas = Canvas.getCanvas();

        int x = WINDOW_LEFT
            + (position - 1) * (Symbol.WIDTH + WINDOW_GAP);
        int y = WINDOW_TOP;

        int left = x - CONTOUR_MARGIN;
        int top = y - CONTOUR_MARGIN;
        int width = Symbol.WIDTH + 2 * CONTOUR_MARGIN;
        int height = Symbol.HEIGHT + 2 * CONTOUR_MARGIN;

        String base = "wheel-contour-" + this;

        canvas.draw(
            base + "-top",
            "black",
            new java.awt.Rectangle(
                left,
                top,
                width,
                CONTOUR_THICKNESS
            )
        );

        canvas.draw(
            base + "-bottom",
            "black",
            new java.awt.Rectangle(
                left,
                top + height - CONTOUR_THICKNESS,
                width,
                CONTOUR_THICKNESS
            )
        );

        canvas.draw(
            base + "-left",
            "black",
            new java.awt.Rectangle(
                left,
                top,
                CONTOUR_THICKNESS,
                height
            )
        );

        canvas.draw(
            base + "-right",
            "black",
            new java.awt.Rectangle(
                left + width - CONTOUR_THICKNESS,
                top,
                CONTOUR_THICKNESS,
                height
            )
        );
    }

    /**
     * Esconde el simbolo actual y deja limpia la rueda.
     */
    void eraseCurrent() {
        Symbol current = currentSymbol();
        if (current != null) current.erase();
    }

    /**
     * Elimina visualmente toda la rueda, incluido su contorno.
     */
    void eraseWheel() {
        for (Symbol symbol : symbols) {
            symbol.erase();
        }

        if (!Canvas.exists()) {
            return;
        }

        Canvas canvas = Canvas.getExistingCanvas();
        String base = "wheel-contour-" + this;
        canvas.erase(base + "-top");
        canvas.erase(base + "-bottom");
        canvas.erase(base + "-left");
        canvas.erase(base + "-right");
    }

    private int indexOf(String color) {
        for (int i = 0; i < symbols.size(); i++) {
            if (symbols.get(i).getColor().equals(color)) {
                return i;
            }
        }
        return -1;
    }
}