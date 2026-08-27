# Round Ball Assistant

## Made by Safaan

A survival-focused yellow smiling companion mod for Minecraft Java Edition.

[![Visit Safaan's YouTube Channel](https://img.shields.io/badge/YouTube-Visit%20My%20Channel-red?logo=youtube&logoColor=white)](https://youtube.com/@safshad-67)

## Target

- Minecraft Java Edition **1.21.1**
- Fabric Loader **0.19.3**
- Fabric API **0.116.9+1.21.1**
- Java **21**

## Current features

- Yellow smiling Round Ball entity
- Persistent companion behavior
- `/roundball summon` command
- Shift + K assistant screen
- Natural-language intent parser for greetings, help, following, finding, item requests, stopping and target requests
- Action service shared by text and future voice input
- Voice Conversations setting and push-to-talk setting
- Provider-based voice architecture so speech recognition and speech synthesis can be added without changing the core conversation system
- Build-time generated PNG face texture
- GitHub Actions build producing a JAR artifact

## Voice note

The core mod deliberately does not bundle a speech-recognition or text-to-speech engine. `VoiceInputProvider` and `VoiceOutputProvider` are integration points for a compatible provider. This keeps the base JAR lightweight and avoids hard-coding an external service.

## Build

Use Java 21 and Gradle 8.10:

```text
gradle build
```

The resulting JAR is in `build/libs/`.

## Controls

- **Shift + K** — open the text assistant
- Voice mode can be enabled from **Voice settings**

The repository is source-first; GitHub Actions builds the actual JAR artifact.
