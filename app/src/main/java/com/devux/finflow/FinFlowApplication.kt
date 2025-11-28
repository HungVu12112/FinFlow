package com.devux.finflow

import androidx.multidex.MultiDexApplication
import com.devux.finflow.helper.PreferencesHelper
import com.devux.finflow.helper.UserManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.runBlocking

@HiltAndroidApp
class FinFlowApplication : MultiDexApplication() {
    @Inject
    lateinit var sharedPreferences: PreferencesHelper

    // Các cờ trạng thái Ads
    var isAdsSdkInitialized = false
        private set

    var isRewardedAdReady = false
        private set

    override fun onCreate() {
        super.onCreate()
        // Gán instance khi ứng dụng được tạo
        instance = this

        // Không cần khởi tạo sharedPreferences thủ công, Hilt đã làm việc đó
        runBlocking {
            UserManager.init(getInstance(), sharedPreferences)
        }
    }

    // --- SỬA ĐỔI LỚN NHẤT NẰM Ở COMPANION OBJECT DƯỚI ĐÂY ---
    companion object {
        private lateinit var instance: FinFlowApplication

        fun getInstance(): FinFlowApplication {
            return instance
        }

        // 1. Di chuyển CoroutineExceptionHandler VÀO BÊN TRONG companion object
        //    và biến nó thành một thuộc tính riêng tư.
        //    Nó không còn phụ thuộc vào instance nữa.
        private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            // Logger.d(throwable)
            // Thêm phần push error lên firebase tại đây nếu cần
        }

        val job = SupervisorJob()

        // 2. Bây giờ các Dispatcher có thể truy cập trực tiếp coroutineExceptionHandler
        //    mà không cần thông qua instance.
        val IODispatcher by lazy {
            Dispatchers.IO + job + coroutineExceptionHandler
        }

        val MainDispatcher by lazy {
            Dispatchers.Main + job + coroutineExceptionHandler
        }

        var isConnectInternet = false
    }

    // Phần code về Ads của bạn không thay đổi, tôi tạm ẩn đi cho gọn
    // ...
}
