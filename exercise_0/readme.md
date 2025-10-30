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
