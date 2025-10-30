
package LunarLander;

import java.util.stream.Stream;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.provider.Arguments;

public class GameModelProvider {
    public static Stream<Arguments> allModels() {
        return Stream.of(
                Arguments.of(Named.of("model0", model0.GameFactory.createGame())),
                Arguments.of(Named.of("model1", model1.GameFactory.createGame())),
                Arguments.of(Named.of("model2", model2.GameFactory.createGame())),
                Arguments.of(Named.of("model3", model3.GameFactory.createGame())),
                Arguments.of(Named.of("model4", model4.GameFactory.createGame())),
                Arguments.of(Named.of("model5", model5.GameFactory.createGame())),
                Arguments.of(Named.of("model6", model6.GameFactory.createGame())),
                Arguments.of(Named.of("model7", model7.GameFactory.createGame())),
                Arguments.of(Named.of("model8", model8.GameFactory.createGame())));
    }
}
