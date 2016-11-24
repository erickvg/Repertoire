package com.example.android.repertoire;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import static com.example.android.repertoire.Data.ProductContract.ProductEntry;


public class InventoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int URL_LOADER = 1;


    private static ProductCursorAdapter mCursorAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InventoryActivity.this, EditorActivity.class);
                startActivity(intent);

            }
        });

        ListView displayView = (ListView) findViewById(R.id.text_view_pet);

        View emptyView = findViewById(R.id.empty_view);
        displayView.setEmptyView(emptyView);

        mCursorAdapter = new ProductCursorAdapter(this, null);
        displayView.setAdapter(mCursorAdapter);

        displayView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(InventoryActivity.this, EditorActivity.class);

                Uri currentPetUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);

                intent.setData(currentPetUri);

                startActivity(intent);

            }
        });
        getLoaderManager().initLoader(URL_LOADER, null, this);
    }

    private void deleteAllProducts(){

        int petsDeleted = getContentResolver().delete(ProductEntry.CONTENT_URI,null,null);
        Log.v("InventoryActivity",petsDeleted + " pets deleted from database");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_inventroy.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_inventory, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllProducts();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


        @Override
        public Loader<Cursor> onCreateLoader ( int id, Bundle args){
            String projection[] = {
                    ProductEntry._ID,
                    ProductEntry.COLUMN_PRODUCT_NAME,
                    ProductEntry.COLUMN_PRODUCT_PRICE,
                    ProductEntry.COLUMN_PRODUCT_QUANTITY,
                    ProductEntry.COLUMN_PRODUCT_SIZE,
                    ProductEntry.COLUMN_PRODUCT_IMAGE,
                    ProductEntry.COLUMN_PRODUCT_SALES

            };

            switch (id) {
                case URL_LOADER:
                    return new CursorLoader(
                            this,
                            ProductEntry.CONTENT_URI,
                            projection,
                            null,
                            null,
                            null
                    );
                default:
                    return null;
            }

        }

        @Override
        public void onLoadFinished (Loader < Cursor > loader, Cursor cursor){

            mCursorAdapter.swapCursor(cursor);

        }

        @Override
        public void onLoaderReset (Loader < Cursor > loader) {

            mCursorAdapter.swapCursor(null);
        }
    }
