package com.violetradio.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.violetradio.app.domain.model.Station

/**
 * Card for displaying a radio station in a list
 */
@Composable
fun StationCard(
    station: Station,
    onStationClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onStationClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Station Logo
            AsyncImage(
                model = station.favicon,
                contentDescription = station.name,
                modifier = Modifier
                    .size(48.dp)
                    .padding(end = 12.dp),
                contentScale = ContentScale.Crop
            )

            // Station Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = station.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    station.bitrate?.let { bitrate ->
                        Text(
                            text = "$bitrate kbps",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = station.country,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Favorite Button
            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (station.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (station.isFavorite) "Usuń z ulubionych" else "Dodaj do ulubionych",
                    tint = if (station.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Menu Button
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Menu",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
