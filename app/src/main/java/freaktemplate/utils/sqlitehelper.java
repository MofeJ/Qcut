package freaktemplate.utils;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class sqlitehelper extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "storage.db";
    private static final int DATABASE_VESION = 1;

    public sqlitehelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VESION);
    }
}
