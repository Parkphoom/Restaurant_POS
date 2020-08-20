package com.example.restaurantpos

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantpos.Adapter.EditRestnameAdapter
import com.example.restaurantpos.Adapter.RestNameItem
import com.example.restaurantpos.DB.DBRestaurantManager
import com.example.restaurantpos.DB.DatabaseHelper
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList

class AddActivity : AppCompatActivity(), View.OnClickListener {

    private var dbRestaurantManager: DBRestaurantManager? = null
    private lateinit var editRestnameAdapter: EditRestnameAdapter
    var rvRestName: RecyclerView? = null
    private var restnameList: List<RestNameItem>? = null
    lateinit var dataDB: MutableList<*>

    private var addbtn :Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        initView()


    }

    private fun initView() {
        dbRestaurantManager = DBRestaurantManager(this)


        addbtn = findViewById(R.id.add_btn)
        addbtn!!.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        loadRestautantTask().execute()
    }


    private fun setUpRecyclerViewRestname(restnameList: ArrayList<RestNameItem>) {
        runOnUiThread(Runnable {
            rvRestName = findViewById<RecyclerView>(R.id.rcNameedit)

            val layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            rvRestName!!.setHasFixedSize(true)
            rvRestName!!.setLayoutManager(layoutManager)
            editRestnameAdapter = EditRestnameAdapter(this, restnameList)
            rvRestName!!.setAdapter(editRestnameAdapter)
        })
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.add_btn ->{
                startActivity(
                    Intent(this, EditRestActivity::class.java)
                    .putExtra(resources.getString(R.string.headeredit),""))
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
                    Log.d("dataDB","$RestaurantName $RestaurantDate")
                    (restnameList as ArrayList<RestNameItem>).add(RestNameItem(Restaurant_ID,RestaurantName))
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
            dbRestaurantManager?.close()
            setUpRecyclerViewRestname(restnameList as ArrayList<RestNameItem>)
        }

    }
}