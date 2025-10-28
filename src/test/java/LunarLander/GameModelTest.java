package LunarLander;

import static LunarLander.GameModelAccess.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.junit.jupiter.api.extension.RegisterExtension;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;

// run tests with: mvn -DtrimStackTrace=false -Dsurefire.printSummary=true test

public class GameModelTest {
    // --- Enum-Helfer: löst z. B. GameState.PAUSED/SHOW_SCORE/GAME_OVER auf ---
    @SuppressWarnings("unchecked")
    private static Object enumConstant(Object model, String enumSimpleName, String constant) {
        try {
            String pkg = model.getClass().getPackageName();
            Class<?> enumClass = Class.forName(pkg + "." + enumSimpleName);
            return Enum.valueOf((Class<Enum>) enumClass.asSubclass(Enum.class), constant);
        } catch (ClassNotFoundException e) {
            try {
                Class<?> enumClass = Class.forName(model.getClass().getName() + "$" + enumSimpleName);
                return Enum.valueOf((Class<Enum>) enumClass.asSubclass(Enum.class), constant);
            } catch (Exception ex) {
                throw new RuntimeException("Cannot resolve enum " + enumSimpleName + "." + constant, ex);
            }
        }
    }

    /** 
     *      Statistik über fehlgeschlagene Tests pro Modell
     */ 
     
    private static Map<String, List<String>> failedTests = new HashMap<>();

    @RegisterExtension
    static TestWatcher watcher = new TestWatcher() {
        @Override
        public void testFailed(ExtensionContext context, Throwable cause) {
            String testName = context.getDisplayName();
            // Extrahiere den Modellnamen aus dem Testnamen (alles nach "=> ")
            String modelName = "unknown";
            int idx = testName.lastIndexOf("=>");
            if (idx != -1) {
                modelName = testName.substring(idx + 2).trim();
            }
            failedTests.computeIfAbsent(modelName, k -> new ArrayList<>()).add(testName);
        }
    };

    @AfterAll
    static void printStatistics() {
        System.out.println("\n--- Fehlerstatistik pro Modell ---");
        failedTests.forEach((modelName, tests) -> {
            System.out.println(modelName + ": " + tests.size() + " Fehler");
            tests.forEach(test -> System.out.println("  - " + test));
        });
    }


    /** Basis: Lander existiert und Größe kann gesetzt werden. */
    @ParameterizedTest(name = "{index} => {0}")
    @MethodSource("LunarLander.GameModelProvider#allModels")
    void testSomething(Object model) {
        var l = lander(model);
        setSize(l, new Dimension2D(2, 2));
        assertNotNull(l);
    }

    /**
     *      Aufgabe 1 a)
     * 
     *      Schubkontrolle Stufenweise
     */

    /**
     * Untere Schub-Grenze über changeThrustLevel darf nicht unterschritten werden.
     */
    @ParameterizedTest(name = "{index} lowerThrustChange => {0}")
    @MethodSource("LunarLander.GameModelProvider#allModels")
    void testLowerThrustBoundChange(Object model) {
        var l = lander(model);
        changeThrust(model, -1);
        assertEquals(0, thrustLevel(l));
    }

    /**
     * Obere Schub-Grenze über changeThrustLevel darf nicht überschritten werden.
     */
    @ParameterizedTest(name = "{index} upperThrustChange => {0}")
    @MethodSource("LunarLander.GameModelProvider#allModels")
    void testUpperThrustBoundChange(Object model) {
        var l = lander(model);
        changeThrust(model, 12);
        assertEquals(11, thrustLevel(l));
    }

    /** Untere Schub-Grenze bei direktem Setzen. */
    @ParameterizedTest(name = "{index} lowerThrustSet => {0}")
    @MethodSource("LunarLander.GameModelProvider#allModels")
    void testLowerThrustBoundSet(Object model) {
        var l = lander(model);
        setThrust(l, -1);
        assertEquals(0, thrustLevel(l));
    }

