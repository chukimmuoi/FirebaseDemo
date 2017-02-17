package com.example.chukimmuoi.firebasedemo.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chukimmuoi.firebasedemo.R;
import com.example.chukimmuoi.firebasedemo.object.Posts;

/**
 * @author : Hanet Electronics
 * @Skype : chukimmuoi
 * @Mobile : +84 167 367 2505
 * @Email : muoick@hanet.com
 * @Website : http://hanet.com/
 * @Project : FirebaseDemo
 * Created by chukimmuoi on 2/9/17.
 */

public class PostViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = PostViewHolder.class.getSimpleName();

    private TextView titleView;

    private TextView authorView;

    private ImageView starView;

    private TextView numStarsView;

    private TextView bodyView;

    public PostViewHolder(View itemView) {
        super(itemView);

        titleView = (TextView) itemView.findViewById(R.id.post_title);
        authorView = (TextView) itemView.findViewById(R.id.post_author);
        starView = (ImageView) itemView.findViewById(R.id.star);
        numStarsView = (TextView) itemView.findViewById(R.id.post_num_stars);
        bodyView = (TextView) itemView.findViewById(R.id.post_body);
    }

    public void bindToPost(Posts posts, View.OnClickListener starClickListener) {
        titleView.setText(posts.title);
        authorView.setText(posts.author);
        numStarsView.setText(String.valueOf(posts.starCount));
        bodyView.setText(posts.body);

        starView.setOnClickListener(starClickListener);
    }

    public TextView getTitleView() {
        return titleView;
    }

    public void setTitleView(TextView titleView) {
        this.titleView = titleView;
    }

    public TextView getAuthorView() {
        return authorView;
    }

    public void setAuthorView(TextView authorView) {
        this.authorView = authorView;
    }

    public ImageView getStarView() {
        return starView;
    }

    public void setStarView(ImageView starView) {
        this.starView = starView;
    }

    public TextView getNumStarsView() {
        return numStarsView;
    }

    public void setNumStarsView(TextView numStarsView) {
        this.numStarsView = numStarsView;
    }

    public TextView getBodyView() {
        return bodyView;
    }

    public void setBodyView(TextView bodyView) {
        this.bodyView = bodyView;
    }
}
