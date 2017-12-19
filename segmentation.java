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

	
	/*����·�����ļ����ڵ�ÿ���ļ�*/
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
	
	/*������������ǰ��ķ����������ļ��У����ĵ��е����ű�����ȡ������
	���δ����µ��ļ���*/
	
    public static void main(String[] args) throws Exception {
        segmentation news = new segmentation();
        
        news.iteratorPath("/home/wangshuo/pj2/download_data");
        for (String title : news.pathName) {
            String READ_PATH = ("/home/wangshuo/pj2/download_data/"+title);
            String OUT_PATH = ("/Users/apple/Documents/FBDP/project2/news/"+title);
            /*�����Ƕ�project1���Լ������ļ���download_data�Ĳ���������project2��
			Training Data�����ƵĲ������ɣ�����ַ��һ���޸ģ������·�BufferReader��ע��λ���л�����һ��*/
            ArrayList<String> list = new ArrayList<String>();
            File file = new File(READ_PATH);
			/*��ȡ�ļ�*/
//            BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(file),"gbk"));
            BufferedReader br = new BufferedReader(new FileReader(file));
            BufferedWriter bw = new BufferedWriter(new FileWriter(OUT_PATH,true));
            String temp;
            while((temp=br.readLine())!=null){
                list.add(temp);
            }
            br.close();
            /*������ݴ浽�����С�ֻ���Project1���Լ�ʹ�á�project2��ѵ����ֱ�Ӷ��뼴��*/
            data = new String[6];
            for (int i = 0; i < list.size(); i++) {
                data = list.get(i).split("  ");
                if(data.length!=5||!data[4].contains("http")||data[3].contains("http")||data[3].isEmpty()) {
                    continue;
                }
                //�˴��Ƕ����ű�����зִʡ���֮ǰ���ƣ�����ע��λ�ÿ��Զ�project2��ѵ�������в�����
//                List<Word> tempseg = WordSegmenter.seg(list.toString());
                List<Word> tempseg = WordSegmenter.seg(data[3]);
                //�����ŵı���������ļ�
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
