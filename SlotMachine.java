import java.util.ArrayList;
import java.util.Random;
import javax.swing.JOptionPane;

/**
 * Clase principal del slot machine donde se busca que el proyecto funcione
 * de manera correcta.
 * @author Yamel Sarmiento - Johan Pinilla
 */
public class SlotMachine
{
    private ArrayList<Wheel> wheels;
    private boolean visible;
    private boolean lastOk;
    private Circle jackpotLight;
    private Random random;
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
     * ninguna rueda (las ruedas hay que crearlas al momento de hacer visible
     * la slot machine)
     */
    public SlotMachine()
    {
        wheels = new ArrayList<>();
        visible = false;
        lastOk = true;
        random = new Random();
    }

    /**
    * Creacion del nuevo Slotmachine invisible, con n ruedas y n simbolos.
    * @param n cantidad de ruedas y simbolos que se crearan
    */
    public SlotMachine(int n ){
        this ();
        if (n < 1 || n > INITIAL_COLORS.length ){
            System.out.println ("La cantidad de ruedas debe estar entre 1 y " + INITIAL_COLORS.length + ".");
            return ;
        }

        for (int i = 1; i <= n; i++){
            addWheel(i);
        }
        for (int i = 1 ; i <= n; i++) {
            addSymbol (i, INITIAL_COLORS[I-1]);
        }
    }
    
    
    /**
     * Crea la slot machine y crea espacios vacios para la creacion de las ruedas
     * segun la posicion determinada.
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
     * Si las posiciones son iguales la operacion no se realiza.
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

        if (w1.islocked() || w2.islocked()){
            return;
        }
        wheels.set(pos1 - 1, w2);
        wheels.set(pos2 - 1, w1);
        renumberWheels();
        succeed();
    }

    /**
     * Fija una rueda para que no pueda girar.
     * Si la rueda ya esta fijada la operacion no se realiza.
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
            fail("La rueda " + target.getPosition() + " ya está fijada.");
            return;
        }
        target.lock();
        succeed();
    }

    /**
     * Suelta una rueda fijada para que pueda girar nuevamente.
     * Si la rueda no esta fijada la operacion no se realiza.
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
            fail("La rueda " + target.getPosition() + " no está fijada.");
            return;
        }
        target.unlock();
        succeed();
    }

    /**
     * Agregar una nueva cantidad de colores validos del CSS para que cada
     * rueda tenga una variedad de colores sobre la cual buscar el jackpot.
     * @param pos posicion del simbolo en la lista
     * @param color color CSS valido del simbolo
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
        int index = clamp(pos, 1, sharedSymbolCount() + 1) - 1;
        for (Wheel wheel : wheels) {
            wheel.addSymbol(index, color);
        }
        succeed();
    }

    /**
     * Quita un symbol color de cada una de las ruedas de la maquina.
     * @param color color del simbolo a eliminar
     */
    public void delSymbol(String color)
    {
        if (!containsColor(color)) {
            fail("No existe ningún símbolo de color " + color + ".");
            return;
        }
        for (Wheel wheel : wheels) {
            wheel.delSymbol(color);
        }
        succeed();
    }

    /**
     * Permite asignar directamente un valor de color a una posicion de rueda
     * en especifico sin tener que realizar un spin.
     * @param wheel posicion de la rueda
     * @param symbol color del simbolo a mostrar
     */
    public void placeSymbol(int wheel, String symbol)
    {
        Wheel target = wheelAt(wheel);
        if (target == null) {
            fail("La máquina no tiene ruedas.");
            return;
        }
        if (!target.placeSymbol(symbol)) {
            fail("La rueda " + target.getPosition() + " no tiene un símbolo " + symbol + ".");
            return;
        }
        succeed();
    }

    /**
     * Gira una rueda de forma aleatoria.
     * @param wheel posicion de la rueda a girar
     */
    public void spin(int wheel)
    {
        Wheel target = wheelAt(wheel);
        if (target == null) {
            fail("La máquina no tiene ruedas.");
            return;
        }
        if (target.getSymbols().isEmpty()) {
            fail("La rueda " + target.getPosition() + " no tiene símbolos.");
            return;
        }
        target.spin(randomSteps(target));
        succeed();
    }

    /**
     * Gira una rueda un numero especifico de pasos.
     * Si el simulador es visible el movimiento se muestra paso a paso.
     * Si la rueda esta fijada la operacion no se realiza.
     * @param wheel posicion de la rueda
     * @param steps numero de pasos, negativos giran en sentido contrario
     */
    public void spin(int wheel, int steps)
    {
        Wheel target = wheelAt(wheel);
        if (target == null) {
            fail("La máquina no tiene ruedas.");
            return;
        }
        if (target.getSymbols().isEmpty()) {
            fail("La rueda " + target.getPosition() + " no tiene símbolos.");
            return;
        }
        if (target.isLocked()) {
            fail("La rueda " + target.getPosition() + " está fijada.");
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
        } else {
            target.spin(steps);
        }
        succeed();
    }

