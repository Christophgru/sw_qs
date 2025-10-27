// package LunarLander;

// import java.util.ArrayList;
// import java.util.List;

// import org.junit.jupiter.api.Assertions;
// import org.junit.jupiter.api.Test;

// import javafx.geometry.Dimension2D;
// import javafx.geometry.Point2D;
// import model0.*;

// public class TestLander {

// 	// example
// 	@Test
// 	public void testSomething() {
// 		final GameModel model = GameFactory.createGame();
// 		final Lander lander = model.getLander();
// 		lander.setSize(new Dimension2D(2, 2));
// 		Assertions.assertNotNull(lander);
// 	}

// 	// Test if lower bound of thrust can be violated via changeThrustLevel
// 	@Test
// 	public void testLowerThrustBoundChange() {
// 		final GameModel model = GameFactory.createGame();
// 		final Lander lander = model.getLander();
// 		model.changeThrustLevel(-1);
// 		Assertions.assertEquals(lander.getThrustLevel(), 0);
// 	}

// 	// Test if upper bound of thrust can be violated via changeThrustLevel
// 	@Test
// 	public void testUpperThrustBoundChange() {
// 		final GameModel model = GameFactory.createGame();
// 		final Lander lander = model.getLander();
// 		model.changeThrustLevel(12);
// 		Assertions.assertEquals(lander.getThrustLevel(), 11);
// 	}

// 	// Test if lower bound of thrust can be violated via setThrustLevel
// 	@Test
// 	public void testLowerThrustBoundSet() {
// 		final GameModel model = GameFactory.createGame();
// 		final Lander lander = model.getLander();
// 		lander.setThrustLevel(-1);
// 		Assertions.assertEquals(0, lander.getThrustLevel());
// 	}

// 	// Test if upper bound of thrust can be violated via setThrustLevel
// 	@Test
// 	public void testUpperThrustBoundSet() {
// 		final GameModel model = GameFactory.createGame();
// 		final Lander lander = model.getLander();
// 		lander.setThrustLevel(12);
// 		Assertions.assertEquals(11, lander.getThrustLevel());
// 	}

// 	// Test if Thrust changes when calling changeThrustLevel with 0
// 	@Test
// 	public void test0ThrustChange() {
// 		final GameModel model = GameFactory.createGame();
// 		final Lander lander = model.getLander();
// 		model.changeThrustLevel(0);
// 		Assertions.assertEquals(0, lander.getThrustLevel());
// 	}

// 	// Test every upward Thrust Step for correctness
// 	@Test
// 	public void testThrustStepsUp() {
// 		final GameModel model = GameFactory.createGame();
// 		final Lander lander = model.getLander();
// 		for (int i = 1; i < 12; i++) {
// 			model.changeThrustLevel(1);
// 			Assertions.assertEquals(i, lander.getThrustLevel());
// 		}
// 	}

// 	// Test every downward Thrust Step for correctness
// 	@Test
// 	public void testThrustStepsDown() {
// 		final GameModel model = GameFactory.createGame();
// 		final Lander lander = model.getLander();
// 		lander.setThrustLevel(11);
// 		for (int i = 11; i > -1; i--) {
// 			Assertions.assertEquals(i, lander.getThrustLevel());
// 			model.changeThrustLevel(-1);
// 		}
// 	}

// 	// Test if toggleFullThrust changes Thrust to 11 if its at 0
// 	@Test
// 	public void toggleFullThrust0to11() { // model3 fails
// 		final GameModel model = GameFactory.createGame();
// 		final Lander lander = model.getLander();
// 		Assertions.assertEquals(0, lander.getThrustLevel());
// 		model.toggleFullThrust();
// 		Assertions.assertEquals(11, lander.getThrustLevel());
// 	}

// 	// Test if toggleFullThrust changes Thrust to 0 if its at 5
// 	@Test
// 	public void toggleFullThrust5to0() {
// 		final GameModel model = GameFactory.createGame();
// 		final Lander lander = model.getLander();
// 		model.changeThrustLevel(5);
// 		Assertions.assertEquals(5, lander.getThrustLevel());
// 		model.toggleFullThrust();
// 		Assertions.assertEquals(0, lander.getThrustLevel());
// 	}

