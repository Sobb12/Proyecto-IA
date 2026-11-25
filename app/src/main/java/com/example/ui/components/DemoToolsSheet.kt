package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.NotificationCategory
import com.example.ui.SproutUiState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DemoToolsSheet(
    uiState: SproutUiState,
    onDismiss: () -> Unit,
    onFastForwardHours: (Int) -> Unit,
    onJumpToHydration: (Float) -> Unit,
    onTriggerNotification: (NotificationCategory) -> Unit,
    onResetDemo: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("demo_tools_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Icon(
                        imageVector = Icons.Filled.Science,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(8.dp).size(22.dp)
                    )
                }
                Column {
                    Text(
                        text = "Herramientas de Demo y Pruebas",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "Adelanta el tiempo para probar transiciones Sana -> Con Sed -> Crítica.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Desfase simulado: +${uiState.virtualOffsetHours}h",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = "Hidratación: ${uiState.hydrationPercent.toInt()}% (${uiState.mood.label})",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Text(
                text = "Adelantar Tiempo (Demo)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilledTonalButton(
                    onClick = { onFastForwardHours(12) },
                    modifier = Modifier.testTag("demo_ff_12h_button")
                ) {
                    Icon(Icons.Filled.FastForward, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("+12 Horas")
                }
                FilledTonalButton(
                    onClick = { onFastForwardHours(24) },
                    modifier = Modifier.testTag("demo_ff_24h_button")
                ) {
                    Icon(Icons.Filled.FastForward, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("+1 Día (24h)")
                }
                FilledTonalButton(
                    onClick = { onFastForwardHours(72) },
                    modifier = Modifier.testTag("demo_ff_72h_button")
                ) {
                    Icon(Icons.Filled.FastForward, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("+3 Días (72h)")
                }
            }

            Text(
                text = "Saltar Directo a un Estado Visual",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { onJumpToHydration(85f) },
                    modifier = Modifier.testTag("demo_jump_healthy_button")
                ) {
                    Text("Sana (85%)")
                }
                OutlinedButton(
                    onClick = { onJumpToHydration(45f) },
                    modifier = Modifier.testTag("demo_jump_thirsty_button")
                ) {
                    Icon(Icons.Filled.WaterDrop, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Con Sed (45%)")
                }
                OutlinedButton(
                    onClick = { onJumpToHydration(18f) },
                    modifier = Modifier.testTag("demo_jump_critical_button")
                ) {
                    Icon(Icons.Filled.Warning, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Crítica (18%)")
                }
            }

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FilledTonalButton(
                    onClick = {
                        onTriggerNotification(NotificationCategory.WATERING)
                        onDismiss()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("demo_trigger_alert_button")
                ) {
                    Icon(Icons.Filled.NotificationsActive, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Probar Alerta")
                }
                OutlinedButton(
                    onClick = {
                        onResetDemo()
                        onDismiss()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("demo_reset_button")
                ) {
                    Icon(Icons.Filled.RestartAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Reiniciar Demo")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