    /** Obere Schub-Grenze bei direktem Setzen. */
    @ParameterizedTest(name = "{index} upperThrustSet => {0}")
    @MethodSource("LunarLander.GameModelProvider#allModels")
    void testUpperThrustBoundSet(Object model) {
        var l = lander(model);
        setThrust(l, 12);
        assertEquals(11, thrustLevel(l));
    }

    /** changeThrustLevel(0) verändert den Schub nicht. */
    @ParameterizedTest(name = "{index} thrustChange0 => {0}")
    @MethodSource("LunarLander.GameModelProvider#allModels")
    void test0ThrustChange(Object model) {
        var l = lander(model);
        changeThrust(model, 0);
        assertEquals(0, thrustLevel(l));
    }

    /** Schrittweise Schuberhöhung 0→11 via changeThrustLevel(+1). */
    @ParameterizedTest(name = "{index} thrustUp => {0}")
    @MethodSource("LunarLander.GameModelProvider#allModels")
    void testThrustStepsUp(Object model) {
        var l = lander(model);
        for (int i = 1; i < 12; i++) {
            changeThrust(model, 1);
            assertEquals(i, thrustLevel(l));
        }
    }

    /** Schrittweise Schubreduzierung 11→0 via changeThrustLevel(-1). */
    @ParameterizedTest(name = "{index} thrustDown => {0}")
    @MethodSource("LunarLander.GameModelProvider#allModels")
    void testThrustStepsDown(Object model) {
        var l = lander(model);
        setThrust(l, 11);
        for (int i = 11; i > -1; i--) {
            assertEquals(i, thrustLevel(l));
            changeThrust(model, -1);
        }
    }

    /** toggleFullThrust: 0 → 11. */
    @ParameterizedTest(name = "{index} fullThrust0to11 => {0}")
    @MethodSource("LunarLander.GameModelProvider#allModels")
    void toggleFullThrust0to11(Object model) {
        var l = lander(model);
        assertEquals(0, thrustLevel(l));
        toggleFullThrust(model);
        assertEquals(11, thrustLevel(l));
    }

    /** toggleFullThrust: 5 → 0. */
    @ParameterizedTest(name = "{index} fullThrust5to0 => {0}")
    @MethodSource("LunarLander.GameModelProvider#allModels")
    void toggleFullThrust5to0(Object model) {
        var l = lander(model);
        changeThrust(model, 5);
        assertEquals(5, thrustLevel(l));
        toggleFullThrust(model);
        assertEquals(0, thrustLevel(l));
    }

    /** toggleFullThrust: 11 → 0. */
    @ParameterizedTest(name = "{index} fullThrust11to0 => {0}")
    @MethodSource("LunarLander.GameModelProvider#allModels")
    void toggleFullThrust11to0(Object model) {
        var l = lander(model);
        changeThrust(model, 11);
        assertEquals(11, thrustLevel(l));
        toggleFullThrust(model);
        assertEquals(0, thrustLevel(l));
    }

    /** Im Pause-Modus ändert sich die Beschleunigung nicht (z. B. trotz Thrust). */
    @ParameterizedTest(name = "{index} pauseStopsTime => {0}")
    @MethodSource("LunarLander.GameModelProvider#allModels")
    void testPauseStopsTime(Object model) {
        var l = lander(model);
        setThrust(l, 11);
        togglePause(model);
        updateElements(model, 1);
        assertEquals(new Point2D(0, 0), getAcceleration(l));
    }

    /** Nach startGame → togglePause ist der Zustand PAUSED. */
    @ParameterizedTest(name = "{index} pauseToggle => {0}")
    @MethodSource("LunarLander.GameModelProvider#allModels")
    void testPauseToggle(Object model) {
        startGame(model);
        togglePause(model);
        assertEquals(enumConstant(model, "GameState", "PAUSED"), getGameState(model));
    }

