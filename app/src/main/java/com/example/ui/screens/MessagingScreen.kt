package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MessageEntity
import com.example.ui.theme.*
import com.example.viewmodel.AppViewModel
import com.example.viewmodel.InboxChatItem
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagingScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val inboxChats by viewModel.inboxChats.collectAsState()
    var activeChatId by remember { mutableStateOf<String?>(null) }
    var activeChatTitle by remember { mutableStateOf("") }
    var activeItemId by remember { mutableStateOf(-1) }
    var activeOtherUserId by remember { mutableStateOf("") }
    var activeOtherUserName by remember { mutableStateOf("") }

    val activeMessages = remember(activeChatId) {
        activeChatId?.let { viewModel.getChatMessages(it) } ?: flowOf(emptyList())
    }.collectAsState(initial = emptyList())

    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var textMessage by remember { mutableStateOf("") }
    var showReviewDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (activeChatId == null) "Mensajes Internos 💬" else activeChatTitle,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    if (activeChatId != null) {
                        IconButton(onClick = { activeChatId = null }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                        }
                    }
                },
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
            if (activeChatId == null) {
                // INBOX VIEW
                if (inboxChats.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChatBubbleOutline,
                                contentDescription = "",
                                tint = SczGreenSecondary.copy(alpha = 0.5f),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Bandeja de entrada vacía",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Tus conversaciones para coordinar el retiro de tus Tiluchis y regalos aparecerán aquí.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(inboxChats) { chat ->
                            InboxItemRow(
                                chat = chat,
                                onClick = {
                                    activeChatId = chat.chatId
                                    activeChatTitle = chat.itemTitle
                                    activeItemId = chat.itemId
                                    activeOtherUserId = chat.otherUserId
                                    activeOtherUserName = chat.otherUserName
                                }
                            )
                            Divider(color = SoftGrayBorder, modifier = Modifier.padding(horizontal = 16.dp))
                        }
                    }
                }
            } else {
                // CHAT CONVERSATION VIEW
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    // Item context header
                    Surface(
                        color = SczGreenPrimary.copy(alpha = 0.05f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Coordinando: $activeChatTitle",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SczGreenPrimary
                                )
                                Text(
                                    text = "Vecino: $activeOtherUserName",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            
                            // Give rating action (Review)
                            if (activeOtherUserId != "SYSTEM") {
                                Button(
                                    onClick = { showReviewDialog = true },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = TiluchisGoldAccent,
                                        contentColor = Color.Black
                                    ),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(32.dp).testTag("write_review_btn")
                                ) {
                                    Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Calificar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Chat list
                    LazyColumn(
                        state = scrollState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 12.dp),
                        contentPadding = PaddingValues(vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(activeMessages.value) { msg ->
                            val isMe = msg.senderId == viewModel.currentUserId
                            val isSystem = msg.senderId == "SYSTEM"

                            ChatBubble(
                                text = msg.text,
                                timestamp = msg.timestamp,
                                isMe = isMe,
                                isSystem = isSystem,
                                senderName = msg.senderName
                            )
                        }
                    }

                    // Auto scroll to bottom when new message arrives
                    LaunchedEffect(activeMessages.value.size) {
                        if (activeMessages.value.isNotEmpty()) {
                            coroutineScope.launch {
                                scrollState.animateScrollToItem(activeMessages.value.size - 1)
                            }
                        }
                    }

                    // Input Row
                    Surface(
                        tonalElevation = 2.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .navigationBarsPadding()
                                .imePadding(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = textMessage,
                                onValueChange = { textMessage = it },
                                placeholder = { Text("Escribe un mensaje al vecino...") },
                                maxLines = 3,
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("chat_input_text"),
                                colors = OutlinedTextFieldDefaults.colors()
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    viewModel.sendChatMessage(
                                        chatId = activeChatId!!,
                                        itemId = activeItemId,
                                        itemTitle = activeChatTitle,
                                        receiverId = activeOtherUserId,
                                        text = textMessage
                                    )
                                    textMessage = ""
                                },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(SczGreenPrimary)
                                    .testTag("send_chat_btn")
                            ) {
                                Icon(Icons.Default.Send, contentDescription = "Enviar", tint = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }

    // Submit Review dialog
    if (showReviewDialog) {
        var starsValue by remember { mutableStateOf(5f) }
        var reviewComment by remember { mutableStateOf("") }
        var reviewError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showReviewDialog = false },
            title = { Text("Calificar a $activeOtherUserName 💚", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Las reseñas reales alimentan nuestro sistema de reputación para garantizar intercambios sanos y libres de fraude en Santa Cruz.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Divider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Calificación:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = TiluchisGoldAccent, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${starsValue.toInt()} Estrellas", fontWeight = FontWeight.Bold)
                        }
                    }

                    Slider(
                        value = starsValue,
                        onValueChange = { starsValue = it },
                        valueRange = 1f..5f,
                        steps = 3
                    )

                    OutlinedTextField(
                        value = reviewComment,
                        onValueChange = { reviewComment = it },
                        label = { Text("Tu Comentario") },
                        placeholder = { Text("Ej: Súper amable, me entregó el mueble rápido. ¡Recomendado!") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (reviewComment.isBlank()) {
                            reviewError = "Por favor ingresa un comentario."
                            return@Button
                        }
                        viewModel.submitReview(
                            itemId = activeItemId,
                            reviewedUserId = activeOtherUserId,
                            rating = starsValue,
                            comment = reviewComment
                        )
                        showReviewDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SczGreenPrimary
                    )
                ) {
                    Text("Enviar Reseña")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReviewDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun InboxItemRow(
    chat: InboxChatItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon circular badge
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    if (chat.otherUserId == "SYSTEM") SczGreenPrimary.copy(alpha = 0.15f)
                    else TiluchisGoldAccent.copy(alpha = 0.2f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (chat.otherUserId == "SYSTEM") "🦉" else "👤",
                fontSize = 20.sp
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Titles
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (chat.otherUserId == "SYSTEM") "Pásalo SCZ" else chat.otherUserName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Detalle: " + chat.itemTitle,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SczGreenPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 120.dp)
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = chat.lastMessage,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ChatBubble(
    text: String,
    timestamp: Long,
    isMe: Boolean,
    isSystem: Boolean,
    senderName: String
) {
    val alignment = when {
        isSystem -> Alignment.CenterHorizontally
        isMe -> Alignment.End
        else -> Alignment.Start
    }

    val containerColor = when {
        isSystem -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
        isMe -> SczGreenPrimary
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val textColor = when {
        isSystem -> SczGreenPrimary
        isMe -> Color.White
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        if (!isSystem && !isMe) {
            Text(
                text = senderName,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 6.dp, bottom = 2.dp)
            )
        }

        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isMe || isSystem) 16.dp else 4.dp,
                        bottomEnd = if (!isMe || isSystem) 16.dp else 4.dp
                    )
                )
                .background(containerColor)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = text,
                fontSize = 13.sp,
                color = textColor,
                fontWeight = if (isSystem) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
