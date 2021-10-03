package com.example.feelhome;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //Collection of CountryItem_Object
    private List<CountryItem> countryList;
    private Button btn_restaurant, btn_shop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //CountryItem_Setter and ArrayList for CountryItem_Object
        fillCountryList();

        final AutoCompleteTextView editText = findViewById(R.id.search);
        AutoCompleteCountryAdapter adapter = new AutoCompleteCountryAdapter(this, countryList);
        editText.setAdapter(adapter);

        btn_restaurant = findViewById(R.id.btn_restaurant);
        btn_shop = findViewById(R.id.btn_shop);

        btn_restaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (haveNetwork()) {
                    String country_name = editText.getText().toString();
                    openMapsActivity(country_name, "restaurant");
                } else
                    showAlertDialog(MainActivity.this, "Internet Connection Error", "Please connect to the Internet.");

            }
        });

        btn_shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (haveNetwork()) {
                    String country_name = editText.getText().toString();
                    openMapsActivity(country_name, "grocery_or_supermarket");
                } else
                    showAlertDialog(MainActivity.this, "Internet Connection Error", "Please connect to the Internet.");
            }
        });

    }


    private void fillCountryList() {
        countryList = new ArrayList<>();
        countryList.add(new CountryItem("Bangladeshi", R.mipmap.flag_bangladesh));
        countryList.add(new CountryItem("Chinese", R.mipmap.flag_china));
        countryList.add(new CountryItem("German", R.mipmap.flag_germany));
        countryList.add(new CountryItem("Indian", R.mipmap.flag_india));
        countryList.add(new CountryItem("Italian", R.mipmap.flag_italy));
        countryList.add(new CountryItem("Mexican", R.mipmap.flag_mexico));
        countryList.add(new CountryItem("Pakistani", R.mipmap.flag_pakistan));
        countryList.add(new CountryItem("Turkish", R.mipmap.flag_turkey));
        countryList.add(new CountryItem("Asian", R.mipmap.flag_continent_asia));
        countryList.add(new CountryItem("European", R.mipmap.flag_continent_europe));
    }

    public void openMapsActivity(String country_name, String btn_pressed) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("country_name", country_name);
        intent.putExtra("btn_pressed", btn_pressed);

        startActivity(intent);
    }

    private boolean haveNetwork() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(MainActivity.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public void showAlertDialog(Context context, String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(message).setTitle(title);

        builder.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
