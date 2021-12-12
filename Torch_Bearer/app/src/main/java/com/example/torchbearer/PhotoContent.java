package com.example.torchbearer;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class PhotoContent {
    static final List<PhotoItem> ITEMS = new ArrayList<>();

    public static void loadSavedImages(File dir) {
        ITEMS.clear();
        if (dir.exists()) {
            File[] files = dir.listFiles();
            for (File file : files) {
                String absolutePath = file.getAbsolutePath();
                String extension = absolutePath.substring(absolutePath.lastIndexOf("."));
                if (extension.equals(".jpg")) {
                    file.delete();
                }
            }
        }
        if (dir.exists()) {
            File[] files = dir.listFiles();
            for (File file : files) {
                String absolutePath = file.getAbsolutePath();
                String extension = absolutePath.substring(absolutePath.lastIndexOf("."));
                if (extension.equals(".jpg")) {
                    loadImage(file);
                }
            }
        }
    }

    public static void deleteSavedImages(File dir) {
        if (dir.exists()) {
            File[] files = dir.listFiles();
            for (File file : files) {
                String absolutePath = file.getAbsolutePath();
                String extension = absolutePath.substring(absolutePath.lastIndexOf("."));
                if (extension.equals(".jpg")) {
                    file.delete();
                }
            }
        }
        ITEMS.clear();
    }

    public static void downloadImage(DownloadManager downloadmanager, Context context, Uri uri) {

        long ts = System.currentTimeMillis();
        uri = uri == null ? Uri.parse(context.getString(R.string.image_download_url)) : uri;

        Log.i("POST_PHOTO", "URL: " + uri.getPath());
        String[] parts = uri.getPath().split("/");
        String fileName = parts[6] + "_" + parts[7];
        Log.i("POST_PHOTO", "Filename: " + fileName);

        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle("My File");
        request.setDescription("Downloading");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setVisibleInDownloadsUi(false);
//        String fileName = ts + ".jpg";
        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName);

        downloadmanager.enqueue(request);
    }

    private static String getDateFromUri(Uri uri){
        String[] split = uri.getPath().split("/");
        String fileName = split[split.length - 1];
        String fileNameNoExt = fileName.split("\\.")[0];
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = format.format(new Date(Long.parseLong(fileNameNoExt)));
        return dateString;
    }

    public static void loadImage(File file) {
        PhotoItem newItem = new PhotoItem();
        newItem.uri = Uri.fromFile(file);
        Log.i("POST_PHOTO", "Download complete: " + file.getName());
//        newItem.date = getDateFromUri(newItem.uri);
        int suffixIndex = file.getName().indexOf('.');
        String[] parts = file.getName().substring(0, suffixIndex).split("_");

        newItem.date = "Posted by " + parts[0] + "\n" + parts[1];

        addItem(newItem);
    }

    private static void addItem(PhotoItem item) {
        ITEMS.add(0, item);
    }
}