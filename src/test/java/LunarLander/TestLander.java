package LunarLander;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javafx.geometry.Dimension2D;
import model0.*;

public class TestLander {
	@Test
	public void testSomething() {
		final GameModel model = GameFactory.createGame();
		final Lander lander = model.getLander();
		lander.setSize(new Dimension2D(2, 2));
		Assertions.assertNotNull(lander);
	}

	@Test
	public void testThrustLevelLowerBound() {
		final GameModel model = GameFactory.createGame();
		model.getLander().setThrustLevel(-1);
		int thrustlevel = model.getLander().getThrustLevel();
		assertEquals(thrustlevel, 0);
	}

	@Test
	public void testThrustLevelHigherBound() {
		final GameModel model = GameFactory.createGame();
		model.getLander().setThrustLevel(12);
		int thrustlevel = model.getLander().getThrustLevel();
		assertEquals(thrustlevel, 11);
	}

	@Test
	public void testTiltAngle() {
		final GameModel model = GameFactory.createGame();
		model.getLander().setTilt(45);
		int tiltagle = model.getLander().getTiltAngle();
		assertEquals(tiltagle, 45);
	}
}
