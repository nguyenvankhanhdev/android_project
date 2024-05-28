package com.example.test.ui.adapters

import android.content.Context
import android.widget.TextView
import com.example.test.R
import com.example.test.models.ProductQuantity
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF

class CustomMarkerView(
    context: Context,
    layoutResource: Int,
    private val productQuantities: List<ProductQuantity>
) : MarkerView(context, layoutResource) {

    private val tvContent: TextView = findViewById(R.id.tvContent)

    // Trong hàm này, ta cập nhật nội dung của tooltip dựa trên BarEntry và Highlight được chọn
    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        if (e == null) return

        val xIndex = e.x.toInt()
        val productQuantity = productQuantities.getOrNull(xIndex)
        if (productQuantity != null) {
            tvContent.text = "${productQuantity.productName}"
        }

        super.refreshContent(e, highlight)
    }

    // Tùy chỉnh vị trí của tooltip
    override fun getOffset(): MPPointF {
        return MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
    }
}