    /** Pause on-off-on: Beschleunigung bleibt 0 nach Update. */
    @ParameterizedTest(name = "{index} pauseStopsTimeOnOffOn => {0}")
    @MethodSource("LunarLander.GameModelProvider#allModels")
    void testPauseStopsTimeToggleOnOffOn(Object model) {
        var l = lander(model);
        setThrust(l, 11);
        togglePause(model);
        togglePause(model);
        togglePause(model);
        updateElements(model, 1);
        assertEquals(new Point2D(0, 0), getAcceleration(l));
    }

    /**
     *      Aufgabe 1 b)
     * 
     *      Kollision mit Hindernissen
     */

    /** Beim Erzeugen des Spiels gibt es 10 Hindernisse. */
    @ParameterizedTest(name = "{index} obstacleCount => {0}")
    @MethodSource("LunarLander.GameModelProvider#allModels")
    void testObstacleCount(Object model) {
        assertEquals(10, getObstacles(model).size());
    }

    /** Kollision: kein Kontakt → Spiel bleibt RUNNING. */
    @ParameterizedTest(name = "{index} collNoContact => {0}")
    @MethodSource("LunarLander.GameModelProvider#allModels")
    void testCollisionNoContact(Object model) {
        var l = lander(model);
        var obs = new ArrayList<Object>();
        obs.add(newTriangleForModel(model, new Point2D(10, 10), new Point2D(12, 10), new Point2D(10, 12)));
        setObstacles(model, obs);
        setSize(l, new Dimension2D(2, 2));
        setPos(l, new Point2D(20, 20));
        startGame(model);
        updateElements(model, 0);
        assertEquals(enumConstant(model, "GameState", "RUNNING"), getGameState(model));
    }

    /** Oberhalb der Spielfeldhöhe → GAME_OVER. */
    @ParameterizedTest(name = "{index} yOutOfBoundUpper => {0}")
    @MethodSource("LunarLander.GameModelProvider#allModels")
    void testLanderYOutOfBoundUpper(Object model) {
        var l = lander(model);
        setObstacles(model, new ArrayList<>());
        setSize(l, new Dimension2D(2, 2));
        float maxY = staticFloat(model, "GAME_AREA_HEIGHT");
        setPos(l, new Point2D(staticFloat(model, "GROUND_LEVEL") + 2, maxY + 100));
        startGame(model);
        updateElements(model, 0);
        assertEquals(enumConstant(model, "GameState", "GAME_OVER"), getGameState(model));
    }

    /** Unterhalb der Spielfeldhöhe → GAME_OVER. */
    @ParameterizedTest(name = "{index} yOutOfBoundLower => {0}")
    @MethodSource("LunarLander.GameModelProvider#allModels")
    void testLanderYOutOfBoundLower(Object model) {
        var l = lander(model);
        setObstacles(model, new ArrayList<>());
        setSize(l, new Dimension2D(2, 2));
        setPos(l, new Point2D(staticFloat(model, "GROUND_LEVEL") + 2, -10));
        startGame(model);
        updateElements(model, 0);
        assertEquals(enumConstant(model, "GameState", "GAME_OVER"), getGameState(model));
    }

    /** Rechts außerhalb der Spielfeldbreite → GAME_OVER. */
    @ParameterizedTest(name = "{index} xOutOfBoundUpper => {0}")
    @MethodSource("LunarLander.GameModelProvider#allModels")
    void testLanderXOutOfBoundUpper(Object model) {
        var l = lander(model);
        setObstacles(model, new ArrayList<>());
        setSize(l, new Dimension2D(2, 2));
        float maxX = staticFloat(model, "GAME_AREA_WIDTH");
        setPos(l, new Point2D(staticFloat(model, "GROUND_LEVEL") + 2, maxX + 20));
        startGame(model);
        updateElements(model, 0);
        assertEquals(enumConstant(model, "GameState", "GAME_OVER"), getGameState(model));
    }

