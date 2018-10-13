package co.ehealth.e_health;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.muddzdev.styleabletoast.StyleableToast;

public class Location extends AppCompatActivity {

    DatabaseReference ePlaceDatabase =
            FirebaseDatabase.getInstance().getReference().child("Places");
    private RecyclerView ePlaces;
    private int placesCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ePlaces = (RecyclerView) findViewById(R.id.requestList);
        ePlaces.setHasFixedSize(true);
        ePlaces.setLayoutManager(new LinearLayoutManager(this));
        ePlaceDatabase.keepSynced(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ePlaceDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        placesCount = (int) dataSnapshot.getChildrenCount();
                        StyleableToast.makeText(Location.this, placesCount + " Locations Available", Toast.LENGTH_LONG, R.style.information).show();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("OUR LOCATIONS");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Places> options =
                new FirebaseRecyclerOptions.Builder<Places>()
                        .setQuery(ePlaceDatabase, Places.class)
                        .build();

        FirebaseRecyclerAdapter<Places, PlacesViewHolder> adapter =
                new FirebaseRecyclerAdapter<Places, PlacesViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final PlacesViewHolder holder, int position, @NonNull final Places model) {

                        final String listID = getRef(position).getKey();

                        holder.recordPhone.setText(model.getPhone());
                        holder.recordAddress.setText(model.getAddress());
                        holder.recordPlace.setText(model.getLocation());

                    }

                    @NonNull
                    @Override
                    public PlacesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.location, viewGroup, false);
                        return new PlacesViewHolder(view);

                    }
                };

        ePlaces.setAdapter(adapter);
        adapter.startListening();
    }

    public static class PlacesViewHolder extends RecyclerView.ViewHolder {

        View eView;
        TextView recordPlace, recordAddress, recordPhone;

        public PlacesViewHolder(@NonNull View itemView) {
            super(itemView);

            eView = itemView;

            recordPlace = (TextView) itemView.findViewById(R.id.record_place);
            recordAddress = (TextView) itemView.findViewById(R.id.record_address_location);
            recordPhone = (TextView) itemView.findViewById(R.id.record_phones);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (item.getItemId()) {
            case android.R.id.home:

                finish();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

}
