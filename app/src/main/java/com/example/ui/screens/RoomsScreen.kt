package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.StoreItem
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassCardSmall
import com.example.ui.theme.*
import com.example.viewmodel.SharedStatsViewModel

@Composable
fun RoomsScreen(viewModel: SharedStatsViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val storeItems by viewModel.storeItems.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .statusBarsPadding()
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = PrimaryPurple,
            indicator = { tabPositions ->
                Box(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[selectedTab])
                        .height(3.dp)
                        .padding(horizontal = 32.dp)
                        .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                        .background(PrimaryPurple)
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Nhà của tôi", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Vật phẩm & Mở khóa", fontWeight = FontWeight.Bold) }
            )
        }

        if (selectedTab == 0) {
            HouseTab(storeItems = storeItems)
        } else {
            ShopTab(viewModel = viewModel, storeItems = storeItems, userCoins = stats.moonCoins)
        }
    }
}

@Composable
fun HouseTab(storeItems: List<StoreItem>) {
    val purchasedItems = storeItems.filter { it.isPurchased }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // 2D House Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(380.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Sky
                drawRect(
                    brush = Brush.verticalGradient(listOf(SkyGradientStart, Color(0xFFBAE6FD))),
                    size = size
                )
                
                // Sun
                drawCircle(
                    color = Color.Yellow,
                    radius = 36.dp.toPx(),
                    center = Offset(size.width - 50.dp.toPx(), 50.dp.toPx())
                )
                
                val floorHeight = 80.dp.toPx()
                val wallWidth = 8.dp.toPx()
                
                // Ground
                drawRect(
                    color = Color(0xFF8B4513),
                    topLeft = Offset(0f, size.height - 40.dp.toPx()),
                    size = Size(size.width, 40.dp.toPx())
                )
                
                // House structure
                val houseWidth = size.width - 80.dp.toPx()
                val houseLeft = 40.dp.toPx()
                val houseBottom = size.height - 40.dp.toPx()
                
                for (i in 0 until 3) {
                    val y = houseBottom - ((i + 1) * floorHeight)
                    
                    // Floor separator
                    drawRect(
                        color = Color(0xFF5C4033),
                        topLeft = Offset(houseLeft, y),
                        size = Size(houseWidth, 4.dp.toPx())
                    )
                    
                    // Left Wall
                    drawRect(
                        color = Color.Gray,
                        topLeft = Offset(houseLeft, y),
                        size = Size(wallWidth, floorHeight)
                    )
                    
                    // Right Wall
                    drawRect(
                        color = Color.Gray,
                        topLeft = Offset(houseLeft + houseWidth - wallWidth, y),
                        size = Size(wallWidth, floorHeight)
                    )
                }
            }

            // Display placed emojis inside the house
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 44.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (purchasedItems.isEmpty()) {
                    Text("🪵", fontSize = 28.sp)
                } else {
                    purchasedItems.take(5).forEach { item ->
                        Text(item.iconEmoji, fontSize = 28.sp)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Trạng thái Ngôi Nhà", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Tầng 1: Đã mở khóa (Sở hữu ${purchasedItems.size} vật phẩm)", color = EmeraldGreen, fontSize = 13.sp)
                Text("Tầng 2: Mở khóa khi tích lũy 3,000 Coins", color = OnSurfaceVariant, fontSize = 13.sp)
                Text("Tầng 3: Mở khóa khi tích lũy 6,000 Coins", color = OnSurfaceVariant, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun ShopTab(
    viewModel: SharedStatsViewModel,
    storeItems: List<StoreItem>,
    userCoins: Int
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Kho vật phẩm mở khóa", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(storeItems, key = { it.id }) { item ->
                StoreItemCard(
                    item = item,
                    userCoins = userCoins,
                    onBuyClick = { viewModel.purchaseItem(item.id) }
                )
            }
        }
    }
}
