package com.lizhi.app.printcode;

import com.lizhi.app.printcode.KeywordTrigger.KeywordTriggerHandle;

public class TestHtmlString {
	
	public static void testTrigger() {
		// 加粗：<b></b>
		// 居中：<c></c>
		// 加宽：<w></w>
		// tab:</t>
		// 斜体：<i></i>
		// 左对齐：<l></l>
		// 右对齐：<r></r>
		// 一维码：<bc></bc>
		// 二维码：<qc></qc>
		// 换行：</br>
		// 下划线：<ul></ul>
		// 一倍字体：<1></1>
		// 二倍字体：<2></2>
		// 三倍字体：<3></3>
		// 四倍字体：<4></4>
		// 五倍字体：<5></5>
		// 六倍字体：<6></6>
		// 七倍字体：<7></7>
		// 八倍字体：<8></8>
		// 图片：<img></img> 放地址,png
		
		String[] keywords = {
				"<b>", "</b>", 
				"<c>", "</c>", 
				"<w>", "</w>", 
				"<t>", "</t>", 
				"<l>", "</l>", 
				"<r>", "</r>", 
				"<bc>", "</bc>", 
				"<qc>", "</qc>", 
				"</br>",  
				"<ul>", "</ul>", 
				"<img>", "</img>", 
				"<1>", "</1>", 
				"<2>", "</2>", 
				"<3>", "</3>", 
				"<4>", "</4>",
				"<5>", "</5>",
				"<6>", "</6>",
				"<7>", "</7>",
				"<8>", "</8>"};
		KeywordTrigger trigger = new KeywordTrigger(keywords);
		trigger.setSource("<b>这是标题</b><br><36>hello，这是36号字 体</36>okay,信息分析完成。");
		trigger.setHandle(new KeywordTriggerHandle() {
			public void trigger(String str, String keyword) {
				
			}
		});
		trigger.parse();
		
		PrinterBuilder pb = new PrinterBuilder();
		String s = pb.center(pb.bold("POS签购单")) + pb.br();
		s += pb.normal("单号：") + "0000269131126133160602" + pb.br();
		s += pb.normal("类型：点单") + pb.br();
		s += pb.normal("桌号：01") + pb.br();
		s += pb.normal("操作员：01") + pb.br();
		s += pb.normal("*********************") + pb.br();
		s += pb.br();
		s += pb.tab("项目") + pb.tab("数量") + pb.tab("单价") + pb.tab("小计") + pb.br();
		s += pb.tab("红烧大排") + pb.tab("1") + pb.tab("6.00") + pb.tab("6.00") + pb.br();
		s += pb.tab("菜肉大混沌") + pb.tab("1") + pb.tab("10.00") + pb.tab("10.00") + pb.br();
		s += pb.normal("*********************") + pb.br();
		s += pb.br();
		s += pb.tab(pb.tab("总计：16.00")) + pb.br();
		s += pb.tab(pb.tab("已付：16.00")) + pb.br();
	}
	
	class PrinterKeywordTriggerHandle extends KeywordTriggerHandle{

		@Override
		public void trigger(String str, String keyword) {
			// TODO Auto-generated method stub
			
		}
		
		private void startTrigger(String  str , String keyword){
			if(keyword.equals("<b>")){
				
			}else if(keyword.equals("<c>")){
				
			}else if(keyword.equals("<w>")){
				
			}else if(keyword.equals("<i>")){
				
			}else if(keyword.equals("<l>")){
				
			}else if(keyword.equals("<r>")){
				
			}else if(keyword.equals("<bc>")){
				
			}else if(keyword.equals("<ul>")){
				
			}else if(keyword.equals("<1>")){
				
			}else if(keyword.equals("<2>")){
				
			}else if(keyword.equals("<3>")){
				
			}else if(keyword.equals("<4>")){
				
			}else if(keyword.equals("<5>")){
				
			}else if(keyword.equals("<6>")){
				
			}else if(keyword.equals("<7>")){
				
			}else if(keyword.equals("<8>")){
				
			}else if(keyword.equals("<img>")){
				
			}
		}
		private void endTrigger(String  str , String keyword){
			if(keyword.equals("</b>")){
				
			}else if(keyword.equals("</c>")){
				
			}else if(keyword.equals("</w>")){
				
			}else if(keyword.equals("</t>")){
				
			}else if(keyword.equals("</l>")){
				
			}else if(keyword.equals("</r>")){
				
			}else if(keyword.equals("</bc>")){
				
			}else if(keyword.equals("</>")){
				
			}
		}
		
	}

}
