# MidPoint CodeGen

A code generation tool designed to simplify the development of midPoint/ConnId connectors by reducing boilerplate code.

## Project Overview

MidPoint CodeGen aims to make connector development more efficient by automating repetitive code patterns. This tool
helps developers focus on implementing core connector functionality instead of writing ObjectClassInfoBuilder and
ConnectorObjectBuilder objects.

## Current Status

This project is in early development. The core functionality is being built, and more detailed documentation will follow
as the project matures.

## Module Structure

- `codegen-core`: Contains the annotations and util classes to use in the code generation process.
- Additional modules will be added as development progresses

## Requirements

- Java 17 or higher
- Maven 3.6 or higher

## Building

```bash
mvn clean install
```

## Future Plans

- Annotation processing for connector code generation
- Maven plugin for easy integration into existing projects

---

*Note: This is an initial README. More comprehensive documentation will be provided as the project develops.*