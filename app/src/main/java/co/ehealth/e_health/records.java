package co.ehealth.e_health;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.muddzdev.styleabletoast.StyleableToast;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import static android.graphics.Color.BLUE;
import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;

public class records extends Fragment {

    public records() {

    }

    private RecyclerView eRecords;
    private DatabaseReference eDatabase, eUsers;
    private View recordsView;
    private FirebaseAuth eAuth;
    String userId = null;
    private TextView visibilityText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        eUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        eAuth = FirebaseAuth.getInstance();
        userId = eAuth.getCurrentUser().getUid();
        eDatabase = FirebaseDatabase.getInstance().getReference().child("Records");

        recordsView = inflater.inflate(R.layout.fragment_records, container, false);

        eRecords = (RecyclerView) recordsView.findViewById(R.id.recordList);
        eRecords.setHasFixedSize(true);
        eRecords.setLayoutManager(new LinearLayoutManager(getContext()));

        visibilityText = (TextView) recordsView.findViewById(R.id.no_records);

        eUsers.keepSynced(true);
        eDatabase.keepSynced(true);
        eUsers.keepSynced(true);

        return recordsView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Requests> options =
                new FirebaseRecyclerOptions.Builder<Requests>()
                        .setQuery(eUsers, Requests.class)
                        .build();

        FirebaseRecyclerAdapter<Requests, RequestsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Requests, RequestsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final RequestsViewHolder holder, int position, @NonNull final Requests model) {

                        final String listID = getRef(position).getKey();

                        String First = model.getFirstname().toString().toUpperCase();
                        String Last = model.getLastname().toString().toUpperCase();
                        String Name = First.concat(" " + Last);
                        holder.recordName.setText(Name);

                        holder.recordLocation.setText(model.getLocation().toString());
                        Picasso.get().load(model.getImage().toString()).networkPolicy(NetworkPolicy.OFFLINE).into(holder.recordPicture, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {

                                Picasso.get().load(model.getImage().toString()).into(holder.recordPicture);

                            }
                        });
                        holder.recordDate.setText(model.getJoined().toString());

                        eDatabase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {


                                if(!dataSnapshot.hasChild(listID)) {

                                    holder.statusIcons.setVisibility(View.GONE);
                                    holder.recordStatus.setText("NO RECORD FOUND");
                                    holder.recordStatus.setTextColor(RED);
                                    holder.statusText.setVisibility(View.GONE);


                                } else {

                                    eDatabase.child(listID).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                                if(snapshot.hasChild("doctor")) {

                                                    if(snapshot.child("doctor").getChildrenCount() != 0) {

                                                        holder.statusText.setVisibility(View.VISIBLE);
                                                        holder.statusIcons.setVisibility(View.GONE);
                                                        holder.statusText.setTextColor(BLUE);
                                                        holder.statusText.setText("PENDING");

                                                    } else {

                                                        holder.statusIcons.setVisibility(View.VISIBLE);
                                                        holder.statusText.setVisibility(View.GONE);
                                                        holder.statusIcons.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check_black_24dp, 0);
                                                    }


                                                } else {

                                                    holder.statusText.setVisibility(View.VISIBLE);
                                                    holder.statusIcons.setVisibility(View.GONE);
                                                    holder.statusText.setTextColor(BLUE);
                                                    holder.statusText.setText("PENDING");

                                                }

                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                }

                                holder.eView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {


                                        if(dataSnapshot.hasChild(listID)) {

                                            Intent singleUserIntent = new Intent(getContext(), SingleUser.class);
                                            singleUserIntent.putExtra("userID", listID);
                                            startActivity(singleUserIntent );

                                        } else {

                                            StyleableToast.makeText(getContext(), "Sorry, User has no Medical Requests", Toast.LENGTH_LONG, R.style.information).show();
                                        }

                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        eUsers.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if(!dataSnapshot.hasChildren()) {

                                    visibilityText.setVisibility(View.VISIBLE);
                                    eRecords.setVisibility(View.GONE);

                                } else {

                                    visibilityText.setVisibility(View.GONE);
                                    eRecords.setVisibility(View.VISIBLE);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                    @NonNull
                    @Override
                    public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.medical, viewGroup, false);
                        return new RequestsViewHolder(view);

                    }
                };

        eRecords.setAdapter(adapter);
        adapter.startListening();

    }

    public static class RequestsViewHolder extends RecyclerView.ViewHolder {

        View eView;
        TextView recordName, recordLocation, recordDate, recordStatus, statusIcons, statusText;
        CircularImageView recordPicture;

        public RequestsViewHolder(@NonNull View itemView) {
            super(itemView);

            eView = itemView;

            recordName = (TextView) itemView.findViewById(R.id.name);
            statusText = (TextView) itemView.findViewById(R.id.status_text);
            statusIcons = (TextView) itemView.findViewById(R.id.status_icon);
            recordLocation = (TextView) itemView.findViewById(R.id.location);
            recordDate = (TextView) itemView.findViewById(R.id.date);
            recordStatus = (TextView) itemView.findViewById(R.id.status);
            recordPicture = (CircularImageView) itemView.findViewById(R.id.record_picture);
        }
    }
}
