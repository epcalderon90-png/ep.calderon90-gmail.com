package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SczGreenPrimary
import com.example.ui.theme.SczGreenSecondary
import com.example.ui.theme.TiluchisGoldAccent
import com.example.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddListingScreen(
    viewModel: AppViewModel,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val user by viewModel.currentUser.collectAsState()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("FREE") } // URGENT, FREE, CHEAP
    var priceInBs by remember { mutableStateOf("") }
    var tiluchisCost by remember { mutableStateOf(1) } // Default 1 Tiluchi, slider for 1, 2, 3 Tiluchis
    var selectedNeighborhood by remember { mutableStateOf(user?.neighborhood ?: "Urbarí") }
    var photo1 by remember { mutableStateOf<String?>(null) }
    var photo2 by remember { mutableStateOf<String?>(null) }
    var showPhotoPickerForBox by remember { mutableStateOf<Int?>(null) }

    val dbNeighborhoods by viewModel.allNeighborhoods.collectAsState()
    val neighborhoods = if (dbNeighborhoods.isEmpty()) {
        listOf("Equipetrol", "Urbarí", "Plan 3000", "Villa Primero de Mayo", "La Morita", "Centro")
    } else {
        dbNeighborhoods.map { it.name }
    }

    var showError by remember { mutableStateOf<String?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Publicar Objeto 🦉", fontWeight = FontWeight.Bold, color = Color.White) },
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
            Text(
                text = "¡Comparte e Incentiva la Solidaridad!",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = SczGreenPrimary
            )
            
            Text(
                text = "Cualquier objeto que publiques pasará por nuestro panel administrativo de moderación local antes de ser visible para los vecinos de Santa Cruz.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Divider()

            // Title
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título del Objeto") },
                placeholder = { Text("Ej: Mesa metálica, Zapatos de colegio...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_title"),
                shape = RoundedCornerShape(12.dp)
            )

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción (Estado, uso, detalles)") },
                placeholder = { Text("Ej: Está seminuevo. Ideal para la cocina, tiene un pequeño rayón...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .testTag("input_description"),
                shape = RoundedCornerShape(12.dp)
            )

            // Required Photo Upload Section
            Divider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Fotos del Objeto (2 Obligatorias) 📸",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = SczGreenPrimary
                )
                Text(
                    text = if (photo1 != null && photo2 != null) "✓ Listas para subir" else "Requerido",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (photo1 != null && photo2 != null) SczGreenPrimary else Color.Red
                )
            }

            Text(
                text = "Sube exactamente 2 fotos consecutivas mostrando diferentes ángulos o el estado real del objeto para garantizar la transparencia antes de publicar.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Photo Slot 1
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (photo1 != null) SczGreenPrimary.copy(alpha = 0.05f) else Color(0xFFF5F5F5))
                        .clickable { showPhotoPickerForBox = 1 }
                        .border(
                            border = BorderStroke(1.dp, if (photo1 != null) SczGreenPrimary else Color(0xFFCCCCCC)),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (photo1 != null) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text(text = photo1!!.split(" ")[0], fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = photo1!!.split(" ").drop(1).joinToString(" "),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = SczGreenPrimary
                            )
                        }
                        // Click to delete top right
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(6.dp)
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Color.Red.copy(alpha = 0.1f))
                                .clickable { photo1 = null },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("✕", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📸", fontSize = 24.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Foto Angular 1", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
                        }
                    }
                }

                // Photo Slot 2
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (photo2 != null) SczGreenPrimary.copy(alpha = 0.05f) else Color(0xFFF5F5F5))
                        .clickable { showPhotoPickerForBox = 2 }
                        .border(
                            border = BorderStroke(1.dp, if (photo2 != null) SczGreenPrimary else Color(0xFFCCCCCC)),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (photo2 != null) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text(text = photo2!!.split(" ")[0], fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = photo2!!.split(" ").drop(1).joinToString(" "),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = SczGreenPrimary
                            )
                        }
                        // Click to delete top right
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(6.dp)
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Color.Red.copy(alpha = 0.1f))
                                .clickable { photo2 = null },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("✕", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📸", fontSize = 24.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Foto Detallada 2", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
                        }
                    }
                }
            }

            Divider()

            // Category selector
            Text(
                text = "Seleccionar Categoría:",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "URGENT" to "⚠️ Urgencia",
                    "FREE" to "🎁 Regalo",
                    "CHEAP" to "🏷️ Baratísimo"
                ).forEach { (catType, label) ->
                    val isSelected = category == catType
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                category = catType
                                if (catType == "URGENT") tiluchisCost = 0 // urgent is free
                            }
                            .testTag("cat_button_$catType"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(
                            width = 2.dp,
                            color = if (isSelected) SczGreenPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        ),
                        color = if (isSelected) SczGreenPrimary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) SczGreenPrimary else MaterialTheme.colorScheme.onSurface,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // Price in Bs for Cheap Categories
            if (category == "CHEAP") {
                OutlinedTextField(
                    value = priceInBs,
                    onValueChange = { priceInBs = it },
                    label = { Text("Precio Económico en Bolivianos (Bs.)") },
                    placeholder = { Text("Ej: 15, 30...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_price"),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Tiluchis Slider mechanism
            if (category != "URGENT") {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SczGreenSecondary.copy(alpha = 0.05f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Valor en Tiluchis 🦉:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "$tiluchisCost Tiluchis 🦉",
                                fontWeight = FontWeight.ExtraBold,
                                color = SczGreenPrimary,
                                fontSize = 14.sp
                            )
                        }
                        Text(
                            text = "Porcentaje de recompensa en Tiluchis que el vecino te transferirá por quedarse tu objeto.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Slider(
                            value = tiluchisCost.toFloat(),
                            onValueChange = { tiluchisCost = it.toInt() },
                            valueRange = 1f..3f,
                            steps = 1
                        )
                    }
                }
            } else {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🦉 Las donaciones urgentes no requieren costo de Tiluchis (0 Tiluchis) para facilitar un rápido retiro solidario.",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            // Neighborhood Selection
            Text(
                text = "📍 Barrio Camba de Ubicación del Objeto:",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    var expanded by remember { mutableStateOf(false) }
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("select_neighborhood_btn")
                    ) {
                        Text("Barrio actual: $selectedNeighborhood")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        neighborhoods.forEach { hood ->
                            DropdownMenuItem(
                                text = { Text(hood) },
                                onClick = {
                                    selectedNeighborhood = hood
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Post Button
            Button(
                onClick = {
                    if (title.isBlank() || description.isBlank()) {
                        showError = "Por favor ingresa un título y una descripción para tu objeto."
                        return@Button
                    }
                    if (photo1 == null || photo2 == null) {
                        showError = "¡Debes subir exactamente 2 fotos del objeto! Esto nos ayuda a comprobar el estado real de lo que estás donando/ofreciendo antes de publicar. 📸"
                        return@Button
                    }
                    if (category == "CHEAP" && priceInBs.toDoubleOrNull() == null) {
                        showError = "Por favor ingresa un precio válido en Bolivianos."
                        return@Button
                    }
                    
                    viewModel.donateItem(
                        title = title,
                        description = description,
                        category = category,
                        priceInBs = priceInBs.toDoubleOrNull() ?: 0.0,
                        tiluchisCost = tiluchisCost,
                        neighborhood = selectedNeighborhood,
                        imageUrl = "${photo1 ?: ""},${photo2 ?: ""}"
                    )
                    showSuccessDialog = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_posting_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SczGreenPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Publicar Objeto 🦉", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    // Success Dialog on post
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("🦉 ¡Publicado con éxito!") },
            text = { Text("Tu objeto ha sido enviado a revisión. Un moderador de 'Pásalo SCZ' lo aprobará en los próximos minutos para asegurar la confianza de la comunidad.") },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onSuccess()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SczGreenPrimary
                    )
                ) {
                    Text("Continuar")
                }
            }
        )
    }

    // Error Dialog
    showError?.let { err ->
        AlertDialog(
            onDismissRequest = { showError = null },
            title = { Text("⚠️ Campo Requerido") },
            text = { Text(err) },
            confirmButton = {
                Button(onClick = { showError = null }) {
                    Text("Aceptar")
                }
            }
        )
    }

    // Foto/Simulacion Dialog Selector
    showPhotoPickerForBox?.let { boxIndex ->
        val presetPhotos = listOf(
            "🛋️ Mueble / Sofá" to "Un sofá ideal para la sala",
            "👟 Zapatos / Calzado" to "Zapatos cómodos semi-nuevos",
            "📚 Libros / Lectura" to "Enciclopedia y libros escolares",
            "🍽️ Vajilla / Vajilla" to "Platos y vajilla para hogar",
            "🖥️ Tecnología / Monitor" to "Pantalla monitor funcionando",
            "🚲 Bicicleta / Transporte" to "Bicicleta ideal para paseo",
            "🧸 Juguete / Peluche" to "Peluche de felpa para niños",
            "👕 Ropa / Vestimenta" to "Polera o chaqueta lavada"
        )

        AlertDialog(
            onDismissRequest = { showPhotoPickerForBox = null },
            title = { Text("Elegir Foto ${boxIndex} 📸", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Selecciona una de las siguientes fotos simuladas de tu galería para subir de forma segura:", fontSize = 12.sp)
                    presetPhotos.forEach { (itemLabel, description) ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (boxIndex == 1) {
                                        photo1 = itemLabel
                                    } else {
                                        photo2 = itemLabel
                                    }
                                    showPhotoPickerForBox = null
                                },
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFFDDDDDD)),
                            color = Color.White
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(itemLabel.split(" ")[0], fontSize = 24.sp)
                                Column {
                                    Text(itemLabel.split(" ").drop(1).joinToString(" "), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text(description, fontSize = 10.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showPhotoPickerForBox = null }) {
                    Text("Cerrar")
                }
            }
        )
    }
}
