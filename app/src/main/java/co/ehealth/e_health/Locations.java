package co.ehealth.e_health;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.muddzdev.styleabletoast.StyleableToast;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class Locations extends Fragment {

    public Locations() {
        // Required empty public constructor
    }

    private RecyclerView ePlaces;
    private DatabaseReference eDatabase, eUsers;
    private View locationsView;
    private FirebaseAuth eAuth;
    String userId = null;
    private TextView visibilityText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        eDatabase = FirebaseDatabase.getInstance().getReference().child("Places");
        eUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        eAuth = FirebaseAuth.getInstance();
        eUsers.keepSynced(true);
        eDatabase.keepSynced(true);
        eUsers.keepSynced(true);
        userId = eAuth.getCurrentUser().getUid();
        locationsView = inflater.inflate(R.layout.fragment_locations, container, false);

        ePlaces = (RecyclerView) locationsView.findViewById(R.id.locationList);
        ePlaces.setHasFixedSize(true);
        ePlaces.setLayoutManager(new LinearLayoutManager(getContext()));

        visibilityText = (TextView) locationsView.findViewById(R.id.no_locations);

        eDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChildren()) {

                    visibilityText.setVisibility(View.GONE);

                } else {

                    visibilityText.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return locationsView;
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Places> options =
                new FirebaseRecyclerOptions.Builder<Places>()
                        .setQuery(eDatabase, Places.class)
                        .build();


        FirebaseRecyclerAdapter<Places, PlacesViewHolder> adapter =
                new FirebaseRecyclerAdapter<Places, PlacesViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final PlacesViewHolder holder, int position, @NonNull Places model) {

                        final String listID = getRef(position).getKey();

                        holder.recordPhone.setText(model.getPhone());
                        holder.recordAddress.setText(model.getAddress());
                        holder.recordPlace.setText(model.getLocation());

                        eUsers.child(userId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if(dataSnapshot.child("Role").getValue().toString().equals("Super")) {

                                    holder.eView.setOnLongClickListener(new View.OnLongClickListener() {
                                        @Override
                                        public boolean onLongClick(View view) {



                                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                            builder.setCancelable(true);
                                            builder.setTitle("DELETE THIS LOCATION?");
                                            builder.setMessage("Are sure you want to DELETE this Data?");
                                            builder.setPositiveButton("DELETE",
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                            eDatabase.child(listID).removeValue();

                                                            StyleableToast.makeText(getContext(), "Location Successfully Removed!", Toast.LENGTH_LONG, R.style.success).show();

                                                        }
                                                    });
                                            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {



                                                }
                                            });

                                            AlertDialog dialog = builder.create();
                                            dialog.show();



                                            return false;
                                        }
                                    });

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

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
}
