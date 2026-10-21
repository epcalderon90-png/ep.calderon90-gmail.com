package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileVerificationScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val user by viewModel.currentUser.collectAsState()
    val reviews by viewModel.myReviews.collectAsState()

    val scrollState = rememberScrollState()

    // Inputs for Verification Drawer
    var dniNum by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var socialUser by remember { mutableStateOf("") }
    var homeHood by remember { mutableStateOf(user?.neighborhood ?: "Urbarí") }

    val neighborhoods = listOf(
        "Equipetrol", "Urbarí", "Plan 3000", "Villa Primero de Mayo", "La Morita", "Centro"
    )

    var premiumModalOpen by remember { mutableStateOf(false) }

    // Mock payment fields
    var cardNumber by remember { mutableStateOf("") }
    var cardName by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf("") }
    var cardCvc by remember { mutableStateOf("") }
    var paymentProcessing by remember { mutableStateOf(false) }
    var paymentSuccessDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil 👥", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SczGreenPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Card Header
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Big avatar
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(SczGreenSecondary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (user?.isPremium == true) "👑" else "👤",
                                fontSize = 32.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = user?.name ?: "Vecino Cruceño",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                if (user?.isVerified == true) {
                                    Icon(
                                        Icons.Default.Verified,
                                        contentDescription = "Verificado",
                                        tint = SczGreenSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = "📍 Barrio: ${user?.neighborhood ?: "Ninguno"}",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                 Icon(Icons.Default.Star, contentDescription = null, tint = TiluchisGoldAccent, modifier = Modifier.size(14.dp))
                                Text(
                                    text = " ${user?.reputation ?: 5.0f} (${user?.reviewsCount ?: 0} reseñas)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = SoftGrayBorder)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Yucas metrics
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Billetera Virtual 🦉", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SczGreenSecondary)
                            Text("${user?.tiluchis ?: 0} Tiluchis Cambas", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = SczGreenPrimary)
                        }

                        // Premium vs Free indicators
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Suscripción de Enlace:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            if (user?.isPremium == true) {
                                Text(
                                    text = "👑 Pásalo Premium Active",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SczGreenSecondary
                                )
                            } else {
                                Text(
                                    text = "Plan Gratuito (${5 - (user?.interactionsCount ?: 0)} interacciones/mes restantes)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = CoralRed
                                )
                            }
                        }
                    }
                }
            }

            // Subscription Premium Banner Option
            if (user?.isPremium == false) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SczGreenPrimary.copy(alpha = 0.08f)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, SczGreenPrimary.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("👑", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Gana Acceso Premium SCZ",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = SczGreenPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Por solo 35 Bs al mes, podrás interactuar sin límites fuera de tu barrio actual (${user?.neighborhood}). Ideal para coordinar regalos con toda Santa Cruz (Plan 3000, Norte, Equipetrol, etc.). Límite gratuito: 5 consultas interbarrio.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { premiumModalOpen = true },
                            colors = ButtonDefaults.buttonColors(containerColor = SczGreenPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("premium_modal_trigger")
                        ) {
                            Text("Subscribirme ahora (35 Bs/mes)")
                        }
                    }
                }
            }

            // Trust Verification Panel (Identificación, Ubicación, Teléfono, Redes)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🔒 Verificación de Confianza Comunitaria",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = SczGreenPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Para regalar o recibir objetos, debes validar tu identidad. ¡Ganarás un obsequio extra de +30 Tiluchis!",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Checklist Statuses
                    VerificationCheckItem("Identificación Oficial (DNI / C.I.)", user?.verifiedId == true)
                    VerificationCheckItem("Ubicación por Coordenadas GPS", user?.verifiedLocation == true)
                    VerificationCheckItem("Teléfono Celular Boliviano (+591)", user?.verifiedPhone == true)
                    VerificationCheckItem("Validación por Redes Sociales", user?.verifiedSocial == true)

                    Spacer(modifier = Modifier.height(16.dp))

                    if (user?.isVerified == false) {
                        // Form inputs to perform verification
                        Text(
                            text = "Completa tu Registro de Confianza:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = dniNum,
                            onValueChange = { dniNum = it },
                            label = { Text("Cédula de Identidad (C.I.)") },
                            placeholder = { Text("Ej: 9876543 SC (Camba)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("verify_ci_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            label = { Text("Teléfono / WhatsApp (+591)") },
                            placeholder = { Text("Ej: 78012345") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("verify_phone_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = socialUser,
                            onValueChange = { socialUser = it },
                            label = { Text("Usuario Red Social (Facebook / Instagram)") },
                            placeholder = { Text("Ej: @ronald_sc") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("verify_social_input")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Home Neighborhood Selector
                        var expandedHoodMenu by remember { mutableStateOf(false) }
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedButton(
                                onClick = { expandedHoodMenu = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("verify_neighborhood_picker")
                            ) {
                                Text("Barrio Camba: $homeHood")
                            }
                            DropdownMenu(
                                expanded = expandedHoodMenu,
                                onDismissRequest = { expandedHoodMenu = false }
                            ) {
                                neighborhoods.forEach { hood ->
                                    DropdownMenuItem(
                                        text = { Text(hood) },
                                        onClick = {
                                            homeHood = hood
                                            expandedHoodMenu = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                if (dniNum.isBlank() || phoneNumber.isBlank() || socialUser.isBlank()) {
                                    return@Button
                                }
                                viewModel.verifyIdentity(
                                    phone = phoneNumber,
                                    ciNumber = dniNum,
                                    neighborhood = homeHood,
                                    socialHandle = socialUser
                                )
                            },
                            enabled = dniNum.isNotBlank() && phoneNumber.isNotBlank() && socialUser.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = SczGreenPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("confirm_verification_btn")
                        ) {
                            Text("Confirmar Verificación (+30 Tiluchis Bonus 🦉)")
                        }
                    } else {
                        // Already verified feedback banner
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SczGreenSecondary.copy(alpha = 0.1f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🎉", fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "¡Perfil Verificado de Confianza!",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SczGreenPrimary
                                    )
                                    Text(
                                        text = "Tu identidad está validada. Vecinos pueden intercambiar contigo con plena seguridad.",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Community Feedback Section
            if (reviews.isNotEmpty()) {
                Text(
                    text = "últimas Valoraciones de Vecinos:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 8.dp)
                )

                reviews.forEach { review ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(review.reviewerName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Row {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = TiluchisGoldAccent, modifier = Modifier.size(14.dp))
                                    Text(" ${review.rating.toInt()}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(review.comment, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }

    // PREMIUM RECURRING MOCK PAYMENT MODAL
    if (premiumModalOpen) {
        AlertDialog(
            onDismissRequest = { premiumModalOpen = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("👑 Pasarela Segura Pásalo Premium", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Suscripción Mensual Recurrente de Enlace Inter-barrios en Santa Cruz.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Divider()

                    Text("Detalle de Pago: 35 Bs./mes (Cancelable en cualquier momento)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = SczGreenPrimary)

                    OutlinedTextField(
                        value = cardNumber,
                        onValueChange = { cardNumber = it },
                        label = { Text("Número de Tarjeta (Visa/Mastercard)") },
                        placeholder = { Text("4242 4242 4242 4242") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = cardExpiry,
                            onValueChange = { cardExpiry = it },
                            label = { Text("Vencimiento") },
                            placeholder = { Text("12/29") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = cardCvc,
                            onValueChange = { cardCvc = it },
                            label = { Text("CVC") },
                            placeholder = { Text("123") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = cardName,
                        onValueChange = { cardName = it },
                        label = { Text("Nombre del Tarjetahabiente") },
                        placeholder = { Text("Ej: Ronald Justiniano") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (paymentProcessing) {
                        CircularProgressIndicator(
                            color = SczGreenPrimary,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (cardNumber.isBlank() || cardName.isBlank() || cardExpiry.isBlank() || cardCvc.isBlank()) {
                            return@Button
                        }
                        paymentProcessing = true
                        // Simulate delay
                        paymentProcessing = false
                        premiumModalOpen = false
                        viewModel.subscribePremium()
                        paymentSuccessDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SczGreenPrimary),
                    enabled = cardNumber.isNotBlank() && cardName.isNotBlank() && cardExpiry.isNotBlank() && cardCvc.isNotBlank() && !paymentProcessing,
                    modifier = Modifier.testTag("submit_premium_payment")
                ) {
                    Text("Pagar 35 Bs 💳")
                }
            },
            dismissButton = {
                TextButton(onClick = { premiumModalOpen = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Payment Success Alert
    if (paymentSuccessDialog) {
        AlertDialog(
            onDismissRequest = { paymentSuccessDialog = false },
            title = { Text("👑 ¡Bienvenido a Pásalo Premium!") },
            text = { Text("Tu pago de 35 Bs. ha sido procesado de forma segura. Disfruta de interacciones ilimitadas entre todos los barrios de Santa Cruz.") },
            confirmButton = {
                Button(
                    onClick = { paymentSuccessDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = SczGreenPrimary)
                ) {
                    Text("¡A Pásalo SCZ! 🦉")
                }
            }
        )
    }
}

@Composable
fun VerificationCheckItem(
    label: String,
    isPassed: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isPassed) Icons.Default.CheckCircle else Icons.Default.Cancel,
            contentDescription = null,
            tint = if (isPassed) SczGreenSecondary else CoralRed,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            color = if (isPassed) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
