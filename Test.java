/**
 * Clase creada para poder ser ejecutada donde JDK sea disponible , como la
 * terminal de git para poder ser ejecutada en el repo, para demostrar que el 
 * proyecto funciona sin la necesidad de tomar ss del BlueJ
 *
 * @author Yamel Sarmiento Johan Pinilla
 */
public class Test
{
    private static int checks = 0;
    private static int passed = 0;

    public static void main(String[] args)
    {
        SlotMachine machine = new SlotMachine();

        machine.addWheel(1);
        machine.addWheel(2);
        machine.addWheel(3);
        check("crear 3 ruedas", machine.ok());

        machine.addSymbol(1, "red");
        machine.addSymbol(2, "tomato");
        machine.addSymbol(3, "cornflowerblue");
        check("agregar 3 símbolos válidos", machine.ok());

        machine.addSymbol(1, "not-a-css-color");
        check("rechazar color inválido", !machine.ok());

        machine.addSymbol(1, "red");
        check("rechazar símbolo duplicado", !machine.ok());

        print("symbols()", machine.symbols());

        machine.placeSymbol(1, "red");
        machine.placeSymbol(2, "red");
        machine.placeSymbol(3, "red");
        print("configuration() tras placeSymbol(*, red)", machine.configuration());
        check("distinctSymbols() == 1 tras alinear las 3 ruedas", machine.distinctSymbols() == 1);
        check("isJackpot() tras alinear las 3 ruedas", machine.isJackpot());

        machine.spin(2);
        print("configuration() tras spin(2)", machine.configuration());

        machine.delWheel(1);
        check("quedan 2 ruedas", machine.symbols().length == 3 && machine.configuration().length == 2);

        machine.exit();

        System.out.println();
        System.out.println(passed + "/" + checks + " verificaciones pasaron.");
        if (passed != checks) {
            System.exit(1);
        }
    }

    private static void check(String label, boolean condition)
    {
        checks++;
        if (condition) {
            passed++;
        }
        System.out.println((condition ? "[OK]   " : "[FALLA]") + " " + label);
    }

    private static void print(String label, String[] values)
    {
        StringBuilder sb = new StringBuilder(label + ": [");
        for (int i = 0; i < values.length; i++) {
            sb.append(values[i]);
            if (i < values.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        System.out.println(sb);
    }
