package LunarLander;

import java.lang.reflect.*;
import java.util.*;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;

// run tests with: mvn -DtrimStackTrace=false -Dsurefire.printSummary=true test

public class GameModelAccess {

    // ---------- tiny reflection helpers ----------

    /**
     * Ruft per Reflection eine öffentliche Methode auf und gibt das Ergebnis
     * typisiert zurück.
     */
    @SuppressWarnings("unchecked")
    public static <T> T call(Object target, String name, Class<?>[] paramTypes, Object... args) {
        try {
            Method m = target.getClass().getMethod(name, paramTypes);
            return (T) m.invoke(target, args);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed calling " + name + " on " + target.getClass(), e);
        }
    }

    /** Liefert das Lander-Objekt aus dem GameModel. */
    public static Object lander(Object model) {
        return call(model, "getLander", new Class<?>[] {});
    }

    /** Liefert den aktuellen Schub-Level des Landers. */
    public static int thrustLevel(Object lander) {
        return call(lander, "getThrustLevel", new Class<?>[] {});
    }

    /** Setzt den Schub-Level direkt. */
    public static void setThrust(Object lander, int v) {
        call(lander, "setThrustLevel", new Class<?>[] { int.class }, v);
    }

    /** Ändert den Schub-Level relativ (z. B. +1 oder -1). */
    public static void changeThrust(Object model, int delta) {
        call(model, "changeThrustLevel", new Class<?>[] { int.class }, delta);
    }

    /** Toggle für vollen Schub (0 ↔ 11 je nach Modell). */
    public static void toggleFullThrust(Object model) {
        call(model, "toggleFullThrust", new Class<?>[] {});
    }

    /** Setzt den Tilt (Grad). */
    public static void setTilt(Object lander, double deg) {
        call(lander, "setTilt", new Class<?>[] { double.class }, deg);
    }

    /** Setzt die Größe des Landers. */
    public static void setSize(Object lander, Dimension2D d) {
        call(lander, "setSize", new Class<?>[] { Dimension2D.class }, d);
    }

    /** Setzt die Position des Landers. */
    public static void setPos(Object lander, Point2D p) {
        call(lander, "setPos", new Class<?>[] { Point2D.class }, p);
    }

    /** Setzt die Geschwindigkeit des Landers. */
    public static void setSpeed(Object lander, Point2D v) {
        call(lander, "setSpeed", new Class<?>[] { Point2D.class }, v);
    }

    /** Liefert die aktuelle Beschleunigung des Landers. */
    public static Point2D getAcceleration(Object lander) {
        return call(lander, "getAcceleration", new Class<?>[] {});
    }

    /** Startet das Spiel. */
    public static void startGame(Object model) {
        call(model, "startGame", new Class<?>[] {});
    }

    /** Schaltet den Pause-Modus um. */
    public static void togglePause(Object model) {
        call(model, "togglePause", new Class<?>[] {});
    }

    /** Führt eine Model-Update-Schrittweite aus (dt in Ticks/Frames). */
    public static void updateElements(Object model, double dt) {
        call(model, "updateElements", new Class<?>[] { double.class }, dt);
    }

    /** Liefert den aktuellen Spielzustand (Enum-Instanz). */
    public static Object getGameState(Object model) {
        return call(model, "getGameState", new Class<?>[] {});
    }

    /** Liefert die Hindernisliste (Triangles) aus dem Modell. */
    @SuppressWarnings("unchecked")
    public static List<Object> getObstacles(Object model) {
        return (List<Object>) call(model, "getObstacles", new Class<?>[] {});
    }

    /** Setzt die Hindernisliste (Triangles) im Modell. */
    public static void setObstacles(Object model, ArrayList<Object> obs) {
        call(model, "setObstacles", new Class<?>[] { ArrayList.class }, obs);
    }

    /** Liest ein statisches float-Feld (z. B. GAME_AREA_HEIGHT) per Reflection. */
    public static float staticFloat(Object model, String fieldName) {
        try {
            Field f = model.getClass().getField(fieldName);
            return f.getFloat(null);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to read static float " + fieldName, e);
        }
    }

    /** Erzeugt ein Triangle-Objekt aus dem Paket des konkreten Modells. */
    public static Object newTriangleForModel(Object model, Point2D a, Point2D b, Point2D c) {
        try {
            String pkg = model.getClass().getPackageName();
            Class<?> tri = Class.forName(pkg + ".Triangle");
            Constructor<?> ctor = tri.getConstructor(Point2D.class, Point2D.class, Point2D.class);
            return ctor.newInstance(a, b, c);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to construct Triangle for " + model.getClass(), e);
        }
    }
}