package org.bigdata.mapreduce.knn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.bigdata.util.DistanceUtil;
import org.bigdata.util.HadoopCfg;
import org.bigdata.util.HadoopUtil;

public class KNNMapReduce {

    public static final String POINTS = "testing_vectors.txt";
    public static final int K = 3;
    public static final int TYPES = 3;
    private static final String JOB_NAME = "knn";

    // train-points
    private static List<Point> trans_points = new ArrayList<>();

    public static void initPoints(String pathin, String filename)
            throws IOException {
        List<String> lines = HadoopUtil.lslFile(pathin, filename);
        for (String line : lines) {
            trans_points.add(new Point(line));
        }
    }

    public static class KNNMapper extends
            Mapper<LongWritable, Text, Text, Text> {

        @Override
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            FileSplit fileSplit = (FileSplit) context.getInputSplit();
            String fileName = fileSplit.getPath().getName();
            if (POINTS.equals(fileName)) {
                Point point1 = new Point(value.toString());
                try {
                    for (Point point2 : trans_points) {
                        double dis = DistanceUtil.getEuclideanDisc(
                                point1.getV(), point2.getV());
                        context.write(new Text(point1.toString()), new Text(
                                point2.getType() + ":" + dis));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static class KNNReducer extends
            Reducer<Text, Text, Text, IntWritable> {

        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            List<DistanceType> list = new ArrayList<>();
            for (Text value : values) {
                list.add(new DistanceType(value.toString()));
            }
            Collections.sort(list);
            int cnt[] = new int[TYPES + 1];
            for (int i = 0, len = cnt.length; i < len; i++) {
                cnt[i] = 0;
            }
            for (int i = 0; i < K; i++) {
                cnt[list.get(i).getType()]++;
            }
            int type = 0;
            int maxx = Integer.MIN_VALUE;
            for (int i = 1; i <= TYPES; i++) {
                if (cnt[i] > maxx) {
                    maxx = cnt[i];
                    type = i;
                }
            }
            context.write(key, new IntWritable(type));
        }

    }

    public static void solve(String pointin, String pathout)
            throws ClassNotFoundException, InterruptedException {
        try {
            Configuration cfg = HadoopCfg.getConfiguration();
            Job job = Job.getInstance(cfg);
            job.setJobName(JOB_NAME);
            job.setJarByClass(KNNMapReduce.class);

            // mapper
            job.setMapperClass(KNNMapper.class);
            job.setMapOutputKeyClass(Text.class);
            job.setMapOutputValueClass(Text.class);

            // reducer
            job.setReducerClass(KNNReducer.class);
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(IntWritable.class);

            FileInputFormat.addInputPath(job, new Path(pointin));
            FileOutputFormat.setOutputPath(job, new Path(pathout));

            job.waitForCompletion(true);

        } catch (IllegalStateException | IllegalArgumentException | IOException e) {
            e.printStackTrace();
        }
    }
	
	//生成结果文件
    public static void main(String[] args) throws ClassNotFoundException,
            InterruptedException, IOException {
        initPoints("/knn", "training_vectors.txt");
        solve("/knn", "/knn_testing_result");
    }
	
	public class DistanceType implements Comparable<DistanceType> {

		private double distance;
		private int type;

		public double getDistance() {
			return distance;
		}

		public void setDistance(double distance) {
			this.distance = distance;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public DistanceType(String s) {
			super();
			String terms[] = s.split(":");
			this.type = Integer.valueOf(terms[0]);
			this.distance = Double.valueOf(terms[1]);
		}

		@Override
		public int compareTo(DistanceType o) {
			return this.getDistance() > o.getDistance() ? 1 : -1;
		}

		@Override
		public String toString() {
			return "DistanceType [distance=" + distance + ", type=" + type + "]";
		}

	}
	
	public class Point {

		private int type;
		private String strpoint;
		private Vector<Double> v = new Vector<>();

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public Vector<Double> getV() {
			return v;
		}

		public void setV(Vector<Double> v) {
			this.v = v;
		}

		public Point(String s) {
			super();
			this.strpoint=s;
			String terms[]=s.split(" ");
			for(int i=0,len=terms.length;i<len-1;i++){
				this.v.add(Double.valueOf(terms[i]));
			}
			this.type=Integer.valueOf(terms[terms.length-1]);
		}

		public Point() {
			super();
		}

		@Override
		public String toString() {
			return this.strpoint;
		}
	}
}