package LunarLander;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.*;
import java.util.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;

// run tests with: mvn -DtrimStackTrace=false -Dsurefire.printSummary=true test

public class TestLanderParameterized {

    // ---------- tiny reflection helpers ----------

    /**
     * Ruft per Reflection eine öffentliche Methode auf und gibt das Ergebnis
     * typisiert zurück.
     */
    @SuppressWarnings("unchecked")
    private static <T> T call(Object target, String name, Class<?>[] paramTypes, Object... args) {
        try {
            Method m = target.getClass().getMethod(name, paramTypes);
            return (T) m.invoke(target, args);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed calling " + name + " on " + target.getClass(), e);
        }
    }

    /** Liefert das Lander-Objekt aus dem GameModel. */
    private static Object lander(Object model) {
        return call(model, "getLander", new Class<?>[] {});
    }

    /** Liefert den aktuellen Schub-Level des Landers. */
    private static int thrustLevel(Object lander) {
        return call(lander, "getThrustLevel", new Class<?>[] {});
    }

    /** Setzt den Schub-Level direkt. */
    private static void setThrust(Object lander, int v) {
        call(lander, "setThrustLevel", new Class<?>[] { int.class }, v);
    }

    /** Ändert den Schub-Level relativ (z. B. +1 oder -1). */
    private static void changeThrust(Object model, int delta) {
        call(model, "changeThrustLevel", new Class<?>[] { int.class }, delta);
    }

    /** Toggle für vollen Schub (0 ↔ 11 je nach Modell). */
    private static void toggleFullThrust(Object model) {
        call(model, "toggleFullThrust", new Class<?>[] {});
    }

    /** Setzt den Tilt (Grad). */
    private static void setTilt(Object lander, double deg) {
        call(lander, "setTilt", new Class<?>[] { double.class }, deg);
    }

    /** Setzt die Größe des Landers. */
    private static void setSize(Object lander, Dimension2D d) {
        call(lander, "setSize", new Class<?>[] { Dimension2D.class }, d);
    }

    /** Setzt die Position des Landers. */
    private static void setPos(Object lander, Point2D p) {
        call(lander, "setPos", new Class<?>[] { Point2D.class }, p);
    }

    /** Setzt die Geschwindigkeit des Landers. */
    private static void setSpeed(Object lander, Point2D v) {
        call(lander, "setSpeed", new Class<?>[] { Point2D.class }, v);
    }

    /** Liefert die aktuelle Beschleunigung des Landers. */
    private static Point2D getAcceleration(Object lander) {
        return call(lander, "getAcceleration", new Class<?>[] {});
    }

    /** Startet das Spiel. */
    private static void startGame(Object model) {
        call(model, "startGame", new Class<?>[] {});
    }

    /** Schaltet den Pause-Modus um. */
    private static void togglePause(Object model) {
        call(model, "togglePause", new Class<?>[] {});
    }

    /** Führt eine Model-Update-Schrittweite aus (dt in Ticks/Frames). */
    private static void updateElements(Object model, double dt) {
        call(model, "updateElements", new Class<?>[] { double.class }, dt);
    }

    /** Liefert den aktuellen Spielzustand (Enum-Instanz). */
    private static Object getGameState(Object model) {
        return call(model, "getGameState", new Class<?>[] {});
    }

    /** Liefert die Hindernisliste (Triangles) aus dem Modell. */
    @SuppressWarnings("unchecked")
    private static List<Object> getObstacles(Object model) {
        return (List<Object>) call(model, "getObstacles", new Class<?>[] {});
    }

    /** Setzt die Hindernisliste (Triangles) im Modell. */
    private static void setObstacles(Object model, ArrayList<Object> obs) {
        call(model, "setObstacles", new Class<?>[] { ArrayList.class }, obs);
    }

    /** Liest ein statisches float-Feld (z. B. GAME_AREA_HEIGHT) per Reflection. */
    private static float staticFloat(Object model, String fieldName) {
        try {
            Field f = model.getClass().getField(fieldName);
            return f.getFloat(null);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to read static float " + fieldName, e);
        }
    }

    /** Erzeugt ein Triangle-Objekt aus dem Paket des konkreten Modells. */
    private static Object newTriangleForModel(Object model, Point2D a, Point2D b, Point2D c) {
        try {
            String pkg = model.getClass().getPackageName();
            Class<?> tri = Class.forName(pkg + ".Triangle");
            Constructor<?> ctor = tri.getConstructor(Point2D.class, Point2D.class, Point2D.class);
            return ctor.newInstance(a, b, c);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to construct Triangle for " + model.getClass(), e);
        }
    }
    // ---------------------------------------------

    /** Basis: Lander existiert und Größe kann gesetzt werden. */
    @ParameterizedTest(name = "{index} => {0}")
    @MethodSource("LunarLander.GameModelProvider#allModels")
    void testSomething(Object model) {
        var l = lander(model);
        setSize(l, new Dimension2D(2, 2));
        assertNotNull(l);
    }

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
}
