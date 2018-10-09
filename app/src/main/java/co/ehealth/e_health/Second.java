package co.ehealth.e_health;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class Second extends Fragment {

    public Second() {
        // Required empty public constructor
    }

    private RecyclerView eRecords;
    private DatabaseReference eDatabase, eUsers, eDatabaseCheck;
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
        eDatabase = FirebaseDatabase.getInstance().getReference().child("Records").child(userId);
        eDatabaseCheck = FirebaseDatabase.getInstance().getReference().child("Records");

        recordsView = inflater.inflate(R.layout.fragment_second, container, false);

        eRecords = (RecyclerView) recordsView.findViewById(R.id.recordList);
        eRecords.setHasFixedSize(true);
        eRecords.setLayoutManager(new LinearLayoutManager(getContext()));

        visibilityText = (TextView) recordsView.findViewById(R.id.no_records);

        eUsers.keepSynced(true);
        eDatabase.keepSynced(true);
        eDatabaseCheck.keepSynced(true);
        eUsers.keepSynced(true);

        eDatabaseCheck.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(userId)) {

                    visibilityText.setVisibility(View.GONE);

                }else{

                    visibilityText.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return recordsView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Medical> options =
                new FirebaseRecyclerOptions.Builder<Medical>()
                        .setQuery(eDatabase, Medical.class)
                        .build();

        FirebaseRecyclerAdapter<Medical, MedicalViewHolder> adapter =
                new FirebaseRecyclerAdapter<Medical, MedicalViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final MedicalViewHolder holder, int position, @NonNull final Medical model) {

                        final String listID = getRef(position).getKey();

                        Picasso.get().load(model.getImage().toString()).networkPolicy(NetworkPolicy.OFFLINE).into(holder.recordPicture, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {

                                Picasso.get().load(model.getImage().toString()).into(holder.recordPicture);

                            }
                        });

                        holder.recordDate.setText(model.getAdded());
                        holder.recordName.setText("MEDICAL REQUEST");
                        holder.statusText.setText(model.getStatus());

                            holder.statusText.setText(model.getStatus());

                            if(holder.statusText.getText().toString().equals("PENDING")) {
                                holder.statusText.setTextColor(RED);

                                holder.eView.setOnLongClickListener(new View.OnLongClickListener() {
                                    @Override
                                    public boolean onLongClick(View view) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                        builder.setCancelable(true);
                                        builder.setTitle("DELETE THIS REQUEST");
                                        builder.setMessage("Are sure you want to delete this request?");
                                        builder.setPositiveButton("DELETE",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                        eDatabase.child(listID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                StyleableToast.makeText(getContext(), "Successfully Deleted", Toast.LENGTH_LONG, R.style.success).show();

                                                            }
                                                        });
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

                        if(holder.statusText.getText().toString().equals("APPROVED")) {

                            holder.statusText.setText("");
                            holder.statusText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check_black_24dp, 0);
                            holder.eView.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View view) {

                                    StyleableToast.makeText(getContext(), "Already Approved, Cant DELETE", Toast.LENGTH_LONG, R.style.information).show();
                                    return false;

                                }
                            });

                        }

                    }

                    @NonNull
                    @Override
                    public MedicalViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.individual, viewGroup, false);
                        return new MedicalViewHolder(view);

                    }
                };

        eRecords.setAdapter(adapter);
        adapter.startListening();

    }

    public static class MedicalViewHolder extends RecyclerView.ViewHolder {

        View eView;
        TextView recordName, recordDate, recordStatus, statusText;
        ImageView recordPicture;

        public MedicalViewHolder(@NonNull View itemView) {
            super(itemView);

            eView = itemView;

            recordName = (TextView) itemView.findViewById(R.id.record_naming);
            statusText = (TextView) itemView.findViewById(R.id.status_icon_ok);
            recordDate = (TextView) itemView.findViewById(R.id.record_naming_date);
            recordStatus = (TextView) itemView.findViewById(R.id.status);
            recordPicture = (ImageView) itemView.findViewById(R.id.record_picture);
        }
    }

}
