package com.gymtracker.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gymtracker.ui.components.FitTrackButton
import com.gymtracker.ui.components.FitTrackTextField
import com.gymtracker.ui.theme.FitTrackBackground
import com.gymtracker.ui.theme.FitTrackSurface
import com.gymtracker.ui.theme.FitTrackTextSecondary
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FitTrackBackground)
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(48.dp)) // Balance
            Text(
                text = "FitTrack",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            IconButton(onClick = { /* TODO: Help */ }) {
                Icon(
                    imageVector = Icons.Default.HelpOutline,
                    contentDescription = "Ajuda",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Bem-vindo de volta!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        FitTrackTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = "E-mail ou nome de usuário"
        )

        Spacer(modifier = Modifier.height(16.dp))

        FitTrackTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = "Senha",
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Esqueci minha senha",
            color = FitTrackTextSecondary,
            fontSize = 14.sp,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .align(Alignment.Start)
                .clickable { /* TODO */ }
                .padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        FitTrackButton(
            text = "Entrar",
            onClick = onLoginSuccess,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Não tem uma conta?",
            color = FitTrackTextSecondary,
            fontSize = 14.sp
        )
        
        Text(
            text = "Criar nova conta",
            color = FitTrackTextSecondary,
            fontSize = 14.sp,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .clickable { onNavigateToRegister() }
                .padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Parceiro Oficial Banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(FitTrackSurface)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Parceiro oficial",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Descontos exclusivos para membros FitTrack",
                    fontSize = 14.sp,
                    color = FitTrackTextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { /* TODO */ },
                    colors = ButtonDefaults.buttonColors(containerColor = FitTrackSurface),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Ver ofertas", color = MaterialTheme.colorScheme.onBackground)
                }
            }
            // A placeholder for the image
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.background)
            )
        }
    }
}
