package kr.co.tacademy.mongsil.mongsil;

import android.content.Context;
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
 * Created by ccei on 2016-08-12.
 */
public class BottomEditDialogFragment extends DialogFragment {
    private static final String POST = "post";
    private static final String REPLY = "reply";
    public interface OnBottomEditDialogListener {
        void onSelectBottomEdit(int select, Post post, ReplyData data);
    }

    OnBottomEditDialogListener onBottomEditDialogListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBottomEditDialogListener) {
            onBottomEditDialogListener = (OnBottomEditDialogListener) context;
        }
    }

    Post post;
    ReplyData replyData;

    public BottomEditDialogFragment() {
    }

    public static BottomEditDialogFragment newInstance(Post post) {
        BottomEditDialogFragment fragment = new BottomEditDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(POST, post);
        fragment.setArguments(bundle);
        return fragment;
    }
    public static BottomEditDialogFragment newInstance(ReplyData data) {
        BottomEditDialogFragment fragment = new BottomEditDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(REPLY, data);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if(getArguments().getParcelable(POST) != null) {
                this.post = getArguments().getParcelable(POST);
            } else {
                this.replyData = getArguments().getParcelable(REPLY);
            }
        }
        setStyle(STYLE_NO_TITLE, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_double_bottom, container, false);
        Button btnFirst = (Button) view.findViewById(R.id.btn_first);
        Button btnSecond = (Button) view.findViewById(R.id.btn_second);
        Button btnClose = (Button) view.findViewById(R.id.btn_close);

        if(post != null) {
            // 글 수정
            btnFirst.setText(getResources().getString(R.string.post_modify));
            btnFirst.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                    onBottomEditDialogListener.onSelectBottomEdit(0, post, null);
                }
            });

            // 글 삭제
            btnSecond.setText(getResources().getString(R.string.post_remove));
            btnSecond.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                    onBottomEditDialogListener.onSelectBottomEdit(1, post, null);
                }
            });
        } else if(replyData != null) {
            // 댓글 수정
            btnFirst.setText(getResources().getString(R.string.reply_modify));
            btnFirst.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                    onBottomEditDialogListener.onSelectBottomEdit(2, null, replyData);
                }
            });

            // 댓글 삭제
            btnSecond.setText(getResources().getString(R.string.reply_remove));
            btnSecond.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                    onBottomEditDialogListener.onSelectBottomEdit(3, null, replyData);
                }
            });

        }

        // 닫기
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        dismissAllowingStateLoss();
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