// 	// Test if toggleFullThrust changes Thrust to 0 if its at 11
// 	@Test
// 	public void toggleFullThrust11to0() {
// 		final GameModel model = GameFactory.createGame();
// 		final Lander lander = model.getLander();
// 		model.changeThrustLevel(11);
// 		Assertions.assertEquals(11, lander.getThrustLevel());
// 		model.toggleFullThrust();
// 		Assertions.assertEquals(0, lander.getThrustLevel());
// 	}

// 	// Test if acceleration of lander changes during pause mode
// 	@Test
// 	public void testPauseStopsTime() {
// 		final GameModel model = GameFactory.createGame();
// 		final Lander lander = model.getLander();
// 		lander.setThrustLevel(11);
// 		model.togglePause();
// 		model.updateElements(1);
// 		Assertions.assertEquals(new Point2D(0, 0), lander.getAcceleration());
// 	}

// 	// Test if pause mode gets applied
// 	@Test
// 	public void testPauseToggle() {
// 		final GameModel model = GameFactory.createGame();
// 		model.startGame();
// 		model.togglePause();
// 		Assertions.assertEquals(GameState.PAUSED, model.getGameState());
// 	}

// 	// Test if acceleration of lander changes during pause mode, if its toggled on -
// 	// off - on
// 	@Test
// 	public void testPauseStopsTimeToggleOnOffOn() {
// 		final GameModel model = GameFactory.createGame();
// 		final Lander lander = model.getLander();
// 		lander.setThrustLevel(11);
// 		model.togglePause();
// 		model.togglePause();
// 		model.togglePause();
// 		model.updateElements(1);
// 		Assertions.assertEquals(new Point2D(0, 0), lander.getAcceleration());
// 	}

// 	// Test if obstacle count is set to 10 on createGame
// 	@Test
// 	public void testObstacleCount() {
// 		final GameModel model = GameFactory.createGame();
// 		final List<Triangle> obstacles = model.getObstacles();
// 		Assertions.assertEquals(10, obstacles.size());
// 	}

// 	// Test if Collision is detected when lander has no contact with obstacle
// 	@Test
// 	public void testCollisionNoContact() { // model1 fails
// 		final GameModel model = GameFactory.createGame();
// 		final Lander lander = model.getLander();
// 		ArrayList<Triangle> obstacles = new ArrayList<Triangle>();
// 		obstacles.add(new Triangle(new Point2D(10, 10), new Point2D(12, 10), new Point2D(10, 12)));
// 		model.setObstacles(obstacles);
// 		lander.setSize(new Dimension2D(2, 2));
// 		lander.setPos(new Point2D(20, 20));
// 		model.startGame();
// 		model.updateElements(0);
// 		Assertions.assertEquals(GameState.RUNNING, model.getGameState());
// 	}

// 	// @Test
// 	// public void testLanderYOutOfBound() { // all models fail
// 	// final GameModel model = GameFactory.createGame();
// 	// final Lander lander = model.getLander();
// 	// ArrayList<Triangle> obstacles = new ArrayList<Triangle>();
// 	// model.setObstacles(obstacles);
// 	// lander.setSize(new Dimension2D(2, 2));
// 	// final float max_y = GameModel.GAME_AREA_HEIGHT;
// 	// lander.setPos(new Point2D(0, max_y + 1));
// 	// model.startGame();
// 	// model.updateElements(0);
// 	// Assertions.assertEquals(GameState.GAME_OVER, model.getGameState());
// 	// }

// 	// Test is Game is lost when violating upper game height
// 	@Test
// 	public void testLanderYOutOfBoundUpper() { // model1 fails
// 		final GameModel model = GameFactory.createGame();
// 		final Lander lander = model.getLander();
// 		ArrayList<Triangle> obstacles = new ArrayList<Triangle>();
// 		model.setObstacles(obstacles);
// 		lander.setSize(new Dimension2D(2, 2));
// 		final float max_y = GameModel.GAME_AREA_HEIGHT;
// 		lander.setPos(new Point2D(GameModel.GROUND_LEVEL + 2, max_y + 100));
// 		model.startGame();
// 		model.updateElements(0);
// 		Assertions.assertEquals(GameState.GAME_OVER, model.getGameState());
// 	}

