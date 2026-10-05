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
    // ------------------------------------------------------------------
    // Requisito 18: ephemeral
    // ------------------------------------------------------------------

    @Test
    public void testEphemeralShrinksOnEachSpinUntilPoint()
    {
        EphemeralSymbol symbol = new EphemeralSymbol("red");

        assertEquals(1.0, symbol.getScale(), 0.0001);
        assertFalse(symbol.isPoint());

        double previous = symbol.getScale();
        for (int i = 0; i < EphemeralSymbol.STEPS_TO_POINT; i++) {
            symbol.onSpin();
            assertTrue(symbol.getScale() < previous);
            previous = symbol.getScale();
        }

        assertTrue(symbol.isPoint());

        symbol.onSpin();
        assertTrue(symbol.isPoint());
    }

    @Test
    public void testEphemeralShrinksOnceForEachSpinOfItsWheel()
    {
        Wheel wheel = new Wheel(1);
        EphemeralSymbol ephemeral = new EphemeralSymbol("red");
        wheel.addSymbol(ephemeral);
        wheel.addSymbol(new Symbol("blue"));

        // Un giro de varios pasos cuenta como UN solo giro.
        wheel.spin(3);

        assertEquals(0.8, ephemeral.getScale(), 0.0001);
    }

    @Test
    public void testEphemeralInMachineBecomesPoint()
    {
        machine.addSymbol("ephemeral", 4, "yellow");
        assertTrue(machine.ok());

        for (int i = 0; i < EphemeralSymbol.STEPS_TO_POINT; i++) {
            machine.spin(1, 1);
        }

        assertTrue(machine.ok());
        assertEquals("ephemeral", machine.symbolTypes()[3]);
    }

    // ------------------------------------------------------------------
    // Requisito 18: shy
    // ------------------------------------------------------------------

    @Test
    public void testShyTogglesVisibilityWhenSelected()
    {
        ShySymbol symbol = new ShySymbol("red");

        assertFalse(symbol.isConcealed());
        symbol.onSelected();
        assertTrue(symbol.isConcealed());
        symbol.onSelected();
        assertFalse(symbol.isConcealed());
    }

    @Test
    public void testShyTogglesWhenPlacedInWheel()
    {
        Wheel wheel = new Wheel(1);
        ShySymbol shy = new ShySymbol("red");
        wheel.addSymbol(shy);
        wheel.addSymbol(new Symbol("blue"));

        wheel.placeSymbol("red");
        assertTrue(shy.isConcealed());

        wheel.placeSymbol("blue");
        assertTrue(shy.isConcealed());

        wheel.placeSymbol("red");
        assertFalse(shy.isConcealed());
    }

    @Test
    public void testShyKeepsItsColorWhileInvisible()
    {
        machine.addSymbol("shy", 4, "yellow");

        machine.placeSymbol(1, "yellow");
        machine.placeSymbol(2, "yellow");
        machine.placeSymbol(3, "yellow");

        assertTrue(machine.ok());
        assertTrue(machine.isJackpot());
        assertEquals(1, machine.distinctSymbols());
    }

    // ------------------------------------------------------------------
    // Tipos de simbolos en la maquina
    // ------------------------------------------------------------------

    @Test
    public void testAddSymbolWithType()
    {
        machine.addSymbol("ephemeral", 4, "yellow");
        machine.addSymbol("shy", 5, "orange");

        assertTrue(machine.ok());
        assertArrayEquals(
            new String[]{"normal", "normal", "normal", "ephemeral", "shy"},
            machine.symbolTypes()
        );
    }

    @Test
    public void testAddSymbolInvalidType()
    {
        machine.addSymbol("invisible", 4, "yellow");

        assertFalse(machine.ok());
        assertEquals(3, machine.symbols().length);
    }

    @Test
    public void testNewWheelKeepsSymbolTypes()
    {
        machine.addSymbol("ephemeral", 4, "yellow");
        machine.addSymbol("shy", 5, "orange");

        machine.addWheel(4);

        assertTrue(machine.ok());
        assertArrayEquals(
            new String[]{"normal", "normal", "normal", "ephemeral", "shy"},
            machine.symbolTypes()
        );
        assertEquals(4, machine.configuration().length);
    }

    // ------------------------------------------------------------------
    // Requisito 19: simbolo cascada
    // ------------------------------------------------------------------

    @Test
    public void testCascadeNeverAppearsWithZeroChance()
    {
        for (int i = 0; i < 200; i++) {
            machine.spin(1);
            machine.spin();
        }

        assertFalse(machine.wonByCascade());
        assertNull(machine.lastWinMessage());
        assertArrayEquals(
            new String[]{"normal", "normal", "normal"},
            machine.symbolTypes()
        );
    }

    @Test
    public void testCascadeMakesInstaJackpot()
    {
        machine.setBonusChance(100);

        machine.spin(1);

        assertTrue(machine.ok());
        assertTrue(machine.wonByCascade());
        assertTrue(machine.isJackpot());
        assertEquals(1, machine.distinctSymbols());
        assertNotNull(machine.lastWinMessage());
    }

    @Test
    public void testCascadeConvertsEverySymbolToShy()
    {
        machine.setBonusChance(100);
        machine.addSymbol("ephemeral", 4, "yellow");

        machine.spin(1);

        assertTrue(machine.wonByCascade());
        for (String type : machine.symbolTypes()) {
            assertEquals("shy", type);
        }
        assertEquals(4, machine.symbols().length);
    }

    @Test
    public void testCascadeOnlyAppearsInFirstCreatedWheel()
    {
        machine.setBonusChance(100);

        for (int i = 0; i < 50; i++) {
            machine.spin(2, 1);
            machine.spin(3, 1);
        }

        assertFalse(machine.wonByCascade());
    }

    @Test
    public void testCascadeAlsoByFullSpin()
    {
        machine.setBonusChance(100);

        machine.spin();

        assertTrue(machine.wonByCascade());
        assertTrue(machine.isJackpot());
    }

    @Test
    public void testCascadeWinsEvenWithLockedWheel()
    {
        machine.setBonusChance(100);
        machine.lock(2);

        machine.spin(1);

        assertTrue(machine.wonByCascade());
        assertTrue(machine.isJackpot());
    }

    @Test
    public void testCascadeNotWhenFirstWheelWasDeleted()
    {
        machine.setBonusChance(100);

        // La rueda 1 es la primera creada: al borrarla nadie puede dispararlo.
        machine.delWheel(1);

        for (int i = 0; i < 50; i++) {
            machine.spin(1, 1);
            machine.spin();
        }

        assertFalse(machine.wonByCascade());
    }

    @Test
    public void testNewWheelNeverGetsCascade()
    {
        machine.setBonusChance(100);
        machine.delWheel(1);
        machine.addWheel(1);

        for (int i = 0; i < 50; i++) {
            machine.spin(1, 1);
        }

        assertFalse(machine.wonByCascade());
    }

    @Test
    public void testWinFlagResetsOnNextSpin()
    {
        machine.setBonusChance(100);
        machine.spin(1);
        assertTrue(machine.wonByCascade());

        machine.setBonusChance(0);
        machine.spin(2, 1);

        assertFalse(machine.wonByCascade());
    }

    @Test
    public void testCascadeAppearsAboutTenPercentOfTheTime()
    {
        int trials = 2000;
        int wins = 0;

        for (int i = 0; i < trials; i++) {
            SlotMachine m = new SlotMachine(3);
            m.spin(1, 1);
            if (m.wonByCascade()) {
                wins++;
            }
        }

        // 10% esperado de 2000 = 200, con holgura amplia
        assertTrue(wins > 120 && wins < 280, "victorias: " + wins);
    }

    @Test
    public void testWheelAllShyPointsAfterConversion()
    {
        Wheel wheel = new Wheel(1);
        wheel.addSymbol(new Symbol("red"));
        wheel.addSymbol(new EphemeralSymbol("blue"));

        assertFalse(wheel.allShyPoints());

        wheel.convertToShyPoints();

        assertTrue(wheel.allShyPoints());
        assertEquals(2, wheel.getSymbols().size());
        assertEquals("red", wheel.getSymbols().get(0).getColor());
        assertEquals("blue", wheel.getSymbols().get(1).getColor());
    }
}