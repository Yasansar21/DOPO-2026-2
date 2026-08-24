import javax.swing.JOptionPane;

/**
 * <p>La clase main se crea con el proposito de tener un acceso y control manual de
 * la slot machine para verificar su funcionamiento general</p>
 *
 * @author Yamel Sarmiento Johan Pinilla
 * 
 */
public class Main
{
    private SlotMachine machine;

    /**
     * Runs the interactive menu until the user chooses to exit.
     *
     * @param args not used
     */
    public static void main(String[] args)
    {
        new Main().run();
    }

    private void run()
    {
        machine = new SlotMachine();
        machine.makeVisible();
        boolean running = true;
        while (running) {
            running = showMenuAndAct();
        }
        machine.exit();
    }

    private boolean showMenuAndAct()
    {
        String[] options = {
            "Agregar rueda", "Eliminar rueda",
            "Agregar símbolo", "Eliminar símbolo",
            "Girar una rueda", "Girar todas",
            "Colocar símbolo", "Ver símbolos",
            "Ver configuración", "¿Jackpot?",
            "Visible/Invisible", "Salir"
        };
        int choice = JOptionPane.showOptionDialog(null, "¿Qué deseas hacer?",
            "SlotMachine", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
            null, options, options[0]);
        return act(choice);
    }

    private boolean act(int choice)
    {
        switch (choice) {
            case 0: addWheel();       break;
            case 1: delWheel();       break;
            case 2: addSymbol();      break;
            case 3: delSymbol();      break;
            case 4: spinOne();        break;
            case 5: machine.spin();   break;
            case 6: placeSymbol();    break;
            case 7: report(machine.symbols());      break;
            case 8: report(machine.configuration()); break;
            case 9: reportJackpot();  break;
            case 10: toggleVisible(); break;
            default: return false;
        }
        return true;
    }

    private void addWheel()
    {
        int pos = askInt("Posición de la nueva rueda:");
        machine.addWheel(pos);
    }

    private void delWheel()
    {
        int pos = askInt("Posición de la rueda a eliminar:");
        machine.delWheel(pos);
    }

    private void addSymbol()
    {
        int pos = askInt("Posición del nuevo símbolo:");
        String color = askText("Color CSS del símbolo (ej: tomato):");
        machine.addSymbol(pos, color);
    }

    private void delSymbol()
    {
        String color = askText("Color CSS del símbolo a eliminar:");
        machine.delSymbol(color);
    }

    private void spinOne()
    {
        int pos = askInt("Posición de la rueda a girar:");
        machine.spin(pos);
    }

    private void placeSymbol()
    {
        int pos = askInt("Posición de la rueda:");
        String color = askText("Color a mostrar:");
        machine.placeSymbol(pos, color);
    }

    private void reportJackpot()
    {
        String msg = machine.isJackpot() ? "¡Sí, hay jackpot!" : "Todavía no hay jackpot.";
        JOptionPane.showMessageDialog(null, msg);
    }

    private void toggleVisible()
    {
        if (askYesNo("¿Hacer visible la máquina? (No = invisible)")) {
            machine.makeVisible();
        } else {
            machine.makeInvisible();
        }
    }

    private void report(String[] values)
    {
        StringBuilder sb = new StringBuilder();
        for (String v : values) {
            sb.append(v == null ? "(vacío)" : v).append("\n");
        }
        JOptionPane.showMessageDialog(null, sb.length() == 0 ? "(sin datos)" : sb.toString());
    }

    private int askInt(String prompt)
    {
        String text = JOptionPane.showInputDialog(prompt);
        try {
            return Integer.parseInt(text.trim());
        } catch (Exception e) {
            return 1;
        }
    }

    private String askText(String prompt)
    {
        return JOptionPane.showInputDialog(prompt);
    }

    private boolean askYesNo(String prompt)
    {
        return JOptionPane.showConfirmDialog(null, prompt, "SlotMachine",
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}