// 	// Test is Game is lost when violating lower game height
// 	@Test
// 	public void testLanderYOutOfBoundLower() { // model1 fails
// 		final GameModel model = GameFactory.createGame();
// 		final Lander lander = model.getLander();
// 		ArrayList<Triangle> obstacles = new ArrayList<Triangle>();
// 		model.setObstacles(obstacles);
// 		lander.setSize(new Dimension2D(2, 2));
// 		lander.setPos(new Point2D(GameModel.GROUND_LEVEL + 2, -10));
// 		model.startGame();
// 		model.updateElements(0);
// 		Assertions.assertEquals(GameState.GAME_OVER, model.getGameState());
// 	}

// 	// Test is Game is lost when violating upper game width
// 	@Test
// 	public void testLanderXOutOfBoundUpper() { // model1 fails
// 		final GameModel model = GameFactory.createGame();
// 		final Lander lander = model.getLander();
// 		ArrayList<Triangle> obstacles = new ArrayList<Triangle>();
// 		model.setObstacles(obstacles);
// 		lander.setSize(new Dimension2D(2, 2));
// 		final float max_x = GameModel.GAME_AREA_WIDTH;
// 		lander.setPos(new Point2D(GameModel.GROUND_LEVEL + 2, max_x + 20));
// 		model.startGame();
// 		model.updateElements(0);
// 		Assertions.assertEquals(GameState.GAME_OVER, model.getGameState());
// 	}

// 	// Test is Game is lost when violating lower game width
// 	@Test
// 	public void testLanderXOutOfBoundLower() { // model1 fails
// 		final GameModel model = GameFactory.createGame();
// 		final Lander lander = model.getLander();
// 		ArrayList<Triangle> obstacles = new ArrayList<Triangle>();
// 		model.setObstacles(obstacles);
// 		lander.setSize(new Dimension2D(2, 2));
// 		lander.setPos(new Point2D(GameModel.GROUND_LEVEL + 2, -10));
// 		model.startGame();
// 		model.updateElements(0);
// 		Assertions.assertEquals(GameState.GAME_OVER, model.getGameState());
// 	}

// 	// Test if Collision is detected when lander has no contact with obstacle and is
// 	// tilted
// 	@Test
// 	public void testCollisionNoContactTilted() { // model1 fails
// 		final GameModel model = GameFactory.createGame();
// 		final Lander lander = model.getLander();
// 		ArrayList<Triangle> obstacles = new ArrayList<Triangle>();
// 		obstacles.add(new Triangle(new Point2D(10, 10), new Point2D(12, 10), new Point2D(10, 12)));
// 		model.setObstacles(obstacles);
// 		lander.setSize(new Dimension2D(2, 2));
// 		lander.setPos(new Point2D(20, 20));
// 		model.changeTilt(45);
// 		model.startGame();
// 		model.updateElements(0);
// 		Assertions.assertEquals(GameState.RUNNING, model.getGameState());
// 	}

// 	// Test if Collision is detected when lander has partial contact with obstacle
// 	@Test
// 	public void testCollisionContact() {
// 		final GameModel model = GameFactory.createGame();
// 		final Lander lander = model.getLander();
// 		ArrayList<Triangle> obstacles = new ArrayList<Triangle>();
// 		obstacles.add(new Triangle(new Point2D(10, 10), new Point2D(12, 10), new Point2D(10, 12)));
// 		model.setObstacles(obstacles);
// 		lander.setSize(new Dimension2D(2, 2));
// 		lander.setPos(new Point2D(10, 12));
// 		model.startGame();
// 		model.updateElements(0);
// 		Assertions.assertEquals(GameState.GAME_OVER, model.getGameState());
// 	}

// 	// Test if Collision is detected when lander is touching one side of the
// 	// obstacle
// 	@Test
// 	public void testCollisionTouchingSide() { // model1 fails
// 		final GameModel model = GameFactory.createGame();
// 		final Lander lander = model.getLander();
// 		ArrayList<Triangle> obstacles = new ArrayList<Triangle>();
// 		obstacles.add(new Triangle(new Point2D(10, 10), new Point2D(12, 10), new Point2D(10, 12)));
// 		model.setObstacles(obstacles);
// 		lander.setSize(new Dimension2D(2, 2));
// 		lander.setPos(new Point2D(9, 11));
// 		model.startGame();
// 		model.updateElements(0);
// 		Assertions.assertEquals(GameState.RUNNING, model.getGameState());
// 	}

