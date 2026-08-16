package com.gymtracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.gymtracker.ui.theme.FitTrackPrimary
import com.gymtracker.ui.theme.FitTrackSurface
import com.gymtracker.util.decodeBase64ToImageBitmap
import org.w3c.dom.HTMLInputElement
import org.w3c.files.FileReader
import org.jetbrains.skia.Image
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap

/**
 * Component for uploading and cropping cover images.
 * Provides file selection, zoom in/out, and panning within a fixed frame.
 */
@Composable
actual fun ImageCropUpload(
    currentCover: String?,
    onCoverSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedImageBase64 by remember { mutableStateOf<String?>(null) }
    var zoom by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var showCropDialog by remember { mutableStateOf(false) }
    var imageLoaded by remember { mutableStateOf(false) }
    var imageSize by remember { mutableStateOf(IntSize.Zero) }

    // Frame dimensions (fixed cover size: 16:9 ratio)
    val frameWidth = 400f
    val frameHeight = 225f

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f),
            colors = CardDefaults.cardColors(containerColor = FitTrackSurface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title
                Text(
                    "Upload da Capa",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    "Selecione uma imagem e ajuste o enquadramento",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(16.dp))

                // File selection button
                if (selectedImageBase64 == null) {
                    Button(
                        onClick = {
                            // Create file input element
                            val input = document.createElement("input") as HTMLInputElement
                            input.type = "file"
                            input.accept = "image/*"
                            input.onchange = { event ->
                                val files = input.files
                                if (files != null && files.length > 0) {
                                    val file = files.item(0)
                                    if (file != null) {
                                        val reader = FileReader()
                                        reader.onload = { _ ->
                                            val base64 = reader.result as? String
                                            if (base64 != null) {
                                                selectedImageBase64 = base64
                                                showCropDialog = true
                                                zoom = 1f
                                                offset = Offset.Zero
                                            }
                                        }
                                        reader.readAsDataURL(file)
                                    }
                                }
                                false
                            }
                            document.body?.appendChild(input)
                            input.click()
                            document.body?.removeChild(input)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FitTrackPrimary),
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        Text(
                            "Escolher Imagem",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // Show current cover if exists
                    if (currentCover != null) {
                        Text(
                            "Capa atual:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(8.dp))
                        CoverImagePreview(
                            coverBase64 = currentCover,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }
                } else {
                    // Image loaded - show crop editor
                    Text(
                        "Ajuste o enquadramento:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(8.dp))

                    // Crop frame container
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.3f))
                            .border(2.dp, FitTrackPrimary, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Image with zoom and pan
                        selectedImageBase64?.let { base64 ->
                            val bitmap = remember(base64) {
                                try {
                                    val cleanBase64 = if (base64.contains(",")) {
                                        base64.substringAfter(",")
                                    } else {
                                        base64
                                    }
                                    val decoded = window.atob(cleanBase64)
                                    val bytes = ByteArray(decoded.length) { decoded[it].code.toByte() }
                                    Image.makeFromEncoded(bytes).toComposeImageBitmap()
                                } catch (e: Exception) {
                                    null
                                }
                            }

                            if (bitmap != null) {
                                val transformState = rememberTransformableState { zoomChange, panChange, _ ->
                                    zoom = (zoom * zoomChange).coerceIn(0.5f, 5f)
                                    offset = Offset(
                                        x = offset.x + panChange.x,
                                        y = offset.y + panChange.y
                                    )
                                }

                                androidx.compose.foundation.Image(
                                    bitmap = bitmap,
                                    contentDescription = "Imagem para capa",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .graphicsLayer {
                                            scaleX = zoom
                                            scaleY = zoom
                                            translationX = offset.x
                                            translationY = offset.y
                                        }
                                        .transformable(state = transformState)
                                        .pointerInput(Unit) {
                                            detectDragGestures { change, dragAmount ->
                                                change.consume()
                                                offset = Offset(
                                                    x = offset.x + dragAmount.x,
                                                    y = offset.y + dragAmount.y
                                                )
                                            }
                                        },
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Zoom controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Zoom out button
                        Button(
                            onClick = {
                                zoom = (zoom - 0.25f).coerceAtLeast(0.5f)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier.size(48.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("-", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }

                        // Zoom level indicator
                        Text(
                            "${(zoom * 100).toInt()}%",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        // Zoom in button
                        Button(
                            onClick = {
                                zoom = (zoom + 0.25f).coerceAtMost(5f)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier.size(48.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("+", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }

                        // Reset button
                        Button(
                            onClick = {
                                zoom = 1f
                                offset = Offset.Zero
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier.size(48.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("R", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Cancel button
                        OutlinedButton(
                            onClick = {
                                selectedImageBase64 = null
                                zoom = 1f
                                offset = Offset.Zero
                            },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Cancelar")
                        }

                        // Confirm button
                        Button(
                            onClick = {
                                selectedImageBase64?.let { base64 ->
                                    onCoverSelected(base64)
                                }
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = FitTrackPrimary),
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "Aplicar",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CoverImagePreview(
    coverBase64: String,
    modifier: Modifier = Modifier
) {
    val bitmap = remember(coverBase64) {
        decodeBase64ToImageBitmap(coverBase64)
    }

    if (bitmap != null) {
        androidx.compose.foundation.Image(
            bitmap = bitmap,
            contentDescription = "Capa atual",
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Erro ao carregar imagem",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
