package com.manonthegithub.hadoopfun;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.StringTokenizer;


public class HadoopFunApp {

    public static void main(String... args) throws Exception {

        Configuration config = new Configuration();
        Job job = Job.getInstance(config, "FunHadoopJob");
        job.setMapperClass(FunMapper.class);
        job.setCombinerClass(SumRed.class);
        job.setReducerClass(SumRed.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job, new Path("tmp"));
        FileOutputFormat.setOutputPath(job, new Path("tmp/out"));
        System.exit(job.waitForCompletion(true) ? 0 : 1);


    }

}

class FunMapper extends Mapper<Object, Text, Text, IntWritable> {

    private Text resKey = new Text("out");

    @Override
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        StringTokenizer itr = new StringTokenizer(value.toString());
        itr.nextToken();
        int val = Integer.parseInt(itr.nextToken());
        context.write(resKey, new IntWritable(val));
    }


}

class SumRed extends Reducer<Text, IntWritable, Text, IntWritable> {
    private IntWritable result = new IntWritable();

    public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        int sum = 0;
        for (IntWritable val : values) {
            sum += val.get();
        }
        result.set(sum);
        context.write(key, result);
    }
}
