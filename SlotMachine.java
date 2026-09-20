import java.util.ArrayList;
import java.util.Random;
import javax.swing.JOptionPane;

/**
 * Clase principal del slot machine donde se busca que el proyecto funcione
 * de manera correcta.
 *
 * @author Yamel Sarmiento - Johan Pinilla
 */
public class SlotMachine
{
    private ArrayList<Wheel> wheels;
    private boolean visible;
    private boolean lastOk;
    private Circle jackpotLight;
    private Random random;
    private Object body;

    private static final int BODY_LEFT = 35;
    private static final int BODY_TOP = 75;
    private static final int BODY_HEIGHT = 120;

    private static final String[] INITIAL_COLORS = {
        "red", "blue", "green", "yellow", "magenta",
        "orange", "purple", "cyan", "tomato", "gold",
        "coral", "crimson", "brown", "black", "white",
        "gray", "silver", "pink", "violet", "indigo",
        "navy", "teal", "lime", "olive", "maroon",
        "chocolate", "salmon", "khaki", "turquoise", "aquamarine",
        "blueviolet", "chartreuse", "darkblue", "darkcyan", "darkgreen",
        "darkmagenta", "darkorange", "darkred", "deeppink", "deepskyblue",
        "forestgreen", "hotpink", "lightblue", "lightcoral", "lightgreen",
        "lightsalmon", "mediumblue", "orchid", "royalblue", "seagreen"
    };

    /**
     * Creacion de una nueva slot machine que inicia de manera invisible,
     * ninguna rueda.
     */
    public SlotMachine()
    {
        wheels = new ArrayList<>();
        visible = false;
        lastOk = true;
        random = new Random();
        body = new Object();
    }

    /**
     * Creacion del nuevo Slotmachine invisible, con n ruedas y n simbolos.
     *
     * @param n cantidad de ruedas y simbolos que se crearan
     */
    public SlotMachine(int n)
    {
        this();

        if (n < 1 || n > INITIAL_COLORS.length) {
            System.out.println(
                "La cantidad de ruedas debe estar entre 1 y "
                + INITIAL_COLORS.length + "."
            );
            return;
        }

        for (int i = 1; i <= n; i++) {
            addWheel(i);
        }

        for (int i = 1; i <= n; i++) {
            addSymbol(i, INITIAL_COLORS[i - 1]);
        }
    }

    /**
     * Crea una nueva rueda en la posicion indicada.
     *
     * @param pos posicion donde se agrega la rueda
     */
    public void addWheel(int pos)
    {
        int target = clamp(pos, 1, wheels.size() + 1);

        Wheel wheel = new Wheel(target);

        copySharedSymbolsInto(wheel);

        wheels.add(target - 1, wheel);

        renumberWheels();

        succeed();
    }

    /**
     * Remueve la rueda asignada a la posicion dada.
     *
     * @param pos posicion de la rueda a eliminar
     */
    public void delWheel(int pos)
    {
        if (wheels.isEmpty()) {
            fail("No hay ruedas en la máquina.");
            return;
        }

        int target = clamp(pos, 1, wheels.size());

        Wheel removed = wheels.remove(target - 1);

        removed.eraseWheel();

        renumberWheels();

        succeed();
    }

    /**
     * Intercambia dos ruedas de posicion dentro de la maquina.
     *
     * @param wheel1 posicion de la primera rueda
     * @param wheel2 posicion de la segunda rueda
     */
    public void swap(int wheel1, int wheel2)
    {
        if (wheels.isEmpty()) {
            fail("La máquina no tiene ruedas.");
            return;
        }

        int pos1 = clamp(wheel1, 1, wheels.size());
        int pos2 = clamp(wheel2, 1, wheels.size());

        if (pos1 == pos2) {
            fail("Las posiciones deben ser diferentes.");
            return;
        }

        Wheel w1 = wheels.get(pos1 - 1);
        Wheel w2 = wheels.get(pos2 - 1);

        /*
         * CORRECCION:
         * isLocked(), no islocked()
         */
        if (w1.isLocked() || w2.isLocked()) {
            return;
        }

        wheels.set(pos1 - 1, w2);
        wheels.set(pos2 - 1, w1);

        renumberWheels();

        succeed();
    }

    /**
     * Fija una rueda para que no pueda girar.
     *
     * @param wheel posicion de la rueda a fijar
     */
    public void lock(int wheel)
    {
        Wheel target = wheelAt(wheel);

        if (target == null) {
            fail("La máquina no tiene ruedas.");
            return;
        }

        if (target.isLocked()) {
            fail("La rueda " + target.getPosition()
                + " ya está fijada.");
            return;
        }

        target.lock();

        succeed();
    }

    /**
     * Suelta una rueda fijada.
     *
     * @param wheel posicion de la rueda a soltar
     */
    public void unlock(int wheel)
    {
        Wheel target = wheelAt(wheel);

        if (target == null) {
            fail("La máquina no tiene ruedas.");
            return;
        }

        if (!target.isLocked()) {
            fail("La rueda " + target.getPosition()
                + " no está fijada.");
            return;
        }

        target.unlock();

        succeed();
    }

