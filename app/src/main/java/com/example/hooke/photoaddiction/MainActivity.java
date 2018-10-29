package com.example.hooke.photoaddiction;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.hooke.photoaddiction.adapters.PhotoAdapter;
import com.example.hooke.photoaddiction.models.AlarmReceiver;
import com.example.hooke.photoaddiction.models.Photo;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String APP_PREFERENCES = "settings";
    public static final String APP_PREFERENCES_MINUTE = "minute";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter photoAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static final String PHOTO_EXT = ".jpg";
    private static final String PHOTO_NAME_PRE = "/photo";
    final int REQUEST_CODE_PHOTO = 1;
    private AlarmReceiver alarmReceiver;
    static SharedPreferences mySharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mySharedPreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        alarmReceiver = new AlarmReceiver();
        setSupportActionBar(toolbar);
        setupFAB();
        letsCheckPermission();
    }

    @Override
    protected void onResume() {
        setupRecycleView();
        super.onResume();
    }

    private List<Photo> initPhotoList(File photoDir) {
        List<Photo> photoList = new ArrayList<>();
        File[] list = photoDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(PHOTO_EXT);
            }
        });
        for (File ph : list) {
            photoList.add(new Photo(ph.getName(),
                    FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID, ph)));
        }
        return photoList;
    }

    private void letsCheckPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                setupRecycleView();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                System.exit(1);
            }
        };
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage(R.string.i_need_permision)
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)
                .check();
    }

    private void letsCheckPermission2() {

        String rationale = "Please provide location permission so that you can ...";
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};
        Permissions.Options options = new Permissions.Options()
                .setRationaleDialogTitle("Info")
                .setSettingsDialogTitle("Warning");
        Permissions.check(this/*context*/, permissions, rationale, options, new PermissionHandler() {
            @Override
            public void onGranted() {
                setupRecycleView();
            }
        });
    }

    private void setupRecycleView() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            File photoDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM).getPath());
            mRecyclerView = (RecyclerView) findViewById(R.id.photo_recycler_view);
            mRecyclerView.setHasFixedSize(true);
            mLayoutManager = new StaggeredGridLayoutManager(2, 1);
            mRecyclerView.setLayoutManager(mLayoutManager);
            photoAdapter = new PhotoAdapter(initPhotoList(photoDir));
            mRecyclerView.setAdapter(photoAdapter);
        }
    }

    private void setupFAB() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoNameGenerator());
                startActivityForResult(intent, REQUEST_CODE_PHOTO);
            }
        });
    }

    private Uri photoNameGenerator() {
        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();
        File newDir = new File(dir);
        if (!newDir.exists()) {
            newDir.mkdir();
        }
        File file = new File(dir + PHOTO_NAME_PRE + System.currentTimeMillis() + PHOTO_EXT);
        return FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID, file);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_notifier) {
            showInputDialog();
            return true;
        } else if (id == R.id.action_stop) {
            setMinute(0);
        }
        return super.onOptionsItemSelected(item);
    }

    public static int getMinute() {
        return mySharedPreferences.getInt(APP_PREFERENCES_MINUTE, 0);
    }

    public void setMinute(int minute) {
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putInt(APP_PREFERENCES_MINUTE, minute);
        editor.apply();

    }

    public void setAlarmReceiver() {
        alarmReceiver.setAlarm(this.getApplicationContext());
    }

    protected void showInputDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText minuteET = (EditText) promptView.findViewById(R.id.minute_edittext);
        minuteET.setText(getMinute() + "");
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        setMinute(toInt(minuteET.getText().toString(), 0));
                        setAlarmReceiver();
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    public static int toInt(String str, int defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }
}
