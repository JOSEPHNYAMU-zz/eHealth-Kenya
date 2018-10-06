package co.ehealth.e_health;

import android.content.Context;
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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class Three extends Fragment {

    public Three() {
        // Required empty public constructor
    }

    private RecyclerView eBlogs;
    private DatabaseReference eDatabase, eUsers;
    private View newsView;
    private FirebaseAuth eAuth;
    String userId = null;
    private TextView visibilityText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        eDatabase = FirebaseDatabase.getInstance().getReference().child("Blogs");
        eUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        eAuth = FirebaseAuth.getInstance();
        eUsers.keepSynced(true);
        eDatabase.keepSynced(true);
        eUsers.keepSynced(true);
        userId = eAuth.getCurrentUser().getUid();

        newsView = inflater.inflate(R.layout.fragment_three, container, false);

        eBlogs = (RecyclerView) newsView.findViewById(R.id.blogList);
        eBlogs.setHasFixedSize(true);
        eBlogs.setLayoutManager(new LinearLayoutManager(getContext()));

        visibilityText = (TextView) newsView.findViewById(R.id.no_news);

                return newsView;
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<News> options =
                new FirebaseRecyclerOptions.Builder<News>()
                .setQuery(eDatabase, News.class)
                .build();

        FirebaseRecyclerAdapter<News, NewsViewHolder> adapter =
                new FirebaseRecyclerAdapter<News, NewsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final NewsViewHolder holder, int position, @NonNull News model) {

//                        final String listID = getRef(position).getKey();


                        eDatabase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                if(!dataSnapshot.hasChildren()) {

                                    visibilityText.setVisibility(View.VISIBLE);
                                    eBlogs.setVisibility(View.GONE);

                                } else {

                                    visibilityText.setVisibility(View.GONE);
                                    eBlogs.setVisibility(View.VISIBLE);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                        String imageName = model.getPicture().toString();
                        Picasso.get().load(imageName).into(holder.blogPicture);

                        holder.newsTitle.setText(model.getTitle().toString().toUpperCase());
                        String body = model.getBody().toString().concat("...");
                        holder.newsBody.setText(body);

                    }

                    @NonNull
                    @Override
                    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row, viewGroup, false);
                        return new NewsViewHolder(view);

                    }
                };

       eBlogs.setAdapter(adapter);
       adapter.startListening();

    }


    public static class NewsViewHolder extends RecyclerView.ViewHolder {

        View eView;

        TextView newsTitle, newsBody;
        ImageView blogPicture;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);

            eView = itemView;

            newsTitle = (TextView) itemView.findViewById(R.id.tit);
            newsBody = (TextView) itemView.findViewById(R.id.bod);
            blogPicture = (ImageView) itemView.findViewById(R.id.pic);

        }
    }
}
