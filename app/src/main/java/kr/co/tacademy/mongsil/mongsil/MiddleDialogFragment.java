package kr.co.tacademy.mongsil.mongsil;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ccei on 2016-08-09.
 */
public class MiddleDialogFragment extends DialogFragment {
    private static final String SELECTOR = "selector";
    private static final String POSTID = "postid";

    private int selector;
    private String postId;

    public MiddleDialogFragment() { }
    public static MiddleDialogFragment newInstance(int selector, String postId) {
        MiddleDialogFragment fragment = new MiddleDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(SELECTOR, selector);
        bundle.putString(POSTID, postId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selector = getArguments().getInt(SELECTOR);
            postId = getArguments().getString(POSTID);
        }
        setStyle(STYLE_NO_TITLE, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_middle, container, false);

        final TextView dialog = (TextView) view.findViewById(R.id.text_dialog);
        View line = view.findViewById(R.id.middle_dialog_line);
        Button negative = (Button) view.findViewById(R.id.btn_negative);
        Button positive = (Button) view.findViewById(R.id.btn_positive);

        negative.setText(getResources().getText(R.string.cancel));
        positive.setText(getResources().getText(R.string.confirm));

        // 셀렉터가 0일 경우 positive와 negative, 1일 경우 positive만
        switch (selector) {
            case 0 : // 삭제하는 경우 [취소 / 확인]
                dialog.setText(getResources().getText(R.string.post_remove_question));
                // if( ~~ ) {
                negative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();
                    }
                });
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();
                        new AsyncPostRemoveResponse().execute(postId);
                    }
                });
                break;
            case 1 : // 삭제 완료한 경우 [확인]
                negative.setVisibility(View.GONE);
                line.setVisibility(View.GONE);
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();
                    }
                });
                break;
            case 2 : // 저장하는 경우 [취소 / 확인]
                dialog.setText(getResources().getText(R.string.save_edit_profile));
                negative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();
                    }
                });
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();
                        if(dialog.getText()
                                == getResources().getText(R.string.post_remove_question)) {

                        }
                    }
                });
                break;
            case 3 : // 저장 완료한 경우 [확인]
                dialog.setText(getResources().getText(R.string.save_edit_profile));
                negative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();
                    }
                });
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();
                        if(dialog.getText()
                                == getResources().getText(R.string.post_remove_question)) {
                            new AsyncPostRemoveResponse().execute(postId);
                        }
                    }
                });
                break;
        }
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        dismiss();
    }

    public class AsyncPostRemoveResponse extends AsyncTask<String, String, String> {

        int responseCode = 0;

        @Override
        protected String doInBackground(String... args) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .build();

            RequestBody formBody = new FormBody.Builder().build();

            Request request = new Request.Builder()
                    .url(String.format(NetworkDefineConstant.POST_SERVER_POST_REMOVE,
                            args[0]))
                    .delete(formBody) //반드시 post로
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("요청오류", "요청을 보내는 데 실패했습니다.");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    boolean flag = response.isSuccessful();
                    responseCode = response.code();
                    if (flag) {
                        Log.e("response결과", response.message()); //읃답에 대한 메세지(OK)
                        Log.e("response응답바디", response.body().string()); //json으로 변신
                    }
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(responseCode >= 200 && responseCode < 400) {
                getActivity().finish();
                MiddleDialogFragment.newInstance(1, postId)
                        .show(getActivity().getSupportFragmentManager(), "middle");
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.TOP;
        window.setAttributes(wlp);
    }
}