    /**
     * Agrega un nuevo color de simbolo a todas las ruedas.
     *
     * @param pos posicion del simbolo
     * @param color color CSS valido
     */
    public void addSymbol(int pos, String color)
    {
        if (!CssColors.isValid(color)) {
            fail("\"" + color + "\" no es un color CSS válido.");
            return;
        }

        if (containsColor(color)) {
            fail("Ya existe un símbolo de color " + color + ".");
            return;
        }

        int index = clamp(
            pos,
            1,
            sharedSymbolCount() + 1
        ) - 1;

        for (Wheel wheel : wheels) {
            wheel.addSymbol(index, color);
        }

        succeed();
    }

    /**
     * Quita un simbolo de color de cada rueda.
     *
     * @param color color del simbolo a eliminar
     */
    public void delSymbol(String color)
    {
        if (!containsColor(color)) {
            fail(
                "No existe ningún símbolo de color "
                + color + "."
            );
            return;
        }

        for (Wheel wheel : wheels) {
            wheel.delSymbol(color);
        }

        succeed();
    }

    /**
     * Permite colocar directamente un simbolo en una rueda.
     *
     * @param wheel posicion de la rueda
     * @param symbol color del simbolo
     */
    public void placeSymbol(int wheel, String symbol)
    {
        Wheel target = wheelAt(wheel);

        if (target == null) {
            fail("La máquina no tiene ruedas.");
            return;
        }

        if (!target.placeSymbol(symbol)) {
            fail(
                "La rueda " + target.getPosition()
                + " no tiene un símbolo " + symbol + "."
            );
            return;
        }

        succeed();
    }

    /**
     * Gira una rueda aleatoriamente.
     *
     * @param wheel posicion de la rueda
     */
    public void spin(int wheel)
    {
        Wheel target = wheelAt(wheel);

        if (target == null) {
            fail("La máquina no tiene ruedas.");
            return;
        }

        if (target.getSymbols().isEmpty()) {
            fail(
                "La rueda " + target.getPosition()
                + " no tiene símbolos."
            );
            return;
        }

        target.spin(randomSteps(target));

        succeed();
    }

    /**
     * Gira una rueda un numero especifico de pasos.
     *
     * @param wheel posicion de la rueda
     * @param steps numero de pasos
     */
    public void spin(int wheel, int steps)
    {
        Wheel target = wheelAt(wheel);

        if (target == null) {
            fail("La máquina no tiene ruedas.");
            return;
        }

        if (target.getSymbols().isEmpty()) {
            fail(
                "La rueda " + target.getPosition()
                + " no tiene símbolos."
            );
            return;
        }

        if (target.isLocked()) {
            fail(
                "La rueda " + target.getPosition()
                + " está fijada."
            );
            return;
        }

        int direction = (steps >= 0) ? 1 : -1;
        int totalSteps = Math.abs(steps);

        if (visible) {
            for (int i = 0; i < totalSteps; i++) {
                target.spin(direction);
                target.drawWheel();
                Canvas.getCanvas().wait(300);
            }
        }
        else {
            target.spin(steps);
        }

        succeed();
    }

    /**
     * Deja la maquina en una configuracion dada.
     *
     * @param setSymbols arreglo con el color deseado
     */
    public void spin(String[] setSymbols)
    {
        if (setSymbols.length != wheels.size()) {
            fail(
                "La configuración no coincide con el número "
                + "de ruedas."
            );
            return;
        }

        boolean anyFailed = false;

        for (int i = 0; i < wheels.size(); i++) {

            Wheel w = wheels.get(i);

            if (w.isLocked()) {
                continue;
            }

            if (!w.placeSymbol(setSymbols[i])) {
                anyFailed = true;
            }
        }

        if (anyFailed) {
            fail(
                "Algún símbolo de la configuración "
                + "no existe en su rueda."
            );
            return;
        }

        succeed();
    }

    /**
     * Gira todas las ruedas de forma aleatoria.
     */
    public void spin()
    {
        if (wheels.isEmpty()) {
            fail("La máquina no tiene ruedas.");
            return;
        }

        for (Wheel wheel : wheels) {

            if (!wheel.getSymbols().isEmpty()
                && !wheel.isLocked()) {

                wheel.spin(randomSteps(wheel));
            }
        }

        succeed();
    }

    /**
     * Devuelve los colores de los simbolos.
     *
     * @return arreglo con los colores
     */
    public String[] symbols()
    {
        lastOk = true;

        return symbolsQuiet();
    }

    /**
     * Retorna cuantos colores distintos se muestran.
     *
     * @return numero de colores distintos
     */
    public int distinctSymbols()
    {
        lastOk = true;

        return distinctSymbolsQuiet();
    }

    /**
     * Muestra los colores visibles.
     *
     * @return arreglo con el color visible
     */
    public String[] configuration()
    {
        lastOk = true;

        String[] result = new String[wheels.size()];

        for (int i = 0; i < wheels.size(); i++) {

            Symbol current = wheels.get(i).currentSymbol();

            result[i] =
                (current == null)
                ? null
                : current.getColor();
        }

        return result;
    }

