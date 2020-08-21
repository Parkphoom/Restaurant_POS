package com.example.restaurantpos

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantpos.Adapter.FoodnameItem
import com.example.restaurantpos.Adapter.MenuAdapter
import com.example.restaurantpos.DB.DBMenuManager
import com.example.restaurantpos.DB.DBRestaurantManager
import com.example.restaurantpos.DB.DatabaseHelper
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.shreyaspatil.MaterialDialog.MaterialDialog
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log


class EditRestActivity : AppCompatActivity(), View.OnClickListener {

    var rvFoodName: RecyclerView? = null
    private var foodnameList: List<FoodnameItem>? = null
    private lateinit var menuAdapter: MenuAdapter
    private var dbRestaurantManager: DBRestaurantManager? = null
    private var dbMenuManager: DBMenuManager? = null

    private var headeredit: TextView? = null
    private var restnameEdt: TextInputEditText? = null
    private var editRestnametextlayout: TextInputLayout? = null
    private var savebtn: Button? = null
    private var deletebtn: Button? = null
    private var addbtn: Button? = null

    var name: String = ""
    var rest_id: Int = -1

    interface OnDelete {

        fun ondeletemenu() {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_rest)

        initView()

    }

    private fun initView() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        dbRestaurantManager = DBRestaurantManager(this)
        dbMenuManager = DBMenuManager(this)

        savebtn = findViewById(R.id.save_btn)
        savebtn!!.setOnClickListener(this)
        deletebtn = findViewById(R.id.delete_btn)
        deletebtn!!.setOnClickListener(this)
        addbtn = findViewById(R.id.add_btn)
        addbtn!!.setOnClickListener(this)

        name = intent.getStringExtra(resources.getString(R.string.headeredit))
        rest_id = intent.getIntExtra(getString(R.string.rest_id), -1)

