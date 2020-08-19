package com.example.restaurantpos.DB

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.restaurantpos.Adapter.MenuAdapter

/**
 * Created by anupamchugh on 19/10/15.
 */
class DatabaseHelper(context: Context?, tb_name: String) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    private var tb_name: String = tb_name
    override fun onCreate(db: SQLiteDatabase) {
        if (tb_name == TABLE_RESTAURANT) {
            db.execSQL(CREATE_TABLE_RESTAURANT)
        }

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (tb_name == TABLE_RESTAURANT) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESTAURANT)
        }
        onCreate(db)
    }

    companion object {
        // Table RESTAURANT Name
        // Database Information
        const val DB_NAME = "SmartPOS_Restaurant.DB"

        // database version
        const val DB_VERSION = 1

        const val TABLE_RESTAURANT = "RESTAURANT"
        const val TABLE_MENU = "MENU"
        const val TABLE_LOG = "LOG"

        // Table RESTAURANT columns
        const val RESTAURANT_ID = "restaurant_Id"
        const val RESTAURANT_NAME = "restaurantName"
        const val RESTAURANT_DATE = "restaurantDate"

        // Table MENU columns
        const val MENU_ID = "menu_Id"
        const val MENU_NAME = "menu_Name"
        const val MENU_PRICE = "menu_Price"

        // Table LOG columns
        const val LOG_ID = "log_Id"
        const val BILL_ID = "bill_Id"
        const val MENU_COUNT = "menu_Count"
        const val LOG_DATE = "log_Date"


        // Creating table query
        private const val CREATE_TABLE_RESTAURANT = ("create table " + TABLE_RESTAURANT + "("
                + RESTAURANT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + RESTAURANT_NAME + " TEXT ,"
                + RESTAURANT_DATE + " TEXT"
                + ");")
    }
}