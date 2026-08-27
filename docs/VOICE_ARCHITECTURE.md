# Round Ball Assistant — Voice Conversation Architecture

Target: Minecraft Java 1.21.1 / Fabric Loader 0.19.3.

## Voice mode

Voice conversations are an optional setting. When enabled, the player can speak directly to the Round Ball Assistant without opening the Shift+K command box. The assistant listens for speech through a configurable voice-input provider, converts the recognized speech into an internal command/conversation request, and produces a spoken response through a configurable text-to-speech provider.

## Player experience

1. Open Round Ball Assistant settings.
2. Enable **Voice Conversations**.
3. Select microphone/input and voice-output providers where available.
4. In voice mode, speak naturally near the assistant.
5. Speech is transcribed locally or through the configured provider.
6. The conversation manager interprets the transcript.
7. The action manager executes an allowed Minecraft action when appropriate.
8. The assistant generates a short response.
9. TTS speaks the response in-game.

The Shift+K text box remains available as a fallback and for players who do not want voice mode.

## Suggested modules

- `voice/VoiceSettings`: persistent enable/disable, input/output volume, language, push-to-talk mode, response voice.
- `voice/VoiceInputProvider`: abstraction for microphone speech recognition.
- `voice/VoiceOutputProvider`: abstraction for speech synthesis.
- `voice/VoiceConversationManager`: controls listening, transcription, interpretation, response and playback states.
- `voice/VoicePermissionState`: tracks whether the player has enabled microphone use in the client/provider.
- `conversation/ConversationManager`: shared conversation layer used by both text GUI and voice mode.
- `actions/ActionManager`: converts interpreted requests into safe in-game actions.
- `client/gui/AssistantSettingsScreen`: settings screen including Voice Conversations toggle.

## Important implementation boundary

Fabric itself does not provide speech recognition or text-to-speech. The mod should therefore use provider interfaces and optional integrations rather than hard-coding one external voice service. If no provider is installed/configured, Voice Conversations stays unavailable and the normal text interface continues to work.

## Voice states

`DISABLED -> IDLE -> LISTENING -> PROCESSING -> SPEAKING -> IDLE`

Errors return to `IDLE` with a short in-game notification. The player can also use push-to-talk to prevent unintended microphone capture.

## Multiplayer

Voice processing should remain client-side where possible. The assistant's resulting Minecraft actions are sent through normal Fabric networking to the logical server when required. Server-side validation should prevent a client voice request from bypassing normal game rules.
