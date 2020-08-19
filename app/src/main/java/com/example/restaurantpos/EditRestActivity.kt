package com.example.restaurantpos

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantpos.Adapter.FoodnameItem
import com.example.restaurantpos.Adapter.MenuAdapter
import com.example.restaurantpos.DB.DBManager
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.shreyaspatil.MaterialDialog.MaterialDialog
import java.text.SimpleDateFormat
import java.util.*

class EditRestActivity : AppCompatActivity(), View.OnClickListener {

    var rvFoodName: RecyclerView? = null
    private var foodnameList: List<FoodnameItem>? = null
    private lateinit var menuAdapter: MenuAdapter
    private var dbManager: DBManager? = null

    private var headeredit: TextView? = null
    private var restnameEdt: TextInputEditText? = null
    private var editRestnametextlayout: TextInputLayout? = null
    private var savebtn: Button? = null
    private var deletebtn: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_rest)

        initView()

        setUpRecyclerViewFoodname()
    }

    private fun initView() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        dbManager = DBManager(this)

        savebtn = findViewById(R.id.save_btn)
        savebtn!!.setOnClickListener(this)
        deletebtn = findViewById(R.id.delete_btn)
        deletebtn!!.setOnClickListener(this)

        val headerstr: String = intent.getStringExtra(resources.getString(R.string.headeredit))
        headeredit = findViewById(R.id.headeredit)
        restnameEdt = findViewById(R.id.editRestname)
        editRestnametextlayout = findViewById(R.id.editRestname_text_layout)
        if (!headerstr.isNullOrEmpty()) {
            headeredit!!.text = headerstr
            restnameEdt!!.setText(headerstr)

        }else{
            headeredit!!.text = "เพิ่มร้านใหม่"
            deletebtn!!.visibility = View.GONE
        }

    }


    private fun setUpRecyclerViewFoodname() {
        runOnUiThread(Runnable {
            rvFoodName = findViewById<RecyclerView>(R.id.rvMenu)

            foodnameList = ArrayList<FoodnameItem>()
            (foodnameList as ArrayList<FoodnameItem>).add(FoodnameItem("เมนู 1"))
            (foodnameList as ArrayList<FoodnameItem>).add(FoodnameItem("เมนู 2"))
            (foodnameList as ArrayList<FoodnameItem>).add(FoodnameItem("เมนู 3"))


            val layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            rvFoodName!!.setHasFixedSize(true)
            rvFoodName!!.setLayoutManager(layoutManager)
            menuAdapter = MenuAdapter(this, foodnameList)
            rvFoodName!!.setAdapter(menuAdapter)
        })
    }

    @SuppressLint("SimpleDateFormat")
    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.save_btn -> {
                if (restnameEdt!!.text.toString().isNullOrEmpty()) {
                    editRestnametextlayout?.error = "จำเป็นต้องกรอก"
                } else {
                    val dateTime = SimpleDateFormat("yyyy/MM/dd HH:mm")
                    val dateTimeNow = dateTime.format(Date())

                    dbManager?.open()
                    dbManager?.insertRESTAURANT(restnameEdt!!.text.toString(), dateTimeNow)
                    dbManager?.close()
                }
            }
            R.id.delete_btn -> {
                createDeleteDialog()
            }
        }
    }

    private fun createDeleteDialog() {
       runOnUiThread(Runnable {
            val mDialog = MaterialDialog.Builder(this)
                .setTitle("ลบร้านค้า?")
                .setMessage("ข้อมูลร้านค้า และรายการอาหารทั้งหมดจะหายไป!!!")
                .setCancelable(true)
                .setPositiveButton(
                    "Delete",
                    R.drawable.icons8_delete_bin_48px_white
                ) { dialogInterface, which ->

                    dialogInterface.dismiss()
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
}