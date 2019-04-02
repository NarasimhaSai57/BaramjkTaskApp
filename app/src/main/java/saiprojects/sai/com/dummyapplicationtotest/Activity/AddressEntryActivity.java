package saiprojects.sai.com.dummyapplicationtotest.Activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;

import saiprojects.sai.com.dummyapplicationtotest.Model.Details;
import saiprojects.sai.com.dummyapplicationtotest.R;

public class AddressEntryActivity extends AppCompatActivity {

    EditText et_address_entry;
    Button btn_currentlocation, btn_ok,btn_bck;
    EditText tv_default_location;

    DataBaseHelper myDb;
    ArrayList<Details> details = new ArrayList<>();


    public static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int variable = 1234;

    Double lat, lan;
    String address;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private boolean mLocationPermissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_entry);


        getLocationPermission();


        myDb = new DataBaseHelper(this);
        tv_default_location = findViewById(R.id.tv_default_location);
        btn_currentlocation = findViewById(R.id.btn_currentlocation);
        btn_ok = findViewById(R.id.btn_ok);
        et_address_entry = findViewById(R.id.et_address_entry);

        btn_bck = findViewById(R.id.btn_bck);
        btn_bck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_currentlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceLocation();
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                String address = et_address_entry.getText().toString();
                String place = tv_default_location.getText().toString();

                if(address!=null && address.equalsIgnoreCase("")){

                    Toast toast = Toast.makeText(getApplicationContext(), "Address can't be empty", Toast.LENGTH_SHORT);
                    toast.show();
                }else if(place!=null && place.equalsIgnoreCase("")){

                    Toast toast = Toast.makeText(getApplicationContext(), "Place can't be empty, Click location button to get Current Place", Toast.LENGTH_SHORT);
                    toast.show();
                }else{

                    Boolean isInserted = myDb.insertData(et_address_entry.getText().toString(),
                            tv_default_location.getText().toString());


                    if (isInserted == true) {
                        et_address_entry.setText("");
                        tv_default_location.setText("");
                        Toast toast = Toast.makeText(getApplicationContext(), "Data inserted Sucessfully", Toast.LENGTH_SHORT);
                        toast.show();
                        //viewDataSeperate();
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(), "Data is not Inserted", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }


            }
        });
    }


    private void getLocationPermission() {
        String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {


                mLocationPermissionGranted = true;
            } else {
                ActivityCompat.requestPermissions(this, permission, variable);
            }


        }
    }

    private void getDeviceLocation() {
        Log.d("@@@@", " getting Device Location");

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(AddressEntryActivity.this);

        try {
            if (mLocationPermissionGranted) {
                Task locations = fusedLocationProviderClient.getLastLocation();


                locations.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {

                        try {
                            if (task.isSuccessful()) {
                                //   Log.d("@@@@", "Location Found");

                                Location currentLocation = (Location) task.getResult();
                                lat = currentLocation.getLatitude();
                                lan = currentLocation.getLongitude();
                                Geocoder gCoder = new Geocoder(AddressEntryActivity.this);
                                ArrayList<Address> addresses = null;
                                try {
                                    addresses = (ArrayList<Address>) gCoder.getFromLocation(lat, lan, 1);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (addresses != null && addresses.size() > 0) {
                                    address = addresses.get(0).getAddressLine(0);
                                    Toast.makeText(AddressEntryActivity.this, "country: " + addresses.get(0).getAddressLine(0), Toast.LENGTH_LONG).show();
                                    tv_default_location.setText(address);
                                }


                            } else {
                                Toast.makeText(AddressEntryActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        } catch (SecurityException s) {

        }
    }
}
