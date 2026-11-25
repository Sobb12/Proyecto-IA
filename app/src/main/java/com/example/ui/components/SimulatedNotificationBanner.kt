package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.model.NotificationCategory
import com.example.ui.SimulatedBanner

@Composable
fun SimulatedNotificationBanner(
    banner: SimulatedBanner?,
    onWaterAction: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = banner != null,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = modifier
    ) {
        if (banner != null) {
            val containerColor = when (banner.category) {
                NotificationCategory.WATERING -> MaterialTheme.colorScheme.tertiaryContainer
                NotificationCategory.SDG11 -> MaterialTheme.colorScheme.secondaryContainer
                NotificationCategory.SDG15 -> MaterialTheme.colorScheme.primaryContainer
            }
            val contentColor = when (banner.category) {
                NotificationCategory.WATERING -> MaterialTheme.colorScheme.onTertiaryContainer
                NotificationCategory.SDG11 -> MaterialTheme.colorScheme.onSecondaryContainer
                NotificationCategory.SDG15 -> MaterialTheme.colorScheme.onPrimaryContainer
            }
            val icon = when (banner.category) {
                NotificationCategory.WATERING -> Icons.Filled.WaterDrop
                NotificationCategory.SDG11 -> Icons.Filled.LocationCity
                NotificationCategory.SDG15 -> Icons.Filled.Eco
            }

            Surface(
                color = containerColor,
                contentColor = contentColor,
                shape = RoundedCornerShape(20.dp),
                shadowElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("simulated_notification_banner")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 14.dp, top = 10.dp, bottom = 10.dp, end = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = banner.category.label,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = banner.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = banner.message,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (banner.category == NotificationCategory.WATERING) {
                        TextButton(
                            onClick = {
                                onWaterAction()
                                onDismiss()
                            },
                            modifier = Modifier.testTag("banner_water_now_button")
                        ) {
                            Text("Regar", fontWeight = FontWeight.Bold)
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("banner_dismiss_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Cerrar notificación"
                        )
                    }
                }
            }
        }
    }
}
