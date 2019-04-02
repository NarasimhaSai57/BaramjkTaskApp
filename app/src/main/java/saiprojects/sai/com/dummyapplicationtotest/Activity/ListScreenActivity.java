package saiprojects.sai.com.dummyapplicationtotest.Activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

import saiprojects.sai.com.dummyapplicationtotest.Model.Details;
import saiprojects.sai.com.dummyapplicationtotest.R;

public class ListScreenActivity extends AppCompatActivity {

    Button btn_add;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    private FusedLocationProviderClient mFusedLocationClient;
    ArrayList<Details> detailsArrayList = new ArrayList<>();
    RecyclerView rv_list;

    LinearLayout ic_nodata;
    DataBaseHelper myDb;

    LinearLayoutManager listManager;
    DetailsListAdapter detailsListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_screen);

        ic_nodata = findViewById(R.id.ic_nodata);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        myDb = new DataBaseHelper(this);
        rv_list = findViewById(R.id.rv_list);

        btn_add = findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                fetchLocation();

            }
        });

        listManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        loadData();
    }

    private void loadData()
    {
        detailsArrayList.clear();
        Cursor res = myDb.getAllData();
        if (res.getCount() == 0)
        {

            Toast toast = Toast.makeText(getApplicationContext(), "DataBase is Empty", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        while (res.moveToNext())
        {


            Details detailsObj = new Details();
            detailsObj.setIdd(res.getString(0));
            detailsObj.setEnteredAddress(res.getString(1));
            detailsObj.setDefaultAddress(res.getString(2));

            detailsArrayList.add(detailsObj);
            Log.i("detailsArrayList", "detailsArrayList --> " + detailsArrayList.toString());

        }
        if (detailsArrayList != null && detailsArrayList.size() > 0)
        {
            // listManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            rv_list.setLayoutManager(listManager);
            rv_list.setHasFixedSize(true);
            detailsListAdapter = new DetailsListAdapter(detailsArrayList);
            rv_list.setAdapter(detailsListAdapter);
        }

        Log.i("!!!", "detailsArrayList -->" + detailsArrayList);
        Log.i("!!!", "detailsArrayList -->" + detailsArrayList.size());
    }


    public class DetailsListAdapter extends RecyclerView.Adapter<DetailsListAdapter.ViewHolder>{

        ArrayList<Details> detailsArrayList;
        public DetailsListAdapter(ArrayList<Details> detailsArrayList) {

            this.detailsArrayList = detailsArrayList;

            if (detailsArrayList.size() > 0)
            {
                ic_nodata.setVisibility(View.GONE);
                rv_list.setVisibility(View.VISIBLE);
            } else {
                ic_nodata.setVisibility(View.VISIBLE);
                rv_list.setVisibility(View.GONE);
            }
        }

        @NonNull
        @Override
        public DetailsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_data_display, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DetailsListAdapter.ViewHolder holder, final int position)
        {
            holder.rtv_address.setText(detailsArrayList.get(position).enteredAddress);
            holder.rtv_place.setText(detailsArrayList.get(position).defaultAddress);

            holder.ic_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    String address = detailsArrayList.get(position).getEnteredAddress();
                    Integer deletedRows = myDb.deleteData(address);

                    if (deletedRows > 0)
                    {
                        detailsListAdapter.notifyDataSetChanged();
                        Toast.makeText(ListScreenActivity.this, "Data Deleted", Toast.LENGTH_LONG).show();
                        viewDataSeperate();
                    } else
                    {
                        Toast.makeText(ListScreenActivity.this, "Data not Deleted give correct name", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return detailsArrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView rtv_address,rtv_place;
            LinearLayout ic_delete;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                ic_delete = itemView.findViewById(R.id.ic_delete);
                rtv_place = itemView.findViewById(R.id.rtv_place);
                rtv_address = itemView.findViewById(R.id.rtv_address);
            }
        }
    }

    public void viewDataSeperate()
    {
        detailsArrayList.clear();
        Cursor res = myDb.getAllData();
        if (res.getCount() == 0)
        {

            Toast toast = Toast.makeText(getApplicationContext(), "DataBase is Empty", Toast.LENGTH_SHORT);
            toast.show();
            detailsArrayList.clear();
            rv_list.setLayoutManager(listManager);
            rv_list.setHasFixedSize(true);
            detailsListAdapter = new DetailsListAdapter(detailsArrayList);
            rv_list.setAdapter(detailsListAdapter);
            detailsListAdapter.notifyDataSetChanged();
            return;
        }

        while (res.moveToNext()) {


            Details play = new Details();
            play.setIdd(res.getString(0));
            play.setEnteredAddress(res.getString(1));
            play.setDefaultAddress(res.getString(2));


            detailsArrayList.add(play);
            Log.i("players", "players --> " + detailsArrayList.toString());

        }

        if (detailsArrayList != null && detailsArrayList.size() > 0) {
            // listManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

            rv_list.setLayoutManager(listManager);
            rv_list.setHasFixedSize(true);
            detailsListAdapter = new DetailsListAdapter(detailsArrayList);
            rv_list.setAdapter(detailsListAdapter);
            detailsListAdapter.notifyDataSetChanged();

        } else {
            detailsListAdapter.notifyDataSetChanged();
        }

        Log.i("!!!", "Amb -->" + detailsArrayList);
        Log.i("!!!", "Amb -->" + detailsArrayList.size());

    }


    private void fetchLocation()
    {
        if (ContextCompat.checkSelfPermission(ListScreenActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(ListScreenActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                new AlertDialog.Builder(this)
                        .setTitle("Required Location Permission")
                        .setMessage("You have to give this permission to acess this feature")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(ListScreenActivity.this,
                                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(ListScreenActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null)
                            {
                                // Logic to handle location object
                                Double latittude = location.getLatitude();
                                Double longitude = location.getLongitude();

                                Toast toast = Toast.makeText(getApplicationContext(), "Location Axcess allowed", Toast.LENGTH_SHORT);
                                toast.show();

                                Intent i = new Intent(ListScreenActivity.this, AddressEntryActivity.class);
                                startActivity(i);
                            }
                        }
                    });

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //abc

                Intent i = new Intent(ListScreenActivity.this, AddressEntryActivity.class);
                startActivity(i);
            } else {

            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        loadData();

    }
}
