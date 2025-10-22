package LunarLander;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javafx.geometry.Dimension2D;

public class TestLander {

	// --- tiny reflection helpers ---
	@SuppressWarnings("unchecked")
	private static <T> T call(Object target, String name, Class<?>[] paramTypes, Object... args) {
		try {
			Method m = target.getClass().getMethod(name, paramTypes);
			return (T) m.invoke(target, args);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException("Failed calling " + name + " on " + target.getClass(), e);
		}
	}

	private static Object lander(Object model) {
		return call(model, "getLander", new Class<?>[] {});
	}

	private static int thrustLevel(Object lander) {
		return call(lander, "getThrustLevel", new Class<?>[] {});
	}

	private static int tiltAngle(Object lander) {
		return call(lander, "getTiltAngle", new Class<?>[] {});
	}

	private static void setThrust(Object lander, int v) {
		call(lander, "setThrustLevel", new Class<?>[] { int.class }, v);
	}

	private static void setTilt(Object lander, double v) {
		call(lander, "setTilt", new Class<?>[] { double.class }, v);
	}

	private static void setSize(Object lander, Dimension2D d) {
		call(lander, "setSize", new Class<?>[] { Dimension2D.class }, d);
	}
	// --------------------------------

	@ParameterizedTest(name = "{index} => {0}")
	@MethodSource("LunarLander.GameModelProvider#allModels")
	void testSomething(Object model) {
		var l = lander(model);
		setSize(l, new Dimension2D(2, 2));
		assertNotNull(l);
	}

	@ParameterizedTest(name = "{index} => {0}")
	@MethodSource("LunarLander.GameModelProvider#allModels")
	void testThrustLevelLowerBound(Object model) {
		var l = lander(model);
		setThrust(l, -1);
		assertEquals(0, thrustLevel(l));
	}

	@ParameterizedTest(name = "{index} => {0}")
	@MethodSource("LunarLander.GameModelProvider#allModels")
	void testThrustLevelHigherBound(Object model) {
		var l = lander(model);
		setThrust(l, 12);
		assertEquals(11, thrustLevel(l));
	}

	@ParameterizedTest(name = "{index} => {0}")
	@MethodSource("LunarLander.GameModelProvider#allModels")
	void testTiltAngle(Object model) {
		var l = lander(model);
		setTilt(l, 45.0);
		assertEquals(45.0, tiltAngle(l));
	}
}
