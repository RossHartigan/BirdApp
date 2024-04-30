package com.example.bird_app

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.lang.Exception

class SQLLiteHelperHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "bird.db"
        private const val DATABASE_VERSION = 12

        private const val TBL_USER = "tbl_user"
        private const val TBL_SIGHTINGS = "tbl_sightings"
        private const val USERNAME = "username"
        private const val PASSWORD = "password"
        private const val BIRD_NAME = "birdName"
        private const val LAT = "lat"
        private const val LONG = "long"
        private const val BIRD_NUMBER = "birdNumber"
    }
    override fun onCreate(db: SQLiteDatabase) {
        val createTblUser =
            "CREATE TABLE $TBL_USER ($USERNAME TEXT, $PASSWORD TEXT);"
        db.execSQL(createTblUser)

        val createTblSighting =
            "CREATE TABLE $TBL_SIGHTINGS ($BIRD_NAME TEXT, $LAT REAL, $LONG REAL, $BIRD_NUMBER INTEGER PRIMARY KEY AUTOINCREMENT);"
        db.execSQL(createTblSighting)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TBL_USER")
        db!!.execSQL("DROP TABLE IF EXISTS $TBL_SIGHTINGS")
        onCreate(db)
    }

    fun insertUser(std: UserModel): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(USERNAME, std.username)
        contentValues.put(PASSWORD, std.password)

        val success = db.insert(TBL_USER, null, contentValues)
        db.close()
        return success
    }

    fun insertSighting(std: UserModel): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(BIRD_NAME, std.birdName)
        contentValues.put(LAT, std.lat)
        contentValues.put(LONG, std.long)

        val success = db.insert(TBL_SIGHTINGS, null, contentValues)
        db.close()
        return success
    }


    @SuppressLint("Range")
    fun getAllUser(): ArrayList<UserModel>{
        val stdList: ArrayList<UserModel> = ArrayList()
        val selectQuery = "SELECT * FROM $TBL_USER"
        val db = this.readableDatabase

        val cursor: Cursor?

        try{
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: Exception){
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var username: String
        var password: String

        if(cursor.moveToFirst()){
            do{
                username = cursor.getString(cursor.getColumnIndex("username"))
                password = cursor.getString(cursor.getColumnIndex("password"))

                val std = UserModel(username = username, password = password)
                stdList.add(std)


            } while(cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return stdList
    }

    @SuppressLint("Range")
    fun getAllSightings(): ArrayList<UserModel> {
        val stdList: ArrayList<UserModel> = ArrayList()
        val selectQuery = "SELECT * FROM $TBL_SIGHTINGS"
        val db = this.readableDatabase

        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var birdName: String
        var lat: Double
        var long: Double
        var birdNumber: Int

        if (cursor.moveToFirst()) {
            do {
                birdName = cursor.getString(cursor.getColumnIndex(BIRD_NAME))
                lat = cursor.getDouble(cursor.getColumnIndex(LAT))
                long = cursor.getDouble(cursor.getColumnIndex(LONG))
                birdNumber = cursor.getInt(cursor.getColumnIndex(BIRD_NUMBER))

                val std = UserModel(birdName = birdName, lat = lat, long = long, birdNumber = birdNumber)
                stdList.add(std)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return stdList
    }

    fun getSightingsCount(): Int {
        val db = this.readableDatabase
        val countQuery = "SELECT * FROM $TBL_SIGHTINGS"
        val cursor = db.rawQuery(countQuery, null)

        val count = cursor.count
        cursor.close()
        db.close()

        return count
    }
}