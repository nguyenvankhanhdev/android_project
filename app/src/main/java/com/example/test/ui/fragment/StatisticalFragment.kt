package com.example.test.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import com.example.test.models.ProductQuantity
import com.example.test.R
import com.example.test.databinding.FragmentStatisticalBinding
import com.example.test.firestoreclass.FirestoreClassKT
import com.example.test.models.Product

import com.example.test.ui.adapters.CustomMarkerView
import com.github.mikephil.charting.charts.BarChart

import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
private val productNames: MutableList<String> = mutableListOf()
private val productQuantities = mutableListOf<ProductQuantity>()
class StatisticalFragment : BaseFragment() {
    private lateinit var barChart : BarChart
    private var _binding: FragmentStatisticalBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStatisticalBinding.inflate(inflater, container, false)
        val root: View = binding.root

        barChart = root.findViewById(R.id.barChart)
        setupBarChart()
        FirestoreClassKT().getAllIdProduct(object : FirestoreClassKT.FirestoreCallback {
            override fun onCallback(products: List<Product>) {
                val totalProducts = products.size
                var completedProducts = 0
                for (product in products) {
                    productNames.add(product.title)
                    FirestoreClassKT().getSLTungSP(product.product_id, object : FirestoreClassKT.FirestoreSLCallback {
                        override fun onCallback(totalQuantity: Int) {
                            val productQuantity = ProductQuantity(product.title, totalQuantity)
                            productQuantities.add(productQuantity)
                            completedProducts++
                            if (completedProducts == totalProducts) {

                                drawBarChart()
                            }
                        }
                    })
                }
            }
        })


        // Refresh the chart
        barChart.invalidate()
        return root
    }


    private fun setupBarChart() {
        barChart.setDrawBarShadow(false)
        barChart.setDrawValueAboveBar(true)
        barChart.description.isEnabled = false
        barChart.setPinchZoom(false)
        barChart.setDrawGridBackground(false)

        val xAxis: XAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        val productIndices = (1..productQuantities.size).map { it.toString() }.toTypedArray()
        xAxis.valueFormatter = IndexAxisValueFormatter(productIndices)

        val leftAxis: YAxis = barChart.axisLeft
        leftAxis.setLabelCount(5, false)
        leftAxis.setDrawGridLines(true)
        leftAxis.spaceTop = 15f
        leftAxis.axisMinimum = 0f

        val rightAxis: YAxis = barChart.axisRight
        rightAxis.isEnabled = false

        // Bật tính năng tooltip
        barChart.setDrawMarkers(true)

        // Thiết lập CustomMarkerView cho biểu đồ
        val markerView = CustomMarkerView(requireContext(), R.layout.ustom_marker_view, productQuantities)
        markerView.chartView = barChart
        barChart.marker = markerView
    }

    private fun drawBarChart() {
        val sortedProducts = productQuantities.sortedByDescending { it.totalQuantity }

        // Chỉ lấy 5 sản phẩm có số lượng cao nhất
        val top5Products = sortedProducts.take(5)

        val entries: ArrayList<BarEntry> = ArrayList()
        top5Products.forEachIndexed { index, productQuantity ->
            entries.add(BarEntry(index.toFloat(), productQuantity.totalQuantity.toFloat()))
            productNames.add(productQuantity.productName) // Thêm tên sản phẩm vào danh sách
        }

        val dataSet = BarDataSet(entries, "Top 5 Product Quantities")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toMutableList()
        val data = BarData(dataSet)
        barChart.data = data
        barChart.barData.barWidth = 0.5f
        barChart.animateY(1500)
        barChart.invalidate()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}