    /** Links außerhalb der Spielfeldbreite → GAME_OVER. */
    @ParameterizedTest(name = "{index} xOutOfBoundLower => {0}")
    @MethodSource("LunarLander.GameModelProvider#allModels")
    void testLanderXOutOfBoundLower(Object model) {
        var l = lander(model);
        setObstacles(model, new ArrayList<>());
        setSize(l, new Dimension2D(2, 2));
        setPos(l, new Point2D(staticFloat(model, "GROUND_LEVEL") + 2, -10));
        startGame(model);
        updateElements(model, 0);
        assertEquals(enumConstant(model, "GameState", "GAME_OVER"), getGameState(model));
    }

    /** Neigungs-Test ohne Kontakt: RUNNING. */
    @ParameterizedTest(name = "{index} collNoContactTilted => {0}")
    @MethodSource("LunarLander.GameModelProvider#allModels")
    void testCollisionNoContactTilted(Object model) {
        var l = lander(model);
        var obs = new ArrayList<Object>();
        obs.add(newTriangleForModel(model, new Point2D(10, 10), new Point2D(12, 10), new Point2D(10, 12)));
        setObstacles(model, obs);
        setSize(l, new Dimension2D(2, 2));
        setPos(l, new Point2D(20, 20));
        call(model, "changeTilt", new Class<?>[] { double.class }, 45.0);
        startGame(model);
        updateElements(model, 0);
        assertEquals(enumConstant(model, "GameState", "RUNNING"), getGameState(model));
    }

    /** Teilkontakt mit Hindernis → GAME_OVER. */
    @ParameterizedTest(name = "{index} collContact => {0}")
    @MethodSource("LunarLander.GameModelProvider#allModels")
    void testCollisionContact(Object model) {
        var l = lander(model);
        var obs = new ArrayList<Object>();
        obs.add(newTriangleForModel(model, new Point2D(10, 10), new Point2D(12, 10), new Point2D(10, 12)));
        setObstacles(model, obs);
        setSize(l, new Dimension2D(2, 2));
        setPos(l, new Point2D(10, 12));
        startGame(model);
        updateElements(model, 0);
        assertEquals(enumConstant(model, "GameState", "GAME_OVER"), getGameState(model));
    }

    /** Berührung an einer Seite, aber keine Kollision → RUNNING. */
    @ParameterizedTest(name = "{index} collTouchingSide => {0}")
    @MethodSource("LunarLander.GameModelProvider#allModels")
    void testCollisionTouchingSide(Object model) {
        var l = lander(model);
        var obs = new ArrayList<Object>();
        obs.add(newTriangleForModel(model, new Point2D(10, 10), new Point2D(12, 10), new Point2D(10, 12)));
        setObstacles(model, obs);
        setSize(l, new Dimension2D(2, 2));
        setPos(l, new Point2D(9, 11));
        startGame(model);
        updateElements(model, 0);
        assertEquals(enumConstant(model, "GameState", "RUNNING"), getGameState(model));
    }

    /** Berührung an einer Ecke, aber keine Kollision → RUNNING. */
    @ParameterizedTest(name = "{index} collTouchingCorner => {0}")
    @MethodSource("LunarLander.GameModelProvider#allModels")
    void testCollisionTouchingCorner(Object model) {
        var l = lander(model);
        var obs = new ArrayList<Object>();
        obs.add(newTriangleForModel(model, new Point2D(10, 10), new Point2D(12, 10), new Point2D(10, 12)));
        setObstacles(model, obs);
        setSize(l, new Dimension2D(2, 2));
        setPos(l, new Point2D(13, 11));
        startGame(model);
        updateElements(model, 0);
        assertEquals(enumConstant(model, "GameState", "RUNNING"), getGameState(model));
    }

    /** Komplett im Hindernis, Seiten berühren → GAME_OVER. */
    @ParameterizedTest(name = "{index} collSidesInside => {0}")
    @MethodSource("LunarLander.GameModelProvider#allModels")
    void testCollisionTouchingSidesInside(Object model) {
        var l = lander(model);
        var obs = new ArrayList<Object>();
        obs.add(newTriangleForModel(model, new Point2D(10, 10), new Point2D(12, 10), new Point2D(10, 12)));
        setObstacles(model, obs);
        setSize(l, new Dimension2D(1, 1));
        setPos(l, new Point2D(10.5, 10.5));
        startGame(model);
        updateElements(model, 0);
        assertEquals(enumConstant(model, "GameState", "GAME_OVER"), getGameState(model));
    }

