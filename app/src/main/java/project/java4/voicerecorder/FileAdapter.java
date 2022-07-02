package project.java4.voicerecorder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileAdapterViewHolder> {

    @NonNull
    @Override
    public FileAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull FileAdapterViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class FileAdapterViewHolder extends RecyclerView.ViewHolder {
        public FileAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
