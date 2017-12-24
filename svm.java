import java.io.IOException;

public class svm{  
    public static void main(String[] args) throws IOException {  
                String[] arg = {"-t","2" ,"/home/wangshuo/eclipse/workspace/svm/src/svm_train_matrix.txt", //训练集  
                        "/home/wangshuo/eclipse/workspace/svm/src/model.txt" }; // 存放SVM训练模型  
  
  
        String[] parg = { "/home/wangshuo/eclipse/workspace/svm/src/svm_test_matrix", //测试数据  
                           "/home/wangshuo/eclipse/workspace/svm/src/model.txt", // 调用训练模型  
                           "/home/wangshuo/eclipse/workspace/svm/src/svm_result.txt" }; //预测结果  
        System.out.println("........SVM运行开始..........");  
        long start=System.currentTimeMillis();   
        svm_train.main(arg); //训练  
        System.out.println("用时:"+(System.currentTimeMillis()-start));   
        //预测  
       // svm_predict.main(parg);   
    }  
}  