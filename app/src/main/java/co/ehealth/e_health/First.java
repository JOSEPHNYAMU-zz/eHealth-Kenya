package co.ehealth.e_health;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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
import java.util.Calendar;
import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class First extends Fragment {

    public First() {

    }

    private DatabaseReference eDatabaseRecords =
            FirebaseDatabase.getInstance().getReference().child("Records");
    private DatabaseReference eDatabaseLatest =
            FirebaseDatabase.getInstance().getReference().child("Latest");
    private DatabaseReference eDatabaseUsers =
            FirebaseDatabase.getInstance().getReference().child("Users");
    private FirebaseAuth eAuth;
    String userId = null;
    private TextView doctorName, Chemist, Phone, Location, middleOne, middleTwo, lastOne, lastTwo;
    private View detailsView;
    private CircularImageView circularImageView;
    private Dialog reminders;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        detailsView = inflater.inflate(R.layout.fragment_first, container, false);

        eAuth = FirebaseAuth.getInstance();
        eDatabaseLatest.keepSynced(true);
        eDatabaseRecords.keepSynced(true);
        eDatabaseUsers.keepSynced(true);
        circularImageView = (CircularImageView) detailsView.findViewById(R.id.doctor_image);
        userId = eAuth.getCurrentUser().getUid();
        doctorName = (TextView) detailsView.findViewById(R.id.doctor_name);
        Chemist = (TextView) detailsView.findViewById(R.id.chemist);
        Phone = (TextView) detailsView.findViewById(R.id.phone);
        Location = (TextView) detailsView.findViewById(R.id.location);
        middleOne = (TextView) detailsView.findViewById(R.id.middle_one);
        middleTwo = (TextView) detailsView.findViewById(R.id.middle_two);
        lastOne = (TextView) detailsView.findViewById(R.id.last_one);
        lastTwo = (TextView) detailsView.findViewById(R.id.last_two);
        reminders = new Dialog(getContext());
        reminders.setContentView(R.layout.reminder);

        eDatabaseLatest.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child("show").getValue().toString().equals("No")) {

                    eDatabaseUsers.child(userId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                            String First = dataSnapshot.child("Firstname").getValue().toString();
                            String Last = dataSnapshot.child("Lastname").getValue().toString();
                            String Name = First.concat(" " + Last);
                            doctorName.setText(Name);
                            Picasso.get().load(dataSnapshot.child("Image").getValue().toString()).networkPolicy(NetworkPolicy.OFFLINE).into(circularImageView, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(Exception e) {

                                    Picasso.get().load(dataSnapshot.child("Image").getValue().toString()).into(circularImageView);

                                }
                            });

                            middleOne.setText("Age");
                            middleTwo.setText(dataSnapshot.child("Age").getValue().toString());
                            lastOne.setText("Gender");
                            lastTwo.setText(dataSnapshot.child("Gender").getValue().toString());

                            Chemist.setText("Medical Enquiries");
                            Phone.setText(dataSnapshot.child("Phone").getValue().toString());
                            Location.setText(dataSnapshot.child("Location").getValue().toString());

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                } else {

                    eDatabaseUsers.child(dataSnapshot.child("doctor").getValue().toString()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                            String First = dataSnapshot.child("Firstname").getValue().toString();
                            String Last = dataSnapshot.child("Lastname").getValue().toString();
                            String Name = "Dr. " + First.concat(" " + Last);
                            doctorName.setText(Name);
                            Picasso.get().load(dataSnapshot.child("Image").getValue().toString()).networkPolicy(NetworkPolicy.OFFLINE).into(circularImageView, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(Exception e) {

                                    Picasso.get().load(dataSnapshot.child("Image").getValue().toString()).into(circularImageView);

                                }
                            });
                            Chemist.setText(dataSnapshot.child("Chemist").getValue().toString());
                            Phone.setText(dataSnapshot.child("Phone").getValue().toString());
                            Location.setText(dataSnapshot.child("Location").getValue().toString());

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return detailsView;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

}
