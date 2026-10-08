package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.StoreItem
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassCardSmall
import com.example.ui.theme.*
import com.example.viewmodel.SharedStatsViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun StoreScreen(viewModel: SharedStatsViewModel) {
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val storeItems by viewModel.storeItems.collectAsStateWithLifecycle()
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }
    val categories = listOf("Tất cả", "Phòng", "Nội thất", "Cây cối", "Thú cưng")
    val categoryKeys = listOf("all", "room", "furniture", "plant", "pet")

    val filteredItems = remember(storeItems, selectedCategoryIndex) {
        val currentKey = categoryKeys[selectedCategoryIndex]
        if (currentKey == "all") storeItems else storeItems.filter { it.category == currentKey }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .statusBarsPadding()
    ) {
        // Header with MoonCoins balance
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Cửa Hàng Moon Girl",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = OnSurfaceVariant
                )
                Text(
                    text = "Trang hoàng không gian tập trung của bạn",
                    fontSize = 12.sp,
                    color = OnSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            // Coin Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(AmberAccent.copy(alpha = 0.15f))
                    .border(1.dp, AmberAccent.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⭐", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    val coinsFormatted = NumberFormat.getNumberInstance(Locale.US).format(stats.moonCoins)
                    Text(
                        text = coinsFormatted,
                        color = AmberAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }

        // Category Filter Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedCategoryIndex,
            containerColor = Color.Transparent,
            contentColor = PrimaryPurple,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                if (selectedCategoryIndex < tabPositions.size) {
                    Box(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[selectedCategoryIndex])
                            .height(3.dp)
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                            .background(PrimaryPurple)
                    )
                }
            },
            divider = {}
        ) {
            categories.forEachIndexed { index, title ->
                Tab(
                    selected = selectedCategoryIndex == index,
                    onClick = { selectedCategoryIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedCategoryIndex == index) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Store Items Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredItems, key = { it.id }) { item ->
                StoreItemCard(
                    item = item,
                    userCoins = stats.moonCoins,
                    onBuyClick = { viewModel.purchaseItem(item.id) }
                )
            }
        }
    }
}

@Composable
fun StoreItemCard(
    item: StoreItem,
    userCoins: Int,
    onBuyClick: () -> Unit
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Emoji Display Frame
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(listOf(Color.White.copy(alpha = 0.8f), PrimaryPurpleLight.copy(alpha = 0.3f))))
                    .border(1.dp, GlassBorder, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(item.iconEmoji, fontSize = 34.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = item.name,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = OnSurfaceVariant,
                maxLines = 1
            )

            if (item.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.description,
                    fontSize = 11.sp,
                    color = OnSurfaceVariant.copy(alpha = 0.65f),
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (item.isPurchased) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(EmeraldGreen.copy(alpha = 0.15f))
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✓ Đã sở hữu",
                        color = EmeraldGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            } else {
                val canAfford = userCoins >= item.price
                Button(
                    onClick = onBuyClick,
                    enabled = canAfford,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (canAfford) AmberAccent else Color.LightGray
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⭐", fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${item.price}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
