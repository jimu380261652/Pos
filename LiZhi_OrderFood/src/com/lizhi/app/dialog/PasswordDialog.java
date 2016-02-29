package com.lizhi.app.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.centerm.smartpos.sys.IInputMethodCtrl;
import com.duiduifu.util.AppConstantByUtil;
import com.duiduifu.util.MD5Util;
import com.duiduifu.view.KeyBoardView;
import com.duiduifu.view.KeyBoardView.OnKeyBoardButtonListener;
import com.duiduifu.view.KeyBoardView.OnKeyBoardLongButtonListener;
import com.pos.lizhiorder.R;

public class PasswordDialog extends BaseDialog implements android.view.View.OnClickListener {

	public static final String MTMS = "mtms";
	public static final String SETTING = "setting";
	
	Context mContext;
	EditText vPassword;
	Button vCancel;
	Button vOk;
	PasswordDialogCallback mCallback;
	KeyBoardView mKeyBoardView;
	TextView vPasswordTitle;
	Activity activity;
	InputMethodManager pwdInputManager;
	IInputMethodCtrl iInputMe;
	
	int currentId;
	
	public String tag = "";
	
	public PasswordDialog(Context context,PasswordDialogCallback callback,Activity activity) {
		super(context);
		mContext = context;
		mCallback = callback;
		this.activity=activity;
		this.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_HOME || keyCode == KeyEvent.KEYCODE_BACK){
					return true;
				}
				return false;

			}
		});
		initUI();
	}
	

	
	@SuppressWarnings("static-access")
	@SuppressLint("InlinedApi")
	private void initUI() {
//		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
		View v = getLayoutInflater().inflate(R.layout.dialog_setting_password, null);	
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(850, 450);
//		iInputMe=SysImplFactory.getInputMethodCtrl();
//		iInputMe.initialization(activity, 600,150, 400, 500, "com.centerm.cbmdemo");
		
		vPasswordTitle = (TextView) v.findViewById(R.id.password_title);
		vPassword = (EditText) v.findViewById(R.id.password_input);
		vPassword.setSelectAllOnFocus(true);
		vPassword.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				return true;
			}
		});
		vCancel = (Button) v.findViewById(R.id.cancel_btn);
		vOk = (Button) v.findViewById(R.id.ok_btn);
//		pwdInputManager=(InputMethodManager)vPassword.getContext().getSystemService(Context.INPUT_METHOD_SERVICE); 
//		vPassword.setOnFocusChangeListener(iInputMe.getInputMethodChangeListener(iInputMe.INPUTMETHOD_TYPE_123_ABC));
		
		mKeyBoardView = (KeyBoardView) v.findViewById(R.id.keyBoardView);
		
		mKeyBoardView.setOnKeyBoardButtonListener(new OnKeyBoardButtonListener() {

			@Override
			public void OnClick(String kb_type,String kb_text) {
				
				if (kb_text.equals(AppConstantByUtil.CLEAR)) {
					
					int index = vPassword.getText().toString().length();  

					if(index<=0)
						return;

					String text = vPassword.getText().toString();
					
					if(text==null || "".equals(text)){
						return;
					}
					
					vPassword.getText().delete(index-1, index);

				}else if(kb_text.equals(AppConstantByUtil.NEXT)){
					
					
				} else {
					vPassword.getText().append(kb_text);
					
				}
				
			}

			

		},true,false, false);
		mKeyBoardView.setOnKeyBoardLongButtonListener(new OnKeyBoardLongButtonListener() {
			
			@Override
			public void OnLongClick(String kb_type, String kb_text) {
				if (kb_text.equals(AppConstantByUtil.CLEAR)) {
					int index = vPassword.getText().toString().length();  
					vPassword.getText().delete(0, index);
					vPassword.setText("");
				}
			}
		}, true, false,false);
		
//		vPassword.setInputType(InputType.TYPE_NULL);
		
		vOk.setOnClickListener(this);
		vCancel.setOnClickListener(this);
		
		vPasswordTitle.setFocusable(true);
		vPasswordTitle.setFocusableInTouchMode(true);
		boolean t = vPasswordTitle.requestFocus();
		
		this.addContentView(v, lp);
	}
	
	
	
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.ok_btn){
			String password = vPassword.getText().toString();
			if(password!=null && !"".equals(password.trim())){
				try {
					password = MD5Util.createSign(password);
					if(MTMS.equals(tag)){
						Log.i("pos_set-psw", MD5Util.createSign("123456"));
						if(password.equals(MD5Util.createSign("123456"))){
							mCallback.onPasswordInputDialogOK(currentId);
							vPassword.setText("");
							this.dismiss();
						}else{
							Toast.makeText(mContext, "密码错误", Toast.LENGTH_SHORT).show();
						}
					}else if(SETTING.equals(tag)){
						if(password.equals(MD5Util.createSign("123456"))){
							mCallback.onPasswordInputDialogOK(currentId);
							vPassword.setText("");
							this.dismiss();
						}else{
							Toast.makeText(mContext, "密码错误", Toast.LENGTH_SHORT).show();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				Toast.makeText(mContext, "请输入密码", Toast.LENGTH_SHORT).show();
			}
			return;
		}
		
		if(v.getId() == R.id.cancel_btn){
			this.dismiss();
			return;
		}
		
		
	}
	
	
	public void showDialog(int currentId,String passTag){
		this.currentId = currentId;
		this.tag = passTag;
		this.show();
	}
	
	
	public interface PasswordDialogCallback{
		public void onPasswordInputDialogOK(int currentId);
	}


	
	

}
