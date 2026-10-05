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

    /** Primera rueda creada: la unica que puede mostrar el simbolo cascada. */
    private Wheel firstWheel;

    /** Probabilidad (0-100) de que el simbolo cascada aparezca al girar. */
    private int bonusChance;

    /** true si la ultima accion de giro termino en un insta jackpot. */
    private boolean cascadeWon;

    /** Ultimo mensaje de victoria mostrado (null si no hay). */
    private String lastWinMessage;

    private static final int BODY_LEFT = 35;
    private static final int BODY_TOP = 75;
    private static final int BODY_HEIGHT = 120;

    /** Probabilidad por defecto del simbolo cascada: 10%. */
    private static final int DEFAULT_BONUS_CHANCE = 10;

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
        firstWheel = null;
        bonusChance = DEFAULT_BONUS_CHANCE;
        cascadeWon = false;
        lastWinMessage = null;
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
     * **
     * Agrega una rueda normal a la maquina
     * @param pos posiscion donde se agregara
     */
    public void addWheel (int pos){
        addWheel(pos, "normal");
    }
    
    /**
     * Agrega una rueda del tipo solicitado 
     * 
     * los tipos son:
     * normla, lefty, rebel
     * 
     * @param pos posicion deonde va la rueda
     * @param type nombre del tipo de rueda
     */
    public void addWheel(int pos, String type){
        /**
         * Se ajusta la posicion para que quede 
         * entre la primera y ultima posicion
         */
        int target = clamp(pos, 1, wheels.size() + 1);
        
        // se crea el objeto wheel, leftWheel o  rebelWheel
        Wheel wheel = createWheel(target, type);

        /**
         * Si el tipo de rueda no existe 
         * la operacion falla
         */
        if (wheel == null){
            fail ("El tipo de rueda no es valido");
            return;
        }
        
        copySharedSymbolsInto(wheel);

        wheels.add(target - 1, wheel);

        /*
         * Solo la primera rueda que se crea en la maquina puede mostrar el
         * simbolo cascada (requisito 19).
         */
        if (firstWheel == null) {
            firstWheel = wheel;
            wheel.enableBonus();
        }

        renumberWheels();

        succeed();
    }
    
    /**
     * Crea una rueda según el tipo solicitado.
     *
     * @param position posición de la nueva rueda
     * @param type nombre del tipo solicitado
     * @return una rueda creada o null si el tipo no existe
     */
    private Wheel createWheel(int position, String type)
    {
        if ("normal".equalsIgnoreCase(type)) {
            return new Wheel(position);
        }
    
        if ("lefty".equalsIgnoreCase(type)) {
            return new LeftyWheel(position);
        }
    
        if ("rebel".equalsIgnoreCase(type)) {
            return new RebelWheel(position);
        }
    
        // Se retorna null para informar que el tipo no es válido.
        return null;
    }
    

    /**
     * Crea una rueda según el tipo solicitado.
     *
     * @param position posición de la nueva rueda
     * @param type nombre del tipo solicitado
     * @return una rueda creada o null si el tipo no existe
     */
    private Wheel createWheel(int position, String type)
    {
        if ("normal".equalsIgnoreCase(type)) {
            return new Wheel(position);
        }
    
        if ("lefty".equalsIgnoreCase(type)) {
            return new LeftyWheel(position);
        }
    
        if ("rebel".equalsIgnoreCase(type)) {
            return new RebelWheel(position);
        }
    
        // Se retorna null para informar que el tipo no es válido.
        return null;
    }
    
   /**
     * **
     * Elimina una rueda de la máquina.
     *
     * Una rueda rebel no se puede eliminar.
     *
     * @param pos posición de la rueda que se desea eliminar
     */
    public void delWheel(int pos)
    {
        if (wheels.isEmpty()) {
            fail("No hay ruedas en la máquina.");
            return;
        }
    
        // Se ajusta la posición recibida a una posición válida.
        int target = clamp(pos, 1, wheels.size());
    
        // Primero se consulta la rueda, sin eliminarla todavía.
        Wheel removed = wheels.get(target - 1);
    
        // Se protege a la rueda rebel.
        if (!removed.canBeDeleted()) {
            fail("La rueda rebel no se puede eliminar.");
            return;
        }
    
        // Ahora sí se elimina de la lista.
        wheels.remove(target - 1);
    
        // Se borra visualmente del Canvas.
        removed.eraseWheel();
    
        // Se actualizan las posiciones restantes.
        renumberWheels();
    
        succeed();
    }
    
    /**
     * **
     * Gira una rueda normal o hace que una lefty copie
     * el estado de la rueda izquierda.
     *
     * @param wheel rueda que recibe la acción
     * @param steps cantidad de pasos solicitados
     */
    private void turnWheel(Wheel wheel, int steps)
    {
        /*
         * Una lefty solo puede copiar si hay una rueda a su izquierda.
         * La posición es base 1, pero ArrayList usa base 0.
         */
        if (wheel.copiesLeftWheel() && wheel.getPosition() > 1) {
            Wheel leftWheel = wheels.get(wheel.getPosition() - 2);
    
            // La lefty muestra el mismo símbolo que la rueda izquierda.
            wheel.copyStateFrom(leftWheel);
        }
        else {
            // Una rueda normal o una lefty en posición 1 gira normalmente.
            wheel.spin(steps);
        }
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
        addSymbol("normal", pos, color);
    }

    /**
     * Agrega un nuevo simbolo de un tipo dado a todas las ruedas.
     *
     * Los tipos son: normal, ephemeral, shy. Cada rueda recibe su propio
     * objeto simbolo (asi el estado de un efimero o un shy es independiente
     * en cada rueda).
     *
     * @param type tipo del simbolo
     * @param pos posicion del simbolo
     * @param color color CSS valido
     */
    public void addSymbol(String type, int pos, String color)
    {
        if (createSymbol(type, color) == null) {
            fail("El tipo de simbolo \"" + type + "\" no es valido.");
            return;
        }

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
            wheel.addSymbol(index, createSymbol(type, color));
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

        beginSpin();

        target.spin(randomSteps(target));

        checkCascade(target);

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

        beginSpin();

        if (visible) {
            /*
             * La animacion mueve la rueda paso a paso, pero todo cuenta como
             * UN solo giro: los simbolos se avisan una vez al empezar y el
             * simbolo final se selecciona una vez al terminar.
             */
            target.startSpin();
            for (int i = 0; i < totalSteps; i++) {
                target.step(direction);
                target.drawWheel();
                Canvas.getCanvas().wait(300);
            }
            target.stopSpin();
        }
        else {
            target.spin(steps);
        }

        checkCascade(target);

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

        beginSpin();

        boolean firstSpun = false;

        for (Wheel wheel : wheels) {

            if (!wheel.getSymbols().isEmpty()
                && !wheel.isLocked()) {

                wheel.spin(randomSteps(wheel));

                if (wheel == firstWheel) {
                    firstSpun = true;
                }
            }
        }

        if (firstSpun) {
            checkCascade(firstWheel);
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
     * Devuelve el tipo de cada simbolo (normal, ephemeral, shy), en el mismo
     * orden que symbols().
     *
     * @return arreglo con el tipo de cada simbolo
     */
    public String[] symbolTypes()
    {
        lastOk = true;

        if (wheels.isEmpty()) {
            return new String[0];
        }

        ArrayList<Symbol> reference = wheels.get(0).getSymbols();
        String[] result = new String[reference.size()];

        for (int i = 0; i < reference.size(); i++) {
            result[i] = reference.get(i).getType();
        }

        return result;
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
     * Cambia la probabilidad con que aparece el simbolo cascada en la primera
     * rueda. Por defecto es 10. Sirve para pruebas y demostraciones
     * (0 = nunca, 100 = siempre).
     *
     * @param percent probabilidad entre 0 y 100
     */
    public void setBonusChance(int percent)
    {
        bonusChance = clamp(percent, 0, 100);
    }

    /**
     * @return true si el ultimo giro termino en un insta jackpot provocado
     * por el simbolo cascada
     */
    public boolean wonByCascade()
    {
        return cascadeWon;
    }

    /**
     * @return el ultimo mensaje de victoria generado, o null si no ha habido
     */
    public String lastWinMessage()
    {
        return lastWinMessage;
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

    /**
     * Se llama al comenzar cualquier accion de giro.
     */
    private void beginSpin()
    {
        cascadeWon = false;
    }

    /**
     * Despues de que gira una rueda, si es la primera rueda creada, tira el
     * 10% de probabilidad de que aparezca el simbolo cascada. Si aparece:
     * se muestra un momento, se vuelve shy (queda invisible) y desencadena
     * la cascada.
     *
     * @param spun rueda que acaba de girar
     */
    private void checkCascade(Wheel spun)
    {
        if (spun != firstWheel || !wheels.contains(spun)) {
            return;
        }

        if (random.nextInt(100) >= bonusChance) {
            return;
        }

        Symbol cascade = spun.appearCascade();

        if (cascade == null) {
            return;
        }

        // Aparece (todavia visible)...
        pause(refreshAndWait(700));

        // ... y en ese momento se vuelve shy: queda invisible.
        cascade.onSelected();
        pause(refreshAndWait(500));

        if (cascade.triggersCascade()) {
            runCascade(spun, cascade.getColor());
        }
    }

    /**
     * Efecto cascada: de rueda en rueda (izquierda a derecha) todos los
     * simbolos pasan a ser shy reducidos a un punto. Cuando todos lo son, se
     * alinea el color ganador en todas las ruedas (insta jackpot) y se avisa.
     *
     * @param origin rueda donde aparecio el simbolo cascada
     * @param winningColor color con el que se alinean todas las ruedas
     */
    private void runCascade(Wheel origin, String winningColor)
    {
        origin.clearBonus();

        for (Wheel wheel : wheels) {
            wheel.convertToShyPoints();
            pause(refreshAndWait(400));
        }

        if (!allShyPoints()) {
            return;
        }

        /*
         * Insta jackpot: se alinean todas las ruedas, incluso las fijadas.
         * forceSymbol no cuenta como seleccion, asi los puntos shy no se
         * ocultan y el jackpot se alcanza a ver.
         */
        for (Wheel wheel : wheels) {
            wheel.forceSymbol(winningColor);
        }

        cascadeWon = true;
        lastWinMessage = "¡HAS GANADO! Insta jackpot: el símbolo cascada "
            + "convirtió todos los símbolos en shy y todas las ruedas "
            + "muestran " + winningColor + ".";

        refresh();
        showMessage(lastWinMessage);
    }

    private boolean allShyPoints()
    {
        for (Wheel wheel : wheels) {
            if (!wheel.allShyPoints()) {
                return false;
            }
        }

        return !wheels.isEmpty();
    }

    /**
     * Redibuja la maquina si es visible.
     *
     * @param millis tiempo de espera si se dibujo
     * @return millis si la maquina es visible, 0 si no (no hay que esperar)
     */
    private int refreshAndWait(int millis)
    {
        if (!visible) {
            return 0;
        }

        refresh();

        return millis;
    }

    private void pause(int millis)
    {
        if (millis > 0 && visible) {
            Canvas.getCanvas().wait(millis);
        }
    }

    private void showMessage(String msg)
    {
        if (visible) {
            JOptionPane.showMessageDialog(
                null,
                msg,
                "SlotMachine",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
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
            wheel.addSymbol(s.copy());
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
