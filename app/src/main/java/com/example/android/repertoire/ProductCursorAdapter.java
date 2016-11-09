package com.example.android.repertoire;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import static com.example.android.repertoire.Data.ProductContract.ProductEntry;

/**
 * Created by Student on 05/11/2016.
 */

public class ProductCursorAdapter extends CursorAdapter {


    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);

    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final int itemId = cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry._ID));

        TextView name = (TextView) view.findViewById(R.id.name);
        TextView price = (TextView) view.findViewById(R.id.summary);


        int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);



        String nameString = cursor.getString(nameColumnIndex);
        String priceString = cursor.getString(priceColumnIndex);



        if (TextUtils.isEmpty(priceString)) {

            priceString = context.getString(R.string.unknown_breed);
        }

        name.setText(nameString);
        price.setText(priceString);



    }
}

