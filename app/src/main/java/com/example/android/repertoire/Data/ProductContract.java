package com.example.android.repertoire.Data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Student on 05/11/2016.
 */

public final class ProductContract {

    private ProductContract(){}

    public static final String CONTENT_AUTHORITY = "com.example.android.repertoire";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PRODUCT = "product";


    public static final class ProductEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCT);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT;

        public final static String TABLE_NAME = "product";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PRODUCT_NAME = "name";
        public final static String COLUMN_PRODUCT_PRICE = "price";
        public final static String COLUMN_PRODUCT_QUANTITY = "quantity";
        public final static String COLUMN_PRODUCT_SIZE = "size";
        public final static String COLUMN_PRODUCT_IMAGE = "image";

        //Values for size.

        public final static int SIZE_SMALL = 0;
        public final static int SIZE_MEDIUM = 1;
        public final static int SIZE_LARGE = 2;

        public static boolean isValidSize (int size){

            if (size == SIZE_SMALL || size == SIZE_MEDIUM || size == SIZE_LARGE){
                return true;
            }
            return false;
        }

    }
}
