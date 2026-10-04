package com.dogfellas.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dogfellas.app.ui.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onBack: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {

    var nombre by remember {
        mutableStateOf("")
    }

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    val infiniteTransition =
        rememberInfiniteTransition(
            label = "register_animation"
        )

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1800,
                easing = EaseInOut
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "card_scale"
    )

    LaunchedEffect(viewModel.isSuccess) {

        if (viewModel.isSuccess) {

            onRegisterSuccess()

            viewModel.resetState()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2D1B12),
                        Color(0xFF4E342E),
                        Color(0xFF6D4C41),
                        Color(0xFF8D6E63)
                    )
                )
            )
    ) {

        // ═════════════════════════════════════════════
        // GLOW
        // ═════════════════════════════════════════════

        Box(
            modifier = Modifier
                .size(320.dp)
                .offset(
                    x = (-80).dp,
                    y = (-80).dp
                )
                .blur(120.dp)
                .background(
                    Color(0x55D7B899),
                    CircleShape
                )
        )


        // ═════════════════════════════════════════════
        // CONTENIDO PRINCIPAL
        // ═════════════════════════════════════════════

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment =
                Alignment.CenterHorizontally,
            verticalArrangement =
                Arrangement.Center
        ) {


            // ═════════════════════════════════════════
            // CARD PRINCIPAL
            // ═════════════════════════════════════════

            Box(
                modifier = Modifier
                    .scale(scale)
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(36.dp)
                    )
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0x33FFFFFF),
                                Color(0x11FFFFFF)
                            )
                        )
                    )
                    .border(
                        width = 2.dp,
                        color = Color(0x55FFFFFF),
                        shape = RoundedCornerShape(36.dp)
                    )
                    .padding(26.dp)
            ) {

                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {


                    // ═════════════════════════════
                    // ICONO
                    // ═════════════════════════════

                    Box(
                        modifier = Modifier
                            .size(95.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFFD7B899),
                                        Color(0xFF8B5A30)
                                    )
                                )
                            ),
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Icon(
                            imageVector =
                                Icons.Rounded.PersonAdd,
                            contentDescription =
                                "Crear cuenta",
                            tint = Color.White,
                            modifier =
                                Modifier.size(48.dp)
                        )
                    }


                    Spacer(
                        modifier = Modifier.height(22.dp)
                    )


                    // ═════════════════════════════
                    // TÍTULO
                    // ═════════════════════════════

                    Text(
                        text = "Crear Cuenta",
                        color = Color(0xFFF5E6CA),
                        fontSize = 30.sp,
                        fontWeight =
                            FontWeight.Bold
                    )


                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )


                    Text(
                        text =
                            "Únete a la familia DogFellas",
                        color = Color(0xFFD7B899),
                        fontSize = 14.sp,
                        textAlign =
                            TextAlign.Center
                    )


                    Spacer(
                        modifier = Modifier.height(30.dp)
                    )


                    // ═════════════════════════════
                    // NOMBRE COMPLETO
                    // ═════════════════════════════

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = {
                            nombre = it
                        },
                        label = {

                            Text(
                                text = "Nombre completo",
                                color =
                                    Color(0xFFF5E6CA)
                            )
                        },
                        singleLine = true,
                        leadingIcon = {

                            Icon(
                                imageVector =
                                    Icons.Default.Person,
                                contentDescription =
                                    null,
                                tint =
                                    Color(0xFFD7B899)
                            )
                        },
                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType =
                                    KeyboardType.Text
                            ),
                        modifier =
                            Modifier.fillMaxWidth(),
                        shape =
                            RoundedCornerShape(18.dp),
                        colors =
                            OutlinedTextFieldDefaults
                                .colors(
                                    focusedTextColor =
                                        Color.White,
                                    unfocusedTextColor =
                                        Color.White,
                                    focusedBorderColor =
                                        Color(0xFFD7B899),
                                    unfocusedBorderColor =
                                        Color(0xFF8D6E63),
                                    cursorColor =
                                        Color(0xFFD7B899),
                                    focusedLabelColor =
                                        Color(0xFFD7B899),
                                    unfocusedLabelColor =
                                        Color(0xFFEADBC8)
                                )
                    )


                    Spacer(
                        modifier = Modifier.height(18.dp)
                    )


                    // ═════════════════════════════
                    // EMAIL
                    // ═════════════════════════════

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                        },
                        label = {

                            Text(
                                text =
                                    "Correo electrónico",
                                color =
                                    Color(0xFFF5E6CA)
                            )
                        },
                        singleLine = true,
                        leadingIcon = {

                            Icon(
                                imageVector =
                                    Icons.Default.Email,
                                contentDescription =
                                    null,
                                tint =
                                    Color(0xFFD7B899)
                            )
                        },
                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType =
                                    KeyboardType.Email
                            ),
                        modifier =
                            Modifier.fillMaxWidth(),
                        shape =
                            RoundedCornerShape(18.dp),
                        colors =
                            OutlinedTextFieldDefaults
                                .colors(
                                    focusedTextColor =
                                        Color.White,
                                    unfocusedTextColor =
                                        Color.White,
                                    focusedBorderColor =
                                        Color(0xFFD7B899),
                                    unfocusedBorderColor =
                                        Color(0xFF8D6E63),
                                    cursorColor =
                                        Color(0xFFD7B899),
                                    focusedLabelColor =
                                        Color(0xFFD7B899),
                                    unfocusedLabelColor =
                                        Color(0xFFEADBC8)
                                )
                    )


                    Spacer(
                        modifier = Modifier.height(18.dp)
                    )


                    // ═════════════════════════════
                    // CONTRASEÑA
                    // ═════════════════════════════

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                        },
                        label = {

                            Text(
                                text = "Contraseña",
                                color =
                                    Color(0xFFF5E6CA)
                            )
                        },
                        singleLine = true,
                        visualTransformation =
                            PasswordVisualTransformation(),
                        leadingIcon = {

                            Icon(
                                imageVector =
                                    Icons.Default.Lock,
                                contentDescription =
                                    null,
                                tint =
                                    Color(0xFFD7B899)
                            )
                        },
                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType =
                                    KeyboardType.Password
                            ),
                        modifier =
                            Modifier.fillMaxWidth(),
                        shape =
                            RoundedCornerShape(18.dp),
                        colors =
                            OutlinedTextFieldDefaults
                                .colors(
                                    focusedTextColor =
                                        Color.White,
                                    unfocusedTextColor =
                                        Color.White,
                                    focusedBorderColor =
                                        Color(0xFFD7B899),
                                    unfocusedBorderColor =
                                        Color(0xFF8D6E63),
                                    cursorColor =
                                        Color(0xFFD7B899),
                                    focusedLabelColor =
                                        Color(0xFFD7B899),
                                    unfocusedLabelColor =
                                        Color(0xFFEADBC8)
                                )
                    )


                    // ═════════════════════════════
                    // ERROR
                    // ═════════════════════════════

                    viewModel.errorMessage?.let {

                        Spacer(
                            modifier =
                                Modifier.height(12.dp)
                        )

                        Text(
                            text = "❌ $it",
                            color =
                                Color(0xFFFF8A80),
                            fontSize = 13.sp,
                            textAlign =
                                TextAlign.Center
                        )
                    }


                    Spacer(
                        modifier =
                            Modifier.height(28.dp)
                    )


                    // ═════════════════════════════
                    // BOTÓN REGISTRAR
                    // ═════════════════════════════

                    Button(
                        onClick = {

                            viewModel.register(
                                nombre.trim(),
                                email.trim(),
                                password
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .border(
                                width = 2.dp,
                                color =
                                    Color(0xFFFFE0B2),
                                shape =
                                    RoundedCornerShape(
                                        100.dp
                                    )
                            ),
                        shape =
                            RoundedCornerShape(100.dp),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    Color(0xFFD7B899),
                                contentColor =
                                    Color(0xFF2D1B12)
                            )
                    ) {

                        if (viewModel.isLoading) {

                            CircularProgressIndicator(
                                color =
                                    Color(0xFF2D1B12),
                                modifier =
                                    Modifier.size(22.dp),
                                strokeWidth = 2.dp
                            )

                        } else {

                            Text(
                                text =
                                    "REGISTRARME",
                                fontWeight =
                                    FontWeight.Bold,
                                fontSize = 16.sp,
                                letterSpacing = 2.sp
                            )
                        }
                    }


                    Spacer(
                        modifier =
                            Modifier.height(18.dp)
                    )


                    // ═════════════════════════════
                    // VOLVER AL LOGIN
                    // ═════════════════════════════

                    Text(
                        text =
                            "¿Ya tienes cuenta? Inicia sesión",
                        color =
                            Color(0xFFD7B899),
                        fontSize = 13.sp,
                        modifier =
                            Modifier.clickable {
                                onBack()
                            }
                    )
                }
            }


            Spacer(
                modifier = Modifier.height(24.dp)
            )


            // ═════════════════════════════════════════
            // PIE
            // ═════════════════════════════════════════

            Text(
                text =
                    "DogFellas • Bogotá, Colombia",
                color =
                    Color(0x99FFFFFF),
                fontSize = 12.sp
            )
        }
    }
}