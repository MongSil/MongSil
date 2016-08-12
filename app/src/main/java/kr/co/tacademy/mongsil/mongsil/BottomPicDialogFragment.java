package kr.co.tacademy.mongsil.mongsil;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

/**
 * Created by ccei on 2016-07-29.
 * EditProfileActivity와 PostingActivity에 사용
 */
public class BottomPicDialogFragment extends DialogFragment {
    public interface OnBottomPicDialogListener {
        void onSelectBottomPic(int select);
    }

    OnBottomPicDialogListener onBottomPicDialogListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBottomPicDialogListener) {
            onBottomPicDialogListener = (OnBottomPicDialogListener) context;
        }
    }

    public BottomPicDialogFragment() {
    }

    public static BottomPicDialogFragment newInstance() {
        BottomPicDialogFragment fragment = new BottomPicDialogFragment();
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        checkPermission();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_triple_bottom, container, false);
        Button btnFirst = (Button) view.findViewById(R.id.btn_first);
        Button btnSecond = (Button) view.findViewById(R.id.btn_second);
        Button btnClose = (Button) view.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        // 카메라
        btnFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 카메라 불러옴
                dismiss();
                onBottomPicDialogListener.onSelectBottomPic(0);

            }
        });

        // 갤러리
        btnSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 갤러리 불러옴
                dismiss();
                onBottomPicDialogListener.onSelectBottomPic(1);
            }
        });

        return view;
    }

    private final int MY_PERMISSION_REQUEST_STORAGE = 100;

    private void checkPermission() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                    || getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // Explain to the user why we need to write the permission.
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                    dialog.setTitle("권한이 필요합니다!")
                            .setMessage("갤러리를 사용하시려면 \"파일 목록\"" +
                                    "접근 권한이 필요합니다. 계속하시겠습니까?")
                            .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(
                                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                MY_PERMISSION_REQUEST_STORAGE);
                                    }
                                }
                            })
                            .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dismiss();
                                }
                            }).create().show();
                }
            } else {
                //사용자가 언제나 허락
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    //사용자가 퍼미션을 OK했을 경우

                } else {

                    //사용자가 퍼미션을 거절했을 경우
                }
                break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        dismiss();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.windowAnimations = R.style.BottomDialogAnimation;
        wlp.gravity = Gravity.BOTTOM;
        wlp.height = getResources().getDimensionPixelSize(R.dimen.dialog_pic_vertical);
        window.setAttributes(wlp);
    }
}
