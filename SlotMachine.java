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
    private Circle jackpotLight; //muestra una luz si se hace un jackpot
    private Random random;

    /**
     * Creacion de una nueva slot machine que inicia de manera invisible ,
     * ninguna rueda (las ruedas hay que crearlas al momento de hacer visible 
     * la slot machine)
     */
    public SlotMachine()
    {
        wheels = new ArrayList<>();
        visible = false;
        lastOk = true;
        random = new Random();
        //jackpotLight = new Circle();
        //jackpotLight.changeSize(30);
        //jackpotLight.changeColor("Green");
    }

    /**
     * 
     * Crea la slot machine y crea espacios vacios para la creacion de las ruedas
     * segun la posicion determinada 
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
     * Remueve la rueda asignada a la posicion dada 
     */
    public void delWheel(int pos)
    {
        if (wheels.isEmpty()) {
            fail("No hay ruedas en la máquina.");
            return;
        }
        int target = clamp(pos, 1, wheels.size());
        Wheel removed = wheels.remove(target - 1);
        removed.eraseCurrent();
        renumberWheels();
        succeed();
    }

    /**
     * Agregar una nueva cantidad de colores validos del CSS para que cada rueda tenga
     * una variedad de colores sobre la cual buscar el jackpot
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
     * Quita un symbol color de cada una de las ruedas de la maquina
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
     * en especifico sin tener que realizar un spin para asignarle el valor a 
     * la rueda
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
     * Gira las ruedas de forma aleatoria
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
     * Gira cada una de las ruedas de la maquina de forma independiente y en
     * caso de no existir ruedas envia el mensaje de error
     */
    public void spin()
    {
        if (wheels.isEmpty()) {
            fail("La máquina no tiene ruedas.");
            return;
        }
        for (Wheel wheel : wheels) {
            if (!wheel.getSymbols().isEmpty()) {
                wheel.spin(randomSteps(wheel));
            }
        }
        succeed();
    }

    /**
     * devuelve los colores en secuencia en orden de cada una de las ruedas,
     * empezando desde la rueda en la posicion 1
     */
    public String[] symbols()
    {
        lastOk = true;
        return symbolsQuiet();
    }

    /**
     * Retorna cuantos colores distintos se estan mostrando en las ruedas creadas
     */
    public int distinctSymbols()
    {
        lastOk = true;
        return distinctSymbolsQuiet();
    }

    /**
     * Muestra los colores en cada una de las ruedas de izquierda a derecha 
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
     * Retorna true si cada una de las ruedas muestran que tienen el mismo color
     */
    public boolean isJackpot()
    {
        lastOk = true;
        return isJackpotQuiet();
    }

    /**
     * Muestra la maquina y cada una de las ruedas previamanete creadas dentro
     * del canvas
     */
    public void makeVisible() {
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
     * Esconde la maquina del canvas pero el funcionamiento de la maquina sigue
     * siendo el mismo asi no se este viendo haciendo spins hasta que se genere 
     * un jackpot
     */
    public void makeInvisible() {
    this.visible = false;
    
    // Solo hace invisible la luz si no es nula
    if (jackpotLight != null) {
        jackpotLight.makeInvisible();
    }
    }

    /**
     * Acaba la simulacion de la maquina 
     */
    public void exit()
    {
        makeInvisible();
    }

    /**
     * Retorna que la ultima operacion de la maquina fue completada de manera
     * satisfactoria
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
            JOptionPane.showMessageDialog(null, msg, "SlotMachine", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refresh()
    {
        if (!visible) {
            return;
        }
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
        return !wheels.isEmpty() && allWheelsHaveSymbol() && distinctSymbolsQuiet() == 1;
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
        return wheels.get(clamp(pos, 1, wheels.size()) - 1);
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
        return wheels.isEmpty() ? 0 : wheels.get(0).getSymbols().size();
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
        ArrayList<Symbol> reference = wheels.get(0).getSymbols();
        String[] result = new String[reference.size()];
        for (int i = 0; i < reference.size(); i++) {
            result[i] = reference.get(i).getColor();
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