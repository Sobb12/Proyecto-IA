package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.model.PlantMood
import com.example.model.PlantType
import com.example.ui.theme.CriticalCoral
import com.example.ui.theme.DewTertiary
import com.example.ui.theme.GrowingGold
import com.example.ui.theme.HealthyGreen
import com.example.ui.theme.ThirstyAmber
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun InteractivePlantAvatar(
    plantName: String,
    plantType: PlantType,
    mood: PlantMood,
    hydrationPercent: Float,
    modifier: Modifier = Modifier
) {
    var tappedBounce by remember { mutableStateOf(false) }
    var showSpeechBubble by remember { mutableStateOf(false) }

    LaunchedEffect(tappedBounce) {
        if (tappedBounce) {
            delay(220L)
            tappedBounce = false
        }
    }

    LaunchedEffect(showSpeechBubble) {
        if (showSpeechBubble) {
            delay(3_500L)
            showSpeechBubble = false
        }
    }

    val bounceScale by animateFloatAsState(
        targetValue = if (tappedBounce) 1.07f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "tapBounce"
    )

    val droopFactor by animateFloatAsState(
        targetValue = when (mood) {
            PlantMood.HEALTHY -> 0f
            PlantMood.GROWING -> -0.15f
            PlantMood.THIRSTY -> 0.45f
            PlantMood.CRITICAL -> 0.95f
        },
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label = "droopFactor"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "plantAmbient")
    val swayPhase by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2_400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sway"
    )
    val sparklePulse by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sparklePulse"
    )
    val badgeBob by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "badgeBob"
    )

    val auraColor = when (mood) {
        PlantMood.HEALTHY -> HealthyGreen.copy(alpha = 0.16f)
        PlantMood.THIRSTY -> ThirstyAmber.copy(alpha = 0.18f)
        PlantMood.CRITICAL -> CriticalCoral.copy(alpha = 0.20f)
        PlantMood.GROWING -> GrowingGold.copy(alpha = 0.26f)
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Floating speech bubble on tap or during Growing
        AnimatedVisibility(
            visible = showSpeechBubble || mood == PlantMood.GROWING,
            enter = fadeIn() + scaleIn(initialScale = 0.85f),
            exit = fadeOut() + scaleOut(targetScale = 0.9f)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(18.dp),
                tonalElevation = 4.dp,
                shadowElevation = 4.dp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Text(
                    text = mood.avatarSpeech,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .size(240.dp)
                .scale(bounceScale)
                .clip(CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    tappedBounce = true
                    showSpeechBubble = true
                }
                .testTag("plant_avatar_canvas")
                .semantics {
                    contentDescription =
                        "$plantName, ${plantType.displayName}, está actualmente ${mood.label} con ${hydrationPercent.toInt()} por ciento de hidratación. Toca para escuchar a tu planta."
                },
            contentAlignment = Alignment.Center
        ) {
            // Soft sunlit gradient backdrop
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                auraColor,
                                auraColor.copy(alpha = 0.04f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Canvas(modifier = Modifier.fillMaxSize()) {
                val swayAngle = when (mood) {
                    PlantMood.CRITICAL -> swayPhase * 0.6f
                    PlantMood.THIRSTY -> swayPhase * 1.4f
                    PlantMood.HEALTHY -> swayPhase * 2.8f
                    PlantMood.GROWING -> swayPhase * 4.5f
                }

                // Draw ground shadow
                drawOval(
                    color = Color.Black.copy(alpha = 0.10f),
                    topLeft = Offset(size.width * 0.24f, size.height * 0.84f),
                    size = Size(size.width * 0.52f, size.height * 0.07f)
                )

                // Draw foliage depending on species & state
                drawPlantFoliage(
                    plantType = plantType,
                    mood = mood,
                    droopFactor = droopFactor,
                    swayAngle = swayAngle,
                    sparklePulse = sparklePulse
                )

                // Draw terracotta/ceramic pot with cute expression!
                drawPotWithFace(
                    plantType = plantType,
                    mood = mood,
                    hydrationPercent = hydrationPercent
                )

                // Draw Growing/Rewarded sparkles & blooming stars
                if (mood == PlantMood.GROWING) {
                    drawGrowingSparkles(sparklePulse)
                }
            }

            // State-specific indicator badges in top-right corner of avatar
            when (mood) {
                PlantMood.THIRSTY -> {
                    Surface(
                        color = ThirstyAmber,
                        shape = CircleShape,
                        shadowElevation = 4.dp,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 18.dp, end = 22.dp)
                            .size(44.dp)
                            .scale(1f + badgeBob * 0.015f)
                            .testTag("thirsty_water_alert_badge")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.WaterDrop,
                                contentDescription = "Alerta de sed: necesita agua",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
                PlantMood.CRITICAL -> {
                    Surface(
                        color = CriticalCoral,
                        shape = RoundedCornerShape(16.dp),
                        shadowElevation = 6.dp,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 14.dp, end = 12.dp)
                            .scale(1f + badgeBob * 0.02f)
                            .testTag("critical_urgent_badge")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Warning,
                                contentDescription = "Alerta urgente: deshidratación crítica",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "¡SECA!",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }
                }
                PlantMood.GROWING -> {
                    Surface(
                        color = GrowingGold,
                        shape = RoundedCornerShape(16.dp),
                        shadowElevation = 6.dp,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 14.dp, end = 12.dp)
                            .scale(sparklePulse * 0.92f)
                            .testTag("growing_bloom_badge")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = "¡Creciendo y floreciendo!",
                                tint = Color(0xFF3E2700),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "¡FLOR!",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF3E2700)
                            )
                        }
                    }
                }
                PlantMood.HEALTHY -> Unit
            }
        }
    }
}

