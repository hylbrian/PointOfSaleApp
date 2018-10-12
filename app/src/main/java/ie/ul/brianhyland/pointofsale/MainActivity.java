package ie.ul.brianhyland.pointofsale;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {

    private TextView mNameTextView, mQuantityTextView, mDateTextView;
    private Item mCurrentItem;
    private Item mClearedItem;
    private ArrayList<Item> mItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNameTextView = findViewById(R.id.name_text);
        mQuantityTextView = findViewById(R.id.quantity_text);
        mDateTextView = findViewById(R.id.date_text);

        registerForContextMenu(mNameTextView);

        mItems = new ArrayList<>();

        mItems.add(new Item("Example 1",30,new GregorianCalendar()));
        mItems.add(new Item("Example 2",50,new GregorianCalendar()));
        mItems.add(new Item("Example 3",40,new GregorianCalendar()));


        //boilerplate code. Don't mess with it
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               insertItem(false);

            }
        });
    }

    private void insertItem(final boolean isEdit){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //Customize dialog

        //builder.setTitle("My title");
        //builder.setMessage("Hello");
        //builder.setPositiveButton("OK", null);
        builder.setTitle("Add an item");
        View view = getLayoutInflater().inflate(R.layout.dialog_add,null,false);
        builder.setView(view);
        final EditText nameEditText = view.findViewById(R.id.edit_name);
        final EditText quantityEditText = view.findViewById(R.id.edit_quantity);
        final CalendarView deliveryDateView = view.findViewById(R.id.calendar_view);
        final GregorianCalendar calendar = new GregorianCalendar();

        //TODO: Populate the dialog with the values if this is an edit

        if (isEdit) {
            nameEditText.setText(mCurrentItem.getName());
            quantityEditText.setText("" + mCurrentItem.getQuantity());
            deliveryDateView.setDate(mCurrentItem.getDeliveryDateTime());
        }

        deliveryDateView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);
            }
        });



        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //TODO: Edit the mCurrent item instead of making a new one
                String name = nameEditText.getText().toString();
                int quantity = Integer.parseInt(quantityEditText.getText().toString());

                if (isEdit){
                    mCurrentItem.setName(name);
                    mCurrentItem.setQuantity(quantity);
                    mCurrentItem.setDeliveryDate(calendar);
                }else{
                    mCurrentItem = new Item(name, quantity, calendar);

                    mItems.add(mCurrentItem);
                }
                showCurrentItem();
            }
        });
        builder.setNegativeButton(android.R.string.cancel,null);

        builder.create().show();
    }

    private void showCurrentItem() {
        mNameTextView.setText(mCurrentItem.getName());
        mQuantityTextView.setText(getString(R.string.quantity_format,mCurrentItem.getQuantity()));
        mDateTextView.setText(getString(R.string.date_format, mCurrentItem.getDeliveryDateString()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //boilerplate, dont mess with
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_context, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_edit:
                //Toast.makeText(this,"TODO Edit", Toast.LENGTH_SHORT).show();
                insertItem(true);
                return true;

            case R.id.action_remove:
                //Toast.makeText(this,"TODO Remove", Toast.LENGTH_SHORT).show();
                mItems.remove(mCurrentItem);
                mCurrentItem = new Item();
                showCurrentItem();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO: later


        switch (item.getItemId()){

            case R.id.action_reset:
                mClearedItem = mCurrentItem;
                mCurrentItem = new Item();
                showCurrentItem();
                final Snackbar snackbar =Snackbar.make(findViewById(R.id.coordinator_layout),"Item cleared", Snackbar.LENGTH_LONG);
                snackbar.setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCurrentItem = mClearedItem;
                        showCurrentItem();
                        Snackbar.make(findViewById(R.id.coordinator_layout), "Item restored", Snackbar.LENGTH_SHORT).show();
                    }
                });
                    snackbar.show();

                return true;
            case R.id.action_search:
                showSearchDialog();
                return true;
            case R.id.action_settings:
                //startActivity(new Intent(Settings.ACTION_SETTINGS));
                startActivity(new Intent(Settings.ACTION_LOCALE_SETTINGS));
                return true;

            case R.id.action_clear_all:
                clearAllDialog();
                return true;


        }



        return super.onOptionsItemSelected(item);
    }

    private void clearAllDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Remove");

        builder.setMessage("Are you sure you want to remove all items?");

        builder.setPositiveButton(android.R.string.ok, null);{
            mItems.clear();


            }


        builder.setNegativeButton(android.R.string.cancel, null);{

        builder.create().show();}
    }

    private void showSearchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("choose_an_item");
        builder.setItems(getNames(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        mCurrentItem = mItems.get(which);
                        showCurrentItem();

                    }
                });

        builder.setNegativeButton(android.R.string.cancel, null);

                builder.create().show();
    }

    private String[] getNames() {
        String[] names = new String[mItems.size()];
        for (int i = 0;i < mItems.size(); i++){
            names[i] = mItems.get(i).getName();
        }
        return names;
    }
}
