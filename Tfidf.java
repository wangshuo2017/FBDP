import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class Tfidf {

    private static ArrayList<String> FileList = new ArrayList<String>(); // the list of file

    //obtain the filenames
    public static String getFileNameWithSuffix(String pathandname) {
        int start = pathandname.lastIndexOf("/");
        if (start != -1) {
            return pathandname.substring(start + 1);
        } else {
            return null;
        }
    }

    //get list of file for the directory, including sub-directory of it
    public static List<String> readDirs(String filepath) throws FileNotFoundException, IOException {
        try {
            File file = new File(filepath);
            if (!file.isDirectory()) {
                System.out.println("输入的[]");
                System.out.println("filepath:" + file.getAbsolutePath());
            } else {
                String[] flist = file.list();
                for (int i = 0; i < flist.length; i++) {
                    File newfile = new File(filepath + flist[i]);
                    if (!newfile.isDirectory()) {
                        FileList.add(newfile.getAbsolutePath());
                    } else if (newfile.isDirectory()) {
                        readDirs(filepath + flist[i] + '/');
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return FileList;
    }

    //read file
    public static String readFile(String file) throws FileNotFoundException, IOException {
        StringBuffer strSb = new StringBuffer(); //String is constant, StringBuffer can be changed.
        InputStreamReader inStrR = new InputStreamReader(new FileInputStream(file), "gbk"); //byte streams to character streams
        BufferedReader br = new BufferedReader(inStrR);
        String line = br.readLine();
        while (line != null) {
            strSb.append(line).append("\r\n");
            line = br.readLine();
        }

        return strSb.toString();
    }

    //word segmentation
    public static ArrayList<String> cutWords(String file) throws IOException {
        ArrayList<String> words = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(file)), "utf-8"));
        String s = null;
        while ((s = reader.readLine()) != null) {
            String cutWordResult[] = s.split(" ");
            for (int i = 0; i < cutWordResult.length; i++) {
                words.add(cutWordResult[i]);
            }
        }
        reader.close();
        return words;
    }

    //term frequency in a file, times for each word
    public static HashMap<String, Integer> normalTF(ArrayList<String> cutwords) {
        HashMap<String, Integer> resTF = new HashMap<String, Integer>();

        for (String word : cutwords) {
            if (resTF.get(word) == null) {
                resTF.put(word, 1);
            } else {
                resTF.put(word, resTF.get(word) + 1);
            }
        }
        return resTF;
    }

    //term frequency in a file, frequency of each word
    public static HashMap<String, Float> tf(ArrayList<String> cutwords) {
        HashMap<String, Float> resTF = new HashMap<String, Float>();

        int wordLen = cutwords.size();
        HashMap<String, Integer> intTF = Tfidf.normalTF(cutwords);

        Iterator iter = intTF.entrySet().iterator(); //iterator for that get from TF
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            resTF.put(entry.getKey().toString(), Float.parseFloat(entry.getValue().toString()) / wordLen);
//          System.out.println(entry.getKey().toString() + " = "+  Float.parseFloat(entry.getValue().toString()) / wordLen);
        }
        return resTF;
    }

    //tf times for file
    public static HashMap<String, HashMap<String, Integer>> normalTFAllFiles(String dirc) throws IOException {
        HashMap<String, HashMap<String, Integer>> allNormalTF = new HashMap<String, HashMap<String, Integer>>();
        List<String> filelist = Tfidf.readDirs(dirc);
        for (String file : filelist) {
            HashMap<String, Integer> dict = new HashMap<String, Integer>();
            ArrayList<String> cutwords = Tfidf.cutWords(file); //get cut word for one file
            dict = Tfidf.normalTF(cutwords);
            allNormalTF.put(file, dict);
        }
        return allNormalTF;
    }

    //tf for all file
    public static HashMap<String, HashMap<String, Float>> tfAllFiles(String dirc) throws IOException {
        HashMap<String, HashMap<String, Float>> allTF = new HashMap<String, HashMap<String, Float>>();
        List<String> filelist = Tfidf.readDirs(dirc);
        System.out.print(filelist);
        for (String file : filelist) {
            HashMap<String, Float> dict = new HashMap<String, Float>();
            ArrayList<String> cutwords = Tfidf.cutWords(file); //get cut words for one file

            dict = Tfidf.tf(cutwords);
            allTF.put(file, dict);
        }
        return allTF;
    }

    public static HashMap<String, Float> idf(HashMap<String, HashMap<String, Float>> all_tf) throws IOException {
        HashMap<String, Float> resIdf = new HashMap<String, Float>();
        HashMap<String, Integer> dict = new HashMap<String, Integer>();
        int docNum = FileList.size();

        for (int i = 0; i < docNum; i++) {
            HashMap<String, Float> temp = all_tf.get(FileList.get(i));
            Iterator iter = temp.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                String word = entry.getKey().toString();
                if (dict.get(word) == null) {
                    dict.put(word, 1);
                } else {
                    dict.put(word, dict.get(word) + 1);
                }
            }
        }
        System.out.println("IDF for every word is:");
        Iterator iter_dict = dict.entrySet().iterator();
        while (iter_dict.hasNext()) {
            Map.Entry entry = (Map.Entry) iter_dict.next();
            float value = (float) Math.log(docNum / Float.parseFloat(entry.getValue().toString()));
            resIdf.put(entry.getKey().toString(), value);
            //这里输入的是key值和value值,每个词对应的idf
//          System.out.println(entry.getKey().toString() + " == " + value);
        }
        return resIdf;
    }

    public static void tf_idf(HashMap<String, HashMap<String, Float>> all_tf, HashMap<String, Float> idfs, String putpath) throws IOException {
        HashMap<String, HashMap<String, Float>> resTfIdf = new HashMap<String, HashMap<String, Float>>();

        int docNum = FileList.size();
        for (int i = 0; i < docNum; i++) {
            String filepath = FileList.get(i);
            HashMap<String, Float> tfidf = new HashMap<String, Float>();
            HashMap<String, Float> temp = all_tf.get(filepath);
            Iterator iter = temp.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                String word = entry.getKey().toString();
                Float value = (float) Float.parseFloat(entry.getValue().toString()) * idfs.get(word) * 10000;
                Integer tem = Math.round(value);
                Float myvalue = (float) tem;
                tfidf.put(word, myvalue);
            }
            resTfIdf.put(filepath, tfidf);
        }
        DisTfIdf(resTfIdf, putpath);
    }

    //排序算法
    public static void Rank(HashMap<String, Float> wordmap, String filename) throws IOException {
        BufferedWriter Writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(filename)), "utf-8"));
        List<String> wordgaopindipin = new ArrayList<String>();
        List<Map.Entry<String, Float>> list = new ArrayList<Map.Entry<String, Float>>(wordmap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Float>>() {
            //降序排序
            public int compare(Entry<String, Float> o1, Entry<String, Float> o2) {
                //return o1.getValue().compareTo(o2.getValue());
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        //排序靠前的60个词及权值
        if (list.size() > 60) {
            for (int i = 0; i < 58; i++) {
                //写入文件
                wordgaopindipin.add(list.get(i).getKey());
                Writer.append(list.get(i).getKey() + " " + list.get(i).getValue() + "\r\n");
            }
        } else {
            for (int i = 0; i < list.size() - 1; i++) {
                //写入文件
                System.out.println(i);
                wordgaopindipin.add(list.get(i).getKey());
                Writer.append(list.get(i).getKey() + " " + list.get(i).getValue() + "\r\n");
            }
        }

        Writer.close();
    }

    public static void DisTfIdf(HashMap<String, HashMap<String, Float>> tfidf, String outpath) throws IOException {
        Iterator iter1 = tfidf.entrySet().iterator();
        while (iter1.hasNext()) {
            Map.Entry entrys = (Map.Entry) iter1.next();
            System.out.println("FileName: " + getFileNameWithSuffix(entrys.getKey().toString()));
            HashMap<String, Float> temp = (HashMap<String, Float>) entrys.getValue();
            //将排序结果输入到文本
            Rank(temp, outpath + getFileNameWithSuffix(entrys.getKey().toString()));
            //这里使用排序输出
        }
    }

    public static void main(String[] args) throws IOException {
        // 输入目录。此处为测试集文件夹。若为训练集文件夹，则将路径修改后重新编译运行即可
        String inputpath = "/home/wangshuo/pj2/download_data/";
        //String inputpath = "/home/wangshuo/pj2/TrainingData/";		
		
		//输出目录
        String outpath = "/home/wangshuo/pj2/test_tfidf/";
		//String outpath = "/home/wangshuo/pj2/train_tfidf/";
        HashMap<String, HashMap<String, Float>> all_tf = tfAllFiles(inputpath);
        System.out.println();
        HashMap<String, Float> idfs = idf(all_tf);
        tf_idf(all_tf, idfs, outpath);
    }
}