private fun DrawScope.drawPlantFoliage(
    plantType: PlantType,
    mood: PlantMood,
    droopFactor: Float,
    swayAngle: Float,
    sparklePulse: Float
) {
    val leafPrimary = when (mood) {
        PlantMood.HEALTHY -> Color(0xFF28A75E)
        PlantMood.GROWING -> Color(0xFF23C46C)
        PlantMood.THIRSTY -> Color(0xFF7CA34B)
        PlantMood.CRITICAL -> Color(0xFFA68B4B)
    }
    val leafHighlight = when (mood) {
        PlantMood.HEALTHY -> Color(0xFF56D185)
        PlantMood.GROWING -> Color(0xFF6EF0A3)
        PlantMood.THIRSTY -> Color(0xFFA4BF68)
        PlantMood.CRITICAL -> Color(0xFFC4AA68)
    }
    val stemColor = when (mood) {
        PlantMood.CRITICAL -> Color(0xFF8C723C)
        else -> Color(0xFF1F7A46)
    }

    val centerX = size.width * 0.5f
    val potTopY = size.height * 0.56f
    val droopOffset = droopFactor * size.height * 0.10f

    rotate(degrees = swayAngle, pivot = Offset(centerX, potTopY)) {
        when (plantType) {
            PlantType.SUCCULENT -> {
                // Rosette layers
                drawLeafPetal(
                    base = Offset(centerX - size.width * 0.04f, potTopY + 4f),
                    tip = Offset(centerX - size.width * 0.25f, potTopY - size.height * 0.14f + droopOffset),
                    width = size.width * 0.11f,
                    color = leafPrimary
                )
                drawLeafPetal(
                    base = Offset(centerX + size.width * 0.04f, potTopY + 4f),
                    tip = Offset(centerX + size.width * 0.25f, potTopY - size.height * 0.14f + droopOffset),
                    width = size.width * 0.11f,
                    color = leafPrimary
                )
                drawLeafPetal(
                    base = Offset(centerX - size.width * 0.02f, potTopY),
                    tip = Offset(centerX - size.width * 0.15f, potTopY - size.height * 0.24f + droopOffset * 0.8f),
                    width = size.width * 0.10f,
                    color = leafHighlight
                )
                drawLeafPetal(
                    base = Offset(centerX + size.width * 0.02f, potTopY),
                    tip = Offset(centerX + size.width * 0.15f, potTopY - size.height * 0.24f + droopOffset * 0.8f),
                    width = size.width * 0.10f,
                    color = leafHighlight
                )
                drawLeafPetal(
                    base = Offset(centerX, potTopY),
                    tip = Offset(centerX, potTopY - size.height * 0.29f + droopOffset * 0.6f),
                    width = size.width * 0.09f,
                    color = leafHighlight
                )
                if (mood == PlantMood.GROWING) {
                    drawBlossom(
                        center = Offset(centerX, potTopY - size.height * 0.30f),
                        radius = size.width * 0.055f * sparklePulse.coerceAtMost(1.1f),
                        petalColor = Color(0xFFFF7AA8)
                    )
                }
            }
            PlantType.FERN -> {
                // Arching fern fronds
                val frondAngles = listOf(-50f, -26f, 0f, 26f, 50f)
                frondAngles.forEachIndexed { idx, baseAngle ->
                    val extraDroop = droopFactor * (if (baseAngle < 0) -22f else 22f)
                    val angle = baseAngle + extraDroop
                    val len = size.height * (if (idx == 2) 0.33f else 0.28f)
                    val rad = (angle - 90f) * (PI / 180.0)
                    val endX = centerX + (cos(rad) * len).toFloat()
                    val endY = potTopY + (sin(rad) * len).toFloat() + droopOffset * 0.5f

                    val frondPath = Path().apply {
                        moveTo(centerX, potTopY + 4f)
                        quadraticTo(
                            centerX + (endX - centerX) * 0.45f,
                            potTopY - len * 0.72f,
                            endX,
                            endY
                        )
                    }
                    drawPath(
                        path = frondPath,
                        color = if (idx % 2 == 0) leafPrimary else leafHighlight,
                        style = Stroke(width = 14f, cap = StrokeCap.Round)
                    )
                    // Little leaflets along the frond
                    for (step in 2..4) {
                        val t = step / 5f
                        val lx = centerX + (endX - centerX) * t
                        val ly = potTopY + (endY - potTopY) * t - 8f * (1f - t)
                        drawCircle(
                            color = if (idx % 2 == 0) leafHighlight else leafPrimary,
                            radius = 10f,
                            center = Offset(lx - 8f, ly)
                        )
                        drawCircle(
                            color = if (idx % 2 == 0) leafHighlight else leafPrimary,
                            radius = 10f,
                            center = Offset(lx + 8f, ly)
                        )
                    }
                }
                if (mood == PlantMood.GROWING) {
                    drawBlossom(
                        center = Offset(centerX - 24f, potTopY - size.height * 0.26f),
                        radius = 11f,
                        petalColor = GrowingGold
                    )
                    drawBlossom(
                        center = Offset(centerX + 26f, potTopY - size.height * 0.24f),
                        radius = 11f,
                        petalColor = GrowingGold
                    )
                }
            }
            PlantType.CACTUS -> {
                // Main cactus trunk (slightly slumps when critical)
                val leanX = droopFactor * 14f
                val bodyWidth = size.width * 0.17f
                val bodyHeight = size.height * 0.31f
                val trunkTopY = potTopY - bodyHeight + droopOffset * 0.4f

                drawRoundRect(
                    color = leafPrimary,
                    topLeft = Offset(centerX - bodyWidth / 2f + leanX * 0.3f, trunkTopY),
                    size = Size(bodyWidth, bodyHeight + 10f),
                    cornerRadius = CornerRadius(bodyWidth / 2f, bodyWidth / 2f)
                )
                // Left arm
                val leftArmPath = Path().apply {
                    moveTo(centerX - bodyWidth * 0.4f, potTopY - bodyHeight * 0.45f)
                    lineTo(centerX - bodyWidth * 1.15f, potTopY - bodyHeight * 0.45f + droopOffset * 0.4f)
                    lineTo(centerX - bodyWidth * 1.15f, potTopY - bodyHeight * 0.80f + droopOffset * 0.7f)
                }
                drawPath(
                    path = leftArmPath,
                    color = leafPrimary,
                    style = Stroke(width = bodyWidth * 0.52f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
                // Right arm
                val rightArmPath = Path().apply {
                    moveTo(centerX + bodyWidth * 0.4f, potTopY - bodyHeight * 0.35f)
                    lineTo(centerX + bodyWidth * 1.15f, potTopY - bodyHeight * 0.35f + droopOffset * 0.4f)
                    lineTo(centerX + bodyWidth * 1.15f, potTopY - bodyHeight * 0.72f + droopOffset * 0.7f)
                }
                drawPath(
                    path = rightArmPath,
                    color = leafHighlight,
                    style = Stroke(width = bodyWidth * 0.50f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
                // Vertical rib highlight
                drawLine(
                    color = leafHighlight,
                    start = Offset(centerX, trunkTopY + 14f),
                    end = Offset(centerX, potTopY - 8f),
                    strokeWidth = 6f,
                    cap = StrokeCap.Round
                )
                // Cactus crown flower (bigger during Growing)
                val flowerRadius = if (mood == PlantMood.GROWING) 17f else if (mood == PlantMood.CRITICAL) 6f else 11f
                drawBlossom(
                    center = Offset(centerX + leanX * 0.3f, trunkTopY - 4f),
                    radius = flowerRadius,
                    petalColor = if (mood == PlantMood.CRITICAL) Color(0xFFB5897A) else Color(0xFFFF6F91)
                )
            }
            PlantType.FLOWERING -> {
                // Curving stem + leaves + cheerful blossoms
                val topX = centerX + droopFactor * 26f
                val topY = potTopY - size.height * 0.28f + droopOffset * 0.7f
                val stemPath = Path().apply {
                    moveTo(centerX, potTopY + 4f)
                    quadraticTo(centerX - 10f, potTopY - size.height * 0.16f, topX, topY)
                }
                drawPath(
                    path = stemPath,
                    color = stemColor,
                    style = Stroke(width = 11f, cap = StrokeCap.Round)
                )
                // Left & right leaves
                drawLeafPetal(
                    base = Offset(centerX - 2f, potTopY - size.height * 0.10f),
                    tip = Offset(centerX - size.width * 0.22f, potTopY - size.height * 0.18f + droopOffset),
                    width = size.width * 0.085f,
                    color = leafPrimary
                )
                drawLeafPetal(
                    base = Offset(centerX + 2f, potTopY - size.height * 0.13f),
                    tip = Offset(centerX + size.width * 0.22f, potTopY - size.height * 0.21f + droopOffset),
                    width = size.width * 0.085f,
                    color = leafHighlight
                )
                // Main flower head
                val bloomScale = when (mood) {
                    PlantMood.GROWING -> 1.30f * sparklePulse.coerceAtMost(1.1f)
                    PlantMood.HEALTHY -> 1.05f
                    PlantMood.THIRSTY -> 0.85f
                    PlantMood.CRITICAL -> 0.65f
                }
                drawBlossom(
                    center = Offset(topX, topY),
                    radius = size.width * 0.09f * bloomScale,
                    petalColor = when (mood) {
                        PlantMood.CRITICAL -> Color(0xFFC9A093)
                        PlantMood.THIRSTY -> Color(0xFFFFA86B)
                        else -> Color(0xFFFF6B8B)
                    }
                )
                if (mood == PlantMood.GROWING || mood == PlantMood.HEALTHY) {
                    drawBlossom(
                        center = Offset(centerX - size.width * 0.13f, potTopY - size.height * 0.23f),
                        radius = size.width * 0.048f,
                        petalColor = GrowingGold
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawLeafPetal(
    base: Offset,
    tip: Offset,
    width: Float,
    color: Color
) {
    val dx = tip.x - base.x
    val dy = tip.y - base.y
    val len = kotlin.math.sqrt(dx * dx + dy * dy).coerceAtLeast(1f)
    val nx = -dy / len * width
    val ny = dx / len * width
    val mid = Offset((base.x + tip.x) * 0.5f, (base.y + tip.y) * 0.5f)

    val path = Path().apply {
        moveTo(base.x, base.y)
        quadraticTo(mid.x + nx, mid.y + ny, tip.x, tip.y)
        quadraticTo(mid.x - nx, mid.y - ny, base.x, base.y)
        close()
    }
    drawPath(path = path, color = color)
}

private fun DrawScope.drawBlossom(
    center: Offset,
    radius: Float,
    petalColor: Color
) {
    for (i in 0 until 5) {
        val angle = (i * 72f) * (PI / 180.0)
        val px = center.x + (cos(angle) * radius * 0.68f).toFloat()
        val py = center.y + (sin(angle) * radius * 0.68f).toFloat()
        drawCircle(
            color = petalColor,
            radius = radius * 0.55f,
            center = Offset(px, py)
        )
    }
    drawCircle(
        color = Color(0xFFFFE066),
        radius = radius * 0.42f,
        center = center
    )
}

private fun DrawScope.drawPotWithFace(
    plantType: PlantType,
    mood: PlantMood,
    hydrationPercent: Float
) {
    val centerX = size.width * 0.5f
    val rimTopY = size.height * 0.55f
    val rimHeight = size.height * 0.085f
    val rimWidth = size.width * 0.48f
    val potBottomY = size.height * 0.87f
    val potTopWidth = size.width * 0.42f
    val potBottomWidth = size.width * 0.30f

    val potMainColor = when (plantType) {
        PlantType.SUCCULENT -> Color(0xFFD16D47)
        PlantType.FERN -> Color(0xFF3E8E7E)
        PlantType.CACTUS -> Color(0xFFDF8549)
        PlantType.FLOWERING -> Color(0xFFD96570)
    }
    val potRimColor = when (plantType) {
        PlantType.SUCCULENT -> Color(0xFFE5825B)
        PlantType.FERN -> Color(0xFF52A896)
        PlantType.CACTUS -> Color(0xFFED9A62)
        PlantType.FLOWERING -> Color(0xFFEC7A85)
    }

    // Soil oval peeking above rim
    val soilColor = if (hydrationPercent < 30f) Color(0xFF9C7B56) else Color(0xFF5A3D28)
    drawOval(
        color = soilColor,
        topLeft = Offset(centerX - rimWidth * 0.44f, rimTopY - 8f),
        size = Size(rimWidth * 0.88f, 18f)
    )

    // Pot body trapezoid
    val potPath = Path().apply {
        moveTo(centerX - potTopWidth / 2f, rimTopY + rimHeight * 0.7f)
        lineTo(centerX + potTopWidth / 2f, rimTopY + rimHeight * 0.7f)
        lineTo(centerX + potBottomWidth / 2f, potBottomY)
        lineTo(centerX - potBottomWidth / 2f, potBottomY)
        close()
    }
    drawPath(path = potPath, color = potMainColor)

    // Pot rim
    drawRoundRect(
        color = potRimColor,
        topLeft = Offset(centerX - rimWidth / 2f, rimTopY),
        size = Size(rimWidth, rimHeight),
        cornerRadius = CornerRadius(14f, 14f)
    )

    // Cute face on the pot!
    val faceY = rimTopY + rimHeight + (potBottomY - rimTopY - rimHeight) * 0.45f
    val eyeSpacing = potTopWidth * 0.21f
    val eyeColor = Color(0xFF222222)

    when (mood) {
        PlantMood.HEALTHY -> {
            // Bright eyes + rosy cheeks + smile
            drawCircle(eyeColor, radius = 6.5f, center = Offset(centerX - eyeSpacing, faceY - 6f))
            drawCircle(eyeColor, radius = 6.5f, center = Offset(centerX + eyeSpacing, faceY - 6f))
            drawCircle(Color.White, radius = 2.2f, center = Offset(centerX - eyeSpacing - 2f, faceY - 8f))
            drawCircle(Color.White, radius = 2.2f, center = Offset(centerX + eyeSpacing - 2f, faceY - 8f))

            drawOval(
                color = Color(0xFFFFA5AB).copy(alpha = 0.65f),
                topLeft = Offset(centerX - eyeSpacing - 14f, faceY + 2f),
                size = Size(14f, 8f)
            )
            drawOval(
                color = Color(0xFFFFA5AB).copy(alpha = 0.65f),
                topLeft = Offset(centerX + eyeSpacing, faceY + 2f),
                size = Size(14f, 8f)
            )

            val smilePath = Path().apply {
                moveTo(centerX - 13f, faceY + 4f)
                quadraticTo(centerX, faceY + 18f, centerX + 13f, faceY + 4f)
            }
            drawPath(
                path = smilePath,
                color = eyeColor,
                style = Stroke(width = 4.5f, cap = StrokeCap.Round)
            )
        }
        PlantMood.GROWING -> {
            // Happy "^ ^" curved eyes + joyful open smile
            val leftEye = Path().apply {
                moveTo(centerX - eyeSpacing - 8f, faceY - 4f)
                quadraticTo(centerX - eyeSpacing, faceY - 14f, centerX - eyeSpacing + 8f, faceY - 4f)
            }
            val rightEye = Path().apply {
                moveTo(centerX + eyeSpacing - 8f, faceY - 4f)
                quadraticTo(centerX + eyeSpacing, faceY - 14f, centerX + eyeSpacing + 8f, faceY - 4f)
            }
            drawPath(leftEye, color = eyeColor, style = Stroke(width = 4.5f, cap = StrokeCap.Round))
            drawPath(rightEye, color = eyeColor, style = Stroke(width = 4.5f, cap = StrokeCap.Round))

            val bigSmile = Path().apply {
                moveTo(centerX - 15f, faceY + 4f)
                quadraticTo(centerX, faceY + 22f, centerX + 15f, faceY + 4f)
                close()
            }
            drawPath(bigSmile, color = Color(0xFF8C1D40))
        }
        PlantMood.THIRSTY -> {
            // Worried eyes + wavy/flat mouth
            drawCircle(eyeColor, radius = 6f, center = Offset(centerX - eyeSpacing, faceY - 5f))
            drawCircle(eyeColor, radius = 6f, center = Offset(centerX + eyeSpacing, faceY - 5f))
            // Slightly slanted brows
            drawLine(
                color = eyeColor,
                start = Offset(centerX - eyeSpacing - 8f, faceY - 14f),
                end = Offset(centerX - eyeSpacing + 6f, faceY - 17f),
                strokeWidth = 3.5f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = eyeColor,
                start = Offset(centerX + eyeSpacing - 6f, faceY - 17f),
                end = Offset(centerX + eyeSpacing + 8f, faceY - 14f),
                strokeWidth = 3.5f,
                cap = StrokeCap.Round
            )
            val worriedMouth = Path().apply {
                moveTo(centerX - 12f, faceY + 9f)
                quadraticTo(centerX - 4f, faceY + 4f, centerX, faceY + 9f)
                quadraticTo(centerX + 4f, faceY + 14f, centerX + 12f, faceY + 9f)
            }
            drawPath(
                path = worriedMouth,
                color = eyeColor,
                style = Stroke(width = 4f, cap = StrokeCap.Round)
            )
        }
        PlantMood.CRITICAL -> {
            // Sad "> <" or droopy eyes + frown + little sweat drop
            drawCircle(eyeColor, radius = 5.5f, center = Offset(centerX - eyeSpacing, faceY - 4f))
            drawCircle(eyeColor, radius = 5.5f, center = Offset(centerX + eyeSpacing, faceY - 4f))
            drawLine(
                color = eyeColor,
                start = Offset(centerX - eyeSpacing - 8f, faceY - 11f),
                end = Offset(centerX - eyeSpacing + 7f, faceY - 16f),
                strokeWidth = 3.5f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = eyeColor,
                start = Offset(centerX + eyeSpacing - 7f, faceY - 16f),
                end = Offset(centerX + eyeSpacing + 8f, faceY - 11f),
                strokeWidth = 3.5f,
                cap = StrokeCap.Round
            )
            val sadFrown = Path().apply {
                moveTo(centerX - 13f, faceY + 13f)
                quadraticTo(centerX, faceY + 2f, centerX + 13f, faceY + 13f)
            }
            drawPath(
                path = sadFrown,
                color = eyeColor,
                style = Stroke(width = 4.5f, cap = StrokeCap.Round)
            )
            // Tiny blue drop on cheek
            drawCircle(
                color = DewTertiary,
                radius = 4.5f,
                center = Offset(centerX + eyeSpacing + 14f, faceY + 4f)
            )
        }
    }
}

private fun DrawScope.drawGrowingSparkles(pulse: Float) {
    val stars = listOf(
        Offset(size.width * 0.20f, size.height * 0.22f) to 11f,
        Offset(size.width * 0.80f, size.height * 0.26f) to 13f,
        Offset(size.width * 0.16f, size.height * 0.45f) to 8f,
        Offset(size.width * 0.84f, size.height * 0.48f) to 9f,
        Offset(size.width * 0.50f, size.height * 0.12f) to 12f
    )
    stars.forEachIndexed { index, (pos, baseSize) ->
        val r = baseSize * (if (index % 2 == 0) pulse else 1.85f - pulse * 0.65f)
        val starPath = Path().apply {
            moveTo(pos.x, pos.y - r)
            lineTo(pos.x + r * 0.28f, pos.y - r * 0.28f)
            lineTo(pos.x + r, pos.y)
            lineTo(pos.x + r * 0.28f, pos.y + r * 0.28f)
            lineTo(pos.x, pos.y + r)
            lineTo(pos.x - r * 0.28f, pos.y + r * 0.28f)
            lineTo(pos.x - r, pos.y)
            lineTo(pos.x - r * 0.28f, pos.y - r * 0.28f)
            close()
        }
        drawPath(path = starPath, color = GrowingGold)
    }
}
