package com.example.restaurantpos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.annotation.IntegerRes
import com.example.restaurantpos.DB.DBMenuManager
import com.example.restaurantpos.DB.DBRestaurantManager
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_add_menu.*
import java.text.SimpleDateFormat
import java.util.*

class AddMenuActivity : AppCompatActivity(), View.OnClickListener {
    private var dbMenuManager: DBMenuManager? = null
    var name: String = ""
    var rest_id: Int = -1

    var savebtn: Button? = null
    var editMenuname: TextInputEditText? = null
    var editPricename: TextInputEditText? = null
    var editMenunamelayout: TextInputLayout? = null
    var editPricelayout: TextInputLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_menu)
        initView()

    }

    private fun initView() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        dbMenuManager = DBMenuManager(this)

        rest_id = intent.getIntExtra(getString(R.string.rest_id), -1)

        savebtn = findViewById(R.id.save_btn)
        savebtn?.setOnClickListener(this)

        editMenuname = findViewById(R.id.editMenuname)
        editPricename = findViewById(R.id.editPrice)
        editMenunamelayout = findViewById(R.id.editMenuname_text_layout)
        editPricelayout = findViewById(R.id.editPrice_text_layout)
        initEDT(editMenuname,editMenunamelayout)
        initEDT(editPricename,editPricelayout)

    }

    private fun initEDT(editText: TextInputEditText?,textInputLayout: TextInputLayout?){
        editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s != null) {
                    if(s.isNotEmpty()){
                        if (textInputLayout != null) {
                            textInputLayout.isErrorEnabled = false
                        }
                    }
                }
            }
        })
    }

    private fun insertMenu(rest_id: Int, menuname: String, menuprice: Int) {
        val dateTime = SimpleDateFormat("yyyy/MM/dd HH:mm")
        val dateTimeNow = dateTime.format(Date())
        dbMenuManager?.open()

        if (dbMenuManager?.insertMENU(rest_id, menuname, menuprice, dateTimeNow)!! > 0) {
            dbMenuManager!!.close()
            Toast.makeText(this, getString(R.string.SaveSuccess), Toast.LENGTH_SHORT)
                .show()
            finish()
        } else {
            dbMenuManager!!.close()
            Toast.makeText(this, getString(R.string.SaveFailed), Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.save_btn -> {
                if(editMenuname?.text.toString().isEmpty()){
                    editMenunamelayout?.error = getString(R.string.Textempty)
                }
                else if(editPrice?.text.toString().isEmpty()){
                    editPricelayout?.error = getString(R.string.Textempty)
                }else{
                    val price :Int = editPrice?.text.toString().trim().toInt()
                  insertMenu(rest_id, editMenuname?.text.toString().trim(),price)
                }
            }
        }
    }
}