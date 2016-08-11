package kr.co.tacademy.mongsil.mongsil;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
/**
 * Created by ccei on 2016-07-29.
 */
public class BottomDialogFragment extends DialogFragment {
    private static final String DIVIDER = "divider";
    private static final String POSTID = "postid";

    public BottomDialogFragment() { }
    public static BottomDialogFragment newInstance(int divider, String postId) {
        BottomDialogFragment f = new BottomDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(DIVIDER, divider);
        bundle.putString(POSTID, postId);
        f.setArguments(bundle);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_bottom, container, false);

        final Bundle bundle = getArguments();

        Button btnFirst = (Button) view.findViewById(R.id.btn_first);
        Button btnSecond = (Button) view.findViewById(R.id.btn_second);
        Button btnClose = (Button) view.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        switch (bundle.getInt(DIVIDER)) {
            case 0 :
                final Intent cameraIntent = new Intent(getActivity(), CameraGalleryActivity.class);

                // 카메라
                btnFirst.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // TODO: 카메라 불러옴
                        dismiss();
                        cameraIntent.putExtra("selector", 0);
                        startActivity(cameraIntent);
                    }
                });

                // 갤러리
                btnSecond.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // TODO: 갤러리 불러옴
                        dismiss();
                        cameraIntent.putExtra("selector", 1);
                        startActivity(cameraIntent);
                    }
                });

                return view;
            case 1 :
                final Intent postIntent = new Intent(getActivity(), PostingActivity.class);
                // TODO : 글 수정하는 인텐트 put 만들어야함

                // 글 수정
                btnFirst.setText(getResources().getString(R.string.post_modify));
                btnFirst.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // TODO: 글 수정 불러옴
                        Intent intent = new Intent(getContext(), PostingActivity.class);
                        intent.putExtra("postid", bundle.getString(POSTID));
                        startActivity(intent);
                        dismiss();
                    }
                });

                // 글 삭제
                btnSecond.setText(getResources().getString(R.string.post_remove));
                btnSecond.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // TODO: 글을 삭제하시겠습니까? 요청
                        dismiss();
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .add(MiddleDialogFragment.newInstance(0, bundle.getString(POSTID)),
                                        "middle_post_remove").commit();
                    }
                });
                return view;
        }
        return null;
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
