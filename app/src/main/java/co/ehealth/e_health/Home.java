package co.ehealth.e_health;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.text.Layout;
import android.text.TextUtils;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.muddzdev.styleabletoast.StyleableToast;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.List;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import io.ghyeok.stickyswitch.widget.StickySwitch;
import studio.carbonylgroup.textfieldboxes.TextFieldBoxes;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth eAuth;
    Dialog profileDialog, statusDialog, newRecord;
    private FirebaseAuth.AuthStateListener eAuthListener;
    DatabaseReference eDatabase = FirebaseDatabase.getInstance()
            .getReference().child("Users");
    public static final int GALLERY_REQUEST = 1;
    public static final int GALLERY_REQUEST_RECORD = 1;
    private CircularImageView circularImageView, userImage;
    private Uri eImageUri = null;
    private Uri eRecordUri = null;
    private StorageReference eImageStore, eRecordStore;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    String userId = null;
    private AutoCompleteTextView StatusEdit;
    private ImageView recordImage;
    DatabaseReference eRecordDatabase = FirebaseDatabase.getInstance()
            .getReference().child("Records");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("eHealth");
        eAuth = FirebaseAuth.getInstance();
        eDatabase.keepSynced(true);
        eRecordDatabase.keepSynced(true);
        profileDialog = new Dialog(this);
        profileDialog.setContentView(R.layout.profile);
        newRecord = new Dialog(this);
        newRecord.setContentView(R.layout.record);
        statusDialog = new Dialog(this);
        statusDialog.setContentView(R.layout.status);
        circularImageView = (CircularImageView) profileDialog.findViewById(R.id.profile_picture);
        eImageStore = FirebaseStorage.getInstance().getReference().child("Profiles");
        eRecordStore = FirebaseStorage.getInstance().getReference().child("Records");
        userId = eAuth.getCurrentUser().getUid();
        StatusEdit = (AutoCompleteTextView) statusDialog.findViewById(R.id.status_text);
        eRecordDatabase.keepSynced(true);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        final TextView Name = (TextView) headerView.findViewById(R.id.user_name);
        final TextView Status = (TextView) headerView.findViewById(R.id.status);
        userImage = (CircularImageView) headerView.findViewById(R.id.user_image);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        eAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() == null) {

                        Intent accountIntent = new Intent(Home.this, Account.class);
                        accountIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(accountIntent);

                }
            }
        };


        eDatabase.child(eAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String fullname = dataSnapshot.child("Firstname").getValue().toString().concat(" " + dataSnapshot.child("Lastname").getValue().toString());
                Status.setText(dataSnapshot.child("Status").getValue().toString());
                Name.setText(fullname);
                StatusEdit.setText(dataSnapshot.child("Status").getValue().toString());
                Picasso.get().load(dataSnapshot.child("Image").getValue().toString()).into(userImage);

                View One, Two;
                One = (View) profileDialog.findViewById(R.id.top_one);
                Two = (View) profileDialog.findViewById(R.id.top_two);

                if(!dataSnapshot.child("Phone").exists()) {

                    Two.setVisibility(View.GONE);

                    showProfile();

                } else {

                    One.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        recordImage = (ImageView) newRecord.findViewById(R.id.recordPicture);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new First(), "HOME");
        adapter.addFragment(new Second(), "RECORDS");
        adapter.addFragment(new Three(), "HEALTH NEWS");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        eAuth.addAuthStateListener(eAuthListener);
        checkUserExist();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);

//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

//        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
//        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

//        MenuItem search = menu.findItem(R.id.action_search);
//        SearchView searchView = (SearchView) search.getActionView();

