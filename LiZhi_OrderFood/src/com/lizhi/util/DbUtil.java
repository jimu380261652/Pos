package com.lizhi.util;
import java.io.File;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.duiduifu.bean.Version;
import com.duiduifu.util.CommonUtil;
/**
 * 
 * @ClassName: DbUtil.java 
 *
 * @Description: SQLITE数据库操作类
 *
 * @author YangTaoLin
 *
 * @date 2013-12-12 上午18:06:09 
 *
 */
public class DbUtil extends SQLiteOpenHelper {

	private static final String DBNAME = "fanke.db";// 数据库名称

	private static final int VERSION = 10; // 数据库版本

	private CommonUtil commonUtil;
	
	private Context mContext;
	
	public DbUtil(Context context) {

		super(context, DBNAME, null, VERSION);
		
		mContext = context;

		commonUtil=new CommonUtil(context);
		
		initDataBase();
		

		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
	
	//初始化数据库
	public void initDataBase(){
		File file_db = new File(commonUtil.getSDCardPath()+DBNAME);
		
		if (!file_db.exists()) {
			try {
				file_db.canExecute();
				file_db.setExecutable(true, false);
				file_db.setReadable(true, false);
				file_db.setWritable(true, false);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
		
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			
			db = SQLiteDatabase.openOrCreateDatabase(
					commonUtil.getDataBaseFile(DBNAME), null);
			
		    
		    
		    
		    try{
		    	
		    	cursor = db.query(Version.TABLE_NAME, null,  null, null, null, null, null);
		    	
		    	if(cursor!=null && cursor.getCount()<=0){
		    		if (Integer.valueOf(cursor.getString(cursor.getColumnIndex(Version.VERSION_CODE)))<VERSION) {
		    			ContentValues cv = new ContentValues();
		    			
		    			cv.put(Version.VERSION_CODE, VERSION);
		    			
		    			cv.put(Version.VERSION_NAME, "The one");
		    			System.out.println("-------------更新");
					}else {
						System.out.println("---------------不更新");
					}
		    		
		    	}else {
				}
		    	
		    }catch(Exception e){
		    	e.printStackTrace();
		    }finally{
		    	if(cursor!=null){
		    		cursor.close();
		    	}
		    }
		    
				
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(db!=null){
					  db.close();
				}
			}
	}
	
	
	
}
