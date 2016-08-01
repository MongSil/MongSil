package kr.co.tacademy.mongsil.mongsil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;

/**
 * Created by ccei on 2016-08-01.
 */
public abstract class NetworkRequest<T> implements Runnable {
    // Http Method 방식
    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_DELETE = "DELETE";

    // 에러 코드(많을 땐 더 정의함)
    public static final int ERROR_CODE_NETWORK = -1;
    public static final int ERROR_CODE_PARSE = -2;
    public static final int ERROR_CODE_HTTP = -3;
    public static final int ERROR_UNKNOWN = 0;

    // 결과 콜백
    NetworkManager.OnResultListener<T> resultListener;
    NetworkManager manager;

    // NetworkManager 객체와 릴레이션을 맺음
    public void setManager(NetworkManager manager) {
        this.manager = manager;
    }

    public void setOnResultListener(
            NetworkManager.OnResultListener<T> listener) {
        resultListener = listener;
    }

    public void sendSuccess() {
        if(resultListener != null && !isCancel) {
            resultListener.onSuccess(this, result);
        }
    }

    public void sendFailure() {
        if(resultListener != null && !isCancel) {
            resultListener.onFailure(this, errorCode,
                    responseCode, responseMessage, errorThrowable);
        }
    }

    T result;
    int errorCode;
    int responseCode;
    String responseMessage;
    Throwable errorThrowable;

    public abstract URL getURL() throws MalformedURLException;

    public String getRequestMethod() {
        return METHOD_GET;
    }

    public int getTimeout() {
        return 15000;
    }

    // 상속받은 클래스에서 각각 필요하다면 재정의함
    public void setOutput(OutputStream out) {
        // 문자열 변경할때나 씀
    }
    public void setRequestHeader(HttpURLConnection connection) {
    }
    public void setConfiguration(HttpURLConnection connection) {

    }

    public abstract T parse(InputStream is) throws ParseException;

    public static final int DEFAULT_RETRY_COUNT = 3;
    private int retryCount = DEFAULT_RETRY_COUNT;

    private boolean isCancel = false;

    public void cancel() {
        isCancel = true;
        if (huc != null) {
            huc.disconnect();
        }
        manager.postCancelProcess(this);
    }

    public boolean isCancel() {
        return isCancel;
    }
    HttpURLConnection huc = null;

    @Override
    public void run() {
        while(retryCount > 0 && !isCancel) {
            try {
                URL url = getURL();
                HttpURLConnection connection =
                        (HttpURLConnection) url.openConnection();
                String method = getRequestMethod();
                if (method == METHOD_POST || method == METHOD_PUT) {
                    connection.setDoOutput(true);
                }
                connection.setRequestMethod(method);
                setRequestHeader(connection);
                setConfiguration(connection);
                connection.setConnectTimeout(getTimeout());
                connection.setReadTimeout(getTimeout());
                if(isCancel) {
                    return;
                }
                if(connection.getDoOutput()) {
                    OutputStream out = connection.getOutputStream();
                    setOutput(out);
                }
                if(isCancel) {
                    return;
                }
                int returnedCode = connection.getResponseCode();
                if(isCancel) {
                    return;
                }
                if (returnedCode >= HttpURLConnection.HTTP_OK &&
                        returnedCode < HttpURLConnection.HTTP_MULT_CHOICE) {
                    huc = connection;
                    InputStream is = connection.getInputStream();
                    result = parse(is);
                    if(isCancel) {
                        return;
                    }
                    manager.sendSuccess(this);
                    return;
                }
                responseCode = returnedCode;
                responseMessage = connection.getResponseMessage();
            } catch (MalformedURLException mue) {
                mue.printStackTrace();
                errorThrowable = mue;
                errorCode = ERROR_CODE_NETWORK;
            } catch (IOException ie) {
                ie.printStackTrace();
                errorThrowable = ie;
                errorCode = ERROR_CODE_NETWORK;
            } catch (ParseException pe) {
                pe.printStackTrace();
                errorThrowable = pe;
                errorCode = ERROR_CODE_PARSE;
            }
            retryCount = 0;
        }

        if(isCancel) {
            manager.sendFailure(this);
        }
    }

    // 요청에 대한 그룹 설정(보통 호출한 클래스(액티비티 or 프레그먼트)
    Object tag = null;
    public void setTag(Object tag) {
        this.tag = tag;
    }
    public Object getTag() {
        return tag;
    }
}
