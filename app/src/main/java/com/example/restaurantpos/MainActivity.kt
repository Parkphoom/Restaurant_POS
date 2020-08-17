package com.example.restaurantpos

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantpos.Adapter.RestNameItem
import com.example.restaurantpos.Adapter.RestuarantNameAdapter
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var restuarantNameAdapter:RestuarantNameAdapter
    var rvName: RecyclerView? = null
    private var nameList: List<RestNameItem>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        runOnUiThread(Runnable {
            rvName = findViewById<RecyclerView>(R.id.rvNamerest)

            nameList = ArrayList<RestNameItem>()
            (nameList as ArrayList<RestNameItem>).add(RestNameItem("asdhnnasdm"))
            (nameList as ArrayList<RestNameItem>).add(RestNameItem("asdhnnasdm"))
            (nameList as ArrayList<RestNameItem>).add(RestNameItem("asdhnnasdm"))


            val layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            rvName!!.setHasFixedSize(true)
            rvName!!.setLayoutManager(layoutManager)
            restuarantNameAdapter = RestuarantNameAdapter(this, nameList)
            rvName!!.setAdapter(restuarantNameAdapter)
        })
    }
}