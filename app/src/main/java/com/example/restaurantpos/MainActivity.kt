package com.example.restaurantpos

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantpos.Adapter.FoodNameAdapter
import com.example.restaurantpos.Adapter.FoodnameItem
import com.example.restaurantpos.Adapter.RestNameItem
import com.example.restaurantpos.Adapter.RestuarantNameAdapter
import com.example.restaurantpos.DB.DBMenuManager
import com.example.restaurantpos.DB.DBRestaurantManager
import com.example.restaurantpos.DB.DatabaseHelper
import com.example.restaurantpos.DB.OnclickItem
import com.shreyaspatil.MaterialDialog.MaterialDialog
import kotlinx.android.synthetic.*
import org.aviran.cookiebar2.CookieBar
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.Method


class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var TAG = "MainActivitylog"

    private var dbRestaurantManager: DBRestaurantManager? = null
    private var dbMenuManager: DBMenuManager? = null
    private lateinit var restuarantNameAdapter: RestuarantNameAdapter
    private var Mainac: RelativeLayout? = null
    var rvRestName: RecyclerView? = null
    private var restnameList: List<RestNameItem>? = null
    private lateinit var foodNameAdapter: FoodNameAdapter
    var rvFoodName: RecyclerView? = null
    private var foodnameList: List<FoodnameItem>? = null
    private var menu_btn: Button? = null
    private var cartbtn: Button? = null
    lateinit var dataDB: MutableList<*>
    var sharedPreferences: SharedPreferences? = null
    var listmenu: ArrayList<String>? = null
    var tv_menucount: TextView? = null
    var tv_menuprice: TextView? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()

    }

    override fun onResume() {
        super.onResume()
        val relativeParams = Mainac?.layoutParams as FrameLayout.LayoutParams
        relativeParams.setMargins(0, 0, 0, 0) // left, top, right, bottom
        Mainac!!.setLayoutParams(relativeParams)

        loadRestautantTask().execute()
        val iscart = sharedPreferences?.getBoolean(getString(R.string.CartStatus), false)
        if (iscart!!) {
            sharedPreferences?.edit()
                ?.putBoolean(getString(R.string.CartStatus), true)
                ?.apply()

            onItemInCart()
        }
    }

    private fun onItemInCart() {
        var playlists = sharedPreferences?.getString(getString(R.string.Incart_listMenu), "")
        var rest_id = sharedPreferences?.getInt(getString(R.string.Incart_restID), -1)
        lateinit var namelist: MutableList<*>
        if (rest_id != -1) {
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
                Log.d("dataDB", namelist[i].toString())
                var values: JSONObject? = JSONObject()
                values = namelist[i] as JSONObject?
                try {
                    RestaurantName = values!!.getString(DatabaseHelper.RESTAURANT_NAME)
                    Log.d("dataDB", "$RestaurantName")
                } catch (e: JSONException) {
                    e.printStackTrace();
                    Log.d("dataDB", e.toString())
                }
            }

            val sb = StringBuilder()
            for (i in 0 until listmenu!!.size) {
                sb.append(listmenu!![i]).append(",")

                for (j in 0 until pricelist.size) {
                    var values: JSONObject? = pricelist[j] as JSONObject?
                    try {
                        if (listmenu!![i] == values!!.getString(DatabaseHelper.MENU_NAME)) {
                            price += values.getInt(DatabaseHelper.MENU_PRICE)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Log.d("dataDB", e.toString())
                    }
                }

            }
            val c = CookieBar.build(this@MainActivity)
                .setCustomView(R.layout.cart_view)
                .setCustomViewInitializer { view ->


                    val bg = view.findViewById<ImageView>(R.id.bg)
                    tv_menucount = view.findViewById<TextView>(R.id.tv_menucount)
                    tv_menuprice = view.findViewById<TextView>(R.id.tv_menuprice)

                    tv_menucount?.text = "${listmenu!!.size} รายการ"
                    tv_menuprice?.text = price.toString()
                    Log.d("tv_menucount", tv_menucount?.id.toString())



                    val btnListener =
                        View.OnClickListener { view ->

                            if (view == bg) {
                                goToCart()
                            }

                        }

                    bg.setOnClickListener(btnListener)

                }
                .setAction(
                    "Close"
                ) { CookieBar.dismiss(this@MainActivity) }
                .setTitle(RestaurantName)
                .setEnableAutoDismiss(false)
                .setSwipeToDismiss(false)
                .setCookiePosition(Gravity.BOTTOM)
                .show()

            val relativeParams = Mainac?.layoutParams as FrameLayout.LayoutParams
            relativeParams.setMargins(0, 0, 0, 70) // left, top, right, bottom
            Mainac!!.setLayoutParams(relativeParams)

//            val params = Mainac?.layoutParams as RelativeLayout.LayoutParams
//            params.setMargins(getPX(0), getPX(0), getPX(0), getPX(60))
//            Mainac!!.setLayoutParams(params)

        }


    }

    private fun getPX(s: Int): Int {
        var r: Resources = this.resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            s.toFloat(),
            r.displayMetrics
        ).toInt()
    }


    private fun initView() {
        sharedPreferences = getSharedPreferences(getString(R.string.RestaurantPref), MODE_PRIVATE)

        dbRestaurantManager = DBRestaurantManager(this)
        dbMenuManager = DBMenuManager(this)
        Mainac= findViewById(R.id.MainActivity)
        menu_btn = findViewById(R.id.menu_btn)
        menu_btn!!.setOnClickListener(this)
        cartbtn = findViewById(R.id.cart_btn)
        cartbtn!!.setOnClickListener(this)
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
            restuarantNameAdapter =
                RestuarantNameAdapter(this, arrayList, object : OnclickItem {
                    override fun onItemClick(restaurantId: Int?) {
                        super.onItemClick(restaurantId)
                        Log.d("onItemClick", "onItemClick: ")
                        if (restaurantId != null) {
                            Log.d("onItemClick", "$restaurantId")
                            loadMenuTask(restaurantId).execute()
                        }
                    }
                })
            rvRestName!!.setAdapter(restuarantNameAdapter)
        })
    }

    private fun setUpRecyclerViewFoodname(arrayList: ArrayList<FoodnameItem>) {
        runOnUiThread(Runnable {
            rvFoodName = findViewById<RecyclerView>(R.id.rvFoodmenu)


            val layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            rvFoodName!!.setHasFixedSize(true)
            rvFoodName!!.setLayoutManager(layoutManager)
            foodNameAdapter = FoodNameAdapter(this, arrayList, object : OnclickItem {
                override fun onItemClick(
                    restaurantId: Int?,
                    menu_name: String?,
                    menu_price: String?
                ) {
                    super.onItemClick(restaurantId, menu_name, menu_price)
                    lateinit var namelist: MutableList<*>

                    dbRestaurantManager?.open()
                    namelist = dbRestaurantManager?.getRESTAURANT_name(restaurantId!!)!!
                    dbRestaurantManager!!.close()

                    var RestaurantName = ""
                    for (i in 0 until namelist.size) {
                        Log.d("dataDB", namelist[i].toString())
                        var values: JSONObject? = JSONObject()
                        values = namelist[i] as JSONObject?
                        try {
                            RestaurantName = values!!.getString(DatabaseHelper.RESTAURANT_NAME)
                            Log.d("dataDB", "$RestaurantName")
                        } catch (e: JSONException) {
                            e.printStackTrace();
                            Log.d("dataDB", e.toString())
                        }
                    }

                    if (RestaurantName.isNotEmpty()) {
//                        var tv_menucount: TextView? = null
                        if (!sharedPreferences?.getBoolean(
                                getString(R.string.CartStatus),
                                false
                            )!!
                        ) {
                            addNeworder(
                                restaurantId!!,
                                RestaurantName,
                                menu_name.toString(),
                                menu_price
                            )
                        } else {
                            var rest_id =
                                sharedPreferences?.getInt(getString(R.string.Incart_restID), -1)
                            if (rest_id != restaurantId) {
                                createNewCartDialog(
                                    restaurantId!!,
                                    RestaurantName,
                                    menu_name.toString(),
                                    menu_price
                                )
                            } else if (restaurantId != -1 && rest_id == restaurantId) {
                                lateinit var pricelist: MutableList<*>
                                var price = 0
                                dbRestaurantManager?.open()
                                pricelist =
                                    dbRestaurantManager?.getRESTAURANT_menu_price(restaurantId!!)!!
                                dbRestaurantManager!!.close()



                                Log.d("addcart", "yes")
                                Log.d("addcart", menu_name.toString())
                                Log.d("addcart", menu_price.toString())
                                listmenu!!.add(menu_name.toString().trim())
                                val sb = StringBuilder()
                                for (i in 0 until listmenu!!.size) {
                                    sb.append(listmenu!![i]).append(",")

                                    for (j in 0 until pricelist.size) {
                                        var values: JSONObject? = pricelist[j] as JSONObject?
                                        try {
                                            if (listmenu!![i] == values!!.getString(DatabaseHelper.MENU_NAME)) {
                                                price += values.getInt(DatabaseHelper.MENU_PRICE)
                                            }
                                        } catch (e: JSONException) {
                                            e.printStackTrace();
                                            Log.d("dataDB", e.toString())
                                        }
                                    }

                                }
                                Log.d("addcart", listmenu.toString())
                                Log.d("addcart", sb.toString())
                                sharedPreferences?.edit()
                                    ?.putString(getString(R.string.Incart_listMenu), sb.toString())
                                    ?.apply()

                                tv_menucount?.text = "${listmenu!!.size} รายการ"
                                tv_menuprice?.text = price.toString()
                                Log.d("tv_menucount", tv_menucount?.id.toString())
                            }

                        }


                    }

                }
            })
            rvFoodName!!.setAdapter(foodNameAdapter)
        })
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.menu_btn -> {
                showMenu(v)
            }
            R.id.cart_btn -> {
                val iscart = sharedPreferences?.getBoolean(getString(R.string.CartStatus), false)
                if (iscart!!) {
                    goToCart()
                }
            }
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
                Log.d("dataDB", dataDB[i].toString())
                var values: JSONObject? = JSONObject()
                values = dataDB[i] as JSONObject?
                try {
                    val Restaurant_ID = values!!.getInt(DatabaseHelper.RESTAURANT_ID)
                    val RestaurantName = values!!.getString(DatabaseHelper.RESTAURANT_NAME)
                    val RestaurantDate = values!!.getString(DatabaseHelper.RESTAURANT_DATE)
                    Log.d("dataDB", "$RestaurantName $RestaurantDate")
                    (restnameList as ArrayList<RestNameItem>).add(
                        RestNameItem(
                            Restaurant_ID,
                            RestaurantName
                        )
                    )
                } catch (e: JSONException) {
                    e.printStackTrace();
                    Log.d("dataDB", e.toString())
                }
            }

            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            dbRestaurantManager?.close()
            setUpRecyclerViewRestname(restnameList as ArrayList<RestNameItem>)
        }

    }


    inner class loadMenuTask(rest_id: Int) : AsyncTask<Void, Void, Void>() {
        val rest_id = rest_id
        override fun onPreExecute() {
            super.onPreExecute()
            dbMenuManager!!.open()

            dataDB = dbMenuManager!!.getDataMENU()!!
            foodnameList = ArrayList<FoodnameItem>()
        }

        override fun doInBackground(vararg params: Void?): Void? {
            for (i in 0 until dataDB.size) {
                Log.d("dataDB", dataDB[i].toString())
                var values: JSONObject? = JSONObject()
                values = dataDB[i] as JSONObject?
                try {
                    val Restaurant_ID = values!!.getInt(DatabaseHelper.RESTAURANT_ID)
                    val MenuName = values.getString(DatabaseHelper.MENU_NAME)
                    val MenuPrice = values.getString(DatabaseHelper.MENU_PRICE)
                    val MenuDate = values.getString(DatabaseHelper.MENU_DATE)
                    Log.d("dataDB", "$MenuName $Restaurant_ID")
                    if (rest_id == Restaurant_ID) {
                        (foodnameList as ArrayList<FoodnameItem>).add(
                            FoodnameItem(Restaurant_ID, MenuName, MenuPrice)
                        )
                    }

                } catch (e: JSONException) {
                    e.printStackTrace();
                    Log.d("dataDB", e.toString())
                }
            }

            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            dbMenuManager?.close()
            setUpRecyclerViewFoodname(foodnameList as ArrayList<FoodnameItem>)
        }

    }

    override fun onPause() {
        super.onPause()
        CookieBar.dismiss(this@MainActivity)
    }

    private fun createNewCartDialog(
        restaurantId: Int,
        RestaurantName: String,
        menu_name: String,
        menu_price: String?
    ) {
        runOnUiThread(Runnable {
            val mDialog = MaterialDialog.Builder(this)
                .setTitle("เริ่มตะกร้าใหม่?")
                .setMessage("หากคุณเลือกร้านอื่น รายการที่เลือกจากร้านเดิมจะถูกลบออก")
                .setCancelable(true)
                .setPositiveButton(
                    "ตกลง",
                    R.drawable.icons8_shopping_cart_60px_1
                ) { dialogInterface, which ->

                    clearCart()

                    dialogInterface.dismiss()

                    addNeworder(restaurantId!!, RestaurantName, menu_name, menu_price)

                }
                .setNegativeButton(
                    "ยกเลิก",
                    R.drawable.icons8_cancel_52px
                ) { dialogInterface, which -> dialogInterface.dismiss() }
                .build()

            // Show Dialog
            mDialog.show()
        })
    }

    private fun addNeworder(
        restaurantId: Int,
        RestaurantName: String,
        menu_name: String,
        menu_price: String?
    ) {
        Log.d("addcart", "no")
        Log.d("addcart", menu_name.toString())
        Log.d("addcart", menu_price.toString())
        listmenu = ArrayList()
        listmenu!!.add(menu_name.toString().replace(" ", ""))
        val sb = StringBuilder()
        for (i in 0 until listmenu!!.size) {
            sb.append(listmenu!![i].replace(", ", "")).append(",")
        }
        Log.d("addcart", listmenu.toString())
        Log.d("addcart", sb.toString())


        sharedPreferences?.edit()
            ?.putBoolean(getString(R.string.CartStatus), true)
            ?.apply()
        sharedPreferences?.edit()
            ?.putInt(getString(R.string.Incart_restID), restaurantId!!)
            ?.apply()
        sharedPreferences?.edit()
            ?.putString(getString(R.string.Incart_listMenu), sb.toString())
            ?.apply()

        val cookieBar = CookieBar.build(this@MainActivity)
            .setCustomView(R.layout.cart_view)
            .setCustomViewInitializer { view ->
                val bg = view.findViewById<ImageView>(R.id.bg)

                tv_menucount = view.findViewById(R.id.tv_menucount)
                tv_menuprice = view.findViewById(R.id.tv_menuprice)
                val btnListener =
                    View.OnClickListener { view ->

                        if (view == bg) {
                            goToCart()
                        }
                    }
                bg.setOnClickListener(btnListener)

            }
            .setAction(
                "Close"
            ) { CookieBar.dismiss(this@MainActivity) }
            .setTitle(RestaurantName)
            .setEnableAutoDismiss(false)
            .setSwipeToDismiss(false)
            .setCookiePosition(Gravity.BOTTOM)
            .show()

        val relativeParams = Mainac?.layoutParams as FrameLayout.LayoutParams
        relativeParams.setMargins(0, 0, 0, 70) // left, top, right, bottom
        Mainac!!.setLayoutParams(relativeParams)

        tv_menucount?.text = "${listmenu!!.size} รายการ"
        tv_menuprice?.text = menu_price.toString()
        Log.d("tv_menucount", tv_menucount?.id.toString())
    }

    private fun clearCart() {
        sharedPreferences?.edit()
            ?.putBoolean(
                getString(R.string.CartStatus),
                false
            )
            ?.apply()
        sharedPreferences?.edit()
            ?.putInt(getString(R.string.Incart_restID), -1)
            ?.apply()
        sharedPreferences?.edit()
            ?.putString(
                getString(R.string.Incart_listMenu),
                ""
            )
            ?.apply()
        tv_menucount?.clearFindViewByIdCache()
        tv_menuprice?.clearFindViewByIdCache()
        CookieBar.dismiss(this@MainActivity);
    }

    private fun goToCart() {
        startActivity(Intent(this, CartActivity::class.java))
    }
}