    /** Komplett im Hindernis, Ecke-zu-Seite → GAME_OVER. */
    @ParameterizedTest(name = "{index} collCornerToSideInside => {0}")
    @MethodSource("LunarLander.GameModelProvider#allModels")
    void testCollisionTouchingCornerToSideInside(Object model) {
        var l = lander(model);
        var obs = new ArrayList<Object>();
        obs.add(newTriangleForModel(model, new Point2D(10, 10), new Point2D(14, 10), new Point2D(10, 14)));
        setObstacles(model, obs);
        setSize(l, new Dimension2D(1, 1));
        setPos(l, new Point2D(11.5, 11.5));
        startGame(model);
        updateElements(model, 0);
        assertEquals(enumConstant(model, "GameState", "GAME_OVER"), getGameState(model));
    }

    /** Komplett im Hindernis, kein Seitenkontakt → GAME_OVER. */
    @ParameterizedTest(name = "{index} collNoTouchInside => {0}")
    @MethodSource("LunarLander.GameModelProvider#allModels")
    void testCollisionNoTouchingInside(Object model) {
        var l = lander(model);
        var obs = new ArrayList<Object>();
        obs.add(newTriangleForModel(model, new Point2D(10, 10), new Point2D(14, 10), new Point2D(10, 14)));
        setObstacles(model, obs);
        setSize(l, new Dimension2D(1, 1));
        setPos(l, new Point2D(10.5, 10.5));
        startGame(model);
        updateElements(model, 0);
        assertEquals(enumConstant(model, "GameState", "GAME_OVER"), getGameState(model));
    }

    /** Landung ohne Crash: tilt=0°, v=(0,0), Score = 11_970_000. */
    @ParameterizedTest(name = "{index} landingNoCrash0deg0vel => {0}")
    @MethodSource("LunarLander.GameModelProvider#allModels")
    void testLandingNoCrashNoTiltNoSpeed(Object model) {
        var l = lander(model);
        setObstacles(model, new ArrayList<>());
        setSize(l, new Dimension2D(1, 1));
        float ground = staticFloat(model, "GROUND_LEVEL");
        float width = staticFloat(model, "GAME_AREA_WIDTH");
        setPos(l, new Point2D(ground + 0.5, width / 2 - 0.5));
        startGame(model);
        updateElements(model, 0);
        assertEquals(enumConstant(model, "GameState", "SHOW_SCORE"), getGameState(model));
        assertEquals(11_970_000, ((Number) call(model, "getCurrentScore", new Class<?>[] {})).intValue());
    }

    /** Landung ohne Crash: tilt=+3°, v=(0,0), gleicher Score. */
    @ParameterizedTest(name = "{index} landingNoCrash±3deg => {0}")
    @MethodSource("LunarLander.GameModelProvider#allModels")
    void testLandingNoCrashTilt3NoSpeed(Object model) {
        var l = lander(model);
        setObstacles(model, new ArrayList<>());
        setSize(l, new Dimension2D(1, 1));
        float ground = staticFloat(model, "GROUND_LEVEL");
        float width = staticFloat(model, "GAME_AREA_WIDTH");
        setPos(l, new Point2D(ground + 0.5, width / 2 - 0.5));
        setTilt(l, 3);
        startGame(model);
        updateElements(model, 0);
        assertEquals(enumConstant(model, "GameState", "SHOW_SCORE"), getGameState(model));
        assertEquals(11_970_000, ((Number) call(model, "getCurrentScore", new Class<?>[] {})).intValue());
    }