// 	// Test if Collision is detected when lander is touching one corner of the
// 	// obstacle
// 	@Test
// 	public void testCollisionTouchingCorner() { // model1 fails
// 		final GameModel model = GameFactory.createGame();
// 		final Lander lander = model.getLander();
// 		ArrayList<Triangle> obstacles = new ArrayList<Triangle>();
// 		obstacles.add(new Triangle(new Point2D(10, 10), new Point2D(12, 10), new Point2D(10, 12)));
// 		model.setObstacles(obstacles);
// 		lander.setSize(new Dimension2D(2, 2));
// 		lander.setPos(new Point2D(13, 11));
// 		model.startGame();
// 		model.updateElements(0);
// 		Assertions.assertEquals(GameState.RUNNING, model.getGameState());
// 	}

// 	// Test if Collision is detected when lander is touching one side of the
// 	// obstacle while being completely convered by it
// 	@Test
// 	public void testCollisionTouchingSidesInside() {
// 		final GameModel model = GameFactory.createGame();
// 		final Lander lander = model.getLander();
// 		ArrayList<Triangle> obstacles = new ArrayList<Triangle>();
// 		obstacles.add(new Triangle(new Point2D(10, 10), new Point2D(12, 10), new Point2D(10, 12)));
// 		model.setObstacles(obstacles);
// 		lander.setSize(new Dimension2D(1, 1));
// 		lander.setPos(new Point2D(10.5, 10.5));
// 		model.startGame();
// 		model.updateElements(0);
// 		Assertions.assertEquals(GameState.GAME_OVER, model.getGameState());
// 	}

// 	// Test if Collision is detected when lander is touching one side of the
// 	// obstacle with one of its corners while being completely convered by it
// 	@Test
// 	public void testCollisionTouchingCornerToSideInside() {
// 		final GameModel model = GameFactory.createGame();
// 		final Lander lander = model.getLander();
// 		ArrayList<Triangle> obstacles = new ArrayList<Triangle>();
// 		obstacles.add(new Triangle(new Point2D(10, 10), new Point2D(14, 10), new Point2D(10, 14)));
// 		model.setObstacles(obstacles);
// 		lander.setSize(new Dimension2D(1, 1));
// 		lander.setPos(new Point2D(11.5, 11.5));
// 		model.startGame();
// 		model.updateElements(0);
// 		Assertions.assertEquals(GameState.GAME_OVER, model.getGameState());
// 	}

// 	// Test if Collision is detected when lander is touching no side of the obstacle
// 	// while being completely convered by it
// 	@Test
// 	public void testCollisionNoTouchingInside() {
// 		final GameModel model = GameFactory.createGame();
// 		final Lander lander = model.getLander();
// 		ArrayList<Triangle> obstacles = new ArrayList<Triangle>();
// 		obstacles.add(new Triangle(new Point2D(10, 10), new Point2D(14, 10), new Point2D(10, 14)));
// 		model.setObstacles(obstacles);
// 		lander.setSize(new Dimension2D(1, 1));
// 		lander.setPos(new Point2D(10.5, 10.5));
// 		model.startGame();
// 		model.updateElements(0);
// 		Assertions.assertEquals(GameState.GAME_OVER, model.getGameState());
// 	}

// 	// Test if crash is detected when the lander lands with 0 tilt and (0, 0) speed
// 	@Test
// 	public void testLandingNoCrashNoTiltNoSpeed() {
// 		final GameModel model = GameFactory.createGame();
// 		final Lander lander = model.getLander();
// 		ArrayList<Triangle> obstacles = new ArrayList<Triangle>();
// 		model.setObstacles(obstacles);
// 		lander.setSize(new Dimension2D(1, 1));
// 		lander.setPos(new Point2D(GameModel.GROUND_LEVEL + 0.5, GameModel.GAME_AREA_WIDTH / 2 - 0.5));
// 		model.startGame();
// 		model.updateElements(0);
// 		Assertions.assertEquals(GameState.SHOW_SCORE, model.getGameState());
// 		Assertions.assertEquals(model.getCurrentScore(), 11970000);
// 	}

