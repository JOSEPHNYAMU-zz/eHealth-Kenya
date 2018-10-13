package co.ehealth.e_health;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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

public class SingleUser extends AppCompatActivity {

    DatabaseReference eRecordDatabase;
    DatabaseReference eDatabaseUsers = FirebaseDatabase.getInstance()
            .getReference().child("Users");
    DatabaseReference eDatabaseLatest = FirebaseDatabase.getInstance()
            .getReference().child("Latest");
    private TextView userName, Location, Phone, Age;
    private CircularImageView circularImageView;
    private RecyclerView eRecords;
    private Dialog screenShot;
    private FirebaseAuth eAuth;
    String userId = null;
    String userKey = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userKey =  getIntent().getExtras().getString("userID");
//        userName = (TextView) findViewById(R.id.user_name);
//        circularImageView = (CircularImageView) findViewById(R.id.user_image);
//        Location = (TextView) findViewById(R.id.loc);
//        Phone = (TextView) findViewById(R.id.phone);
//        Age = (TextView) findViewById(R.id.age);
        eRecords = (RecyclerView) findViewById(R.id.requestList);
        eRecords.setHasFixedSize(true);
        eRecords.setLayoutManager(new LinearLayoutManager(this));
        eAuth = FirebaseAuth.getInstance();
        userId = eAuth.getCurrentUser().getUid();

        eRecordDatabase = FirebaseDatabase.getInstance()
                .getReference().child("Records").child(userKey);

        eDatabaseUsers.keepSynced(true);
        eRecordDatabase.keepSynced(true);
        screenShot = new Dialog(this);
        screenShot.setContentView(R.layout.screenshot);

        eDatabaseUsers.child(userKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (getSupportActionBar() != null){
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setDisplayShowHomeEnabled(true);
                    getSupportActionBar().setTitle(dataSnapshot.child("Firstname").getValue().toString());
                }

//                String First = dataSnapshot.child("Firstname").getValue().toString();
//                String Last = dataSnapshot.child("Lastname").getValue().toString();
//                String Name = First.concat(" " + Last);
//                userName.setText(Name);
//                Picasso.get().load(dataSnapshot.child("Image").getValue().toString()).into(circularImageView);
//                Age.setText(dataSnapshot.child("Age").getValue().toString());
//                Phone.setText(dataSnapshot.child("Phone").getValue().toString());
//                Location.setText(dataSnapshot.child("Location").getValue().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //FAB


            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Medical> options =
                new FirebaseRecyclerOptions.Builder<Medical>()
                        .setQuery(eRecordDatabase, Medical.class)
                        .build();

        FirebaseRecyclerAdapter<Medical, MedicalViewHolder> adapter =
                new FirebaseRecyclerAdapter<Medical, MedicalViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final MedicalViewHolder holder, int position, @NonNull final Medical model) {

                        final String listID = getRef(position).getKey();

                        holder.recordPicture.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final ImageView picha = (ImageView) screenShot.findViewById(R.id.pic_record);
                                screenShot.show();
                                Picasso.get().load(model.getImage()).networkPolicy(NetworkPolicy.NO_CACHE).into(picha, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError(Exception e) {

                                        Picasso.get().load(model.getImage()).into(picha);

                                    }
                                });
                            }
                        });


                        holder.eView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if(model.getIcon().toString().equals("No")) {

                                    AlertDialog.Builder builder = new AlertDialog.Builder(SingleUser.this);
                                    builder.setCancelable(true);
                                    builder.setTitle("APPROVE ENQUIRY STATUS");
                                    builder.setMessage("Are sure you want to approve this Patient?");
                                    builder.setPositiveButton("APPROVE",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    final DatabaseReference latestData = eDatabaseLatest.child(userKey);

                                                    eRecordDatabase.child(listID).child("icon").setValue("Yes");
                                                    eRecordDatabase.child(listID).child("doctor").setValue(userId);
                                                    eRecordDatabase.child(listID).child("status").setValue("APPROVED");
                                                    eRecordDatabase.child(listID).child("color").setValue("GREEN");

                                                    latestData.child("doctor").setValue(userId);
                                                    latestData.child("record").setValue(listID);
                                                    latestData.child("show").setValue("Yes");


                                                    StyleableToast.makeText(SingleUser.this, "Approved For Medication", Toast.LENGTH_LONG, R.style.success).show();

                                                }
                                            });
                                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {



                                        }
                                    });

                                    AlertDialog dialog = builder.create();
                                    dialog.show();


                                } else {

                                    StyleableToast.makeText(SingleUser.this, "Approved For Medication", Toast.LENGTH_LONG, R.style.success).show();

                                }
                            }
                        });

                        Picasso.get().load(model.getImage()).networkPolicy(NetworkPolicy.NO_CACHE).into(holder.recordPicture, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {

                                Picasso.get().load(model.getImage()).into(holder.recordPicture);

                            }
                        });
                        holder.recordName.setText("RECORD REQUEST");
                        holder.recordDate.setText(model.getAdded().toString());
                        String iconName = model.getAdded().toString();

                        if(model.getIcon().toString().equals("No")) {

                            holder.recordStatusOk.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_brightness_1_black_24dp, 0);

                        } else {

                            holder.recordStatusOk.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check_black_24dp, 0);

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
        TextView recordName, recordDate, recordStatusOk;
        ImageView recordPicture;

        public MedicalViewHolder(@NonNull View itemView) {
            super(itemView);

            eView = itemView;

            recordName = (TextView) itemView.findViewById(R.id.record_naming);
            recordDate = (TextView) itemView.findViewById(R.id.record_naming_date);
            recordStatusOk = (TextView) itemView.findViewById(R.id.status_icon_ok);
            recordPicture = (ImageView) itemView.findViewById(R.id.record_picture);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (item.getItemId()) {
            case android.R.id.home:

                Intent accountIntent = new Intent(SingleUser.this, Admin.class);

                accountIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(accountIntent);

                finish();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

}
