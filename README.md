# Image Editor Web Application

A modern web-based image editor built with Java backend and HTML/CSS/JavaScript frontend. Apply various transformations to your images including grayscale conversion, color inversion, brightness adjustment, rotations, and mirroring effects.

## Features

- **Upload Images**: Drag & drop or click to upload images
- **Real-time Processing**: Instant image transformations
- **Multiple Effects**:
  - Convert to Grayscale
  - Invert Colors
  - Adjust Brightness (with percentage control)
  - Rotate 90° and 180°
  - Mirror horizontally and vertically
- **Responsive Design**: Works on desktop and mobile devices
- **Modern UI**: Beautiful gradient design with smooth animations

## Architecture

- **Backend**: Java 17 with built-in HTTP server
- **Image Processing**: Pure Java AWT BufferedImage operations
- **Frontend**: Vanilla HTML5, CSS3, and JavaScript
- **Communication**: REST API with JSON and Base64 encoded images

## Quick Start

### Prerequisites

- Java 17 or higher
- Maven (optional, for building)

### Running the Application

1. **Using pre-compiled classes** (recommended):
   ```bash
   java -cp target/classes com.example.ImageEditorServer
   ```

2. **Using Maven** (if Maven is installed):
   ```bash
   mvn compile exec:java -Dexec.mainClass="com.example.ImageEditorServer"
   ```

3. **Open your browser** and navigate to `http://localhost:8080`

### Building from Source

```bash
# Compile the project
javac -cp src/main/java -d target/classes src/main/java/com/example/*.java src/main/java/com/example/imageeditor/*.java

# Or using Maven (if available)
mvn clean compile
```

## Project Structure

```
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── com/example/
│   │   │   │   ├── ImageEditorServer.java    # Web server with REST API
│   │   │   │   └── imageeditor/
│   │   │   │       ├── ImageProcessor.java   # Image processing utilities
│   │   │   │       └── ImageEditorApp.java   # Original CLI application
│   │   └── resources/
│   │       └── index.html                    # Frontend application
│   └── test/
│       └── java/
│           └── com/example/imageeditor/
│               └── ImageProcessorTest.java  # Unit tests
├── k8s/                                      # Kubernetes deployment files
├── Dockerfile                                # Container configuration
├── pom.xml                                   # Maven configuration
└── README.md
```

## API Endpoints

### GET /
Serves the main HTML application.

### POST /process
Processes an uploaded image with specified transformation.

**Request Body (JSON):**
```json
{
  "image": "base64-encoded-image-data",
  "editType": "grayscale|invert|brightness|rotate90|rotate180|mirrorRight|mirrorBottom",
  "percentage": 50  // Only required for brightness adjustment
}
```

**Response (JSON):**
```json
{
  "editedImage": "base64-encoded-processed-image"
}
```

## Development

### Adding New Image Effects

1. Implement the effect in `ImageProcessor.java`
2. Add the case to the switch statement in `ImageEditorServer.java`
3. Update the frontend HTML and JavaScript

### Testing

Run the unit tests:
```bash
mvn test
```

### Docker

Build and run with Docker:
```bash
docker build -t image-editor .
docker run -p 8080:8080 image-editor
```

## CI/CD

The project includes GitHub Actions workflow (`.github/workflows/ci.yml`) that:
- Runs on push/PR to main branch
- Tests with JDK 17
- Runs unit tests in headless mode

## Technologies Used

- **Java 17**: Core application logic
- **AWT/BufferedImage**: Image processing
- **HttpServer**: Built-in Java web server
- **HTML5/CSS3**: Modern web frontend
- **JavaScript (ES6+)**: Client-side interactivity
- **Base64**: Image data encoding
- **Maven**: Build management
- **JUnit 5**: Unit testing

## Browser Support

- Chrome 70+
- Firefox 65+
- Safari 12+
- Edge 79+

## Contributing

1. Fork the repository
2. Create a feature branch
3. Add tests for new functionality
4. Ensure all tests pass
5. Submit a pull request

## License

This project is open source and available under the MIT License.

## Legacy CLI Version

The original command-line interface is still available in `ImageEditorApp.java`. Run it with:
```bash
java -cp target/classes com.example.imageeditor.ImageEditorApp
```

