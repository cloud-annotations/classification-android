package com.example.android.tflitecamerademo;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class ClassificationAdapter extends RecyclerView.Adapter<ClassificationAdapter.ClassificationViewHolder> {
    private List<Classification> mClassifications;

    static class ClassificationViewHolder extends RecyclerView.ViewHolder {
        private TextView mLabel;
        private TextView mScore;
        private ProgressBar mProgressBar;
        private Drawable mRedDrawable;
        private Drawable mYellowDrawable;
        private Drawable mGreenDrawable;

        private ClassificationViewHolder(View v) {
            super(v);
            mLabel = v.findViewById(R.id.label);
            mScore = v.findViewById(R.id.score);
            mProgressBar = v.findViewById(R.id.progress_bar);
            mRedDrawable = ContextCompat.getDrawable(v.getContext(), R.drawable.red_progress);
            mYellowDrawable = ContextCompat.getDrawable(v.getContext(),R.drawable.yellow_progress);
            mGreenDrawable = ContextCompat.getDrawable(v.getContext(),R.drawable.green_progress);
        }
    }

    ClassificationAdapter(List<Classification> classifications) {
        mClassifications = classifications;
    }

    void updateClassifications(List<Classification> classifications) {
        mClassifications = classifications;
        notifyDataSetChanged();
    }

    @Override
    public ClassificationAdapter.ClassificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.classification_item, parent, false);
        return new ClassificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ClassificationViewHolder holder, int position) {
        String label = mClassifications.get(position).label;
        Float score = mClassifications.get(position).score;
        holder.mLabel.setText(label);
        holder.mScore.setText(String.format(Locale.getDefault(), "%1.2f", score));

        if (score <= 0.66666) {
            holder.mProgressBar.setProgressDrawable(holder.mRedDrawable);
        } else if (score <= 0.83333) {
            holder.mProgressBar.setProgressDrawable(holder.mYellowDrawable);
        } else {
            holder.mProgressBar.setProgressDrawable(holder.mGreenDrawable);
        }

        // We need the progress to end up being at least 8dp.
        // 8dp / 107dp * 100 = 7.47663551 ~ 7
        int progress = Math.max(7, (int)(score * 100));
        holder.mProgressBar.setProgress(progress);
    }

    @Override
    public int getItemCount() {
        return mClassifications.size();
    }
}
