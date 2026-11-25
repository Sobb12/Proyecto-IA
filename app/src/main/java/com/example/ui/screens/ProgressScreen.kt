package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.ALL_ECO_FACTS
import com.example.model.EcoFact
import com.example.model.SdgTag
import com.example.ui.SproutUiState
import com.example.ui.theme.Sdg11Amber
import com.example.ui.theme.Sdg15Green

private data class OdsModuleInfo(
    val tag: SdgTag,
    val hook: String,
    val contributionExplanation: String,
    val icon: ImageVector,
    val accentColor: Color,
    val testTag: String
)

private val ODS_MODULES = listOf(
    OdsModuleInfo(
        tag = SdgTag.SDG11,
        hook = "Las plantas en ventanas y balcones refrescan tu barrio y limpian el aire urbano.",
        contributionExplanation = "Al cuidar tu planta en casa ayudas a construir ciudades más verdes y saludables: sus hojas filtran polvo del aire, regulan la temperatura de tu habitación y te enseñan a ahorrar agua dulce, igual que los techos verdes y parques esponja de las ciudades sostenibles.",
        icon = Icons.Filled.LocationCity,
        accentColor = Sdg11Amber,
        testTag = "ods_11_expandable_card"
    ),
    OdsModuleInfo(
        tag = SdgTag.SDG15,
        hook = "Cada maceta con tierra viva protege microbios, polinizadores y la biodiversidad terrestre.",
        contributionExplanation = "Cuando riegas y cuidas tu planta con responsabilidad proteges un pequeño ecosistema terrestre: la tierra sana alberga millones de microorganismos, las flores atraen abejas y mariposas que cruzan la ciudad, y cada hoja verde captura carbono del aire.",
        icon = Icons.Filled.Eco,
        accentColor = Sdg15Green,
        testTag = "ods_15_expandable_card"
    )
)

@Composable
fun ProgressScreen(
    uiState: SproutUiState,
    modifier: Modifier = Modifier
) {
    var expandedSdg11 by rememberSaveable { mutableStateOf(false) }
    var expandedSdg15 by rememberSaveable { mutableStateOf(false) }

    val gardenerRank = when {
        uiState.totalWaterings >= 10 -> "Campeón Eco ODS"
        uiState.totalWaterings >= 6 -> "Botánico Urbano"
        uiState.totalWaterings >= 3 -> "Guardián de Hojas"
        else -> "Aprendiz de Semilla"
    }

    val indexedFacts = ALL_ECO_FACTS.mapIndexed { index, fact ->
        fact to (index < uiState.unlockedFactsCount)
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 640.dp)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Banner Principal de Progreso Diario
            item {
                Card(
                    shape = MaterialTheme.shapes.large,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(168.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = R.drawable.img_eco_banner),
                            contentDescription = "Ilustración de barrio verde y ciudad sostenible",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Black.copy(alpha = 0.15f),
                                            Color.Black.copy(alpha = 0.74f)
                                        )
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    text = "Nivel: $gardenerRank",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                            Text(
                                text = "Progreso de Cuidado Diario",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "Mantén hidratada a ${uiState.plantName} para subir de nivel y desbloquear datos.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.92f)
                            )
                        }
                    }
                }
            }

            // Sección Limpia de Métricas Diarias (Desacoplada del contenido educativo ODS)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Tus Estadísticas de Cuidado",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatTile(
                            icon = Icons.Filled.LocalFireDepartment,
                            value = "${uiState.streakDays}",
                            label = "Días de Cuidado",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("stat_streak_days")
                        )
                        StatTile(
                            icon = Icons.Filled.Opacity,
                            value = "${uiState.totalWaterings}",
                            label = "Riegos",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("stat_total_waterings")
                        )
                        StatTile(
                            icon = Icons.Filled.AutoAwesome,
                            value = "${uiState.unlockedFactsCount}/${uiState.totalFactsCount}",
                            label = "Datos Eco",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("stat_unlocked_facts")
                        )
                    }
                }
            }

            // Separador visual claro entre métricas diarias y sección educativa ODS
            item {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f),
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }

            // Sección Separada e Interactiva de los ODS (ODS 11 y ODS 15)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Objetivos de Desarrollo Sostenible (ODS)",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Explora cómo cuidar tu planta en casa aporta a las metas globales de la ONU.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Tarjeta Expandible ODS 11
            item {
                val module = ODS_MODULES[0]
                val sdgFacts = indexedFacts.filter { it.first.sdg == SdgTag.SDG11 }
                OdsExpandableCard(
                    module = module,
                    isExpanded = expandedSdg11,
                    onToggle = { expandedSdg11 = !expandedSdg11 },
                    factsWithUnlockState = sdgFacts
                )
            }

            // Tarjeta Expandible ODS 15
            item {
                val module = ODS_MODULES[1]
                val sdgFacts = indexedFacts.filter { it.first.sdg == SdgTag.SDG15 }
                OdsExpandableCard(
                    module = module,
                    isExpanded = expandedSdg15,
                    onToggle = { expandedSdg15 = !expandedSdg15 },
                    factsWithUnlockState = sdgFacts
                )
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }
}

