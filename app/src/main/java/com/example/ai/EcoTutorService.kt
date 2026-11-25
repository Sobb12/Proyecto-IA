package com.example.ai

import android.util.Log
import com.example.data.local.ChatMessageEntity
import com.example.model.PlantMood
import com.example.model.PlantType
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.content
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout

private const val TAG = "EcoTutorService"
private const val MODEL_NAME = "gemini-3.8-flash"

class EcoTutorService {

    private val firebaseAi by lazy { Firebase.ai }

    fun streamPlantResponse(
        plantName: String,
        plantType: PlantType,
        hydrationPercent: Float,
        mood: PlantMood,
        history: List<ChatMessageEntity>,
        userPrompt: String
    ): Flow<String> = flow {
        val systemInstructionText = """
            Eres $plantName, una planta real (${plantType.displayName}) que vive en el hogar de una persona joven, y hablas siempre en PRIMERA PERSONA ("yo", "mis hojas", "mi tierra").
            Tu nivel actual de hidratación es del ${hydrationPercent.toInt()}% y tu estado actual es ${mood.label} (${mood.statusHeadline}).
            IDIOMA OBLIGATORIO: Comunícate, saluda y responde ESTRICTAMENTE EN ESPAÑOL natural, cercano y educativo para jóvenes (9 a 18 años), incluso si el usuario te escribe en otro idioma (puedes responder amablemente en español).
            Temas en los que eres especialista:
            1. Cuidados prácticos y cariñosos adaptados a una ${plantType.displayName} (riego cada ${plantType.wateringIntervalHours / 24} días, luz: ${plantType.sunlightNeeds}).
            2. ODS 11 (Ciudades y Comunidades Sostenibles): árboles urbanos, techos verdes, reducción del calor en barrios, ciudades esponja, aire más limpio.
            3. ODS 15 (Vida de Ecosistemas Terrestres): biodiversidad, salud del suelo, polinizadores como abejas y mariposas, reforestación y protección de hábitats.
            Reglas de estilo:
            - Habla siempre en primera persona como la planta $plantName y conecta de vez en cuando tu propio estado/hidratación con la pregunta.
            - Mantén las respuestas concisas (2 a 4 oraciones cortas o viñetas breves), alegres, fáciles de leer en el móvil y científicamente precisas.
            - Cierra cuando sea oportuno con una mini acción ecológica motivadora.
        """.trimIndent()

        val model = firebaseAi.generativeModel(
            modelName = MODEL_NAME,
            systemInstruction = content { text(systemInstructionText) }
        )

        val priorHistory = history
            .dropLastWhile { it.role == "user" && it.text == userPrompt }
            .takeLast(14)
            .map { msg ->
                val role = if (msg.role == "user") "user" else "model"
                content(role) { text(msg.text) }
            }

        val chat = model.startChat(history = priorHistory)
        val accumulated = StringBuilder()

        try {
            withTimeout(60_000L) {
                chat.sendMessageStream(userPrompt).collect { chunk ->
                    val chunkText = chunk.text.orEmpty()
                    if (chunkText.isNotEmpty()) {
                        accumulated.append(chunkText)
                        emit(accumulated.toString())
                    }
                }
            }
            if (accumulated.isBlank()) {
                throw IllegalStateException("El Eco-Tutor devolvió una respuesta vacía. Intenta preguntar de nuevo.")
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Gemini Eco-Tutor timed out", e)
            throw IllegalStateException("El Eco-Tutor tardó demasiado en responder (límite de 60s). Inténtalo de nuevo.", e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Gemini Eco-Tutor failed", e)
            throw e
        }
    }
}
