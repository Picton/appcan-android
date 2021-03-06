/*
 *  Copyright (C) 2014 The AppCan Open Source Project.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.

 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.

 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.zywx.wbpalmstar.engine;

import android.os.Build;
import android.text.TextUtils;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;

import org.zywx.wbpalmstar.base.BConstant;

import java.lang.reflect.Method;

public class EBrowserSetting implements EBrowserBaseSetting {

    public static final String DESKTOP_USERAGENT = "Mozilla/5.0 (Macintosh; "
            + "U; Intel Mac OS X 10_6_3; en-us) AppleWebKit/533.16 (KHTML, "
            + "like Gecko) Version/5.0 Safari/533.16";
    public static final String IPHONE_USERAGENT = "Mozilla/5.0 (iPhone; U; "
            + "CPU iPhone OS 4_0 like Mac OS X; en-us) AppleWebKit/532.9 "
            + "(KHTML, like Gecko) Version/4.0.5 Mobile/8A293 Safari/6531.22.7";
    public static final String IPAD_USERAGENT = "Mozilla/5.0 (iPad; U; "
            + "CPU OS 3_2 like Mac OS X; en-us) AppleWebKit/531.21.10 "
            + "(KHTML, like Gecko) Version/4.0.4 Mobile/7B367 Safari/531.21.10";
    public static final String FROYO_USERAGENT = "Mozilla/5.0 (Linux; U; "
            + "Android " + Build.VERSION.RELEASE + "; en-us; " + Build.MODEL
            + " Build/FRF91) AppleWebKit/533.1 "
            + "(KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";

    public static final String USERAGENT = FROYO_USERAGENT;
    public static final String USERAGENT_APPCAN = BConstant.USERAGENT_APPCAN;
    public static String USERAGENT_NEW;

    protected WebSettings mWebSetting;
    protected EBrowserView mBrwView;
    protected boolean mWebApp;

    public EBrowserSetting(EBrowserView inView) {
        mWebSetting = inView.getSettings();
        mBrwView = inView;
        USERAGENT_NEW = mWebSetting.getUserAgentString() + USERAGENT_APPCAN;
        BConstant.USERAGENT_NEW=mWebSetting.getUserAgentString() + USERAGENT_APPCAN;
    }

    public void initBaseSetting(boolean webApp) {
        mWebApp = webApp;
        mWebSetting.setSaveFormData(false);
        mWebSetting.setSavePassword(false);
        mWebSetting.setLightTouchEnabled(false);
        mWebSetting.setJavaScriptEnabled(true);
        mWebSetting.setNeedInitialFocus(false);
        mWebSetting.setSupportMultipleWindows(false);
        mWebSetting.setGeolocationEnabled(true);
        // mWebSetting.setNavDump(false);
        //mWebSetting.setPluginsEnabled(true);
        mWebSetting.setJavaScriptCanOpenWindowsAutomatically(false);
        mWebSetting.setUseWideViewPort(false);
        mWebSetting.setLoadsImagesAutomatically(true);
        mWebSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        mWebSetting.setUserAgentString(USERAGENT_NEW);
        mWebSetting.setRenderPriority(RenderPriority.HIGH);
        mWebSetting.setDefaultTextEncodingName("UTF-8");
        mWebSetting.setBuiltInZoomControls(true);
        mWebSetting.setDisplayZoomControls(false);
        mWebSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebSetting.setDomStorageEnabled(true);//开启DOM storage API功能

        if (Build.VERSION.SDK_INT <= 7) {
            invokeHtml5(mWebSetting);
        }
        if (Build.VERSION.SDK_INT >= 14) {
            mWebSetting.setTextZoom(100);;
        }
        if (webApp) {
            mWebSetting.setUseWideViewPort(true);
//			mWebSetting.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            //允许混合模式，解决个别页面或者视频无法正常显示和播放的问题（http与https）
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mWebSetting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            }
            return;
        }
//		mWebSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        // disables the actual onscreen controls from showing up
        mWebSetting.setBuiltInZoomControls(false);
        // disables the ability to zoom
        mWebSetting.setSupportZoom(false);
        mWebSetting.setDefaultFontSize(ESystemInfo.getIntence().mDefaultFontSize);
        mWebSetting.setDefaultFixedFontSize(ESystemInfo.getIntence().mDefaultFontSize);
//		float sca = mBrwView.getScale();
//		if (sca != 1.0f) {
        if (Build.VERSION.SDK_INT <= 18) {
            mWebSetting.setDefaultZoom(ESystemInfo.getIntence().mDefaultzoom.getValue());
        }


//		}

    }

    @Override
    public void setDefaultFontSize(int size) {
        if (mWebApp) {
            return;
        }
        mWebSetting.setDefaultFontSize(size);
        mWebSetting.setDefaultFixedFontSize(size);
    }

    @Override
    public void setSupportZoom() {
        mWebSetting.setSupportZoom(true);
        mWebSetting.setBuiltInZoomControls(true);
    }

    @Override
    public void setUserAgent(String userAgent) {
        if (TextUtils.isEmpty(userAgent)) {
            mWebSetting.setUserAgentString(USERAGENT_NEW);
        } else {
            mWebSetting.setUserAgentString(userAgent);
        }
    }

    @SuppressWarnings("rawtypes")
    private void invokeHtml5(WebSettings setting) {
        try {
            String path = mBrwView.getContext().getDir("database", 0).getPath();
            Class[] paramTypes = new Class[]{boolean.class};
            Class[] param1 = new Class[]{String.class};
            Method databaseEnabled = WebSettings.class.getMethod("setDatabaseEnabled", paramTypes);
            databaseEnabled.invoke(setting, true);
            Method domStorageEnabled = WebSettings.class.getMethod("setDomStorageEnabled", paramTypes);
            domStorageEnabled.invoke(setting, true);
            Method databasePath = WebSettings.class.getMethod("setDatabasePath", param1);
            databasePath.invoke(setting, path);
        } catch (Exception e) {
            ;
        }
    }
}
