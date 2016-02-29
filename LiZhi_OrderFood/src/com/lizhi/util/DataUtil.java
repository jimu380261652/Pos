package com.lizhi.util;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.pos.lizhiorder.R;


public class DataUtil {

	public DataUtil( ){
	}
	
	//ʱ��
	//�ַ�ת����ʱ��Date
	public static Date Convert2Date(String dateString)
	{
		return Convert2Date(dateString,"yyyy-MM-dd");
	}
	public static Date Convert2Date(String dateString, String type) {
		if (dateString == null || dateString.trim().equals("") || dateString.trim().equals("null"))
			return null;
		DateFormat df = new SimpleDateFormat(type);
		Date date = new Date();
		try {
			date = df.parse(dateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block\
			Log.e("StringToDate", dateString + "    " + e);
			e.printStackTrace();
		}
		return date;
	}
	 public static void removeDuplicateWithOrder(List<String> list) {  
		    Set<String> set = new HashSet<String>();  
		     List<String> newList = new ArrayList<String>();  
		   for (Iterator<String> iter = list.iterator(); iter.hasNext();) {  
			   String element = iter.next();  
		         if (set.add(element))  
		            newList.add(element);
		      }   
		     list.clear();  
		     list.addAll(newList);  
		    System.out.println( " remove duplicate " + list);  
		 }  
	/**
	 * ��ȡ��Ļ�ĳߴ�
	 * @param context
	 * @return
	 */
	public static int[] getScreenSize(Context context){
		WindowManager wm = (WindowManager) context.getSystemService(
						    Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();//��Ļ���
		int height = wm.getDefaultDisplay().getHeight();//��Ļ�߶�
		int[] size = {width,height};
		
		return size;
	}
	
	/**
	 * ��ȡ״̬���߶�
	 * @param context
	 * @return
	 */
	public static int getStatusBarHeight(Context context){
		int statusBarHeight = 0;
		// ����ֻ��Ҫ��ȡ��Ļ�߶�
		int screenHeight = getScreenSize(context)[1];
		
		switch(screenHeight){
		case 240:
			statusBarHeight = 20;
			break;
		case 480:
			statusBarHeight = 25;
			break;
		case 800:
			statusBarHeight = 38;
			break;
		default:
			break;
		}
		
		return statusBarHeight;
	}
	//ȥ���ַ��еĿո��Ʊ?���еȷ�� 
		public static String filterString(String str){
			String dest = ""; 
			Pattern p = Pattern.compile("\\s*|\t|\r|\n"); 
			Matcher m = p.matcher(str);   
			dest = m.replaceAll("");
			return dest;
		}
		//�̶��绰У��
		public static boolean isGuDingPhone(String str){
			String regex = "^0\\d{2,3}(\\-)?\\d{7,8}$";
			return str.matches(regex);
		}
		
		//�ֻ����У��
		public static boolean isCellPhone(String str){
			String regex = "^(1(([35][0-9])|(47)|[8][0126789]))\\d{8}$";
			return str.matches(regex);
		}
		//����У��
		public static boolean isEmail(String str) {
			String regex = "^([\\w-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
			return str.matches(regex);
		}
		//IPУ��
		public static boolean isIP(String str) {
			String num = "(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)";
			String regex = "^" + num + "\\." + num + "\\." + num + "\\." + num
					+ "$";
			return str.matches(regex);
		}
		//��ַURLУ��
		public static boolean IsUrl(String str) {
			String regex = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";
			return str.matches(regex);
		}
		//���֤У��
		public static boolean IsIDcard(String str) {
			String regex = "(^\\d{18}$)|(^\\d{15}$)";
			return str.matches(regex);
		}
		//����У��
		public static boolean isDate(String str) {
			String regex = "^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-)) (20|21|22|23|[0-1]?\\d):[0-5]?\\d:[0-5]?\\d$";
			return str.matches(regex);
		}
		//�Ƿ�������
		public static boolean isContainsChinese(String str) { 
			 String regEx = "[\u4e00-\u9fa5]"; 
			 Pattern pat = Pattern.compile(regEx); 
			Matcher matcher = pat.matcher(str); 
			boolean flg = false; 
			if (matcher.find()) { 
				flg = true;
				} 
			return flg;
			}
		//�Ƿ��������ַ�У��
		public static boolean checkString(String qString) {
			String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~��@#��%����&*��������+|{}������������������������]";
			  Pattern p = Pattern.compile(regEx);
			  Matcher m = p.matcher(qString);
			  return m.find();
//			 if(qString.replaceAll("[a-z]*[A-Z]*\\d*_*\\s*", "").length()==0){
//				 return false;
//			 }
//			 return true;
		}
	//��ȡR�ļ��е���ԴID
	public static int getResourceId(Context ctx, String resourceName) {
		return getId(ctx, "id", resourceName);
	}

	public static int getStringId(Context ctx, String stringName) {
		return getId(ctx, "string", stringName);
	}

	private static int getId(Context ctx, String type, String name) {
		return ctx.getResources().getIdentifier(name, type,
				ctx.getPackageName());
	}
	/** 
     * ����ֻ�ķֱ��ʴ� dp �ĵ�λ ת��Ϊ px(����) 
     */  
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
  
    /** 
     * ����ֻ�ķֱ��ʴ� px(����) �ĵ�λ ת��Ϊ dp 
     */  
    public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }  
    
    /** 
     * ����ֻ�ķֱ��ʿ�� 
     */ 
    public static int getScreenWidth(Context context)
    {
    	return ((Activity)context).getWindowManager().getDefaultDisplay().getWidth();
    }
    
    /** 
     * ����ֻ�ķֱ��ʸ߶� 
     */ 
    public static int getScreenHeight(Context context)
    {
    	return ((Activity)context).getWindowManager().getDefaultDisplay().getHeight();
    }
  //���item����listview�߶�
  	public static void setListViewHeight(ListView listView) {
          ListAdapter listAdapter = listView.getAdapter();
          if (listAdapter == null) {
              return;
          }
          int totalHeight = 0;
          for (int i = 0; i < listAdapter.getCount(); i++) {
              View listItem = listAdapter.getView(i, null, listView);
              listItem.measure(0, 0);
              totalHeight += listItem.getMeasuredHeight();
          }
          ViewGroup.LayoutParams params = listView.getLayoutParams();
          params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
//          params.height += 5;//if without this statement,the listview will be a little short
          listView.setLayoutParams(params);
      }
  	public static void setListViewHeight(Context context,ListView listView,int n) {
        int totalHeight = 0;
        totalHeight=n*dip2px(context, context.getResources().getDimension(R.dimen.list_item_height));
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight+ (listView.getDividerHeight() * (n - 1)) ;//
        params.height -= 1;//if without this statement,the listview will be a little short
        listView.setLayoutParams(params);
    }
  	//���item����listview�߶�
  		@SuppressLint("NewApi") public static void setGridViewHeight(Context context,GridView listView,int n) {
  			ListAdapter listAdapter = listView.getAdapter();
  	        if (listAdapter == null) {
  	            return;
  	        }
  	        int totalHeight = 0;
  	        for (int i = 0; i < (listAdapter.getCount()+n-1)/n; i++) {
  	            View listItem = listAdapter.getView(i, null, listView);
  	            listItem.measure(0, 0);
  	            totalHeight += listItem.getMeasuredHeight();
  	        }
  	        ViewGroup.LayoutParams params = listView.getLayoutParams();
  	        int spacing=dip2px(context, 10);
  	        params.height = totalHeight+spacing*((listAdapter.getCount()-1)/n)+listView.getPaddingBottom()+listView.getPaddingTop();
  	        params.height += 5;//if without this statement,the listview will be a little short
  	        listView.setLayoutParams(params);
  	    }
  	//���item����listview�߶�
  		@SuppressLint("NewApi") public static int getGridViewHeight(GridView listView,int n) {
  	        ListAdapter listAdapter = listView.getAdapter();
  	        if (listAdapter == null) {
  	            return 0;
  	        }
  	        int totalHeight = 0;
  	        for (int i = 0; i < (listAdapter.getCount()+1)/n; i++) {
  	            View listItem = listAdapter.getView(i, null, listView);
  	            listItem.measure(0, 0);
  	            totalHeight += listItem.getMeasuredHeight();
  	        }
  	        return totalHeight+5;
  	    }
  	//���item����listview�߶�
  		@SuppressLint("NewApi") public static void setViewHeight(ViewPager listView,int height) {
  	       
   	        
  	          ViewGroup.LayoutParams params = listView.getLayoutParams();
    	        params.height = height;
    	        params.height += 5;//if without this statement,the listview will be a little short
  		    listView.setLayoutParams(params);
  	    }
  		 /**
  	     *
  	     * @param x ͼ��Ŀ��
  	     * @param y ͼ��ĸ߶�
  	     * @param image ԴͼƬ
  	     * @param outerRadiusRat Բ�ǵĴ�С
  	     * @return Բ��ͼƬ
  	     */
  	   public static Bitmap createFramedPhoto(int x, int y, Bitmap image, float outerRadiusRat) {
  	        //���Դ�ļ��½�һ��darwable����
  	        Drawable imageDrawable = new BitmapDrawable(image);

  	        // �½�һ���µ����ͼƬ
  	        Bitmap output = Bitmap.createBitmap(x, y, Bitmap.Config.ARGB_8888);
  	        Canvas canvas = new Canvas(output);

  	        // �½�һ������
  	        RectF outerRect = new RectF(0, 0, x, y);

  	        // ����һ����ɫ��Բ�Ǿ���
  	        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
  	        paint.setColor(Color.RED);
  	        paint.setAntiAlias(true);
  	        canvas.drawRoundRect(outerRect, outerRadiusRat, outerRadiusRat, paint);

  	        // ��ԴͼƬ���Ƶ����Բ�Ǿ�����
  	        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
  	        imageDrawable.setBounds(0, 0, x, y);
  	        canvas.saveLayer(outerRect, paint, Canvas.ALL_SAVE_FLAG);
  	        imageDrawable.draw(canvas);
  	        canvas.restore();

  	        return output;
  	    }
  	  public static Bitmap createFramedPhoto( Bitmap bitmap, float roundPixels) {
	        //���Դ�ļ��½�һ��darwable����
  		 //����һ����ԭʼͼƬһ���Сλͼ
          Bitmap roundConcerImage = Bitmap.createBitmap(bitmap.getWidth(),
                  bitmap.getHeight(), Config.ARGB_8888);
          //��������λͼroundConcerImage�Ļ���
          Canvas canvas = new Canvas(roundConcerImage);
          //��������
          Paint paint = new Paint();
          //����һ����ԭʼͼƬһ���С�ľ���
          Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
          RectF rectF = new RectF(rect);
          // ȥ���
          paint.setAntiAlias(true);
          //��һ����ԭʼͼƬһ���С��Բ�Ǿ���
          canvas.drawRoundRect(rectF, roundPixels, roundPixels, paint);
          //�����ཻģʽ
          paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
          //��ͼƬ��������ȥ
          canvas.drawBitmap(bitmap, null, rect, paint);
          return roundConcerImage;
	    }
}
