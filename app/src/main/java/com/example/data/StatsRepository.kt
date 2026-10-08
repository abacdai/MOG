package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class StatsRepository(
    private val statsDao: UserStatsDao,
    private val sessionDao: FocusSessionDao,
    private val storeDao: StoreItemDao
) {

    val stats: Flow<UserStats> = statsDao.getStats().map { it ?: UserStats() }
    val recentSessions: Flow<List<FocusSession>> = sessionDao.getAllSessions()
    val storeItems: Flow<List<StoreItem>> = storeDao.getAllItems()
    val inventory: Flow<List<StoreItem>> = storeDao.getPurchasedItems()

    private fun getStartOfDayTimestamp(): Long {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    suspend fun initDatabaseIfNeeded() {
        val current = statsDao.getStats().first()
        val todayStart = getStartOfDayTimestamp()
        val todayMinutes = sessionDao.getTotalMinutesSinceSync(todayStart) ?: 0

        if (current == null) {
            // New install: First time starts with 0 for all statistics
            statsDao.upsertStats(
                UserStats(
                    id = 1,
                    moonCoins = 0,
                    streak = 0,
                    totalFocusMinutesToday = 0,
                    totalSessionsCompleted = 0,
                    sleepHours = 0f,
                    screenTimeHours = 0f,
                    level = 1,
                    lastFocusDate = ""
                )
            )
        } else {
            // Returning user: ALWAYS PRESERVE moonCoins, streak, level, totalSessionsCompleted
            // Synchronize today's focus minutes based on actual sessions completed today (0 if new day)
            statsDao.upsertStats(
                current.copy(
                    totalFocusMinutesToday = todayMinutes
                )
            )
        }

        // Initialize Store Catalog if empty
        if (storeDao.getItemCount() == 0) {
            val initialCatalog = listOf(
                // Rooms
                StoreItem("room_ocean", "Bể Cá Đại Dương", "room", 1500, "🌊", "Không gian đại dương tĩnh lặng, thư giãn"),
                StoreItem("room_forest", "Khu Vườn Bí Mật", "room", 2500, "🌿", "Phòng phủ kín rêu xanh và tiếng chim hót"),
                StoreItem("room_space", "Trạm Không Gian", "room", 4000, "🚀", "Ngắm dải ngân hà và trăng sao vô tận"),
                // Furniture
                StoreItem("furn_lamp", "Đèn Ngủ Tinh Cầu", "furniture", 100, "💡", "Ánh sáng dịu nhẹ hỗ trợ giấc ngủ"),
                StoreItem("furn_chair", "Ghế Đọc Sách Lười", "furniture", 200, "🪑", "Êm ái cho những giờ học dài"),
                StoreItem("furn_desk", "Bàn Gỗ Tự Nhiên", "furniture", 350, "🪵", "Gọn gàng và tối giản"),
                StoreItem("furn_bookshelf", "Kệ Sách Cổ Điển", "furniture", 450, "📚", "Chứa đựng tri thức và sự tập trung"),
                // Plants & Pets
                StoreItem("plant_succulent", "Chậu Sen Đá", "plant", 150, "🪴", "Dễ chăm sóc và mang lại năng lượng xanh"),
                StoreItem("plant_bonsai", "Cây Bonsai Mini", "plant", 300, "🎍", "Biểu tượng của sự kiên trì"),
                StoreItem("pet_cat", "Mèo Mướp Ngủ Say", "pet", 500, "🐈", "Bạn đồng hành luôn ngủ ngon bên cạnh"),
                StoreItem("pet_rabbit", "Thỏ Trắng Tinh Nghịch", "pet", 600, "🐇", "Nhảy nhót quanh phòng mỗi khi hoàn thành mục tiêu")
            )
            storeDao.insertInitialItems(initialCatalog)
        }
    }

    suspend fun completeFocusSession(durationMinutes: Int, mode: String): Int {
        if (durationMinutes <= 0) return 0
        // Coins rule: 100 coins per 25m base, scaled proportionally
        val coinsEarned = (durationMinutes * 4).coerceAtLeast(10)
        
        // 1. Record completed session in database
        sessionDao.insertSession(
            FocusSession(
                durationMinutes = durationMinutes,
                coinsEarned = coinsEarned,
                mode = mode
            )
        )

        // 2. Fetch current stats
        val current = statsDao.getStats().first() ?: UserStats()
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        
        // Calculate streak:
        val newStreak = if (current.lastFocusDate != todayStr) {
            val yesterdayCal = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -1)
            }
            val yesterdayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(yesterdayCal.time)
            if (current.lastFocusDate == yesterdayStr) {
                current.streak + 1
            } else {
                // First session or streak reset after gap
                1
            }
        } else {
            // Already active today, maintain streak (at least 1)
            current.streak.coerceAtLeast(1)
        }

        // 3. Compute today's focus minutes from all completed sessions today
        val todayStart = getStartOfDayTimestamp()
        val todayTotalMinutes = sessionDao.getTotalMinutesSinceSync(todayStart) ?: durationMinutes

        val newCoins = current.moonCoins + coinsEarned
        val newCompletedSessions = current.totalSessionsCompleted + 1
        val newLevel = 1 + (newCompletedSessions / 5)

        statsDao.upsertStats(
            current.copy(
                moonCoins = newCoins,
                streak = newStreak,
                totalFocusMinutesToday = todayTotalMinutes,
                totalSessionsCompleted = newCompletedSessions,
                level = newLevel.coerceAtLeast(current.level),
                lastFocusDate = todayStr
            )
        )

        return coinsEarned
    }

    suspend fun buyStoreItem(itemId: String): Result<StoreItem> {
        val currentStats = statsDao.getStats().first() ?: return Result.failure(Exception("Chưa có hồ sơ người dùng"))
        val item = storeDao.getItemById(itemId) ?: return Result.failure(Exception("Vật phẩm không tồn tại"))
        
        if (item.isPurchased) {
            return Result.failure(Exception("Bạn đã sở hữu vật phẩm này rồi!"))
        }

        if (currentStats.moonCoins < item.price) {
            return Result.failure(Exception("Không đủ MoonCoins! Cần thêm ${item.price - currentStats.moonCoins} coins."))
        }

        // Deduct coins & Mark item purchased
        val rowsUpdated = statsDao.spendCoins(item.price)
        if (rowsUpdated > 0) {
            storeDao.markAsPurchased(itemId)
            return Result.success(item)
        } else {
            return Result.failure(Exception("Giao dịch không thành công. Hãy thử lại!"))
        }
    }

    suspend fun updateDeviceHealth(screenHours: Float, sleepHours: Float) {
        val current = statsDao.getStats().first() ?: UserStats()
        statsDao.upsertStats(
            current.copy(
                screenTimeHours = if (screenHours > 0f) screenHours else current.screenTimeHours,
                sleepHours = if (sleepHours > 0f) sleepHours else current.sleepHours
            )
        )
    }

    suspend fun updateScreenTime(hours: Float) {
        val current = statsDao.getStats().first() ?: UserStats()
        statsDao.upsertStats(current.copy(screenTimeHours = hours))
    }

    suspend fun updateSleep(hours: Float) {
        val current = statsDao.getStats().first() ?: UserStats()
        statsDao.upsertStats(current.copy(sleepHours = hours))
    }
}
