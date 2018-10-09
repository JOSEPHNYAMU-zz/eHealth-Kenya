package co.ehealth.e_health;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.muddzdev.styleabletoast.StyleableToast;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.List;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class Admin extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private FloatingActionButton fab;
    Dialog newsEdit;
    private Uri eImageUri = null;
    private static final int GALLERY_REQUEST = 1;
    private ImageView newImage;
    private AutoCompleteTextView Title, Body;
    private CircularProgressButton saveNews;
    private StorageReference eStorage;
    DatabaseReference eDatabase = FirebaseDatabase.getInstance()
            .getReference().child("Blogs");
    private FirebaseAuth eAuth;
    private FirebaseAuth.AuthStateListener eAuthListener;
    String userId = null;
    Dialog contactUs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        newsEdit = new Dialog(this);
        newsEdit.setContentView(R.layout.newsedit);
        newImage = (ImageView) newsEdit.findViewById(R.id.blogImage);
        Title = (AutoCompleteTextView) newsEdit.findViewById(R.id.title_name);
        Body = (AutoCompleteTextView) newsEdit.findViewById(R.id.body);
        saveNews = (CircularProgressButton) newsEdit.findViewById(R.id.addNews);
        eStorage = FirebaseStorage.getInstance().getReference().child("Blogs");
        eDatabase.keepSynced(true);
        eAuth = FirebaseAuth.getInstance();
        userId = eAuth.getCurrentUser().getUid();
        contactUs = new Dialog(this);
        contactUs.setContentView(R.layout.contactedit);


        eAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() == null) {

                    Intent accountIntent = new Intent(Admin.this, Account.class);
                    accountIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(accountIntent);

                }

            }
        };

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                StyleableToast.makeText(Admin.this, "1", Toast.LENGTH_LONG, R.style.information).show();


            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("ADMIN PANEL USERS");
        }

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            public void onPageSelected(int position) {

                if (position == 0) {

                    getSupportActionBar().setTitle("ADMIN PANEL USERS");

                    fab.setImageDrawable(ContextCompat.getDrawable(Admin.this, R.drawable.ic_bullet));

                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            StyleableToast.makeText(Admin.this, "1", Toast.LENGTH_LONG, R.style.information).show();


                        }
                    });

                }


                if (position == 1) {

                    getSupportActionBar().setTitle("ADMIN PANEL NEWS");

                    fab.setImageDrawable(ContextCompat.getDrawable(Admin.this, R.drawable.ic_add));

                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            showNewsEdit();

                        }
                    });

                }

            }
        });

    }


    private void setupViewPager(ViewPager viewPager) {
        Admin.ViewPagerAdapter adapter = new Admin.ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new records(), "USERS & REQUESTS");
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        return super.onCreateOptionsMenu(menu);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_contact) {

            contactUs.show();

        }

        switch (item.getItemId()) {
            case android.R.id.home:

                Intent accountIntent = new Intent(Admin.this, Home.class);

                accountIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(accountIntent);

                finish();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }


    public void showNewsEdit() {

        TextView newsClose = (TextView) newsEdit.findViewById(R.id.close_news);

        newsEdit.setCanceledOnTouchOutside(false);
        newsEdit.show();

        newsClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                newsEdit.dismiss();

            }
        });

        newImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);

            }
        });

        saveNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startSavingNews();

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {

            eImageUri = data.getData();
            newImage.setImageURI(eImageUri);

            CropImage.activity(eImageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(3, 2)
                    .start(this);
        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK) {

                eImageUri = result.getUri();

                newImage.setImageURI(eImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }

    }


    private void startSavingNews() {

        final String titles = Title.getText().toString().trim();
        final String bodies = Body.getText().toString().trim();

        // Clear Errors
        Title.setError(null);
        Body.setError(null);

        boolean cancel = false;
        View focusView = null;

        if(!TextUtils.isEmpty(titles) && !TextUtils.isEmpty(bodies)) {

            if(eImageUri != null) {

                saveNews.startAnimation();

                final StorageReference filePath = eStorage.child(eImageUri.getLastPathSegment());
                filePath.putFile(eImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                final DatabaseReference news = eDatabase.push();
                                news.child("title").setValue(titles);
                                news.child("body").setValue(bodies);
                                news.child("author").setValue(userId);
                                news.child("picture").setValue(uri.toString());

                                saveNews.revertAnimation();

                                Body.setText("");

                                Title.setText("");

                                newImage.setBackgroundResource(R.drawable.placeholder);

                                StyleableToast.makeText(Admin.this, "News Article Successfully Saved", Toast.LENGTH_LONG, R.style.success).show();

                            }
                        });

                    }
                });

            } else {

                StyleableToast.makeText(Admin.this, "Please upload an Image for the News Item", Toast.LENGTH_LONG, R.style.error).show();

            }


        } else {


            // Validate Title
            if (TextUtils.isEmpty(titles)) {
                Title.setError(getString(R.string.required_title));
                focusView = Title;
                cancel = true;

            }


            // Validate Body
            if (TextUtils.isEmpty(bodies)) {

                Body.setError(getString(R.string.required_body));
                focusView = Body;
                cancel = true;

            }


        }

    }
}