    /**
     * Retorna true si todas las ruedas muestran el mismo color.
     *
     * @return true si hay jackpot
     */
    public boolean isJackpot()
    {
        lastOk = true;

        return isJackpotQuiet();
    }

    /**
     * Muestra la maquina y las ruedas.
     */
    public void makeVisible()
    {
        this.visible = true;

        Canvas canvas = Canvas.getCanvas();

        drawBody();

        if (jackpotLight == null) {
            jackpotLight = new Circle();
            jackpotLight.moveHorizontal(30);
            jackpotLight.moveVertical(0);
        }

        jackpotLight.makeVisible();

        for (Wheel w : wheels) {
            w.drawWheel();
        }

        canvas.setSpinAction(new Runnable(){
            public void run(){
                spin();
            }
        });

        updateJackpotLight();
    }

    /**
     * Esconde la maquina.
     */
    public void makeInvisible()
    {
        if (visible && Canvas.exists()) {
            for (Wheel wheel : wheels) {
                wheel.eraseWheel();
            }

            Canvas.getExistingCanvas().erase(body);
        }

        this.visible = false;

        if (jackpotLight != null) {
            jackpotLight.makeInvisible();
        }
    }

    /**
     * Acaba la simulacion.
     */
    public void exit()
    {
        makeInvisible();
    }

    /**
     * Retorna si la ultima operacion fue correcta.
     *
     * @return true si fue exitosa
     */
    public boolean ok()
    {
        return lastOk;
    }

    private void succeed()
    {
        lastOk = true;

        refresh();
    }

    private void fail(String msg)
    {
        lastOk = false;

        showError(msg);
    }

    private void showError(String msg)
    {
        if (visible) {

            JOptionPane.showMessageDialog(
                null,
                msg,
                "SlotMachine",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void refresh()
    {
        if (!visible) {
            return;
        }

        drawBody();

        for (Wheel wheel : wheels) {
            wheel.drawWheel();
        }

        updateJackpotLight();
    }

    /**
     * Dibuja el cuerpo gris oscuro que contiene las ruedas.
     */
    private void drawBody()
    {
        if (wheels.isEmpty()) {
            return;
        }

        int wheelWidth = Symbol.WIDTH + 20;
        int bodyWidth = wheels.size() * wheelWidth + 20;

        Canvas.getCanvas().draw(
            body,
            new java.awt.Color(65, 65, 65),
            new java.awt.Rectangle(
                BODY_LEFT,
                BODY_TOP,
                bodyWidth,
                BODY_HEIGHT
            )
        );
    }

    private void updateJackpotLight()
    {
        if (isJackpotQuiet()) {
            jackpotLight.makeVisible();
        }
        else {
            jackpotLight.makeInvisible();
        }
    }

    private boolean isJackpotQuiet()
    {
        return !wheels.isEmpty()
            && allWheelsHaveSymbol()
            && distinctSymbolsQuiet() == 1;
    }

    private int distinctSymbolsQuiet()
    {
        ArrayList<String> distinct = new ArrayList<>();

        for (Wheel wheel : wheels) {

            Symbol current = wheel.currentSymbol();

            if (current != null
                && !distinct.contains(current.getColor())) {

                distinct.add(current.getColor());
            }
        }

        return distinct.size();
    }

    private boolean allWheelsHaveSymbol()
    {
        for (Wheel wheel : wheels) {

            if (wheel.currentSymbol() == null) {
                return false;
            }
        }

        return true;
    }

    private Wheel wheelAt(int pos)
    {
        if (wheels.isEmpty()) {
            return null;
        }

        return wheels.get(
            clamp(pos, 1, wheels.size()) - 1
        );
    }

    private int clamp(int value, int min, int max)
    {
        if (value < min) {
            return min;
        }

        if (value > max) {
            return max;
        }

        return value;
    }

    private int sharedSymbolCount()
    {
        return wheels.isEmpty()
            ? 0
            : wheels.get(0).getSymbols().size();
    }

    private boolean containsColor(String color)
    {
        for (String existing : symbolsQuiet()) {

            if (existing.equals(color)) {
                return true;
            }
        }

        return false;
    }

    private String[] symbolsQuiet()
    {
        if (wheels.isEmpty()) {
            return new String[0];
        }

        ArrayList<Symbol> reference =
            wheels.get(0).getSymbols();

        String[] result =
            new String[reference.size()];

        for (int i = 0; i < reference.size(); i++) {

            result[i] =
                reference.get(i).getColor();
        }

        return result;
    }

    private void copySharedSymbolsInto(Wheel wheel)
    {
        if (wheels.isEmpty()) {
            return;
        }

        for (Symbol s : wheels.get(0).getSymbols()) {
            wheel.addSymbol(s.getColor());
        }
    }

    private void renumberWheels()
    {
        for (int i = 0; i < wheels.size(); i++) {
            wheels.get(i).setPosition(i + 1);
        }
    }

    private int randomSteps(Wheel wheel)
    {
        int size = wheel.getSymbols().size();

        return 1 + random.nextInt(size);
    }
}
