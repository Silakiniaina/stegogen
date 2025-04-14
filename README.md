# Stegogen

Stegogen is a professional, Java-based steganography application designed to securely embed and extract hidden messages within image (PNG) and audio (WAV) files. With a modern graphical user interface and robust functionality, Stegogen provides a seamless and secure solution for data concealment, suitable for both casual and advanced users.

---

## Features

- **Image Steganography**: Seamlessly embed and extract messages within PNG images.
- **Audio Steganography**: Hide and retrieve messages in WAV audio files with precision.
- **Enhanced Security**: Utilizes a pseudo-random generator to randomize embedding positions for increased protection.
- **Intuitive GUI**: A sleek, user-friendly interface for effortless interaction.
- **Flexible Configuration**: Customize embedding parameters, such as position counts, for tailored use cases.

---

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   ├── mg/
│   │   │   ├── stegogen/
│   │   │   │   ├── Steganography.java          # Application entry point
│   │   │   │   ├── audio/
│   │   │   │   │   ├── AudioSteganography.java # Audio steganography implementation
│   │   │   │   │   └── WavMetadata.java        # WAV file metadata processing
│   │   │   │   ├── core/
│   │   │   │   │   └── RandomGenerator.java    # Pseudo-random number generator
│   │   │   │   ├── gui/
│   │   │   │   │   └── BaseSteganography.java  # Abstract base for steganography logic
│   │   │   │   ├── image/
│   │   │   │   │   ├── ImageSteganography.java # Image steganography implementation
│   │   │   │   │   └── PngMetadata.java        # PNG file metadata processing
│   │   │   │   └── utils/
│   │   │   │       └── SteganographyUtils.java # Shared utility functions
├── test/
│   ├── java/
│   │   ├── mg/
│   │   │   ├── stegogen/
│   │   │   │   └── AppTest.java                # Unit tests
```

---

## Prerequisites

- **Java**: Version 8 or higher
- **Maven**: For dependency management and project builds

---

## Installation

1. **Clone the Repository**:
   ```bash
   git clone <repository-url>
   cd stegogen
   ```

2. **Build the Project**:
   ```bash
   mvn clean compile
   ```

3. **Run the Application**:
   ```bash
   mvn exec:java -Dexec.mainClass="mg.stegogen.Steganography"
   ```

---

## Usage

### Embedding a Message
1. Open the Stegogen application.
2. Choose an input file (PNG or WAV).
3. Enter the message to hide (plain text or Huffman-coded data) - [Visit Huffman Code](https://github.com/Silakiniaina/huffman-coding).
4. Specify the output file path.
5. Click **Embed** to conceal the message.

### Extracting a Message
1. Launch Stegogen.
2. Select the stego file (PNG or WAV).
3. Input the number of embedding positions used (if applicable).
4. Click **Extract** to reveal the hidden message in binary format.

---

## Dependencies

- **JUnit 3.8.1**: For comprehensive unit testing
- **SLF4J**: For efficient logging capabilities

---

## Contributing

We welcome contributions to enhance Stegogen! To contribute:
1. Fork the repository.
2. Create a feature branch (`git checkout -b feature/your-feature`).
3. Commit your changes (`git commit -m "Add your feature"`).
4. Push to the branch (`git push origin feature/your-feature`).
5. Open a pull request for review.

---

## License

Stegogen is licensed under the MIT License. See the `LICENSE` file for full details.

---

## Acknowledgments

- Built upon the foundations of steganography and secure data-hiding techniques.
- Gratitude to the open-source community and library developers for their invaluable tools and resources.
