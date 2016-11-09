package com.example.android.repertoire;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.repertoire.Data.ProductContract;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;

import static com.example.android.repertoire.Data.ProductContract.ProductEntry;
import static com.example.android.repertoire.Data.ProductProvider.LOG_TAG;

/**
 * Created by Student on 05/11/2016.
 */

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText mImageEditText;



    private EditText mNameEditText;

    private EditText mPriceEditText;

    private ImageView mPicture;

    private Spinner mSizeSpinner;

    private TextView mQuantityTextView;

    int quantity = 0;

   private ByteArrayOutputStream img;

    /**
     * EditText field to enter the item's quantity
     */
    private EditText mQuantityEditText;

    String imgDecodableString;

    /**
     * Size of the product. The possible values are:
     * 0 for small, 1 for medium, 2 for large.
     */
    private int mSize = ProductEntry.SIZE_SMALL;

    private static final int URL_LOADER = 0;

    private Uri mCurrentProductUri;

    private Uri mCurrentImageUri;

    private boolean mProductChanged = false;

    private static final int PICK_IMAGE_REQUEST = 0;


    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mProductChanged = true;
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        if (mCurrentProductUri == null) {

            setTitle("Add a Product");
            invalidateOptionsMenu();
        } else {

            setTitle(getString(R.string.editor_activity_title_edit_pet));
            getLoaderManager().initLoader(URL_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_price);
        mSizeSpinner = (Spinner) findViewById(R.id.spinner_size);
        mQuantityTextView = (TextView) findViewById(R.id.quantity_text_view);
        mPicture = (ImageView) findViewById(R.id.editor_image);


        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mSizeSpinner.setOnTouchListener(mTouchListener);
        mQuantityTextView.setOnTouchListener(mTouchListener);
        mPicture.setOnTouchListener(mTouchListener);

        setupSpinner();


        Button imageButton = (Button) findViewById(R.id.image_button);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if (Build.VERSION.SDK_INT < 19) {
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                } else {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                }
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        Button orderButton = (Button) findViewById(R.id.order_button);
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Formulate the current record data into an email message to send to supplier
                //including product name, description and the name of the supplier
                //Get the name for use in the subject line and the subject text

                Editable nameEditable = mNameEditText.getText();
                String name = nameEditable.toString();

                //Get the description for use in the body of the email

                Editable descriptionEditable = mPriceEditText.getText();
                String description = descriptionEditable.toString();

                //Get the manufacturer for use in the body of the email

                Editable manufacturerEditable = mQuantityEditText.getText();
                String manufacturer = manufacturerEditable.toString();
                String emailMessage = ("Please order the following product using the standard order amount: \n" +
                        "\n" + "Name: " + name +
                        "\n" + "Price: " + description +
                        "\n" + "Quantity: " + manufacturer);

                //Call the intent that launches the mail client to send the email including the subject and body

                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_SUBJECT,
                        getString(R.string.email_subject, name));
                intent.putExtra(Intent.EXTRA_TEXT, emailMessage);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code READ_REQUEST_CODE.
        // If the request code seen here doesn't match, it's the response to some other intent,
        // and the below code shouldn't run at all.

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.  Pull that uri using "resultData.getData()"

            if (resultData != null) {
                mCurrentImageUri = resultData.getData();
                Log.i(LOG_TAG, "Uri: " + mCurrentImageUri.toString());
                mPicture.setImageBitmap(getBitmapFromUri(mCurrentImageUri));
            }
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) {

        ParcelFileDescriptor parcelFileDescriptor = null;
        try {
            parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to load image.", e);
            return null;
        } finally {
            try {
                if (parcelFileDescriptor != null) {
                    parcelFileDescriptor.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(LOG_TAG, "Error closing ParcelFile Descriptor");
            }
        }
    }

    /**
     * Setup the dropdown spinner that allows the user to select the status of the item.
     */

    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout

        ArrayAdapter statusSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line

        statusSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner

        mSizeSpinner.setAdapter(statusSpinnerAdapter);

        // Set the integer mSelected to the constant values

        mSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mSize = ProductEntry.SIZE_LARGE;
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mSize = ProductEntry.SIZE_MEDIUM;
                    } else {
                        mSize = ProductEntry.SIZE_SMALL;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSize = ProductEntry.SIZE_SMALL;
            }
        });
    }

    /**
     * Get user input from editor and save item into database locally.
     */

    private void saveProduct() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space

        String nameString = mNameEditText.getText().toString().trim();
        String quantityString = mQuantityTextView.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();


        // Check if this is supposed to be a new item
        // and check if all the fields in the editor are blank

        if (mCurrentProductUri == null &&
                TextUtils.isEmpty(nameString) &&
                TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) &&
                mSize == ProductEntry.SIZE_SMALL) {
            // Since no fields were modified, we can return early without creating a new item.
            // No need to create ContentValues and no need to do any ContentProvider operations.

            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and item attributes from the editor are the values.


        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, priceString);
        values.put(ProductEntry.COLUMN_PRODUCT_SIZE, mSize);
        values.put(ProductEntry.COLUMN_PRODUCT_IMAGE,img.toByteArray());

        // If the quantity is not provided by the user, don't try to parse the string into an
        // integer value. Use 0 by default.

        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);

        // If the price is not provided by the user, don't try to parse the string into an
        // integer value. Use 0 by default.

        float price = 0;
        if (!TextUtils.isEmpty(priceString)) {
            price = Float.parseFloat(priceString);
        }
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, price);

        // Determine if this is a new or existing item by checking if mCurrentItemUri is null or not

        if (mCurrentProductUri == null) {
            // This is a NEW item, so insert a new item into the provider,
            // returning the content URI for the new item.

            Uri newUri = getContentResolver().insert(ProductContract.ProductEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.

            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.

                Toast.makeText(this, getString(R.string.editor_insert_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.

                Toast.makeText(this, getString(R.string.editor_insert_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING item, so update the item with content URI: mCurrentItemUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentItemUri will already identify the correct row in the database that
            // we want to modify.

            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.

            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.

                Toast.makeText(this, getString(R.string.editor_update_pet_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.

                Toast.makeText(this, getString(R.string.editor_update_pet_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
    //Set up quantity counter.

    /**
     * this method is called when minus button is clicked.
     */
    public void decrement(View view) {

        if (quantity == 0) {

            Toast.makeText(this, "no items are ordered", Toast.LENGTH_SHORT).show();
            return;
        }
        quantity = quantity - 1;
        displayQuantity(quantity);
    }

    public void increment(View v) {
        if (quantity == 100) {

            Toast.makeText(this, "overload!", Toast.LENGTH_SHORT).show();
            return;
        }
        quantity = quantity + 1;
        displayQuantity(quantity);
    }

    /**
     * This method displays the given quantity value on the screen.
     */
    private void displayQuantity(int quantity) {
        TextView quantityTextView = (TextView) findViewById(R.id.quantity_text_view);
        quantityTextView.setText("" + quantity);
    }

    /**
     * Calculates the price of the order.
     *
     * @return Total price.
     */
    private int calculatePrice() {

        int Baseprice = Integer.parseInt(mPriceEditText.getText().toString());

        return (quantity * Baseprice);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (mCurrentProductUri == null) {

            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                saveProduct();
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                if (!mProductChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
                };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mProductChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                finish();
            }
        };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String projection[] = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_SIZE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_IMAGE};


        return new CursorLoader(
                this,
                mCurrentProductUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {

            return;
        }

        if (cursor.moveToFirst()) {

            byte[] blob = cursor.getBlob(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_IMAGE));
            Bitmap bmp = BitmapFactory.decodeByteArray(blob, 0, blob.length);
            String name = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME));
            int price = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE));
            int quantity = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY));
            int size = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SIZE));

            mNameEditText.setText(name);
            mPriceEditText.setText(Integer.toString(price));
            mQuantityTextView.setText(Integer.toString(quantity));
            mPicture.setImageBitmap(BitmapFactory.decodeFile(String.valueOf(img)));

            if (mImageEditText != null) {
                Uri imageUri = Uri.parse(mImageEditText.getText().toString());
                mPicture.setImageBitmap(getBitmapFromUri(imageUri));
            }

            switch (size) {

                case ProductEntry.SIZE_LARGE:
                    mSizeSpinner.setSelection(1);
                    break;
                case ProductEntry.SIZE_MEDIUM:
                    mSizeSpinner.setSelection(2);
                    break;
                default:
                    mSizeSpinner.setSelection(0);
                    break;

            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityTextView.setInputType(0);
        mSizeSpinner.setSelection(0);


    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }

            }


        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deletePet();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deletePet() {

        if (mCurrentProductUri != null) {

            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);

            if (rowsDeleted == 0) {

                Toast.makeText(this, getString(R.string.editor_delete_pet_failed), Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, getString(R.string.editor_delete_pet_successful), Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
}
