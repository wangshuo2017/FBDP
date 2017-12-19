import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.Word;

public class segmentation {
    static String[] data;
    static final String OUT_PATH="test.txt";

    File stock;
    File[] files;
    List<String> pathName = new ArrayList<String>();

	
	/*遍历路径中文件夹内的每个文件*/
    public void iteratorPath(String dir) {
        stock = new File(dir);
        files = stock.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    pathName.add(file.getName());
                }else if (file.isDirectory()) {
                    iteratorPath(file.getAbsolutePath());
                }
            }
        }
    }
	
	/*主函数，利用前面的方法，遍历文件夹，将文档中的新闻标题提取出来，
	依次存入新的文件中*/
	
    public static void main(String[] args) throws Exception {
        segmentation news = new segmentation();
        
        news.iteratorPath("/home/wangshuo/pj2/download_data");
        for (String title : news.pathName) {
            String READ_PATH = ("/home/wangshuo/pj2/download_data/"+title);
            String OUT_PATH = ("/Users/apple/Documents/FBDP/project2/news/"+title);
            /*这里是对project1测试集所在文件夹download_data的操作。对于project2的
			Training Data做类似的操作即可，将地址做一个修改，并将下方BufferReader的注释位置切换到下一行*/
            ArrayList<String> list = new ArrayList<String>();
            File file = new File(READ_PATH);
			/*读取文件*/
//            BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(file),"gbk"));
            BufferedReader br = new BufferedReader(new FileReader(file));
            BufferedWriter bw = new BufferedWriter(new FileWriter(OUT_PATH,true));
            String temp;
            while((temp=br.readLine())!=null){
                list.add(temp);
            }
            br.close();
            /*相关数据存到数组中。只针对Project1测试集使用。project2的训练集直接读入即可*/
            data = new String[6];
            for (int i = 0; i < list.size(); i++) {
                data = list.get(i).split("  ");
                if(data.length!=5||!data[4].contains("http")||data[3].contains("http")||data[3].isEmpty()) {
                    continue;
                }
                //此处是对新闻标题进行分词。与之前类似，调整注释位置可以对project2的训练集进行操作。
//                List<Word> tempseg = WordSegmenter.seg(list.toString());
                List<Word> tempseg = WordSegmenter.seg(data[3]);
                //把新闻的标题输出到文件
                String titles = StringUtils.strip(tempseg.toString().replaceAll("[,.%/(A-Za-z0-9)]",""),"[]");
                titles = titles.replace("\\s+"," ");
                titles = titles.replace("|"," ");
                bw.write(titles);
                bw.newLine();
            }
            bw.close();
        }
    }
}
