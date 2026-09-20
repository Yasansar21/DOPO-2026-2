import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias de los metodos principales de SlotMachine.
 *
 * Se prueban las operaciones de ruedas, simbolos, giros, bloqueo,
 * configuracion y jackpot.
 *
 * @author Yamel Sarmiento - Johan Pinilla
 */
public class SlotMachineTest
{
    private SlotMachine machine;

    @BeforeEach
    public void setUp()
    {
        machine = new SlotMachine();

        machine.addWheel(1);
        machine.addWheel(2);
        machine.addWheel(3);

        machine.addSymbol(1, "red");
        machine.addSymbol(2, "blue");
        machine.addSymbol(3, "green");
    }

    @AfterEach
    public void tearDown()
    {
        if (machine != null) {
            machine.exit();
        }
    }

    @Test
    public void testAddWheel()
    {
        machine.addWheel(4);

        assertTrue(machine.ok());
        assertEquals(4, machine.configuration().length);
    }

    @Test
    public void testDelWheel()
    {
        machine.delWheel(2);

        assertTrue(machine.ok());
        assertEquals(2, machine.configuration().length);
        assertEquals(3, machine.symbols().length);
    }

    @Test
    public void testSwap()
    {
        machine.placeSymbol(1, "red");
        machine.placeSymbol(2, "blue");
        machine.placeSymbol(3, "green");

        machine.swap(1, 3);

        assertTrue(machine.ok());
        assertArrayEquals(
            new String[]{"green", "blue", "red"},
            machine.configuration()
        );
    }

    @Test
    public void testLockAndUnlock()
    {
        machine.lock(1);
        assertTrue(machine.ok());

        machine.spin(1, 1);
        assertEquals("red", machine.configuration()[0]);

        machine.unlock(1);
        assertTrue(machine.ok());

        machine.spin(1, 1);
        assertEquals("blue", machine.configuration()[0]);
    }

    @Test
    public void testAddSymbol()
    {
        machine.addSymbol(4, "yellow");

        assertTrue(machine.ok());
        assertArrayEquals(
            new String[]{"red", "blue", "green", "yellow"},
            machine.symbols()
        );
    }

    @Test
    public void testAddInvalidSymbol()
    {
        machine.addSymbol(4, "not-a-color");

        assertFalse(machine.ok());
        assertArrayEquals(
            new String[]{"red", "blue", "green"},
            machine.symbols()
        );
    }

    @Test
    public void testAddDuplicateSymbol()
    {
        machine.addSymbol(4, "red");

        assertFalse(machine.ok());
        assertArrayEquals(
            new String[]{"red", "blue", "green"},
            machine.symbols()
        );
    }

    @Test
    public void testDelSymbol()
    {
        machine.delSymbol("blue");

        assertTrue(machine.ok());
        assertArrayEquals(
            new String[]{"red", "green"},
            machine.symbols()
        );
    }

    @Test
    public void testPlaceSymbol()
    {
        machine.placeSymbol(2, "green");

        assertTrue(machine.ok());
        assertEquals("green", machine.configuration()[1]);
    }

    @Test
    public void testPlaceSymbolInvalid()
    {
        machine.placeSymbol(2, "purple");

        assertFalse(machine.ok());
        assertEquals("red", machine.configuration()[1]);
    }

    @Test
    public void testSpinOneWheel()
    {
        machine.placeSymbol(1, "red");
        machine.spin(1, 1);

        assertTrue(machine.ok());
        assertEquals("blue", machine.configuration()[0]);
    }

    @Test
    public void testSpinSeveralSteps()
    {
        machine.placeSymbol(1, "red");
        machine.spin(1, 2);

        assertEquals("green", machine.configuration()[0]);
    }

    @Test
    public void testSpinNegative()
    {
        machine.placeSymbol(1, "red");
        machine.spin(1, -1);

        assertEquals("green", machine.configuration()[0]);
    }

    @Test
    public void testSpinConfiguration()
    {
        machine.spin(new String[]{"green", "red", "blue"});

        assertTrue(machine.ok());
        assertArrayEquals(
            new String[]{"green", "red", "blue"},
            machine.configuration()
        );
    }

    @Test
    public void testSpinAll()
    {
        machine.spin();

        assertTrue(machine.ok());
        assertEquals(3, machine.configuration().length);
    }

    @Test
    public void testSymbols()
    {
        assertArrayEquals(
            new String[]{"red", "blue", "green"},
            machine.symbols()
        );
    }

    @Test
    public void testDistinctSymbols()
    {
        assertEquals(3, machine.distinctSymbols());

        machine.placeSymbol(2, "red");
        assertEquals(2, machine.distinctSymbols());
    }

    @Test
    public void testConfiguration()
    {
        assertArrayEquals(
            new String[]{"red", "red", "red"},
            machine.configuration()
        );
    }

    @Test
    public void testJackpot()
    {
        assertTrue(machine.isJackpot());

        machine.placeSymbol(2, "blue");
        assertFalse(machine.isJackpot());
    }

    @Test
    public void testOkAfterError()
    {
        machine.delSymbol("purple");

        assertFalse(machine.ok());
    }

    @Test
    public void testSlotMachineContestSolve()
    {
        SlotMachineContest contest = new SlotMachineContest();
        int[][] actions = contest.solve(3);

        assertEquals(2, actions.length);
        assertEquals(2, actions[0][0]);
        assertEquals(-1, actions[0][1]);
        assertEquals(3, actions[1][0]);
        assertEquals(-2, actions[1][1]);
    }

    @Test
    public void testMakeVisibleAndInvisible()
    {
        machine.makeVisible();
        machine.makeInvisible();

        assertTrue(true);
    }

    @Test
    public void testExit()
    {
        machine.makeVisible();
        machine.exit();

        assertTrue(true);
    }
}