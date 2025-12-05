package com.example.kostlin.ui.screen.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kostlin.R
import com.example.kostlin.ui.components.BottomNavigation
import com.example.kostlin.ui.components.BottomNavRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit = {},
    onEditProfile: () -> Unit = {},
    onNavigate: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Data pengguna (bisa diganti dengan data dari ViewModel)
    var userData by remember {
        mutableStateOf(
            UserProfile(
                name = "John Doe",
                email = "johndoe@example.com",
                phone = "+62 812-3456-7890",
                joinDate = "Bergabung pada Januari 2023",
                profileImageUrl = null // URL gambar profil jika ada
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil Saya") },
                actions = {
                    IconButton(onClick = { onEditProfile() }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profil"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigation(
                currentRoute = BottomNavRoute.PROFILE.route,
                onNavigate = onNavigate
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header Profil
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Foto Profil
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    userData.profileImageUrl?.let { url ->
                        // Load image from URL
                        // Image(painter = rememberImagePainter(url), contentDescription = "Profile")
                    } ?: run {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_person),
                            contentDescription = "Profile",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Nama Pengguna
                Text(
                    text = userData.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                // Email
                Text(
                    text = userData.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Nomor Telepon
                Text(
                    text = userData.phone,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Tanggal Bergabung
                Text(
                    text = userData.joinDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Menu Profil
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Menu Item - Pengaturan Akun
                ProfileMenuItem(
                    icon = Icons.Default.Person,
                    text = "Pengaturan Akun",
                    onClick = { /* Navigate to account settings */ }
                )

                // Menu Item - Kos Saya
                ProfileMenuItem(
                    icon = Icons.Default.Home,
                    text = "Kos Saya",
                    onClick = { onNavigate("my_kos") }
                )

                // Menu Item - Favorit
                ProfileMenuItem(
                    icon = Icons.Default.Favorite,
                    text = "Favorit",
                    onClick = { onNavigate("favorites") }
                )

                // Menu Item - Riwayat
                ProfileMenuItem(
                    icon = Icons.Default.History,
                    text = "Riwayat",
                    onClick = { onNavigate("history") }
                )

                // Menu Item - Bantuan
                ProfileMenuItem(
                    icon = Icons.Default.Help,
                    text = "Bantuan",
                    onClick = { onNavigate("help") }
                )

                // Menu Item - Keluar
                ProfileMenuItem(
                    icon = Icons.Default.ExitToApp,
                    text = "Keluar",
                    onClick = onLogout,
                    textColor = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ProfileMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = textColor,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    Divider(
        color = MaterialTheme.colorScheme.surfaceVariant,
        thickness = 0.5.dp,
        modifier = Modifier.padding(start = 40.dp)
    )
}

// Data class untuk menyimpan data profil pengguna
data class UserProfile(
    val name: String,
    val email: String,
    val phone: String,
    val joinDate: String,
    val profileImageUrl: String? = null
)