// 	// Test if crash is detected when the lander lands with 3 tilt and (0, 0) speed
// 	@Test
// 	public void testLandingNoCrashTilt3NoSpeed() {
// 		final GameModel model = GameFactory.createGame();
// 		final Lander lander = model.getLander();
// 		ArrayList<Triangle> obstacles = new ArrayList<Triangle>();
// 		model.setObstacles(obstacles);
// 		lander.setSize(new Dimension2D(1, 1));
// 		lander.setPos(new Point2D(GameModel.GROUND_LEVEL + 0.5, GameModel.GAME_AREA_WIDTH / 2 - 0.5));
// 		lander.setTilt(3);
// 		model.startGame();
// 		model.updateElements(0);
// 		Assertions.assertEquals(GameState.SHOW_SCORE, model.getGameState());
// 		Assertions.assertEquals(model.getCurrentScore(), 11970000);
// 	}

// 	// Test if crash is detected when the lander lands with -3 tilt and (0, 0) speed
// 	@Test
// 	public void testLandingNoCrashTiltMinus3NoSpeed() {
// 		final GameModel model = GameFactory.createGame();
// 		final Lander lander = model.getLander();
// 		ArrayList<Triangle> obstacles = new ArrayList<Triangle>();
// 		model.setObstacles(obstacles);
// 		lander.setSize(new Dimension2D(1, 1));
// 		lander.setPos(new Point2D(GameModel.GROUND_LEVEL + 0.5, GameModel.GAME_AREA_WIDTH / 2 - 0.5));
// 		lander.setTilt(-3);
// 		model.startGame();
// 		model.updateElements(0);
// 		Assertions.assertEquals(GameState.SHOW_SCORE, model.getGameState());
// 		Assertions.assertEquals(model.getCurrentScore(), 11970000);
// 	}

// 	// Test if crash is detected when the lander lands with 9 tilt and (0, 0) speed
// 	@Test
// 	public void testLandingNoCrashTilt9NoSpeed() {
// 		final GameModel model = GameFactory.createGame();
// 		final Lander lander = model.getLander();
// 		ArrayList<Triangle> obstacles = new ArrayList<Triangle>();
// 		model.setObstacles(obstacles);
// 		lander.setSize(new Dimension2D(1, 1));
// 		lander.setPos(new Point2D(GameModel.GROUND_LEVEL + 0.5, GameModel.GAME_AREA_WIDTH / 2 - 0.5));
// 		lander.setTilt(9);
// 		model.startGame();
// 		model.updateElements(0);
// 		Assertions.assertEquals(GameState.SHOW_SCORE, model.getGameState());
// 		Assertions.assertEquals(model.getCurrentScore(), 11970000);
// 	}

// 	// Test if crash is detected when the lander lands with -9 tilt and (0, 0) speed
// 	@Test
// 	public void testLandingNoCrashTiltMinus9NoSpeed() {
// 		final GameModel model = GameFactory.createGame();
// 		final Lander lander = model.getLander();
// 		ArrayList<Triangle> obstacles = new ArrayList<Triangle>();
// 		model.setObstacles(obstacles);
// 		lander.setSize(new Dimension2D(1, 1));
// 		lander.setPos(new Point2D(GameModel.GROUND_LEVEL + 0.5, GameModel.GAME_AREA_WIDTH / 2 - 0.5));
// 		lander.setTilt(-9);
// 		model.startGame();
// 		model.updateElements(0);
// 		Assertions.assertEquals(GameState.SHOW_SCORE, model.getGameState());
// 		Assertions.assertEquals(model.getCurrentScore(), 11970000);
// 	}

// 	// Test if crash is detected when the lander lands with 0 tilt and (0, -1) speed
// 	@Test
// 	public void testLandingNoCrashNoTiltSpeedMinus1() {
// 		final GameModel model = GameFactory.createGame();
// 		final Lander lander = model.getLander();
// 		ArrayList<Triangle> obstacles = new ArrayList<Triangle>();
// 		model.setObstacles(obstacles);
// 		lander.setSize(new Dimension2D(1, 1));
// 		lander.setPos(new Point2D(GameModel.GROUND_LEVEL + 0.5, GameModel.GAME_AREA_WIDTH / 2 - 0.5));
// 		lander.setSpeed(new Point2D(0, -1));
// 		model.startGame();
// 		model.updateElements(0);
// 		Assertions.assertEquals(GameState.SHOW_SCORE, model.getGameState());
// 		Assertions.assertEquals(model.getCurrentScore(), 119);
// 	}

