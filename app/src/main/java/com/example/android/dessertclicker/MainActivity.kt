
package com.example.android.dessertclicker

import android.content.ActivityNotFoundException
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.databinding.DataBindingUtil
import com.example.android.dessertclicker.databinding.ActivityMainBinding
import timber.log.Timber

//sử dụng các khóa này cho cả việc lưu và truy xuất dữ liệu từ gói trạng thái phiên bản.
const val KEY_REVENUE = "revenue_key"
const val KEY_DESSERT_SOLD = "dessert_sold_key"
const val KEY_TIMER_SECONDS = "timer_seconds_key"

class MainActivity : AppCompatActivity() {

    private var revenue = 0
    private var dessertsSold = 0
    private lateinit var dessertTimer: DessertTimer

    // Chứa tất cả các chế độ xem
    private lateinit var binding: ActivityMainBinding

    /** Dessert Data **/

    /**
     * Lớp dữ liệu đơn giản đại diện cho món tráng miệng. Bao gồm số nguyên id tài nguyên được liên kết với
     * hình ảnh, giá mà nó được bán và startProductionAmount, xác định thời điểm
     * món tráng miệng bắt đầu được sản xuất.
     */
    data class Dessert(val imageId: Int, val price: Int, val startProductionAmount: Int)

    // Tạo một danh sách tất cả các món tráng miệng, theo thứ tự khi chúng bắt đầu được sản xuất
    private val allDesserts = listOf(
        Dessert(R.drawable.cupcake, 5, 0),
        Dessert(R.drawable.donut, 10, 5),
        Dessert(R.drawable.eclair, 15, 20),
        Dessert(R.drawable.froyo, 30, 50),
        Dessert(R.drawable.gingerbread, 50, 100),
        Dessert(R.drawable.honeycomb, 100, 200),
        Dessert(R.drawable.icecreamsandwich, 500, 500),
        Dessert(R.drawable.jellybean, 1000, 1000),
        Dessert(R.drawable.kitkat, 2000, 2000),
        Dessert(R.drawable.lollipop, 3000, 4000),
        Dessert(R.drawable.marshmallow, 4000, 8000),
        Dessert(R.drawable.nougat, 5000, 16000),
        Dessert(R.drawable.oreo, 6000, 20000)
    )
    private var currentDessert = allDesserts[0]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("onCreate called")

        //Sử dụng dữ liệu Binding để tham chiếu đến các chế độ xem
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        dessertTimer = DessertTimer(this.lifecycle)

        if (savedInstanceState != null){
            //Thêm phương thức getInt() khôi phục số lượng món tráng miệng đã bán và giá trị của bộ đếm thời gian
            revenue = savedInstanceState.getInt(KEY_REVENUE, 0)
            dessertsSold = savedInstanceState.getInt(KEY_DESSERT_SOLD, 0)
            dessertTimer.secondsCount = savedInstanceState.getInt(KEY_TIMER_SECONDS, 0)
            showCurrentDessert()
        }
        binding.dessertButton.setOnClickListener {
            onDessertClicked()
        }

        // Đặt TextViews thành các giá trị phù hợp
        binding.revenue = revenue
        binding.amountSold = dessertsSold

        // Đảm bảo hiển thị đúng món tráng miệng
        binding.dessertButton.setImageResource(currentDessert.imageId)
    }

    override fun onStart() {
        super.onStart()
        Timber.i("onStart Called")
    }

    override fun onResume() {
        super.onResume()
        Timber.i("onResume Called")
    }
    override fun onPause() {
        super.onPause()
        Timber.i("onPause Called")
    }

    override fun onStop() {
        super.onStop()
        Timber.i("onStop Called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.i("onDestroy Called")
    }

    override fun onRestart() {
        super.onRestart()
        Timber.i("onRestart Called")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        Timber.i("onSaveInstanceState Called")
        outState.putInt(KEY_REVENUE, revenue)
        outState.putInt(KEY_DESSERT_SOLD, dessertsSold)
        outState.putInt(KEY_TIMER_SECONDS, dessertTimer.secondsCount)
    }

    /**
     * Cập nhật điểm số khi nhấp vào món tráng miệng. Có thể cho thấy một món tráng miệng mới.
     */
    private fun onDessertClicked() {

        // Cập nhật điểm số
        revenue += currentDessert.price
        dessertsSold++

        binding.revenue = revenue
        binding.amountSold = dessertsSold

        // Hiển thị món tráng miệng tiếp theo
        showCurrentDessert()
    }

    /**
     * Xác định món tráng miệng nào sẽ được trưng bày.
     */
    private fun showCurrentDessert() {
        var newDessert = allDesserts[0]
        for (dessert in allDesserts) {
            if (dessertsSold >= dessert.startProductionAmount) {
                newDessert = dessert
            }
            // Danh sách món tráng miệng được sắp xếp theo startProductionAmount. Khi bạn bán được nhiều món tráng miệng hơn,
            // bạn sẽ bắt đầu sản xuất các món tráng miệng đắt tiền hơn được xác định bởi startProductionAmount
            // Chúng tôi biết phá vỡ ngay khi chúng tôi thấy món tráng miệng có "startProductionAmount" lớn hơn
            // so với số lượng đã bán.
            else break
        }

        // Nếu món tráng miệng mới thực sự khác với món tráng miệng hiện tại, hãy cập nhật hình ảnh
        if (newDessert != currentDessert) {
            currentDessert = newDessert
            binding.dessertButton.setImageResource(newDessert.imageId)
        }
    }

    /**
     * Menu methods
     */
    private fun onShare() {
        val shareIntent = ShareCompat.IntentBuilder.from(this)
            .setText(getString(R.string.share_text, dessertsSold, revenue))
            .setType("text/plain")
            .intent
        try {
            startActivity(shareIntent)
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(this, getString(R.string.sharing_not_available),
                Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.shareMenuButton -> onShare()
        }
        return super.onOptionsItemSelected(item)
    }
}
