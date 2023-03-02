package com.example.android.dessertclicker

import android.os.Handler
import timber.log.Timber
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

class DessertTimer(lifecycle: Lifecycle) : LifecycleObserver {

    // Số giây được tính kể từ khi bộ đếm thời gian bắt đầu
    var secondsCount = 0

    /**
     * [Handler] là một lớp dùng để xử lý một hàng thư (được gọi là [android.os.Message]s)
     * hoặc các hành động (được gọi là [Runnable]s)
     */
    private var handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable
//sử dụng phương thức addObserver() để kết nối đối tượng vòng đời được truyền từ chủ sở hữu
    // (hoạt động) đến lớp này (người quan sát).
    init {
        lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun startTimer() {
        // Tạo hành động có thể chạy được, in ra nhật ký và tăng bộ đếm giây
        runnable = Runnable {
            secondsCount++
            Timber.i("Timer is at : $secondsCount")
            // postDelayed thêm lại hành động vào hàng đợi các hành động mà Trình xử lý đang thực hiện
            // bởi vì. Thông số delayMillis yêu cầu trình xử lý chạy runnable trong
            // 1 giây (1000ms)
            handler.postDelayed(runnable, 1000)
        }

        // Đây là những gì ban đầu khởi động bộ đếm thời gian
        handler.postDelayed(runnable, 1000)

        // Lưu ý rằng Chủ đề mà trình xử lý chạy trên đó được xác định bởi một lớp có tên là Looper.
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stopTimer() {
        // Xóa tất cả các bài đăng đang chờ xử lý của runnable khỏi hàng đợi của trình xử lý, ngăn chặn hiệu quả hẹn giờ
        handler.removeCallbacks(runnable)
    }
}