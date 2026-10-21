package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.data.ClaimResult
import com.example.data.ItemListingEntity
import com.example.ui.theme.*
import com.example.viewmodel.AppViewModel

@Composable
fun BentoDashboard(
    user: com.example.data.UserEntity?,
    urgentCount: Int,
    onUrgentClick: () -> Unit,
    onTiluchisClick: () -> Unit,
    onVerifyClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Block A: Urgent Donations Banner (Full width card)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(84.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(BentoUrgentBg)
                .clickable(onClick = onUrgentClick)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = BentoUrgentBadge.copy(alpha = 0.2f),
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.CenterEnd)
                    .offset(x = 20.dp, y = 15.dp)
            )

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Donaciones Urgentes",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = BentoUrgentText
                    )
                    Text(
                        text = "Vecinos necesitan apoyo hoy",
                        fontSize = 12.sp,
                        color = BentoUrgentText.copy(alpha = 0.75f)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = BentoUrgentBadge,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = if (urgentCount > 0) "$urgentCount ACTIVAS" else "0 ACTIVAS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoUrgentText,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Row of 2 boxes: Mis Tiluchis & Verificación de Barrio
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(132.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Block B: Mis Tiluchis
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, BentoBorder),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(BentoPurpleNeutral),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🦉", fontSize = 10.sp)
                            }
                            Text(
                                text = "Mis Tiluchis",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoDarkText.copy(alpha = 0.7f)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${user?.tiluchis ?: 0}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = BentoPurple
                        )
                    }

                    Button(
                        onClick = onTiluchisClick,
                        colors = ButtonDefaults.buttonColors(containerColor = BentoPurple),
                        shape = RoundedCornerShape(100.dp),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(26.dp)
                    ) {
                        Text("CARGAR", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            // Block C: Verificación de Barrio
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(24.dp))
                    .background(BentoIdentityBg)
                    .clickable(onClick = onVerifyClick)
                    .padding(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = BentoIdentityText.copy(alpha = 0.08f),
                    modifier = Modifier
                        .size(80.dp)
                        .align(Alignment.BottomEnd)
                        .offset(x = 10.dp, y = 10.dp)
                )

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = BentoIdentityText,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "CONFIDENCIAL",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoIdentityText
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Barrio Seguro",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoIdentityText
                        )
                    }

                    Text(
                        text = if (user?.isVerified == true) "✓ Verificado" else "98% Confiable",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoIdentityText.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Block D: Reputation block
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = BentoPurpleSoft),
            border = BorderStroke(1.dp, BentoGrayDivider)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = BentoPurple,
                    modifier = Modifier.size(16.dp)
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Reputación Vecinal",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoDarkText
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        for (i in 1..4) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(BentoPurple)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(100.dp))
                                .background(BentoPurple.copy(alpha = 0.15f))
                        )
                    }
                }

                Text(
                    text = "4.5",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoDarkText
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: AppViewModel,
    onNavigateToChat: (String) -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToPremium: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listings by viewModel.filteredListings.collectAsState()
    val activeCategory by viewModel.selectedCategory.collectAsState()
    val activeNeighborhood by viewModel.selectedNeighborhood.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val user by viewModel.currentUser.collectAsState()

    var selectedItemForDetail by remember { mutableStateOf<ItemListingEntity?>(null) }
    var claimMessage by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var requirePremiumMessage by remember { mutableStateOf<String?>(null) }

    val rawListings by viewModel.allListings.collectAsState()
    val urgentCount = remember(rawListings) { rawListings.count { it.category == "URGENT" && it.moderationStatus == "APPROVED" } }

    val dbNeighborhoods by viewModel.allNeighborhoods.collectAsState()
    val neighborhoods = remember(dbNeighborhoods) {
        listOf("TODOS") + dbNeighborhoods.map { it.name }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BentoBackground)
                    .padding(horizontal = 16.dp, vertical = 2.dp)
            ) {
                Spacer(modifier = Modifier.height(30.dp)) // Safe edge padding
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = com.example.R.drawable.ic_pasalo_logo),
                                contentDescription = "Pásalo SCZ Logo",
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Pásalo SCZ",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = BentoPurple
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color(0xFF49454F),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${user?.neighborhood ?: "Equipetrol"}, Santa Cruz".uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF49454F),
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    // Avatar button
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(BentoPurpleNeutral)
                            .border(BorderStroke(2.dp, Color.White), CircleShape)
                            .clickable { onNavigateToProfile() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Perfil",
                            tint = Color(0xFF21005D),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Search field (Bento style)
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Buscar cocina, estante, ropa...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                    trailingIcon = if (searchQuery.isNotEmpty()) {
                        {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                            }
                        }
                    } else null,
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = BentoPurple,
                        unfocusedBorderColor = BentoBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("search_bar")
                )
                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BentoBackground)
        ) {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Feature Bento Grid
                item {
                    BentoDashboard(
                        user = user,
                        urgentCount = urgentCount,
                        onUrgentClick = { viewModel.setCategoryFilter("URGENT") },
                        onTiluchisClick = {
                            errorMessage = "¡Recarga tus Tiluchis coordinando hermosas donaciones con tus vecinos o suscríbete a Pásalo Premium! 🦉"
                        },
                        onVerifyClick = { onNavigateToProfile() }
                    )
                }

                // Neighborhood Filter Horizontal Row label
                item {
                    Text(
                        text = "📍 FILTRAR POR BARRIO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoPurple,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 2.dp)
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(neighborhoods) { hood ->
                            val isSelected = activeNeighborhood == hood
                            Surface(
                                onClick = { viewModel.setNeighborhoodFilter(hood) },
                                shape = RoundedCornerShape(100.dp),
                                color = if (isSelected) BentoPurple else Color.White,
                                border = if (isSelected) null else BorderStroke(1.dp, BentoBorder),
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Text(
                                    text = hood,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else BentoDarkText,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }

                // Category Filter Horizontal Row label
                val categories = listOf(
                    "ALL" to "📁 Todos",
                    "URGENT" to "⚠️ Urgentes",
                    "FREE" to "🎁 Regalos",
                    "CHEAP" to "🏷️ Baratísimos"
                )

                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        items(categories) { (cat, label) ->
                            val isSelected = activeCategory == cat
                            Surface(
                                onClick = { viewModel.setCategoryFilter(cat) },
                                shape = RoundedCornerShape(100.dp),
                                color = if (isSelected) {
                                    if (cat == "URGENT") CoralRed else BentoPurple
                                } else Color.White,
                                border = if (isSelected) null else BorderStroke(1.dp, BentoBorder),
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else BentoDarkText,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }

                // Grid Divider line spacer
                item {
                    Divider(color = BentoGrayDivider, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp))
                }

                // Main List Feed
                if (listings.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Inbox,
                                    contentDescription = "No hay objetos",
                                    tint = BentoPurple.copy(alpha = 0.4f),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No hay ofertas por el momento",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BentoDarkText
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Prueba cambiando los filtros de barrio o categoría en Santa Cruz.",
                                    fontSize = 12.sp,
                                    color = Color(0xFF49454F)
                                )
                            }
                        }
                    }
                } else {
                    items(listings) { item ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                            ListingCardItem(
                                item = item,
                                onClick = { selectedItemForDetail = item }
                            )
                        }
                    }
                }
            }
        }
    }


    // Detail Dialog / BottomSheet details
    selectedItemForDetail?.let { item ->
        AlertDialog(
            onDismissRequest = { selectedItemForDetail = null },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = when(item.category) {
                            "URGENT" -> Icons.Default.PriorityHigh
                            "FREE" -> Icons.Default.CardGiftcard
                            else -> Icons.Default.MonetizationOn
                        },
                        contentDescription = null,
                        tint = when(item.category) {
                            "URGENT" -> CoralRed
                            "FREE" -> SczGreenSecondary
                            else -> TiluchisGoldAccent
                        }
                    )
                    Text(
                        text = item.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    // Accent Indicator Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                when (item.category) {
                                    "URGENT" -> CoralRed.copy(alpha = 0.1f)
                                    "FREE" -> SczGreenSecondary.copy(alpha = 0.1f)
                                    else -> TiluchisGoldAccent.copy(alpha = 0.1f)
                                }
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when(item.category) {
                                "URGENT" -> "⚠️ DONACIÓN URGENTE (0 Tiluchis)"
                                "FREE" -> "🎁 REGALO COMÚN (${item.tiluchisCost} Tiluchis)"
                                else -> "🏷️ BARATÍSIMO (${item.tiluchisCost} Tiluchis + Bs. ${item.priceInBs})"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = when(item.category) {
                                "URGENT" -> CoralRed
                                "FREE" -> SczGreenPrimary
                                else -> SczGreenSecondary
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = item.description,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Render 2 photos if available in item.imageUrl
                    if (item.imageUrl.contains(",")) {
                        val photos = item.imageUrl.split(",")
                        if (photos.size >= 2 && photos[0].isNotBlank() && photos[1].isNotBlank()) {
                            Text(
                                text = "📸 FOTOS DEL OBJETO:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoPurple,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                photos.take(2).forEachIndexed { index, photoString ->
                                    val emoji = photoString.split(" ").firstOrNull() ?: "📸"
                                    Card(
                                        modifier = Modifier.weight(1f).height(80.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
                                        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
                                    ) {
                                        Column(
                                            modifier = Modifier.fillMaxSize(),
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(emoji, fontSize = 28.sp)
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = if (index == 0) "Ángulo 1 (Frente)" else "Ángulo 2 (Detalle)",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Divider()

                    Spacer(modifier = Modifier.height(8.dp))

                    // Location / Barrio
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = SczGreenSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Barrio: ${item.neighborhood}",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }

                    // Donor
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = SczGreenPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Ofertante: ${item.donorName}",
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = TiluchisGoldAccent,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = " ${item.donorReputation}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Neighborhood Warning if different
                    if (user?.neighborhood != item.neighborhood) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Este objeto está fuera de tu barrio registrado (${user?.neighborhood ?: "Ninguno"}). Intercambiar requiere verificación y consume una de tus 5 interacciones mensuales fuera de barrio si no eres Premium.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val currentItem = item
                        selectedItemForDetail = null
                        viewModel.claimListingItem(currentItem) { result ->
                            when (result) {
                                is ClaimResult.Success -> {
                                    claimMessage = "¡Objeto reservado con éxito! Se ha abierto un chat interno con ${currentItem.donorName} para coordinar la entrega."
                                    onNavigateToChat(result.chatId)
                                }
                                is ClaimResult.Error -> {
                                    errorMessage = result.message
                                }
                                is ClaimResult.RequirePremium -> {
                                    requirePremiumMessage = result.message
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SczGreenPrimary
                    ),
                    modifier = Modifier.testTag("claim_button")
                ) {
                    Text("¡Quiero Pásalo SCZ! 🦉")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedItemForDetail = null }) {
                    Text("Cerrar")
                }
            }
        )
    }

    // Claim Success Dialog
    claimMessage?.let { msg ->
        AlertDialog(
            onDismissRequest = { claimMessage = null },
            title = { Text("🦉 ¡Reservado!") },
            text = { Text(msg) },
            confirmButton = {
                Button(onClick = { claimMessage = null }) {
                    Text("Aceptar")
                }
            }
        )
    }

    // General Error Dialog
    errorMessage?.let { msg ->
        AlertDialog(
            onDismissRequest = { errorMessage = null },
            title = { Text("⚠️ Acción Requerida") },
            text = { Text(msg) },
            confirmButton = {
                Button(
                    onClick = {
                        errorMessage = null
                        onNavigateToProfile()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SczGreenPrimary
                    )
                ) {
                    Text("Ir a Mi Perfil")
                }
            },
            dismissButton = {
                TextButton(onClick = { errorMessage = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Require Premium Dialog
    requirePremiumMessage?.let { msg ->
        AlertDialog(
            onDismissRequest = { requirePremiumMessage = null },
            title = { Text("👑 Pásalo Premium Requerido") },
            text = { Text(msg) },
            confirmButton = {
                Button(
                    onClick = {
                        requirePremiumMessage = null
                        onNavigateToPremium()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SczGreenPrimary
                    )
                ) {
                    Text("Obtener Premium (35 Bs)")
                }
            },
            dismissButton = {
                TextButton(onClick = { requirePremiumMessage = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun ListingCardItem(
    item: ItemListingEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(116.dp)
            .clickable(onClick = onClick)
            .testTag("listing_card_${item.id}"),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, BentoBorder),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Flat bento-elegant style
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Left representation block with gray bg
            Box(
                modifier = Modifier
                    .width(96.dp)
                    .fillMaxHeight()
                    .background(Color(0xFFF3F3F3)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when(item.category) {
                        "URGENT" -> Icons.Default.Favorite
                        "FREE" -> Icons.Default.CardGiftcard
                        else -> Icons.Default.MonetizationOn
                    },
                    contentDescription = null,
                    tint = when(item.category) {
                        "URGENT" -> CoralRed
                        "FREE" -> BentoPurple
                        else -> Color(0xFF137333)
                    },
                    modifier = Modifier.size(36.dp)
                )
            }

            // Right content details block
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Category Pill matching HTML tags
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = when(item.category) {
                                "URGENT" -> BentoUrgentBg
                                "FREE" -> BentoPurpleSoft
                                else -> Color(0xFFE6F4EA)
                            }
                        ) {
                            Text(
                                text = when(item.category) {
                                    "URGENT" -> "Urgente"
                                    "FREE" -> "Regalo"
                                    else -> "Baratísimo"
                                }.uppercase(),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = when(item.category) {
                                    "URGENT" -> BentoUrgentText
                                    "FREE" -> BentoPurple
                                    else -> Color(0xFF137333)
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }

                        // Relative duration marker
                        Text(
                            text = "Hace 5 min",
                            fontSize = 10.sp,
                            color = Color(0xFF938F99)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = item.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoDarkText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = item.neighborhood,
                        fontSize = 11.sp,
                        color = Color(0xFF49454F)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when(item.category) {
                            "URGENT" -> "Gratis"
                            "FREE" -> "${item.tiluchisCost} Tiluchis"
                            else -> "${item.tiluchisCost} Tiluchis • Bs.${item.priceInBs}"
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = BentoPurple
                    )

                    // Chat round element symbol from Bento Grid design mockup
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(BentoPurpleSoft),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChatBubble,
                            contentDescription = "Chat",
                            tint = BentoPurple,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
    }
}
