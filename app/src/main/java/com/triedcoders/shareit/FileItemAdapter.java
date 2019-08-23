package com.triedcoders.shareit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;

public class FileItemAdapter extends RecyclerView.Adapter<FileItemAdapter.ViewHolder> implements Filterable {

    private ArrayList<FileItem> items, itemsFiltered;
    Context ctx;

    // View lookup cache
    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView fileName, postRating, postDate, extension, fileSize;
        public ImageView fileTypeImage;
        public ViewHolder(View itemView){
            super(itemView);
            this.fileName = itemView.findViewById(R.id.fileName);
            this.postRating = itemView.findViewById(R.id.postRating);
            this.extension = itemView.findViewById(R.id.extension);
            this.postDate = itemView.findViewById(R.id.postDate);
            this.fileTypeImage = itemView.findViewById(R.id.fileTypeImage);
            this.fileSize = itemView.findViewById(R.id.fileSize);
        }
    }

    public FileItemAdapter(ArrayList<FileItem> items){
        this.items = items;
        this.itemsFiltered = items;
    }

    @Override
    public int getItemCount(){
        return this.itemsFiltered.size();
    }

    public void addFileItem(FileItem item){
        items.add(item);
        itemsFiltered = new ArrayList<>(items);
        notifyDataSetChanged();
    }

    public void sort(Comparator<? super FileItem> c){
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        ctx = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View fileItem = layoutInflater.inflate(R.layout.file_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(fileItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position){
        final FileItem fileItem = itemsFiltered.get(position);
        viewHolder.fileName.setText(fileItem.getFileName());
        viewHolder.postRating.setText(String.format(Locale.ENGLISH, "%d",fileItem.getRating()));
        viewHolder.extension.setText(fileItem.extension());
        viewHolder.postDate.setText(fileItem.posted());
        viewHolder.fileTypeImage.setImageResource(fileItem.getRepresentaion().getIcon());
        viewHolder.fileSize.setText(fileItem.getFormattedFileSize(ctx));
    }

    @Override
    public Filter getFilter(){
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String constraint = charSequence.toString();
                if(constraint.isEmpty()){
                    itemsFiltered = new ArrayList<>(items);
                }else{
                    ArrayList<FileItem> filtered = new ArrayList<>();
                    for (FileItem fileItem: items){
                        if(fileItem.getFileName().toLowerCase().contains(constraint.toLowerCase())){
                            filtered.add(fileItem);
                        }
                    }
                    itemsFiltered = filtered;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = itemsFiltered;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                itemsFiltered = (ArrayList<FileItem>)filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
