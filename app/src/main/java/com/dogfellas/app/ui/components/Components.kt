package com.dogfellas.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.dogfellas.app.ui.theme.*

// ─────────────────────────────────────────────
// TOP HEADER PREMIUM
// ─────────────────────────────────────────────
@Composable
fun DogFellasHeader(
    showBackButton: Boolean = false,
    onHomeClick: (() -> Unit)? = null
) {

    Box {

        // Glow superior
        Box(
            modifier = Modifier
                .size(180.dp)
                .offset(x = (-40).dp, y = (-40).dp)
                .blur(100.dp)
                .background(
                    Color(0x33D7B899),
                    CircleShape
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF2D1B12),
                            Color(0xFF4E342E)
                        )
                    )
                )
                .border(
                    1.dp,
                    Color(0x33FFFFFF)
                )
                .padding(
                    horizontal = 20.dp,
                    vertical = 18.dp
                ),

            verticalAlignment = Alignment.CenterVertically
        ) {

            // LOGO
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                BrownLight,
                                Color(0xFF8B5A30)
                            )
                        )
                    )
                    .border(
                        2.dp,
                        Color(0x55FFFFFF),
                        RoundedCornerShape(16.dp)
                    ),

                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "🐕",
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // TITULOS
            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "DogFellas",

                    color = Cream,

                    fontWeight = FontWeight.ExtraBold,

                    fontSize = 24.sp
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "LOCALIZA A TU MANADA",

                    color = BrownLight,

                    fontSize = 10.sp,

                    letterSpacing = 3.sp
                )
            }

            // BOTÓN HOME
            if (showBackButton && onHomeClick != null) {

                Button(
                    onClick = onHomeClick,

                    shape = RoundedCornerShape(100.dp),

                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrownLight,
                        contentColor = BrownDark
                    )
                ) {

                    Text(
                        text = "🏠 Inicio",

                        fontWeight = FontWeight.Bold,

                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
// MENU ITEM PREMIUM
// ─────────────────────────────────────────────
@Composable
fun DogFellasMenuItem(
    icon: String,
    iconBackground: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(24.dp)
            )
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF3A241A),
                        Color(0xFF5A3A22)
                    )
                )
            )
            .border(
                1.dp,
                Color(0x33FFFFFF),
                RoundedCornerShape(24.dp)
            )
            .clickable { onClick() }
            .padding(
                horizontal = 18.dp,
                vertical = 18.dp
            ),

        verticalAlignment = Alignment.CenterVertically
    ) {

        // ICON BOX
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            iconBackground,
                            Color(0xFF8B5A30)
                        )
                    )
                ),

            contentAlignment = Alignment.Center
        ) {

            Text(
                text = icon,
                fontSize = 30.sp
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // TEXTOS
        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = title,

                color = Cream,

                fontWeight = FontWeight.Bold,

                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,

                color = Tan,

                fontSize = 13.sp
            )
        }

        Text(
            text = "›",

            color = BrownLight,

            fontSize = 30.sp,

            fontWeight = FontWeight.Bold
        )
    }
}

// ─────────────────────────────────────────────
// SECTION TITLE
// ─────────────────────────────────────────────
@Composable
fun SectionTitle(text: String) {

    Column {

        Text(
            text = text,

            color = Cream,

            fontWeight = FontWeight.ExtraBold,

            fontSize = 30.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .width(90.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(100.dp))
                .background(BrownLight)
        )
    }
}

// ─────────────────────────────────────────────
// INFO BANNER PREMIUM
// ─────────────────────────────────────────────
@Composable
fun InfoBanner(
    icon: String,
    text: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF2D1B12),
                        Color(0xFF4E342E)
                    )
                )
            )
            .border(
                1.dp,
                Color(0x33FFFFFF),
                RoundedCornerShape(24.dp)
            )
            .padding(18.dp),

        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            BrownLight,
                            Color(0xFF8B5A30)
                        )
                    )
                ),

            contentAlignment = Alignment.Center
        ) {

            Text(
                text = icon,
                fontSize = 24.sp
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = text,

            color = Cream,

            fontSize = 14.sp,

            lineHeight = 22.sp
        )
    }
}

// ─────────────────────────────────────────────
// MODAL PREMIUM
// ─────────────────────────────────────────────
@Composable
fun ConstructionModal(
    featureName: String,
    featureIcon: String,
    onDismiss: () -> Unit
) {

    Dialog(
        onDismissRequest = onDismiss
    ) {

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(30.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF3A241A),
                            Color(0xFF5A3A22)
                        )
                    )
                )
                .border(
                    1.dp,
                    Color(0x44FFFFFF),
                    RoundedCornerShape(30.dp)
                )
                .padding(28.dp)
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    BrownLight,
                                    Color(0xFF8B5A30)
                                )
                            )
                        ),

                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = featureIcon,
                        fontSize = 42.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "En Construcción",

                    color = Cream,

                    fontWeight = FontWeight.Bold,

                    fontSize = 26.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = featureName,

                    color = BrownLight,

                    fontSize = 16.sp,

                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Esta funcionalidad estará disponible próximamente en DogFellas.",

                    color = Tan,

                    textAlign = TextAlign.Center,

                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,

                    shape = RoundedCornerShape(100.dp),

                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrownLight,
                        contentColor = BrownDark
                    ),

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {

                    Text(
                        text = "ENTENDIDO",

                        fontWeight = FontWeight.Bold,

                        letterSpacing = 2.sp
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
// FOOTER
// ─────────────────────────────────────────────
@Composable
fun DogFellasFooter() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 18.dp),

        contentAlignment = Alignment.Center
    ) {

        Text(
            text = "🐕  🦴  🐩  🦴  🐕",

            fontSize = 20.sp,

            color = BrownLight,

            letterSpacing = 4.sp
        )
    }
}

// ─────────────────────────────────────────────
// CARD VETERINARIA PREMIUM
// ─────────────────────────────────────────────
@Composable
fun VeterinariaCard(
    nombre: String,
    direccion: String,
    onClick: () -> Unit = {}
) {

    Card(
        onClick = onClick,

        modifier = Modifier
            .width(280.dp)
            .padding(vertical = 6.dp),

        shape = RoundedCornerShape(28.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF2D1B12),
                            Color(0xFF4E342E)
                        )
                    )
                )
                .border(
                    1.dp,
                    Color(0x33FFFFFF),
                    RoundedCornerShape(28.dp)
                )
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),

                verticalAlignment = Alignment.CenterVertically
            ) {

                // ICONO
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    BrownLight,
                                    Color(0xFF8B5A30)
                                )
                            )
                        ),

                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "🐾",
                        fontSize = 34.sp
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = nombre,

                        color = Cream,

                        fontWeight = FontWeight.Bold,

                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = direccion,

                        color = Tan,

                        fontSize = 13.sp,

                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4CAF50))
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            text = "Veterinaria cercana",

                            color = BrownLight,

                            fontSize = 11.sp,

                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }
}