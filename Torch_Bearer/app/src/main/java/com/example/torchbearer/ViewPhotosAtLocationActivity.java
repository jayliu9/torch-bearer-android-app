package com.example.torchbearer;

import static com.example.torchbearer.PhotoContent.deleteSavedImages;
import static com.example.torchbearer.PhotoContent.downloadImage;
import static com.example.torchbearer.PhotoContent.loadSavedImages;
import static com.example.torchbearer.PostPhotoActivity.TAG;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.example.torchbearer.databinding.ActivityViewPhotosAtLocationBinding;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ViewPhotosAtLocationActivity extends AppCompatActivity implements PhotoFragment.OnListFragmentInteractionListener{

//    private ActivityViewPhotosAtLocationBinding binding;

    private ViewPhotosAtLocationActivity context;
    private DownloadManager downloadManager;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView recyclerView;
    private BroadcastReceiver onComplete;
    private View progressBar;
    StorageReference storageReference;
    List<Uri> uriList;
    int currentPhotoIndex;

    String currentLocation;
    String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photos_at_location);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;
        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        if (recyclerViewAdapter == null) {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_fragment);
            recyclerView = (RecyclerView) currentFragment.getView();
            recyclerViewAdapter = ((RecyclerView) currentFragment.getView()).getAdapter();
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                    DividerItemDecoration.VERTICAL);
            recyclerView.addItemDecoration(dividerItemDecoration);
        }

        // TODO: replace with actual location and user
        currentLocation = "san diego";
        currentUser = "John Smith";

        getSupportActionBar().setTitle("Posts at " + currentLocation);
        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference images = storageReference.child(currentLocation + "/");

        uriList = new ArrayList<>();
        currentPhotoIndex = 0;

        images.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference prefix : listResult.getPrefixes()) {
                            // All the prefixes under listRef.
                            // You may call listAll() recursively on them.
                            prefix.listAll()
                                    .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                                        @Override
                                        public void onSuccess(ListResult nestedListResult) {
                                            for (StorageReference prefix : nestedListResult.getPrefixes()) {
                                                // All the prefixes under listRef.
                                                // You may call listAll() recursively on them.
                                            }
                                            for (StorageReference item : nestedListResult.getItems()) {
                                                // All the items under listRef.
                                                item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        Log.i("DOWNLOAD", "Success " + uri.toString());
                                                        uriList.add(uri);
                                                        downloadImage(downloadManager, context, uri);
                                                    }
                                                });
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.i("DOWNLOAD", "FAILED");
                                        }
                                    });
                        }
                        for (StorageReference item : listResult.getItems()) {
                            // All the items under listRef.
                            item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.i("DOWNLOAD", "Success " + uri.toString());
                                    uriList.add(uri);
                                    downloadImage(downloadManager, context, uri);
                                }
                            });
                        }


//                        Uri uri;
//                        for (; currentPhotoIndex < uriList.size() && currentPhotoIndex < 10; currentPhotoIndex++) {
//                            Log.i(TAG, "DOWNLOAD " + currentPhotoIndex);
//                            uri = uriList.get(currentPhotoIndex);
//                            downloadImage(downloadManager, context, uri);
//                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("DOWNLOAD", "FAILED");
                    }
                });

        progressBar = findViewById(R.id.indeterminateBar);

        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.VISIBLE);
                        fab.setVisibility(View.GONE);
                        Uri uri = null;
                        if (currentPhotoIndex < uriList.size()) {
                            uri = uriList.get(currentPhotoIndex);
                            currentPhotoIndex++;
                        }
                        downloadImage(downloadManager, context, uri);

                    }
                });
            }
        });

        onComplete = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "DOWNLOAD complete" + currentPhotoIndex);

                String filePath="";
                DownloadManager.Query q = new DownloadManager.Query();
                q.setFilterById(intent.getExtras().getLong(DownloadManager.EXTRA_DOWNLOAD_ID));
                Cursor c = downloadManager.query(q);

                if (c.moveToFirst()) {
                    @SuppressLint("Range") int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        @SuppressLint("Range") String downloadFileLocalUri = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                        filePath = Uri.parse(downloadFileLocalUri).getPath();
                    }
                }
                c.close();
                PhotoContent.loadImage(new File(filePath));
                recyclerViewAdapter.notifyItemInserted(0);
                progressBar.setVisibility(View.GONE);
//                fab.setVisibility(View.VISIBLE);
//                fab.setVisibility(View.INVISIBLE);
            }
        };

        context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        Uri uri;
        Log.i(TAG, "DOWNLOAD " + currentPhotoIndex);
        Log.i(TAG, "URIList " + uriList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_delete) {
//            deleteSavedImages(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS));
//            recyclerViewAdapter.notifyDataSetChanged();
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop()
    {
        unregisterReceiver(onComplete);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                loadSavedImages(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS));
                PhotoContent.ITEMS.clear();
                deleteSavedImages(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS));
                recyclerViewAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onListFragmentInteraction(PhotoItem item) {
        // This is where you'd handle clicking an item in the list
    }

    public void backToMainActivity(View view) {
        startActivity(new Intent(ViewPhotosAtLocationActivity.this, MapActivity.class));
    }

    public void showMyPhotoOnly(View view) {
        StorageReference images = storageReference.child(currentLocation + "/" + currentUser + "/");
//        progressBar.setVisibility(View.VISIBLE);
        PhotoContent.ITEMS.clear();
        images.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference prefix : listResult.getPrefixes()) {
                            // All the prefixes under listRef.
                            // You may call listAll() recursively on them.
                            prefix.listAll()
                                    .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                                        @Override
                                        public void onSuccess(ListResult nestedListResult) {
                                            for (StorageReference prefix : nestedListResult.getPrefixes()) {
                                                // All the prefixes under listRef.
                                                // You may call listAll() recursively on them.
                                            }
                                            for (StorageReference item : nestedListResult.getItems()) {
                                                // All the items under listRef.
                                                item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        Log.i("DOWNLOAD", "Success " + uri.toString());
                                                        uriList.add(uri);
                                                        downloadImage(downloadManager, context, uri);
                                                    }
                                                });
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.i("DOWNLOAD", "FAILED");
                                        }
                                    });
                        }
                        for (StorageReference item : listResult.getItems()) {
                            // All the items under listRef.
                            item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.i("DOWNLOAD", "Success " + uri.toString());
                                    uriList.add(uri);
                                    downloadImage(downloadManager, context, uri);
                                }
                            });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("DOWNLOAD", "FAILED");
                    }
                });

        recyclerViewAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
//        fab.setVisibility(View.VISIBLE);
    }
}