package co.ehealth.e_health;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.muddzdev.styleabletoast.StyleableToast;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class Blog extends AppCompatActivity {

    String userKey = null;
    String userId = null;
    private TextView bodyText;
    DatabaseReference eDatabase = FirebaseDatabase.getInstance()
            .getReference().child("Blogs");
    DatabaseReference eDatabaseUsers = FirebaseDatabase.getInstance()
            .getReference().child("Users");
    DatabaseReference eLikes = FirebaseDatabase.getInstance().getReference().child("Likes");
    private FirebaseAuth eAuth;
    ImageView blogPicture;
    private Boolean eLikeProcess = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("MEDICAL BLOG");
        }

        eDatabase.keepSynced(true);
        eDatabaseUsers.keepSynced(true);
        eLikes.keepSynced(true);
        eAuth = FirebaseAuth.getInstance();
        userId = eAuth.getCurrentUser().getUid();
        userKey =  getIntent().getExtras().getString("userID");
        bodyText = (TextView) findViewById(R.id.bodyBlog);
        blogPicture = (ImageView) findViewById(R.id.picSingle);

        eDatabase.child(userKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                bodyText.setText(dataSnapshot.child("body").getValue().toString());

                Picasso.get().load(dataSnapshot.child("picture").getValue().toString()).networkPolicy(NetworkPolicy.OFFLINE).into(blogPicture, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                        Picasso.get().load(dataSnapshot.child("image").getValue().toString()).into(blogPicture);

                    }
                });

                eDatabaseUsers.child(dataSnapshot.child("author").getValue().toString()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                eLikeProcess = true;

                eLikes.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (eLikeProcess) {

                            if (dataSnapshot.child(userKey).hasChild(userId)) {

                                eLikes.child(userKey).child(userId).removeValue();
                                eLikeProcess = false;

                            } else {

                                eLikes.child(userKey).child(userId).setValue(1);
                                eLikeProcess = false;
                                StyleableToast.makeText(Blog.this, "You like this", Toast.LENGTH_LONG, R.style.success).show();


                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });


        eLikes.child(userKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild(userId)) {

                        fab.setImageResource(R.drawable.ic_thumb_up_red);

                    } else {

                        fab.setImageResource(R.drawable.ic_thumb_up_black_24dp);

                    }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (item.getItemId()) {
            case android.R.id.home:

                finish();


            default:
                return super.onOptionsItemSelected(item);
        }

    }

}
