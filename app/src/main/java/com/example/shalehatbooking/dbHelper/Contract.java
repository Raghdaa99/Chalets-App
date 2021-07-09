package com.example.shalehatbooking.dbHelper;

import android.provider.BaseColumns;

public class Contract {
    public Contract() {
    }

    public static class FavouriteTable implements BaseColumns {
        public static final String TABLE_NAME = "favourite";
        public static final String COL_ID = "id";
        public static final String COL_ID_CHALET = "idChalet";
        public static final String COL_ID_USER = "idUser";
        public static final String COL_IMG = "imgChalet";
        public static final String COL_NAME_CHALET = "nameChalet";
        public static final String COL_PRICE = "price";
        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT ," +
                COL_ID_CHALET + " TEXT NOT NULL, " +
                COL_ID_USER + " TEXT NOT NULL, " +
                COL_IMG + " TEXT NOT NULL, " +
                COL_NAME_CHALET + " TEXT NOT NULL, " +
                COL_PRICE + " REAL NOT NULL);";
        public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