    /**
     * Deja la maquina en una configuracion dada por el arreglo de colores.
     * Las ruedas fijadas no se modifican.
     * Si el arreglo no coincide con el numero de ruedas la operacion no
     * se realiza.
     * @param setSymbols arreglo con el color visible deseado por cada rueda
     */
    public void spin(String[] setSymbols)
    {
        if (setSymbols.length != wheels.size()) {
            fail("La configuración no coincide con el número de ruedas.");
            return;
        }
        boolean anyFailed = false;
        for (int i = 0; i < wheels.size(); i++) {
            Wheel w = wheels.get(i);
            if (w.isLocked()) continue;
            if (!w.placeSymbol(setSymbols[i])) {
                anyFailed = true;
            }
        }
        if (anyFailed) {
            fail("Algún símbolo de la configuración no existe en su rueda.");
            return;
        }
        succeed();
    }

    /**
     * Gira cada una de las ruedas de la maquina de forma independiente y
     * aleatoria. Las ruedas fijadas no se giran.
     */
    public void spin()
    {
        if (wheels.isEmpty()) {
            fail("La máquina no tiene ruedas.");
            return;
        }
        for (Wheel wheel : wheels) {
            if (!wheel.getSymbols().isEmpty() && !wheel.isLocked()) {
                wheel.spin(randomSteps(wheel));
            }
        }
        succeed();
    }

    /**
     * Devuelve los colores en secuencia en orden de cada una de las ruedas,
     * empezando desde la rueda en la posicion 1.
     * @return arreglo con los colores de todos los simbolos de la rueda 1
     */
    public String[] symbols()
    {
        lastOk = true;
        return symbolsQuiet();
    }

    /**
     * Retorna cuantos colores distintos se estan mostrando en las ruedas.
     * @return numero de colores distintos visibles
     */
    public int distinctSymbols()
    {
        lastOk = true;
        return distinctSymbolsQuiet();
    }

    /**
     * Muestra los colores visibles en cada rueda de izquierda a derecha.
     * @return arreglo con el color visible de cada rueda
     */
    public String[] configuration()
    {
        lastOk = true;
        String[] result = new String[wheels.size()];
        for (int i = 0; i < wheels.size(); i++) {
            Symbol current = wheels.get(i).currentSymbol();
            result[i] = (current == null) ? null : current.getColor();
        }
        return result;
    }

    /**
     * Retorna true si todas las ruedas muestran el mismo color.
     * @return true si hay jackpot
     */
    public boolean isJackpot()
    {
        lastOk = true;
        return isJackpotQuiet();
    }

    /**
     * Muestra la maquina y cada una de las ruedas dentro del canvas.
     */
    public void makeVisible()
    {
        this.visible = true;
        if (jackpotLight == null) {
            jackpotLight = new Circle();
        }
        jackpotLight.makeVisible();
        for (Wheel w : wheels) {
            w.drawWheel();
        }
    }

    /**
     * Esconde la maquina del canvas pero el funcionamiento sigue siendo
     * el mismo en modo invisible.
     */
    public void makeInvisible()
    {
        this.visible = false;
        if (jackpotLight != null) {
            jackpotLight.makeInvisible();
        }
    }

    /**
     * Acaba la simulacion de la maquina.
     */
    public void exit()
    {
        makeInvisible();
    }

    /**
     * Retorna si la ultima operacion fue completada satisfactoriamente.
     * @return true si la ultima operacion fue exitosa
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
            JOptionPane.showMessageDialog(null, msg, "SlotMachine",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refresh()
    {
        if (!visible) return;
        for (Wheel wheel : wheels) {
            wheel.drawWheel();
        }
        updateJackpotLight();
    }

    private void updateJackpotLight()
    {
        if (isJackpotQuiet()) {
            jackpotLight.makeVisible();
        } else {
            jackpotLight.makeInvisible();
        }
    }

    private boolean isJackpotQuiet()
    {
        return !wheels.isEmpty() && allWheelsHaveSymbol()
            && distinctSymbolsQuiet() == 1;
    }

    private int distinctSymbolsQuiet()
    {
        ArrayList<String> distinct = new ArrayList<>();
        for (Wheel wheel : wheels) {
            Symbol current = wheel.currentSymbol();
            if (current != null && !distinct.contains(current.getColor())) {
                distinct.add(current.getColor());
            }
        }
        return distinct.size();
    }

    private boolean allWheelsHaveSymbol()
    {
        for (Wheel wheel : wheels) {
            if (wheel.currentSymbol() == null) return false;
        }
        return true;
    }

    private Wheel wheelAt(int pos)
    {
        if (wheels.isEmpty()) return null;
        return wheels.get(clamp(pos, 1, wheels.size()) - 1);
    }

    private int clamp(int value, int min, int max)
    {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    private int sharedSymbolCount()
    {
        return wheels.isEmpty() ? 0 : wheels.get(0).getSymbols().size();
    }

    private boolean containsColor(String color)
    {
        for (String existing : symbolsQuiet()) {
            if (existing.equals(color)) return true;
        }
        return false;
    }

    private String[] symbolsQuiet()
    {
        if (wheels.isEmpty()) return new String[0];
        ArrayList<Symbol> reference = wheels.get(0).getSymbols();
        String[] result = new String[reference.size()];
        for (int i = 0; i < reference.size(); i++) {
            result[i] = reference.get(i).getColor();
        }
        return result;
    }

    private void copySharedSymbolsInto(Wheel wheel)
    {
        if (wheels.isEmpty()) return;
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
