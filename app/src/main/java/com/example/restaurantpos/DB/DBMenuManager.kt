package com.example.restaurantpos.DB

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import org.json.JSONException
import org.json.JSONObject

class DBMenuManager(private val context: Context) {
    private var dbHelper: DatabaseHelper? = null
    private var database: SQLiteDatabase? = null

    @Throws(SQLException::class)
    fun open(): DBMenuManager {
        dbHelper = DatabaseHelper(context, DatabaseHelper.TABLE_MENU)
        database = dbHelper!!.writableDatabase
//        getDBsize();
        return this
    }

    val dBsize: Unit
        get() {
            val f = context.getDatabasePath(DatabaseHelper.DB_NAME)
            val dbSize = f.length()
            Log.d(TAG, "getDBsize: $dbSize")
        }

    fun close() {
        dbHelper!!.close()
    }

    fun getDBsize() {
        val f = context.getDatabasePath(DatabaseHelper.DB_NAME)
        val dbSize = f.length()
        Log.d(TAG, "getDBsize: $dbSize")
    }

    private val maxData: Int
        private get() {
            val countQuery =
                "SELECT count(" + DatabaseHelper.MENU_ID + ") FROM " + DatabaseHelper.TABLE_MENU

//        String selectQuery = "SELECT _ID FROM " + TABLE_NAME + " LIMIT " + limit + " OFFSET " + newoffset;
            database = dbHelper!!.readableDatabase
            //        Cursor cursor = database.rawQuery(selectQuery, new String[]{String.valueOf(k)});
            val countcursor = database!!.rawQuery(countQuery, null)
            Log.d("cursorr", countcursor.toString())
            Log.d("cursorr", countcursor?.count.toString())
            database!!.beginTransaction()
            var maxdata = 0
            try {
                for (i in 0 until countcursor.count) {
                    countcursor.moveToNext()
                    for (j in 0 until countcursor.columnCount) {
//                    Log.d("getcount", countcursor.getColumnName(j));
                        maxdata = countcursor.getInt(j)
                        Log.d("count", "max =$maxdata")
                    }
                }
                database!!.setTransactionSuccessful() // marks a commit
            } finally {
                database!!.endTransaction()
            }
            countcursor.close()
            return maxdata
        }

    fun getDataMENU(): MutableList<Any?>? {
        val selectQuery =
            "SELECT " + DatabaseHelper.MENU_ID + "," + DatabaseHelper.RESTAURANT_ID + "," + DatabaseHelper.MENU_NAME +
                    "," + DatabaseHelper.MENU_PRICE + "," + DatabaseHelper.MENU_DATE + " " +
                    "FROM " + DatabaseHelper.TABLE_MENU
        database = dbHelper!!.readableDatabase
        val cursor = database!!.rawQuery(selectQuery, null)
        //        Cursor cursor = database.query(DATABASE_TABLE, rank, null, null, null, null, yourColumn+" DESC");
        val data: MutableList<Any?> = mutableListOf<Any?>()
        Log.d("cursorr", "getDataMENU")
        Log.d("cursorr", cursor.toString())
        database!!.beginTransaction()
        try {
            for (j in 0 until cursor.count) {
                // execute SQL

                // get the data into array, or class variable
                cursor.moveToNext()
                val obj = JSONObject()
                for (i in 0 until cursor.columnCount) {
                    Log.d("DBBBBB", cursor.getColumnName(i))
                    Log.d("DBBBBB", cursor.getString(i))
                    try {
                        obj.put(cursor.getColumnName(i), cursor.getString(i))
                    } catch (e: JSONException) {
                        Log.d("DBBBBB", e.toString())
                        e.printStackTrace()
                    }
                }
                data.add(obj)

            }
            database!!.setTransactionSuccessful() // marks a commit
        } finally {
            database!!.endTransaction()
        }
        cursor.close()
        return data
    }

    //    fun insertMENU(rest_id: Int, namemenu: String?, price: Int, date: String?): Boolean {
//        val ih = DatabaseUtils.InsertHelper(database, DatabaseHelper.TABLE_MENU)
//        // Get the numeric indexes for each of the columns that we're updating
//
//        val NameMenu = ih.getColumnIndex(DatabaseHelper.MENU_NAME)
//        val PriceMenu = ih.getColumnIndex(DatabaseHelper.MENU_PRICE)
//        val Date = ih.getColumnIndex(DatabaseHelper.MENU_DATE)
//        val Restaurant_ID = ih.getColumnIndex(DatabaseHelper.RESTAURANT_ID)
//        try {
//            // Get the InsertHelper ready to insert a single row
//            ih.prepareForInsert()
//
//            // Add the data for each column
//            ih.bind(NameMenu, namemenu)
//            ih.bind(PriceMenu, price)
//            ih.bind(Date, date)
//            ih.bind(Restaurant_ID, rest_id)
//
//            // Insert the row into the database.
//            ih.execute()
//            Log.d("DBManager", "Success")
//            return true
//        } catch (e: Exception) {
//            Log.d("DBManager", e.toString())
//            return false
//        } finally {
//            ih.close() // See comment below from Stefan Anca
//            Log.d("DBManager", "close")
//        }
//    }
    fun insertMENU(rest_id: Int, namemenu: String, price: Int, date: String): Long? {
        val cv = ContentValues()
        cv.put(DatabaseHelper.RESTAURANT_ID, rest_id)
        cv.put(DatabaseHelper.MENU_NAME, namemenu)
        cv.put(DatabaseHelper.MENU_PRICE, price)
        cv.put(DatabaseHelper.MENU_DATE, date)

        return database!!.insert(DatabaseHelper.TABLE_MENU, null, cv)
    }

    fun fetchMENU(): Cursor? {
        val columns = arrayOf(
            DatabaseHelper.MENU_ID,
            DatabaseHelper.RESTAURANT_ID,
            DatabaseHelper.MENU_NAME,
            DatabaseHelper.MENU_PRICE,
            DatabaseHelper.MENU_DATE
        )
        val cursor =
            database!!.query(DatabaseHelper.TABLE_MENU, columns, null, null, null, null, null)
        cursor?.moveToFirst()
        return cursor
    }


    fun updateMENU(oldQuantity: String?, newQuantity: String?): Boolean {
        val contentValues = ContentValues()
        val whereClause: String = DatabaseHelper.MENU_NAME + "= '" + oldQuantity + "'"


        // if you want to update with respect of quantity too. try this where and whereArgs below

        //final String whereClause = SQLiteCBLC.COL_ORDNAME + " =? AND " + SQLiteCBLC.COL_QUANTITY + " =?";
        //final String[] whereArgs = {
        //orderName, String.valueOf(oldQuantity)
        //};
        contentValues.put(DatabaseHelper.MENU_NAME, newQuantity)
        return database?.update(
            DatabaseHelper.TABLE_MENU, contentValues,
            whereClause, null
        )!! > 0
    }

    fun deleteMENU(name: String) {
        try {
            database?.execSQL("DELETE FROM " + DatabaseHelper.TABLE_MENU + " WHERE " + DatabaseHelper.MENU_NAME + "= '" + name + "'");

        } catch (e: Exception) {
            Log.e(TAG, "deleteMENU: $e")
        }

    }

    companion object {
        private const val TAG = "SQL_error"
    }
}