package LunarLander;

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
	public void testSomethingElse() {
		Assertions.assertNotNull(null);
	}
}
