import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class vectorization {

    private static final int dimension = 1000;
    private File stock;
    private File[] files;
    private List<String> pathName = new ArrayList<String>();

    public static void main(String[] args) throws Exception {
        //用list记录模型的词
        String READ_PATH = ("/home/wangshuo/pj2/chi_words.txt");
        ArrayList<String> dic = new ArrayList<String>();
        File file = new File(READ_PATH);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String t;
        while ((t = br.readLine()) != null) {
            dic.add(t);
        }
        br.close();
        String[] vector = new String[dimension];

        Integer id = 0;
        vectorization news = new vectorization();
        news.iteratorPath("/home/wangshuo/pj2/test_tfidf/");
        for (String title : news.pathName) {
            String INPUT_PATH = ("/home/wangshuo/pj2/test_tfidf/" + title);
            String OUT_PATH = "testing_vectors";
			/*下面实现了依据chi_words的词汇，在输入路径的文件中对各个词进行搜索，若某个词在
			chi_words的1000个词中，则将tfidf值赋在相应的位置，生成向量。
			此处是对测试集向量化的结果，对于训练集的操作还需在每行的末尾加上代表情感的数字编号。*/
            File file1 = new File(INPUT_PATH);
            for (int i = 0; i < vector.dimension; i++) {
                vector[i] = "0";
            }
            BufferedReader br1 = new BufferedReader(new FileReader(file1));
            BufferedWriter bw1 = new BufferedWriter(new FileWriter(OUT_PATH, true));
            String temp;
            String[] temp1 = new String[2];
            int k = 0;
            while ((temp = br1.readLine()) != null) {
                temp1 = temp.split(" ");
                k = dic.indexOf(temp1[0]);
                if (k != -1) {
                    vector[k] = temp1[1];
                }
            }
            br1.close();
        }
    }
	
	public void iteratorPath(String dir) {
        stock = new File(dir);
        files = stock.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    pathName.add(file.getName());
                } else if (file.isDirectory()) {
                    iteratorPath(file.getAbsolutePath());
                }
            }
        }
    }
}
