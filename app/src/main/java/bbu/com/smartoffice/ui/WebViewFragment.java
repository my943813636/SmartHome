package bbu.com.smartoffice.ui;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import java.io.IOException;

import bbu.com.smartoffice.Model.DeviceInfoModel;
import bbu.com.smartoffice.R;
import bbu.com.smartoffice.adapter.DeviceRvAdapter;
import bbu.com.smartoffice.base.BaseFragment;
import bbu.com.smartoffice.jsonBean.RuleBean;
import bbu.com.smartoffice.net.HttpRequest;
import bbu.com.smartoffice.presenter.WebViewPresenter;
import bbu.com.smartoffice.utils.OneNetUtils;
import bbu.com.smartoffice.utils.TransitionUtil;
import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static bbu.com.smartoffice.ManageActivity.manageActivity;

/**
 * Created by G on 2016/11/15 0015.
 */

public class WebViewFragment extends BaseFragment<WebViewPresenter, DeviceInfoModel> {


    @Bind(R.id.tbBack)
    ImageView tbBack;
    @Bind(R.id.webView)
    WebView mWebView;
    private View rootView;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_webview, container, false);
        ButterKnife.bind(this, rootView);
        setListener();
        transition();
        initWebView();
        return rootView;
    }

    private void transition() {
        setExitTransition(TransitionUtil.getTransition(R.transition.slid_left));
        setEnterTransition(TransitionUtil.getTransition(R.transition.slid_right));
    }


    /**
     * 视图监听
     */
    private void setListener() {
        tbBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manageActivity.getFragmentManager().popBackStack();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    private void initWebView() {
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        mWebView.loadUrl("file:///android_asset/hack/hack1.html");
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        mWebView.addJavascriptInterface(this, "GetAndroid");

    }

    @Override
    public boolean onInterceptBackClick() {
        manageActivity.getFragmentManager().popBackStack();
        return true;
    }

    @JavascriptInterface
    public void queryDevices(String id) {

        try {

            HttpRequest.getInstance().queryDevice(id, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "网络错误", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = response.body().string();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mWebView.loadUrl("javascript:appendDeviceItem('" + result + "')");
                            } catch (Exception e) {
                                Logger.d("javascript:appendDeviceItem('" + result + "')");
                            }

                        }
                    });
                }
            });
        } catch (IOException e) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "网络错误", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    //TODO
    @JavascriptInterface
    public void sendCMD(String json) {
        Gson g = new Gson();
        RuleBean ruleBean = g.fromJson(json, RuleBean.class);
        DeviceRvAdapter.ruleName = ruleBean.getRulename();
        OneNetUtils.SendCmd("4069468", json).subscribe(s ->
                {
                    Toast.makeText(getActivity(), "规则发送成功", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(() -> manageActivity.getFragmentManager().popBackStack(), 2000L);
                }
        );

    }

}

