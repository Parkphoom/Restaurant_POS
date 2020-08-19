package com.example.restaurantpos

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantpos.Adapter.FoodNameAdapter
import com.example.restaurantpos.Adapter.FoodnameItem
import com.example.restaurantpos.Adapter.RestNameItem
import com.example.restaurantpos.Adapter.RestuarantNameAdapter
import com.example.restaurantpos.DB.DBManager
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.Method
import java.util.*


class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var dbManager: DBManager? = null

    private lateinit var restuarantNameAdapter: RestuarantNameAdapter
    var rvRestName: RecyclerView? = null
    private var restnameList: List<RestNameItem>? = null
    private lateinit var foodNameAdapter: FoodNameAdapter
    var rvFoodName: RecyclerView? = null
    private var foodnameList: List<FoodnameItem>? = null
    private var menu_btn: Button? = null
    lateinit var dataDB: MutableList<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()


        setUpRecyclerViewFoodname()

    }

    private fun initView() {
        dbManager = DBManager(this)
        loadRestautantTask().execute()

        menu_btn = findViewById(R.id.menu_btn)
        menu_btn!!.setOnClickListener(this)
    }

    fun showMenu(anchor: View?) {
        val popup = PopupMenu(this, anchor)
        popup.getMenuInflater().inflate(R.menu.main_menu_appbar, popup.getMenu())
        setForceShowIcon(popup)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.addmenu -> {
                    startActivity(Intent(this, AddActivity::class.java))
                    true
                }
                R.id.exportmenu -> {

                    true
                }

                else -> false
            }
        }
        popup.show()

    }

    fun setForceShowIcon(popupMenu: PopupMenu) {
        try {
            val fields = popupMenu.javaClass.declaredFields
            for (field in fields) {
                if ("mPopup" == field.name) {
                    field.isAccessible = true
                    val menuPopupHelper = field[popupMenu]
                    val classPopupHelper = Class.forName(
                        menuPopupHelper
                            .javaClass.name
                    )
                    val setForceIcons: Method = classPopupHelper.getMethod(
                        "setForceShowIcon", Boolean::class.javaPrimitiveType
                    )
                    setForceIcons.invoke(menuPopupHelper, true)
                    break
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private fun setUpRecyclerViewRestname(arrayList: ArrayList<RestNameItem>) {
        runOnUiThread(Runnable {
            rvRestName = findViewById<RecyclerView>(R.id.rvNamerest)

            val layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            rvRestName!!.setHasFixedSize(true)
            rvRestName!!.setLayoutManager(layoutManager)
            restuarantNameAdapter = RestuarantNameAdapter(this, arrayList)
            rvRestName!!.setAdapter(restuarantNameAdapter)
        })
    }

    private fun setUpRecyclerViewFoodname() {
        runOnUiThread(Runnable {
            rvFoodName = findViewById<RecyclerView>(R.id.rvFoodmenu)

            foodnameList = ArrayList<FoodnameItem>()
            (foodnameList as ArrayList<FoodnameItem>).add(FoodnameItem("asdhnnasdm"))
            (foodnameList as ArrayList<FoodnameItem>).add(FoodnameItem("asdhnnasdm"))
            (foodnameList as ArrayList<FoodnameItem>).add(FoodnameItem("asdhnnasdm"))


            val layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            rvFoodName!!.setHasFixedSize(true)
            rvFoodName!!.setLayoutManager(layoutManager)
            foodNameAdapter = FoodNameAdapter(this, foodnameList)
            rvFoodName!!.setAdapter(foodNameAdapter)
        })
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.menu_btn -> {
                showMenu(v)
            }
        }
    }

    private inner class loadRestautantTask : AsyncTask<Void, Void, Void>() {

        override fun onPreExecute() {
            super.onPreExecute()
            dbManager!!.open()

            dataDB = dbManager!!.getDataRESTAURANT()!!
            restnameList = ArrayList<RestNameItem>()
        }

        override fun doInBackground(vararg params: Void?): Void? {
            for (i in 0 until dataDB.size) {
                Log.d("dataDB", dataDB[i].toString())
                var values: JSONObject? = JSONObject()
                values = dataDB[i] as JSONObject?
                try {
                    val RestaurantName = values!!.getString("restaurantName")
                    val RestaurantDate = values!!.getString("restaurantDate")
                    Log.d("dataDB","$RestaurantName $RestaurantDate")
                    (restnameList as ArrayList<RestNameItem>).add(RestNameItem(RestaurantName))
                }
                catch (e : JSONException) {
                    e.printStackTrace();
                    Log.d("dataDB", e.toString())
                }
            }

            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            dbManager?.close()
            setUpRecyclerViewRestname(restnameList as ArrayList<RestNameItem>)
        }

    }
}