//        searchView.setQueryHint("SEARCH");
//        // Assumes current activity is the searchable activity
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default


        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {

            final AutoCompleteTextView Phone, Location, Age;
            final Button cancelRegistration;
            Phone = (AutoCompleteTextView) profileDialog.findViewById(R.id.phone);
            Location = (AutoCompleteTextView) profileDialog.findViewById(R.id.location);
            Age = (AutoCompleteTextView) profileDialog.findViewById(R.id.age);

            eDatabase.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Phone.setText(dataSnapshot.child("Phone").getValue().toString());
                    Location.setText(dataSnapshot.child("Location").getValue().toString());
                    Age.setText(dataSnapshot.child("Age").getValue().toString());
                    final CircularImageView circularImageView = (CircularImageView) profileDialog.findViewById(R.id.profile_picture);
                    Picasso.get().load(dataSnapshot.child("Image").getValue().toString()).into(circularImageView);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            final CircularProgressButton updateData = (CircularProgressButton) profileDialog.findViewById(R.id.update_profile);
            updateData.setText("UPDATE PROFILE");

            showProfile();

        }

        if (id == R.id.action_new) {

            newRecord();

        }

        if(item.getItemId() == R.id.action_logout) {

            logout();

        }

        return super.onOptionsItemSelected(item);
    }

    private void newRecord() {


        newRecord.show();

        final AutoCompleteTextView descInput = (AutoCompleteTextView) newRecord.findViewById(R.id.description);
        final CircularProgressButton addRecord = (CircularProgressButton) newRecord.findViewById(R.id.addNewRecord);
        final TextView closeRecord = (TextView) newRecord.findViewById(R.id.close_record);
        final String desc = descInput.getText().toString().trim();

        closeRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                newRecord.dismiss();

            }
        });

        recordImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST_RECORD);
            }
        });


        addRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(eRecordUri != null) {

                    addRecord.startAnimation();

                    final StorageReference filePath = eRecordStore.child(eRecordUri.getLastPathSegment());
                    filePath.putFile(eRecordUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    final DatabaseReference newRec = eRecordDatabase.child(userId).push();
                                    newRec.child("description").setValue(desc);
                                    newRec.child("image").setValue(uri.toString());

                                    addRecord.revertAnimation();

                                    descInput.setText("");

                                    recordImage.setBackgroundResource(R.drawable.placeholder);

                                    StyleableToast.makeText(Home.this, "Record Successfully Submitted", Toast.LENGTH_LONG, R.style.success).show();

                                }
                            });

                        }
                    });


                } else {

                    StyleableToast.makeText(Home.this, "Please Upload Doctors Prescription Screenshot", Toast.LENGTH_LONG, R.style.information).show();

                }


            }
        });

    }

    private void checkUserExist() {

        eDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!dataSnapshot.hasChild(userId)) {

                    logout();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_camera) {

        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_status) {

            showStatus();

        }else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_admin) {


            Intent accountIntent = new Intent(Home.this, Admin.class);

            accountIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(accountIntent);



        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return false;
    }

    public void showProfile() {

        final AutoCompleteTextView Phone, Location, Age;

        final TextView cancelRegistration;

        Phone = (AutoCompleteTextView) profileDialog.findViewById(R.id.phone);
        Location = (AutoCompleteTextView) profileDialog.findViewById(R.id.location);
        Age = (AutoCompleteTextView) profileDialog.findViewById(R.id.age);
        final StickySwitch Gender = (StickySwitch) profileDialog.findViewById(R.id.gender);

        final CircularProgressButton updateData = (CircularProgressButton) profileDialog.findViewById(R.id.update_profile);
        final CircularImageView circularImageView = (CircularImageView) profileDialog.findViewById(R.id.profile_picture);

        circularImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);

                return true;
            }
        });

        circularImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                StyleableToast.makeText(Home.this, "Please Long Press to select Image", Toast.LENGTH_LONG, R.style.information).show();

            }
        });


        cancelRegistration = (TextView) profileDialog.findViewById(R.id.close_profile);

        cancelRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileDialog.dismiss();
            }
        });

        profileDialog.setCanceledOnTouchOutside(false);
        profileDialog.show();

        updateData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Clear Errors
                Phone.setError(null);
                Location.setError(null);
                Age.setError(null);

                boolean cancel = false;
                View focusView = null;

                final String phone = Phone.getText().toString();
                final String location = Location.getText().toString();
                final String age = Age.getText().toString();
                final String gender = Gender.getText().toString();

                if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(location) &&
                        !TextUtils.isEmpty(age)) {

                    if(eImageUri != null) {

                        // Check Phone Length
                        if (isPhoneValid(phone)) {

                            Phone.setError(getString(R.string.phone_length));
                            Phone.requestFocus();
                            cancel = true;

                        } else {

                            updateData.startAnimation();

                            final StorageReference filePath = eImageStore.child(eImageUri.getLastPathSegment());

                            filePath.putFile(eImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            eDatabase.child(userId).child("Phone").setValue(phone);
                                            eDatabase.child(userId).child("Location").setValue(location);
                                            eDatabase.child(userId).child("Age").setValue(age);
                                            eDatabase.child(userId).child("Gender").setValue(gender);
                                            eDatabase.child(userId).child("Image").setValue(uri.toString());

                                            updateData.revertAnimation();
                                            profileDialog.dismiss();
                                            StyleableToast.makeText(Home.this, "Profile completed, Welcome to eHealth", Toast.LENGTH_LONG, R.style.success).show();

                                        }
                                    });

                                }
                            });

                        }

                    } else {

                        StyleableToast.makeText(Home.this, "Please upload profile ic_placeholder_image", Toast.LENGTH_LONG, R.style.error).show();

                    }

                } else {

                    // Validate Phone
                    if (TextUtils.isEmpty(phone)) {

                        Phone.setError(getString(R.string.required_phone));
                        focusView = Phone;
                        cancel = true;

                    }


                    // Validate Location
                    if (TextUtils.isEmpty(location)) {

                        Location.setError(getString(R.string.required_location));
                        focusView = Location;
                        cancel = true;

                    }


                    // Validate Age
                    if (TextUtils.isEmpty(age)) {

                        Age.setError(getString(R.string.required_age));
                        focusView = Age;
                        cancel = true;

                    }

                }

            }
        });

    }

    private boolean isPhoneValid(String password) {
        return password.length() < 10;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }


        if(requestCode == GALLERY_REQUEST_RECORD && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();
        }


        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK) {

                eImageUri = result.getUri();
                eRecordUri = result.getUri();

                recordImage.setImageURI(eRecordUri);

                circularImageView.setImageURI(eImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }
    }


    public void showStatus() {

        statusDialog.setCanceledOnTouchOutside(false);
        statusDialog.show();

        final Button closeStatus;
        closeStatus = (Button) statusDialog.findViewById(R.id.close_status);
        final CircularProgressButton saveStatus = (CircularProgressButton) statusDialog.findViewById(R.id.update_status);

        saveStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String statusInfo = StatusEdit.getText().toString();

                if(!TextUtils.isEmpty(statusInfo)) {

                    saveStatus.startAnimation();

                    eDatabase.child(userId).child("Status").setValue(statusInfo);

                    StyleableToast.makeText(Home.this, "Status Successfully Updated", Toast.LENGTH_LONG, R.style.success).show();

                    saveStatus.revertAnimation();
                    statusDialog.show();


                } else {

                    StyleableToast.makeText(Home.this, "Status is Required", Toast.LENGTH_LONG, R.style.error).show();

                }



            }
        });

        closeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                statusDialog.dismiss();

            }
        });


    }


    private void logout() {

        eAuth.signOut();
        Intent accountIntent = new Intent(Home.this, Account.class);
        accountIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(accountIntent);
    }

}
