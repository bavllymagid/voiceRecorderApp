package project.java4.voicerecorder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileAdapterViewHolder> {
    private File[] allFiles;
    private OnItemListClicked onItemListClicked;

    public FileAdapter(File[] allFiles , OnItemListClicked onItemListClicked){
        this.allFiles = allFiles;
        this.onItemListClicked = onItemListClicked;
    }

    @NonNull
    @Override
    public FileAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_item , parent, false);
        return new FileAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileAdapterViewHolder holder, int position) {
        holder.fileName.setText(allFiles[position].getName());
        Date d = new Date(allFiles[position].lastModified());
        holder.fileDate.setText(d.toString());
    }

    @Override
    public int getItemCount() {
        return allFiles.length;
    }

    public class FileAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageView list_image;
        TextView fileName;
        TextView fileDate;

        public FileAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            list_image = itemView.findViewById(R.id.imageView);
            fileName = itemView.findViewById(R.id.name);
            fileDate = itemView.findViewById(R.id.date);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemListClicked.OnItemCLicked( allFiles[getAdapterPosition()], getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            onItemListClicked.OnItemLongCLicked(allFiles[getAdapterPosition()] , getAdapterPosition());
            return false;
        }
    }

    public interface OnItemListClicked{
        void OnItemCLicked(File file , int position);
        void OnItemLongCLicked(File file , int position);
    }
}