// 	// Test if crash is detected when the lander lands with 0 tilt and (0, 1) speed
// 	@Test
// 	public void testLandingNoCrashNoTiltSpeed1() {
// 		final GameModel model = GameFactory.createGame();
// 		final Lander lander = model.getLander();
// 		ArrayList<Triangle> obstacles = new ArrayList<Triangle>();
// 		model.setObstacles(obstacles);
// 		lander.setSize(new Dimension2D(1, 1));
// 		lander.setPos(new Point2D(GameModel.GROUND_LEVEL + 0.5, GameModel.GAME_AREA_WIDTH / 2 - 0.5));
// 		lander.setSpeed(new Point2D(0, 1));
// 		model.startGame();
// 		model.updateElements(0);
// 		Assertions.assertEquals(GameState.SHOW_SCORE, model.getGameState());
// 		Assertions.assertEquals(model.getCurrentScore(), 119);
// 	}

// 	// Test if crash is detected when the lander lands with 45 tilt and (0, 0) speed
// 	@Test
// 	public void testLandingCrashTilted() { // model2 fails
// 		final GameModel model = GameFactory.createGame();
// 		final Lander lander = model.getLander();
// 		ArrayList<Triangle> obstacles = new ArrayList<Triangle>();
// 		model.setObstacles(obstacles);
// 		lander.setSize(new Dimension2D(1, 1));
// 		lander.setPos(new Point2D(GameModel.GROUND_LEVEL + 0.5, GameModel.GAME_AREA_WIDTH / 2 - 0.5));
// 		lander.setTilt(45);
// 		model.startGame();
// 		model.updateElements(0);
// 		Assertions.assertEquals(GameState.GAME_OVER, model.getGameState());
// 	}

// 	// Test if crash is detected when the lander lands with 0 tilt and (0, -3) speed
// 	@Test
// 	public void testLandingCrashSpeedMinus3() {
// 		final GameModel model = GameFactory.createGame();
// 		final Lander lander = model.getLander();
// 		ArrayList<Triangle> obstacles = new ArrayList<Triangle>();
// 		model.setObstacles(obstacles);
// 		lander.setSize(new Dimension2D(1, 1));
// 		lander.setPos(new Point2D(GameModel.GROUND_LEVEL + 0.5, GameModel.GAME_AREA_WIDTH / 2 - 0.5));
// 		lander.setSpeed(new Point2D(0, -3));
// 		model.startGame();
// 		model.updateElements(0);
// 		Assertions.assertEquals(GameState.GAME_OVER, model.getGameState());
// 	}

// 	// Test if crash is detected when the lander lands with 0 tilt and (0, 3) speed
// 	@Test
// 	public void testLandingCrashSpeed3() {
// 		final GameModel model = GameFactory.createGame();
// 		final Lander lander = model.getLander();
// 		ArrayList<Triangle> obstacles = new ArrayList<Triangle>();
// 		model.setObstacles(obstacles);
// 		lander.setSize(new Dimension2D(1, 1));
// 		lander.setPos(new Point2D(GameModel.GROUND_LEVEL + 0.5, GameModel.GAME_AREA_WIDTH / 2 - 0.5));
// 		lander.setSpeed(new Point2D(0, 3));
// 		model.startGame();
// 		model.updateElements(0);
// 		Assertions.assertEquals(GameState.GAME_OVER, model.getGameState());
// 	}

// 	// Test if crash is detected when the lander lands with 45 tilt and (0, -3)
// 	// speed
// 	@Test
// 	public void testLandingCrashSpeedAndTilt() {
// 		final GameModel model = GameFactory.createGame();
// 		final Lander lander = model.getLander();
// 		ArrayList<Triangle> obstacles = new ArrayList<Triangle>();
// 		model.setObstacles(obstacles);
// 		lander.setSize(new Dimension2D(1, 1));
// 		lander.setPos(new Point2D(GameModel.GROUND_LEVEL + 0.5, GameModel.GAME_AREA_WIDTH / 2 - 0.5));
// 		lander.setSpeed(new Point2D(0, -3));
// 		lander.setTilt(45);
// 		model.startGame();
// 		model.updateElements(0);
// 		Assertions.assertEquals(GameState.GAME_OVER, model.getGameState());
// 	}

// }
