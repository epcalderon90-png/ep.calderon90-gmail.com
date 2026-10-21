package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ItemListingEntity
import com.example.data.NeighborhoodEntity
import com.example.data.UserEntity
import com.example.ui.theme.CoralRed
import com.example.ui.theme.SczGreenPrimary
import com.example.ui.theme.SczGreenSecondary
import com.example.ui.theme.TiluchisGoldAccent
import com.example.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val pendingListings by viewModel.adminPendingListings.collectAsState()
    val allNeighborhoods by viewModel.allNeighborhoods.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0 = Publicaciones, 1 = Barrios, 2 = Perfiles
    var newNeighborhoodName by remember { mutableStateOf("") }
    var neighborhoodError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Moderación Administrativa 🛡️", fontWeight = FontWeight.Bold, color = Color.White) },
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
        ) {
            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = SczGreenPrimary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PostAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Avisos (${pendingListings.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }}
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Barrios", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }}
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Perfiles", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }}
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                when (selectedTab) {
                    0 -> {
                        // Publicaciones Tab
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Shield,
                                contentDescription = null,
                                tint = SczGreenPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Aprobación de Donaciones e Intercambios",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Controla la confianza aprobando donaciones y descartando avisos sospechosos.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Divider()
                        Spacer(modifier = Modifier.height(12.dp))

                        if (pendingListings.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = SczGreenSecondary, modifier = Modifier.size(64.dp))
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text("¡Al día! Todo verificado", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("No hay objetos pendientes de moderación.", fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .weight(1f)
                            ) {
                                items(pendingListings) { item ->
                                    AdminApprovalCard(
                                        item = item,
                                        onApprove = { viewModel.moderateApproveListing(item.id) },
                                        onReject = { viewModel.moderateRejectListing(item.id) }
                                    )
                                }
                            }
                        }
                    }
                    1 -> {
                        // Barrios Tab
                        Text("Registrar Nuevos Barrios de Santa Cruz 🗺️", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SczGreenPrimary)
                        Text("Agrega nuevos barrios para que los vecinos puedan categorizar e intercambiar objetos localmente.", fontSize = 11.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(12.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
                            border = BorderStroke(1.dp, Color(0xFFEEEEEE)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Agregar Nuevo Barrio", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = newNeighborhoodName,
                                        onValueChange = {
                                            newNeighborhoodName = it
                                            neighborhoodError = null
                                        },
                                        placeholder = { Text("Ej. Equipetrol, Las Palmas, Norte", fontSize = 12.sp) },
                                        isError = neighborhoodError != null,
                                        singleLine = true,
                                        modifier = Modifier.weight(1f).testTag("neighborhood_input_field"),
                                        textStyle = MaterialTheme.typography.bodyMedium
                                    )
                                    Button(
                                        onClick = {
                                            if (newNeighborhoodName.isBlank()) {
                                                neighborhoodError = "El nombre no puede estar vacío."
                                            } else if (allNeighborhoods.any { it.name.equals(newNeighborhoodName.trim(), ignoreCase = true) }) {
                                                neighborhoodError = "Este barrio ya está registrado."
                                            } else {
                                                viewModel.addNeighborhood(newNeighborhoodName.trim())
                                                newNeighborhoodName = ""
                                                neighborhoodError = null
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = SczGreenPrimary),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.testTag("add_neighborhood_btn")
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Agregar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                if (neighborhoodError != null) {
                                    Text(
                                        text = neighborhoodError ?: "",
                                        color = CoralRed,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }

                        Divider()
                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Barrios Activos (${allNeighborhoods.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        if (allNeighborhoods.isEmpty()) {
                            Text("No hay barrios registrados.", fontSize = 12.sp, color = Color.Gray)
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(allNeighborhoods) { hood ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = Color.White),
                                        border = BorderStroke(1.dp, Color(0xFFEEEEEE)),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = SczGreenPrimary, modifier = Modifier.size(18.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(hood.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            }
                                            IconButton(
                                                onClick = { viewModel.removeNeighborhood(hood.name) },
                                                modifier = Modifier.testTag("delete_hood_${hood.name}")
                                            ) {
                                                Icon(Icons.Default.Delete, contentDescription = "Eliminar Barrio", tint = CoralRed, modifier = Modifier.size(20.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    2 -> {
                        // Perfiles Tab (Eliminar perfiles falsos)
                        Text("Control de Perfiles Falsos 🚨", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = CoralRed)
                        Text("Detecta y elimina perfiles que infringan las normas o simulen ser usuarios legítimos.", fontSize = 11.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(12.dp))

                        Divider()
                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Lista de Vecinos Registrados (${allUsers.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(allUsers) { user ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    border = BorderStroke(1.dp, if (user.id == viewModel.currentUserId) SczGreenPrimary else Color(0xFFEEEEEE)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(user.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                    if (user.id == viewModel.currentUserId) {
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Surface(
                                                            shape = RoundedCornerShape(4.dp),
                                                            color = SczGreenPrimary.copy(alpha = 0.1f)
                                                        ) {
                                                            Text("Tú", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SczGreenPrimary, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                                        }
                                                    }
                                                    if (user.isVerified) {
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Surface(
                                                            shape = RoundedCornerShape(4.dp),
                                                            color = Color(0xFFE8F5E9)
                                                        ) {
                                                            Text("Verificado ✔️", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32), modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                                        }
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text("Email: ${user.email} • Tel: ${user.phone}", fontSize = 11.sp, color = Color.Gray)
                                                Text("Barrio: ${user.neighborhood} • Reputación: ⭐ ${user.reputation} (${user.reviewsCount} reviews)", fontSize = 11.sp, color = Color.Gray)
                                                Text("Saldo: 🦉 ${user.tiluchis} Tiluchis", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = SczGreenPrimary)
                                            }

                                            // Deletion control for fake users (except current moderator user for safety)
                                            if (user.id != viewModel.currentUserId) {
                                                Button(
                                                    onClick = { viewModel.deleteUserProfile(user.id) },
                                                    colors = ButtonDefaults.buttonColors(containerColor = CoralRed.copy(alpha = 0.1f), contentColor = CoralRed),
                                                    border = BorderStroke(1.dp, CoralRed),
                                                    shape = RoundedCornerShape(8.dp),
                                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                                    modifier = Modifier.testTag("delete_user_btn_${user.id}")
                                                ) {
                                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar Vecino Falso", modifier = Modifier.size(14.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Eliminar Falso", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminApprovalCard(
    item: ItemListingEntity,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_card_${item.id}"),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when(item.category) {
                        "URGENT" -> CoralRed.copy(alpha = 0.12f)
                        "FREE" -> SczGreenSecondary.copy(alpha = 0.12f)
                        else -> TiluchisGoldAccent.copy(alpha = 0.12f)
                    }
                ) {
                    Text(
                        text = when(item.category) {
                            "URGENT" -> "⚠️ Donación Urgente"
                            "FREE" -> "🎁 Regalo"
                            else -> "🏷️ Baratísimo"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when(item.category) {
                            "URGENT" -> CoralRed
                            "FREE" -> SczGreenPrimary
                            else -> SczGreenSecondary
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🦉", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${item.tiluchisCost} Tiluchis",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (item.category == "CHEAP") {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Bs. ${item.priceInBs}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SczGreenPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = item.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = item.description,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            // Render 2 photos for admin review if available
            if (item.imageUrl.contains(",")) {
                val photos = item.imageUrl.split(",")
                if (photos.size >= 2 && photos[0].isNotBlank() && photos[1].isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "📸 FOTOS COMPROBADAS (2 REQUERIDAS):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SczGreenPrimary
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        photos.take(2).forEachIndexed { index, photoString ->
                            val emoji = photoString.split(" ").firstOrNull() ?: "📸"
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(60.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
                                border = BorderStroke(1.dp, Color(0xFFEEEEEE))
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(emoji, fontSize = 22.sp)
                                        Text(
                                            text = if (index == 0) "Ángulo 1" else "Ángulo 2",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Metadata footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Barrio: ${item.neighborhood}", fontSize = 11.sp, color = SczGreenSecondary, fontWeight = FontWeight.SemiBold)
                Text("Ofertante: ${item.donorName}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Moderation Actions Layout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Reject Button
                Button(
                    onClick = onReject,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CoralRed,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .testTag("admin_btn_reject_${item.id}")
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Descartar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                // Approve Button
                Button(
                    onClick = onApprove,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SczGreenPrimary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .testTag("admin_btn_approve_${item.id}")
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Aprobar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