    /** Landung ohne Crash: tilt=-3°, v=(0,0), gleicher Score. */
    @ParameterizedTest(name = "{index} landingNoCrash-3deg => {0}")
    @MethodSource("LunarLander.GameModelProvider#allModels")
    void testLandingNoCrashTiltMinus3NoSpeed(Object model) {
        var l = lander(model);
        setObstacles(model, new ArrayList<>());
        setSize(l, new Dimension2D(1, 1));
        float ground = staticFloat(model, "GROUND_LEVEL");
        float width = staticFloat(model, "GAME_AREA_WIDTH");
        setPos(l, new Point2D(ground + 0.5, width / 2 - 0.5));
        setTilt(l, -3);
        startGame(model);
        updateElements(model, 0);
        assertEquals(enumConstant(model, "GameState", "SHOW_SCORE"), getGameState(model));
        assertEquals(11_970_000, ((Number) call(model, "getCurrentScore", new Class<?>[] {})).intValue());
    }

    /** Landung ohne Crash: tilt=±9°, v=(0,0), gleicher Score. */
    @ParameterizedTest(name = "{index} landingNoCrash±9deg => {0}")
    @MethodSource("LunarLander.GameModelProvider#allModels")
    void testLandingNoCrashTilt9NoSpeed(Object model) {
        var l = lander(model);
        setObstacles(model, new ArrayList<>());
        setSize(l, new Dimension2D(1, 1));
        float ground = staticFloat(model, "GROUND_LEVEL");
        float width = staticFloat(model, "GAME_AREA_WIDTH");
        setPos(l, new Point2D(ground + 0.5, width / 2 - 0.5));
        setTilt(l, 9);
        startGame(model);
        updateElements(model, 0);
        assertEquals(enumConstant(model, "GameState", "SHOW_SCORE"), getGameState(model));
        assertEquals(11_970_000, ((Number) call(model, "getCurrentScore", new Class<?>[] {})).intValue());
    }

    /** Landung ohne Crash: tilt=-9°, v=(0,0), gleicher Score. */
    @ParameterizedTest(name = "{index} landingNoCrash-9deg => {0}")
    @MethodSource("LunarLander.GameModelProvider#allModels")
    void testLandingNoCrashTiltMinus9NoSpeed(Object model) {
        var l = lander(model);
        setObstacles(model, new ArrayList<>());
        setSize(l, new Dimension2D(1, 1));
        float ground = staticFloat(model, "GROUND_LEVEL");
        float width = staticFloat(model, "GAME_AREA_WIDTH");
        setPos(l, new Point2D(ground + 0.5, width / 2 - 0.5));
        setTilt(l, -9);
        startGame(model);
        updateElements(model, 0);
        assertEquals(enumConstant(model, "GameState", "SHOW_SCORE"), getGameState(model));
        assertEquals(11_970_000, ((Number) call(model, "getCurrentScore", new Class<?>[] {})).intValue());
    }

    /** Landung ohne Crash: v=(0,-1) bzw. (0,1), Score = 119. */
    @ParameterizedTest(name = "{index} landingNoCrash v=(0,-1) => {0}")
    @MethodSource("LunarLander.GameModelProvider#allModels")
    void testLandingNoCrashNoTiltSpeedMinus1(Object model) {
        var l = lander(model);
        setObstacles(model, new ArrayList<>());
        setSize(l, new Dimension2D(1, 1));
        float ground = staticFloat(model, "GROUND_LEVEL");
        float width = staticFloat(model, "GAME_AREA_WIDTH");
        setPos(l, new Point2D(ground + 0.5, width / 2 - 0.5));
        setSpeed(l, new Point2D(0, -1));
        startGame(model);
        updateElements(model, 0);
        assertEquals(enumConstant(model, "GameState", "SHOW_SCORE"), getGameState(model));
        assertEquals(119, ((Number) call(model, "getCurrentScore", new Class<?>[] {})).intValue());
    }

