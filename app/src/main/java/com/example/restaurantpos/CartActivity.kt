package com.example.restaurantpos

import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantpos.Adapter.*
import com.example.restaurantpos.DB.DBMenuManager
import com.example.restaurantpos.DB.DBRestaurantManager
import com.example.restaurantpos.DB.DatabaseHelper
import com.example.restaurantpos.DB.OnclickItem
import org.json.JSONException
import org.json.JSONObject

class CartActivity : AppCompatActivity(), View.OnClickListener {

    private var dbRestaurantManager: DBRestaurantManager? = null
    var sharedPreferences: SharedPreferences? = null
    private var dbMenuManager: DBMenuManager? = null

    private val TAG = "CartActivity"

    var backbtn: Button? = null
    var paybtn: Button? = null
    lateinit var dataDB: MutableList<*>
    private var restnameList: List<RestNameItem>? = null
    var listmenu: ArrayList<String>? = null
    private lateinit var cartAdapter: CartAdapter
    var rvCart: RecyclerView? = null

    private var tv_restname :TextView?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        initView()


    }

    private fun initView() {
        sharedPreferences = getSharedPreferences(getString(R.string.RestaurantPref), MODE_PRIVATE)

        dbRestaurantManager = DBRestaurantManager(this)
        dbMenuManager = DBMenuManager(this)

        backbtn = findViewById(R.id.back_btn)
        backbtn?.setOnClickListener(this)
        paybtn = findViewById(R.id.pay_btn)
        paybtn?.setOnClickListener(this)
        tv_restname = findViewById(R.id.tv_restname)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.back_btn ->{
                onBackPressed()
            }
            R.id.pay_btn ->{
                Log.d(TAG, "onClick: ")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        loadRestautantTask().execute()
        val iscart = sharedPreferences?.getBoolean(getString(R.string.CartStatus), false)
        if (iscart!!) {


            onItemInCart()
        }
    }


    private fun onItemInCart() {
        var playlists = sharedPreferences?.getString(getString(R.string.Incart_listMenu), "")
        var rest_id = sharedPreferences?.getInt(getString(R.string.Incart_restID), -1)
        lateinit var namelist: MutableList<*>
        if(rest_id != -1){
            val pattern = """\W+""".toRegex()
            val words = pattern.split(playlists.toString()).filter { it.isNotBlank() }
            listmenu = ArrayList()
            listmenu?.addAll(words)


            lateinit var pricelist: MutableList<*>
            var price = 0
            dbRestaurantManager?.open()
            pricelist =
                dbRestaurantManager?.getRESTAURANT_menu_price(rest_id!!)!!
            namelist = dbRestaurantManager?.getRESTAURANT_name(rest_id!!)!!
            dbRestaurantManager!!.close()

            var RestaurantName = ""
            for (i in 0 until namelist.size) {
                Log.d(TAG, namelist[i].toString())
                var values: JSONObject? = JSONObject()
                values = namelist[i] as JSONObject?
                try {
                    RestaurantName = values!!.getString(DatabaseHelper.RESTAURANT_NAME)
                    tv_restname?.text = RestaurantName
                    Log.d(TAG, "$RestaurantName")
                } catch (e: JSONException) {
                    e.printStackTrace();
                    Log.d(TAG, e.toString())
                }
            }
            val lr = listmenu!!.groupingBy { it }.eachCount().filter { it.value > 1 }
            val mlistmenu :ArrayList<CartItem> = ArrayList()
            for (entry in lr) {
                Log.d(TAG, "onItemInCart: ${entry.key} ${entry.value}")
                mlistmenu.add(CartItem(entry.key,entry.value.toString(),entry.value.toString()))
            }
            setUpRecyclerViewCart(mlistmenu)
//            val sb = StringBuilder()
//            for (i in 0 until listmenu!!.size) {
//                sb.append(listmenu!![i]).append(",")
//
//                for (j in 0 until pricelist.size) {
//                    var values: JSONObject? = pricelist[j] as JSONObject?
//                    try {
//                        if(listmenu!![i] == values!!.getString(DatabaseHelper.MENU_NAME)){
//                            price += values.getInt(DatabaseHelper.MENU_PRICE)
//                            Log.d(TAG, listmenu!![i])
//                        }
//                    } catch (e: JSONException) {
//                        e.printStackTrace();
//                        Log.d(TAG, e.toString())
//                    }
//                }
//
//            }


        }



    }


    private inner class loadRestautantTask : AsyncTask<Void, Void, Void>() {

        override fun onPreExecute() {
            super.onPreExecute()
            dbRestaurantManager!!.open()

            dataDB = dbRestaurantManager!!.getDataRESTAURANT()!!
            restnameList = ArrayList<RestNameItem>()
        }

        override fun doInBackground(vararg params: Void?): Void? {
            for (i in 0 until dataDB.size) {
                Log.d(TAG, dataDB[i].toString())
                var values: JSONObject? = JSONObject()
                values = dataDB[i] as JSONObject?
                try {
                    val Restaurant_ID = values!!.getInt(DatabaseHelper.RESTAURANT_ID)
                    val RestaurantName = values!!.getString(DatabaseHelper.RESTAURANT_NAME)
                    val RestaurantDate = values!!.getString(DatabaseHelper.RESTAURANT_DATE)
                    Log.d(TAG, "$RestaurantName $RestaurantDate")
//                    (restnameList as ArrayList<RestNameItem>).add(
//                        RestNameItem(
//                            Restaurant_ID,
//                            RestaurantName
//                        )
//                    )
                } catch (e: JSONException) {
                    e.printStackTrace();
                    Log.d(TAG, e.toString())
                }
            }

            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            dbRestaurantManager?.close()
//            setUpRecyclerViewRestname(restnameList as ArrayList<RestNameItem>)
        }

    }
    private fun setUpRecyclerViewCart(arrayList: ArrayList<CartItem>) {
        runOnUiThread(Runnable {
            rvCart = findViewById<RecyclerView>(R.id.rvCart)

            val layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            rvCart!!.setHasFixedSize(true)
            rvCart!!.setLayoutManager(layoutManager)
            cartAdapter = CartAdapter(this, arrayList, object : OnclickItem {
                override fun onItemClick(restaurantId: Int?) {
                    super.onItemClick(restaurantId)
                    Log.d("onItemClick", "onItemClick: ")
                    if (restaurantId != null) {
                        Log.d("onItemClick", "$restaurantId")
                    }
                }
            })
            rvCart!!.setAdapter(cartAdapter)
        })
    }

}