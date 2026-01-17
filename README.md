# Image Editor (Java)

Small CLI image editor demonstrating basic operations (grayscale, invert, rotate, mirror, brightness).

Build & run

- Build: mvn test (runs tests) or mvn package
- Run (from project root): mvn exec:java -Dexec.mainClass="com.example.imageeditor.ImageEditorApp"

Notes

- The original single-file project has been split into `ImageProcessor` (utilities) and `ImageEditorApp` (CLI), with unit tests.
- Legacy files were moved to `legacy/` for reference.

## CI & Local testing

- A GitHub Actions workflow is included at `.github/workflows/ci.yml` which runs `mvn test` on JDK 17 and sets headless mode for AWT-based tests.
- Locally: ensure Maven is installed (or add the Maven Wrapper by running `mvn -N io.takari:maven:wrapper`). Then run `mvn test`.
- If you run tests on a headless runner, the workflow sets `-Djava.awt.headless=true` to avoid AWT display issues.

