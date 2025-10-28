# LunarLander Test Project

JUnit 5 tests for the provided **LunarLander Mutants** JAR.  
This repo is a standard **Maven** project (Java 17). Tests live under `src/test/java`.

---

## 0) Prerequisites

- **Java JDK 17** (make sure Maven uses it)
- **Maven 3.9+**
- (Optional) **VS Code** with the Java Extension Pack, or any Java IDE

### Windows (with Chocolatey)
```powershell
choco install temurin17 -y         # JDK 17 (Adoptium)
choco install maven -y             # Maven
refreshenv                          # or open a new terminal
mvn -version                        # should show Java 17


git clone <your-repo-url>
cd <your-project-folder>   # the folder containing pom.xml
git pull                   # if you already cloned earlier


mvn install:install-file ^
  -Dfile="lib/LunarLander_Mutants.jar" ^
  -DgroupId=edu.youruni ^
  -DartifactId=lunar-lander-mutants ^
  -Dversion=1.0.0 ^
  -Dpackaging=jar


mvn clean test


mvn -Dtest=TestLander test


mvn -DskipTests clean package
```


## Test Results:

```
--- Fehlerstatistik pro Modell ---
model3: 1 Fehler
  - 4 fullThrust0to11 => model3
model2: 1 Fehler
  - 3 landingCrash tilted => model2
model1: 8 Fehler
  - 2 collNoContact => model1
  - 2 yOutOfBoundLower => model1
  - 2 yOutOfBoundUpper => model1
  - 2 collNoContactTilted => model1
  - 2 collTouchingCorner => model1
  - 2 xOutOfBoundLower => model1
  - 2 xOutOfBoundUpper => model1
  - 2 collTouchingSide => model1
```