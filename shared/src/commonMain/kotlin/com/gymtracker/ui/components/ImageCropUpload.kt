package com.gymtracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gymtracker.ui.theme.FitTrackPrimary
import com.gymtracker.ui.theme.FitTrackSurface
import com.gymtracker.util.decodeBase64ToImageBitmap

@Composable
expect fun ImageCropUpload(
    currentCover: String?,
    onCoverSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
)
