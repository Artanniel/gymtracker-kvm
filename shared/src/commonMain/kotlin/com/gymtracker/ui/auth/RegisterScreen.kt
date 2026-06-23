package com.gymtracker.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gymtracker.ui.components.FitTrackButton
import com.gymtracker.ui.components.FitTrackTextField
import com.gymtracker.ui.theme.FitTrackBackground
import com.gymtracker.ui.theme.FitTrackTextSecondary

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
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
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                text = "Criar Conta",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.width(48.dp)) // Balance for back button
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Junte-se ao FitTrack",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Registre-se para acompanhar seus treinos e atingir seus objetivos.",
            fontSize = 16.sp,
            color = FitTrackTextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        FitTrackTextField(
            value = name,
            onValueChange = { name = it },
            placeholder = "Nome completo"
        )

        Spacer(modifier = Modifier.height(16.dp))

        FitTrackTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = "E-mail"
        )

        Spacer(modifier = Modifier.height(16.dp))

        FitTrackTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = "Senha",
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(32.dp))

        FitTrackButton(
            text = "Criar Conta",
            onClick = onRegisterSuccess,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Já tem uma conta? ",
                color = FitTrackTextSecondary,
                fontSize = 14.sp
            )
            Text(
                text = "Entrar",
                color = FitTrackTextSecondary,
                fontSize = 14.sp,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .clickable { onNavigateBack() }
                    .padding(vertical = 8.dp)
            )
        }
    }
}
