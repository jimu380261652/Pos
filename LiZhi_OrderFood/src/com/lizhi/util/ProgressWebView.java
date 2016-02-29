package com.lizhi.util;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;
import android.widget.ProgressBar;
/**
 * 自定义WebView带进度条
 * @author rt.zhoujunfang
 *
 */
@SuppressWarnings("deprecation")
public class ProgressWebView extends WebView {
	/** 进度�?*/
	private ProgressBar progressbar;
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {

	    if (event.getAction() == MotionEvent.ACTION_DOWN){

	        int temp_ScrollY = getScrollY();
	        scrollTo(getScrollX(), getScrollY() + 1);
	        scrollTo(getScrollX(), temp_ScrollY);

	    }

	    return super.onTouchEvent(event);
	}
	 
	 
	public ProgressWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 初始化进度条
		progressbar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
		// 设置进度条风�?
		progressbar.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, 3, 0, 0));
		
		addView(progressbar);
		setWebChromeClient(new WebChromeClient());
		
	}
	
	public class WebChromeClient extends android.webkit.WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
            	// 加载完成隐藏进度�?
                progressbar.setVisibility(GONE);
            } else {
                if (progressbar.getVisibility() == GONE)
                    progressbar.setVisibility(VISIBLE);
                progressbar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }

    }
	
	
	@Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		LayoutParams lp = (LayoutParams) progressbar.getLayoutParams();
        lp.x = l;
        lp.y = t;
        progressbar.setLayoutParams(lp);
        super.onScrollChanged(l, t, oldl, oldt);
    }
}