        Log.d("rest_id", "initView:$rest_id ")
        headeredit = findViewById(R.id.headeredit)
        restnameEdt = findViewById(R.id.editRestname)
        editRestnametextlayout = findViewById(R.id.editRestname_text_layout)
        if (!name.isEmpty()) {
            headeredit!!.text = name
            restnameEdt!!.setText(name)

        } else {
            headeredit!!.text = "เพิ่มร้านใหม่"
            deletebtn!!.visibility = View.GONE
        }

    }

    override fun onResume() {
        super.onResume()
        Log.d("TAG", "onResume: ")
        if (rest_id != -1) {
            loadMenuTask().execute()
        }
    }

    private fun setUpRecyclerViewFoodname(arrayList: ArrayList<FoodnameItem>) {
        runOnUiThread(Runnable {
            rvFoodName = findViewById<RecyclerView>(R.id.rvMenu)

            val layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            rvFoodName!!.setHasFixedSize(true)
            rvFoodName!!.setLayoutManager(layoutManager)
            menuAdapter = MenuAdapter(this, arrayList,dbMenuManager,object : OnDelete{
                override fun ondeletemenu() {
                    super.ondeletemenu()
                    loadMenuTask().execute()
                }
            })
            rvFoodName!!.setAdapter(menuAdapter)
        })
    }

    @SuppressLint("SimpleDateFormat")
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.save_btn -> {
                if (restnameEdt!!.text.toString().isNullOrEmpty()) {
                    editRestnametextlayout?.error = "จำเป็นต้องกรอก"
                } else if (name != restnameEdt!!.text.toString() && name.isNotEmpty()) {
                    updateRestautantTask(name, restnameEdt!!.text.toString()).execute()

                } else if (name.isEmpty() && restnameEdt!!.text.toString().isNotEmpty()) {
                    insertRestautantTask(restnameEdt!!.text.toString()).execute()
                }
            }

            R.id.delete_btn -> {
                if (name.isEmpty()) {
                    editRestnametextlayout?.error = "จำเป็นต้องกรอก"
                } else {
                    createDeleteDialog(name)

                }
            }
            R.id.add_btn -> {
                if (rest_id != -1) {

                    startActivity(
                        Intent(this, AddMenuActivity::class.java)
                        .putExtra(resources.getString(R.string.headeredit),name)
                        .putExtra(resources.getString(R.string.rest_id),rest_id))


                }

            }
        }
    }


    private fun createDeleteDialog(name: String) {
        runOnUiThread(Runnable {
            val mDialog = MaterialDialog.Builder(this)
                .setTitle("ลบร้านค้า?")
                .setMessage("ข้อมูลร้านค้า และรายการอาหารทั้งหมดจะหายไป")
                .setCancelable(true)
                .setPositiveButton(
                    "Delete",
                    R.drawable.icons8_delete_bin_48px_white
                ) { dialogInterface, which ->
                    dbRestaurantManager?.open()
                    dbRestaurantManager?.deleteRESTAURANT(name)
                    dbRestaurantManager?.close()

                    Toast.makeText(this, getString(R.string.DeleteSuccess), Toast.LENGTH_SHORT)
                        .show()
                    dialogInterface.dismiss()
                    finish()


                }
                .setNegativeButton(
                    "Cancel",
                    R.drawable.icons8_cancel_52px
                ) { dialogInterface, which -> dialogInterface.dismiss() }
                .build()

            // Show Dialog
            mDialog.show()
        })
    }

    lateinit var dataDB: MutableList<*>

    private inner class updateRestautantTask(oldname: String, newname: String) :
        AsyncTask<Void, Void, Void>() {
        val oldname = oldname
        val newname = newname
        var isfound = false

        override fun onPreExecute() {
            super.onPreExecute()
            dbRestaurantManager!!.open()

            dataDB = dbRestaurantManager!!.getDataRESTAURANT()!!
        }

        override fun doInBackground(vararg params: Void?): Void? {
            for (i in 0 until dataDB.size) {
                Log.d("dataDB", dataDB[i].toString())
                var values: JSONObject? = JSONObject()
                values = dataDB[i] as JSONObject?
                try {
                    val RestaurantName = values!!.getString("restaurantName")
                    if (newname == RestaurantName) {
                        isfound = true
                    }
//                    val RestaurantDate = values!!.getString("restaurantDate")
                    Log.d("dataDB", "$RestaurantName")
                } catch (e: JSONException) {
                    e.printStackTrace();
                    Log.d("dataDB", e.toString())
                }
            }

            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)

            if (isfound) {
                Toast.makeText(
                    this@EditRestActivity,
                    getString(R.string.NameMatch_Error),
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else if (dbRestaurantManager?.updateRESTAURANT(oldname, newname)!! && !isfound) {
                Toast.makeText(
                    this@EditRestActivity,
                    getString(R.string.SaveSuccess),
                    Toast.LENGTH_SHORT
                )
                    .show()
                finish()
            } else {
                Toast.makeText(
                    this@EditRestActivity,
                    getString(R.string.SaveFailed),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

            dbRestaurantManager?.close()
        }
    }

    private inner class insertRestautantTask(newname: String) : AsyncTask<Void, Void, Void>() {
        val newname = newname
        var isfound = false

        override fun onPreExecute() {
            super.onPreExecute()
            dbRestaurantManager!!.open()

            dataDB = dbRestaurantManager!!.getDataRESTAURANT()!!
        }

        override fun doInBackground(vararg params: Void?): Void? {
            for (i in 0 until dataDB.size) {
                Log.d("dataDB", dataDB[i].toString())
                var values: JSONObject? = JSONObject()
                values = dataDB[i] as JSONObject?
                try {
                    val RestaurantName = values!!.getString("restaurantName")
                    if (newname == RestaurantName) {
                        isfound = true
                    }
//                    val RestaurantDate = values!!.getString("restaurantDate")
                    Log.d("dataDB", "$RestaurantName")
                } catch (e: JSONException) {
                    e.printStackTrace();
                    Log.d("dataDB", e.toString())
                }
            }

            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            val dateTime = SimpleDateFormat("yyyy/MM/dd HH:mm")
            val dateTimeNow = dateTime.format(Date())

            if (isfound) {
                Toast.makeText(
                    this@EditRestActivity,
                    getString(R.string.NameMatch_Error),
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else if (dbRestaurantManager?.insertRESTAURANT(
                    restnameEdt!!.text.toString(),
                    dateTimeNow
                )!! && !isfound
            ) {
                Toast.makeText(
                    this@EditRestActivity,
                    getString(R.string.SaveSuccess),
                    Toast.LENGTH_SHORT
                )
                    .show()
                finish()
            } else {
                Toast.makeText(
                    this@EditRestActivity,
                    getString(R.string.SaveFailed),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

            dbRestaurantManager?.close()
        }
    }

    private inner class loadMenuTask : AsyncTask<Void, Void, Void>() {

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
                    val MenuName = values!!.getString(DatabaseHelper.MENU_NAME)
                    val MenuPrice = values.getString(DatabaseHelper.MENU_PRICE)
                    val MenuDate = values!!.getString(DatabaseHelper.MENU_DATE)
                    Log.d("dataDB", "$MenuName $Restaurant_ID")
                    if (rest_id == Restaurant_ID) {
                        (foodnameList as ArrayList<FoodnameItem>).add(
                            FoodnameItem(
                                Restaurant_ID,
                                MenuName,
                                MenuPrice
                            )
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


}