    /** Landung ohne Crash: v=(0,1), Score = 119. */
    @ParameterizedTest(name = "{index} landingNoCrash v=(0,1) => {0}")
    @MethodSource("LunarLander.GameModelProvider#allModels")
    void testLandingNoCrashNoTiltSpeed1(Object model) {
        var l = lander(model);
        setObstacles(model, new ArrayList<>());
        setSize(l, new Dimension2D(1, 1));
        float ground = staticFloat(model, "GROUND_LEVEL");
        float width = staticFloat(model, "GAME_AREA_WIDTH");
        setPos(l, new Point2D(ground + 0.5, width / 2 - 0.5));
        setSpeed(l, new Point2D(0, 1));
        startGame(model);
        updateElements(model, 0);
        assertEquals(enumConstant(model, "GameState", "SHOW_SCORE"), getGameState(model));
        assertEquals(119, ((Number) call(model, "getCurrentScore", new Class<?>[] {})).intValue());
    }

    /** Crash-Szenario: tilt=45°, v=(0,0) → GAME_OVER. */
    @ParameterizedTest(name = "{index} landingCrash tilted => {0}")
    @MethodSource("LunarLander.GameModelProvider#allModels")
    void testLandingCrashTilted(Object model) {
        var l = lander(model);
        setObstacles(model, new ArrayList<>());
        setSize(l, new Dimension2D(1, 1));
        float ground = staticFloat(model, "GROUND_LEVEL");
        float width = staticFloat(model, "GAME_AREA_WIDTH");
        setPos(l, new Point2D(ground + 0.5, width / 2 - 0.5));
        setTilt(l, 45);
        startGame(model);
        updateElements(model, 0);
        assertEquals(enumConstant(model, "GameState", "GAME_OVER"), getGameState(model));
    }

    /** Crash-Szenario: v=(0,-3) → GAME_OVER. */
    @ParameterizedTest(name = "{index} landingCrash v=(0,-3) => {0}")
    @MethodSource("LunarLander.GameModelProvider#allModels")
    void testLandingCrashSpeedMinus3(Object model) {
        var l = lander(model);
        setObstacles(model, new ArrayList<>());
        setSize(l, new Dimension2D(1, 1));
        float ground = staticFloat(model, "GROUND_LEVEL");
        float width = staticFloat(model, "GAME_AREA_WIDTH");
        setPos(l, new Point2D(ground + 0.5, width / 2 - 0.5));
        setSpeed(l, new Point2D(0, -3));
        startGame(model);
        updateElements(model, 0);
        assertEquals(enumConstant(model, "GameState", "GAME_OVER"), getGameState(model));
    }

    /** Crash-Szenario: v=(0,3) → GAME_OVER. */
    @ParameterizedTest(name = "{index} landingCrash v=(0,3) => {0}")
    @MethodSource("LunarLander.GameModelProvider#allModels")
    void testLandingCrashSpeed3(Object model) {
        var l = lander(model);
        setObstacles(model, new ArrayList<>());
        setSize(l, new Dimension2D(1, 1));
        float ground = staticFloat(model, "GROUND_LEVEL");
        float width = staticFloat(model, "GAME_AREA_WIDTH");
        setPos(l, new Point2D(ground + 0.5, width / 2 - 0.5));
        setSpeed(l, new Point2D(0, 3));
        startGame(model);
        updateElements(model, 0);
        assertEquals(enumConstant(model, "GameState", "GAME_OVER"), getGameState(model));
    }

    /** Crash-Szenario: v=(0,-3) und tilt=45° → GAME_OVER. */
    @ParameterizedTest(name = "{index} landingCrash v=(0,-3),tilt=45 => {0}")
    @MethodSource("LunarLander.GameModelProvider#allModels")
    void testLandingCrashSpeedAndTilt(Object model) {
        var l = lander(model);
        setObstacles(model, new ArrayList<>());
        setSize(l, new Dimension2D(1, 1));
        float ground = staticFloat(model, "GROUND_LEVEL");
        float width = staticFloat(model, "GAME_AREA_WIDTH");
        setPos(l, new Point2D(ground + 0.5, width / 2 - 0.5));
        setSpeed(l, new Point2D(0, -3));
        setTilt(l, 45);
        startGame(model);
        updateElements(model, 0);
        assertEquals(enumConstant(model, "GameState", "GAME_OVER"), getGameState(model));
    }
}
