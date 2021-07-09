package com.example.shalehatbooking.dbHelper;
import com.example.shalehatbooking.dbHelper.Contract.*;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
import com.example.shalehatbooking.model.Favourite;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    private SQLiteDatabase sqLiteDatabase;

    public DatabaseHandler(@Nullable Context context) {
        super(context, "chalet.db", null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteDatabase = sqLiteDatabase;
        sqLiteDatabase.execSQL(FavouriteTable.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(FavouriteTable.DROP_TABLE);

        onCreate(sqLiteDatabase);
    }


    public boolean insertFavourite(Favourite favourite) {
         sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FavouriteTable.COL_NAME_CHALET, favourite.getNameChalet());
        contentValues.put(FavouriteTable.COL_IMG, favourite.getImgChalet());
        contentValues.put(FavouriteTable.COL_ID_CHALET, favourite.getIdChalet());
        contentValues.put(FavouriteTable.COL_ID_USER, favourite.getIdUser());
        contentValues.put(FavouriteTable.COL_PRICE, favourite.getPrice());
        long result = sqLiteDatabase.insert(FavouriteTable.TABLE_NAME, null, contentValues);
        return result > 0;
    }

    public List<Favourite> getAllFavourite(String user_id) {
        sqLiteDatabase = getReadableDatabase();
        List<Favourite> favouriteList = new ArrayList<>();
        String sql = "select * from " + FavouriteTable.TABLE_NAME + " where " + FavouriteTable.COL_ID_USER + " =?";
        Cursor cursor = sqLiteDatabase.rawQuery(sql, new String[]{user_id});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(FavouriteTable.COL_ID));
                String img = cursor.getString(cursor.getColumnIndex(FavouriteTable.COL_IMG));
                String name = cursor.getString(cursor.getColumnIndex(FavouriteTable.COL_NAME_CHALET));
                String idUser = cursor.getString(cursor.getColumnIndex(FavouriteTable.COL_ID_USER));
                String idChalet = cursor.getString(cursor.getColumnIndex(FavouriteTable.COL_ID_CHALET));
                double price = cursor.getDouble(cursor.getColumnIndex(FavouriteTable.COL_PRICE));
                Favourite favourite = new Favourite(id,idChalet,idUser,img, name,price);
                favouriteList.add(favourite);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return favouriteList;
    }



    public boolean deleteFavourite(Favourite favourite) {
        sqLiteDatabase = getWritableDatabase();
        long result = sqLiteDatabase.delete(FavouriteTable.TABLE_NAME, FavouriteTable.COL_ID + "=? and "+FavouriteTable.COL_ID_USER+"=?",
                new String[]{String.valueOf(favourite.getId()),favourite.getIdUser()});
        return result > 0;
    }

}
