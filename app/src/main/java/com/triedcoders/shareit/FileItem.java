package com.triedcoders.shareit;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.TimeUnit;

class IconMap{
    static int doc = R.drawable.doc;
    static int pdf = R.drawable.pdf;
    static int sheet = R.drawable.spread;
    static int file = R.drawable.unregistered;
    static int ppt = R.drawable.ppt;
    static int image = R.drawable.image;

    public static class TypeRepresentation{
        private int mImageId, mColorId;
        public TypeRepresentation(int imageId, int colorId){
            mImageId = imageId;
            mColorId = colorId;
        }

        public int getIcon(){
            return mImageId;
        }

        public int getColor(){
            return mColorId;
        }
    }

    public static TypeRepresentation getRepresentation(String extension){
        extension = extension.toLowerCase();
        switch (extension){
            case "pdf":
                return new TypeRepresentation(pdf, R.color.pdf_color);
            case "xls": case "xlsx": case "csv":
                return new TypeRepresentation(sheet, R.color.sheet_color);
            case "ppt": case "pptx":
                return new TypeRepresentation(ppt, R.color.ppt_color);
            case "doc": case "docx":
                return new TypeRepresentation(doc, R.color.doc_color);
            case "png": case "jpg": case "jpeg":
                return new TypeRepresentation(image, R.color.image_color);
            default:
                return new TypeRepresentation(file, R.color.file_color);
        }
    }

}

@SuppressWarnings("ALL")
public class FileItem {
    private String fileName="", postedBy="you", path="";
    private int rating=4;
    private long fileSize;
    private Date postDate;
    private Uri fileUri;
    public long id;

    public static final String TABLE_NAME = "files";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PATH = "path";
    public static final String COLUMN_RATING = "rating";
    public static final String COLUMN_SIZE = "size";
    public static final String COLUMN_DATE = "date";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_PATH + " TEXT,"
                    + COLUMN_SIZE + " INTEGER,"
                    + COLUMN_RATING + " INTEGER,"
                    + COLUMN_DATE + " INTEGER"
                    + ")";

    FileItem(long id, String path, int rating, long size, long timeStamp){
        this.setFileName(path);
        this.setRating(rating);
        this.setFileSize(size);
        this.id = id;
        this.setPostDate(timeStamp);
    }

    FileItem(Uri uri, Context ctx){
        this.prepareFromUri(uri, ctx);
        this.setRating(4);
        this.setPostedBy("unknown");
    }

    public boolean isImage(){
        return extension() == "jpg" || extension() == "jpeg" || extension() == "png";
    }

    private void prepareFromUri(Uri uri, Context ctx){
        Cursor cursor = ctx.getContentResolver().query(uri, null, null, null, null, null);
        if(cursor != null && cursor.moveToFirst()){
            try {
                int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                this.setFileName(cursor.getString(index));
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                if(!cursor.isNull(sizeIndex)) {
                    this.setFileSize(Long.valueOf(cursor.getString(sizeIndex)));
                }
            }finally {
                cursor.close();
            }
        }else{
            this.setFileName(uri.getPath());
        }
        this.fileUri = uri;
        this.postDate = new Date();
    }

    public String getFileName() {
        return fileName;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public int getRating() {
        return rating;
    }

    public String getPath(){
        return this.path;
    }

    public IconMap.TypeRepresentation getRepresentaion(){
        return IconMap.getRepresentation(this.extension());
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setFileName(String name) {
        this.path = name;
        if(name.contains("/")){
            String[] split = name.split("/");
            this.fileName = split[split.length - 1];
        }else{
            this.fileName = name;
        }
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setPostDate(long timeStamp) {
        this.postDate = new Date(timeStamp);
    }

    public static String prettyTime(long milliseconds){
        if (milliseconds/1000 < 60){
            return "just now";
        }
        else if (milliseconds/1000 < 3600){
            // time is less than a hour
            return TimeUnit.MILLISECONDS.toMinutes(milliseconds) + " min ago";
        }
        else if (milliseconds/1000 < 86400){
            // time is less than a day
            return TimeUnit.MILLISECONDS.toHours(milliseconds) + "hr ago";
        }
        else if (milliseconds/86400000 < 7){
            // time is less than a week
            return TimeUnit.MILLISECONDS.toDays(milliseconds) + "days ago";
        }
        else if (milliseconds/86400000 < 30){
            // time is within a month
            return Long.valueOf(TimeUnit.MILLISECONDS.toDays(milliseconds)/7) + "weeks ago";
        }
        else if (milliseconds/86400000 < 365){
            // time is within a year
            return Long.valueOf(TimeUnit.MILLISECONDS.toDays(milliseconds)/30) + "months ago";
        }
        else{
            return Long.valueOf(TimeUnit.MILLISECONDS.toDays(milliseconds)/365) + "years ago";
        }
    }

    public String posted(){
        Date now = new Date();
        long diff = now.getTime() - this.postDate.getTime();
        return FileItem.prettyTime(diff);
    }

    public void setFileSize(long size){
        this.fileSize = size;
    }

    public long getFileSize(){
        return this.fileSize;
    }

    public String getFormattedFileSize(Context ctx){
        return android.text.format.Formatter.formatShortFileSize(ctx, this.getFileSize());
    }

    public String extension(){
        if(this.fileName.contains(".")){
            String[] split = this.fileName.split("\\.");
            return split[split.length-1];
        }else{
            return "file";
        }
    }

    public static Comparator<FileItem> extensionComparator = (FileItem item1, FileItem item2)->{
        return item1.extension().compareTo(item2.extension());
    };

    public static Comparator<FileItem> nameComparator = (FileItem item1, FileItem item2)->{
        return item1.getFileName().compareTo(item2.getFileName());
    };

    public static Comparator<FileItem> ratingComparator = (FileItem item1, FileItem item2)->{
        return item1.getRating() -  item2.getRating();
    };

    public static Comparator<FileItem> fileSizeComparator = (FileItem item1, FileItem item2)->{
        return Long.compare(item1.getFileSize(), item2.getFileSize());
    };

    public static Comparator<FileItem> postDateComparator = (FileItem item1, FileItem item2)->{
        return item1.posted().compareTo(item2.posted());
    };
}
