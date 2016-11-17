package bbu.com.smartoffice.base;


import android.app.Fragment;
import android.os.Bundle;

import bbu.com.smartoffice.utils.Tutil;

import static bbu.com.smartoffice.ManageActivity.manageActivity;

/**
 * Created by G on 2016/11/15 0015.
 */

public class BaseFragment<P extends BasePresenter> extends Fragment {
    BasePresenter presenter;

    /**
     * 获取一个 Fragment实例  如果Fragment之前已经载入 就返回之前的Fragment
     *
     * @param cls
     * @return
     */
    public static BaseFragment getInstance(Class<? extends BaseFragment> cls) {
        Fragment fragmentByTag = manageActivity.getFragmentManager().findFragmentByTag(cls.getName());
        if (fragmentByTag == null) {
            try {
                fragmentByTag = cls.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return (BaseFragment) fragmentByTag;
    }

    /**
     * 保存一个粉presenter实例 , 通知present调用  onStart
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = Tutil.getT(this, 0);
        if (presenter != null)
            presenter.onStart();
    }

    /**
     * 清空Present 引用 释放资源
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter != null)
            presenter.onDestroy();
    }
}
