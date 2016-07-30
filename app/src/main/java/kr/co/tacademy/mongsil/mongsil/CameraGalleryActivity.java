package kr.co.tacademy.mongsil.mongsil;

import android.Manifest;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Han on 2016-07-30.
 */
public class CameraGalleryActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<MediaStoreData>> {
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 10;
    private static final int REQ_CODE_SELECT_IMAGE = 20;

    // TODO : 갤러리 리사이클러뷰의 대대적인 수정이 필요함
    // 리사이클러뷰를 사용하기 위해서
    // MediaStoreData.class, MediaStoreDataLoader.class, GalleryRecyclerAdapter가 사용됨

    RecyclerView galleryRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        int selector = intent.getExtras().getInt("selector");
        if(selector == 0) {
            // 카메라를 선택한 경우
            setContentView(R.layout.activity_custom_camera);
            Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            TextView textCancel = (TextView)findViewById(R.id.text_cancel);
            textCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        } else {
            // 갤러리를 선택한 경우
            setContentView(R.layout.layout_gallery);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);

            // 퍼미션 권한 확인
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // 권한이 없을 경우
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // 사용자가 임의로 권한을취소시킨 경우
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    dialog.setTitle("권한이 필요합니다!")
                            .setMessage("갤러리를 사용하시려면 \"파일 목록\"" +
                                    "접근 권한이 필요합니다. 계속하시겠습니까?")
                            .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(
                                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 10);
                                    }
                                }
                            })
                            .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            }).create().show();
                    // 재요청
                    ActivityCompat.requestPermissions(
                            this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            PERMISSIONS_REQUEST_READ_CONTACTS);
                } else {
                    // 최초로 권한을 요청하는 경우(첫실행)
                    ActivityCompat.requestPermissions(
                            this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            }
        }
    }

    @Override
    public android.content.Loader<List<MediaStoreData>> onCreateLoader(int i, Bundle bundle) {
        new MediaStoreDataLoader(this);
        return null;
    }

    @Override
    public void onLoadFinished(android.content.Loader<List<MediaStoreData>> loader, List<MediaStoreData> mediaStoreDatas) {
        GalleryRecyclerAdapter adapter =
                new GalleryRecyclerAdapter(this, mediaStoreDatas);
        galleryRecycler.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(android.content.Loader<List<MediaStoreData>> loader) { }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (requestCode == 10) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // 리사이클러로 갤러리를 받아옴
                    getLoaderManager().initLoader(R.id.loader_id_media_store_data, null, this);
                    galleryRecycler = (RecyclerView) findViewById(R.id.gallery_recycler);
                    GridLayoutManager gridLayoutManager
                            = new GridLayoutManager(this, 3);
                    gridLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
                    galleryRecycler.setLayoutManager(gridLayoutManager);
                    galleryRecycler.setHasFixedSize(true);
                }
            }
            else {
                finish();
            }

        }
    }
}
