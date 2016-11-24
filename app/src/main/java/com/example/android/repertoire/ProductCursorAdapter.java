package com.example.android.repertoire;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.android.repertoire.Data.ProductContract.ProductEntry;

/**
 * Created by Student on 05/11/2016.
 */

public class ProductCursorAdapter extends CursorAdapter {

    Context mContext;
    Cursor mCursor;

    int newQuantity;
    int newSales;
    int rowsAffected;


    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);

    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);

    }


    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        mContext = context;
        mCursor = cursor;


        TextView nameView = (TextView) view.findViewById(R.id.name);
        TextView priceView = (TextView) view.findViewById(R.id.price);
        final TextView quantityView = (TextView) view.findViewById(R.id.in_stock);
        ImageView imageView = (ImageView) view.findViewById(R.id.image_item);
        final TextView salesView = (TextView) view.findViewById(R.id.sales);
        final TextView sizeView = (TextView) view.findViewById(R.id.size);

        int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
        int sizeColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SIZE);
        int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        int salesColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SALES);
        int imageColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_IMAGE);

        int itemIdIndex = cursor.getColumnIndex(ProductEntry._ID);

        final int itemId = cursor.getInt(itemIdIndex);



        String name = cursor.getString(nameColumnIndex);
        int price = cursor.getInt(priceColumnIndex);
        int size = cursor.getInt(sizeColumnIndex);
        int quantity = cursor.getInt(quantityColumnIndex);
        int sales = cursor.getInt(salesColumnIndex);
        byte[] productImage = cursor.getBlob(imageColumnIndex);
        if (productImage != null) {
            Bitmap bmpProduct = BitmapFactory.decodeByteArray(productImage, 1, productImage.length);
            imageView.setImageBitmap(bmpProduct);
        }


        String spinnerSize;
        switch (size) {
            case 0:

                spinnerSize = "small";
                break;
            case 1:

                spinnerSize = "medium";
                break;
            case 2:

                spinnerSize = "large";
                break;
            default:
                spinnerSize = "invalid";
        }

        Button buyButton = (Button) view.findViewById(R.id.buy_button);
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rowsAffected = productSales(itemId, quantityView, salesView);
                if (rowsAffected != 0) {

                    quantityView.setText(String.valueOf(newQuantity));
                    salesView.setText(String.valueOf(newSales));
                } else {
                    Toast.makeText(mContext, "Update not possible", Toast.LENGTH_SHORT).show();
                }


            }
        });

        nameView.setText(name);
        priceView.setText(String.valueOf(price));
        quantityView.setText(String.valueOf(quantity));
        salesView.setText(String.valueOf(sales));
        sizeView.setText(spinnerSize);

    }

    public  int productSales(int rowId,TextView qTextView, TextView sTextView){
        mCursor.moveToPosition(rowId);
        int oldSales = Integer.parseInt(sTextView.getText().toString());
        int oldQuant = Integer.parseInt(qTextView.getText().toString());

        if (oldQuant > 0){
            newQuantity = oldQuant - 1;
            newSales = oldSales + 1;

            ContentValues values = new ContentValues();
            values.put(ProductEntry.COLUMN_PRODUCT_SALES,String.valueOf(newSales));
            values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY,String.valueOf(newQuantity));
            Uri currentProductUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI,rowId);

            rowsAffected = mContext.getContentResolver().update(currentProductUri,values,null,null);
        }
        return rowsAffected;

    }

}