@Composable
private fun StatTile(
    icon: ImageVector,
    value: String,
    label: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun OdsExpandableCard(
    module: OdsModuleInfo,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    factsWithUnlockState: List<Pair<EcoFact, Boolean>>
) {
    val unlockedCount = factsWithUnlockState.count { it.second }

    Card(
        onClick = onToggle,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = if (isExpanded) 2.dp else 1.dp,
            color = if (isExpanded) module.accentColor else MaterialTheme.colorScheme.outlineVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isExpanded) 4.dp else 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
            .testTag(module.testTag)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Estado Colapsado (por defecto): ícono + insignia ODS + título + gancho breve
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    color = module.accentColor.copy(alpha = 0.15f),
                    shape = CircleShape,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = module.icon,
                            contentDescription = module.tag.title,
                            tint = module.accentColor,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = module.accentColor.copy(alpha = 0.14f),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = module.tag.code,
                                style = MaterialTheme.typography.labelSmall,
                                color = module.accentColor,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = "$unlockedCount/${factsWithUnlockState.size} datos",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = module.tag.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = module.hook,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Indicador / botón claro para expandir o colapsar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onToggle,
                    modifier = Modifier.testTag("${module.testTag}_toggle_button")
                ) {
                    Text(
                        text = if (isExpanded) "Ver menos" else "Clic para más información",
                        color = module.accentColor,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = if (isExpanded) "Contraer detalles" else "Expandir detalles",
                        tint = module.accentColor
                    )
                }
            }

            // Estado Expandido: explicación detallada + datos ecológicos desbloqueados del ODS
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    HorizontalDivider(color = module.accentColor.copy(alpha = 0.25f))

                    Surface(
                        color = module.accentColor.copy(alpha = 0.08f),
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "¿Cómo ayuda cuidar tu planta?",
                                style = MaterialTheme.typography.titleSmall,
                                color = module.accentColor,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = module.contributionExplanation,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Text(
                        text = "Datos Eco Desbloqueados (${module.tag.code})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    factsWithUnlockState.forEach { (fact, isUnlocked) ->
                        OdsFactItem(
                            fact = fact,
                            isUnlocked = isUnlocked,
                            accentColor = module.accentColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OdsFactItem(
    fact: EcoFact,
    isUnlocked: Boolean,
    accentColor: Color
) {
    Surface(
        color = if (isUnlocked) {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.28f)
        },
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (isUnlocked) {
                Text(
                    text = fact.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = fact.summary,
                    style = MaterialTheme.typography.bodySmall
                )
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f),
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Acción en casa: ${fact.actionTip}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "Dato bloqueado",
                        tint = accentColor.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Dato Misterioso #${fact.id} — Registra otro riego en tu Jardín para desbloquearlo.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
