package cn.pso.cmdpsofs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class Data {

	private static boolean jud(Double[] d){
		for(int i=0;i<d.length-1;i++){
			for(int j=i+1;j<d.length;j++){
				if(d[i] == d[j]){
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * 将同一个迭代次数结果集合起来
	 * @param name
	 * @param time
	 * @param type
	 * @param g
	 * @throws IOException
	 */
	private void date(String name,int time,String type,int g) throws IOException{
		String path = "D:\\Vincent_Kwok\\KNN of "+type+"\\testfile\\"+name;
		String path1 = "D:\\Vincent_Kwok\\KNN of "+type+"\\testfile\\"+name+"\\iteration";
		File f = new File(path1);
		if(!f.isDirectory()){
			f.mkdir();
		}
		int times = time/10;
		new File(path1+"\\"+"a-1-"+name+"-"+type+".dat").createNewFile();
		PrintWriter pw = new PrintWriter(new FileOutputStream(path1+"\\"+"a-1-"+name+"-"+type+".dat"),true);
		for(int j=0;j<g;j++){
			@SuppressWarnings("resource")
			BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(path+"\\1-"+j+name+"-"+type+"-result.dat")));
			while(true){
				String data = bf.readLine();
				if(data == null){
					break;
				}
				pw.write(data);
				pw.write("\n");
				pw.flush();
			}
		}
		pw.close();
		for(int i=1;i<times+1;i++){
			new File(path1+"\\"+"a-"+i+"0-"+name+"-"+type+".dat").createNewFile();
			PrintWriter pw1 = new PrintWriter(new FileOutputStream(path1+"\\"+"a-"+i+"0-"+name+"-"+type+".dat"),true);
			for(int j=0;j<g;j++){
				@SuppressWarnings("resource")
				BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(path+"\\"+i+"0-"+j+name+"-"+type+"-result.dat")));
				while(true){
					String data = bf.readLine();
					if(data == null){
						break;
					}
					pw1.write(data);
					pw1.write("\n");
					pw1.flush();
				}
			}
			pw1.close();
		}
	}
	
	/**
	 * 计算同一个迭代次数下所有最优结果
	 * @param name
	 * @param type
	 * @throws IOException
	 */
	private void getResult(String name,String type) throws IOException{
		File f = new File("D:\\Vincent_Kwok\\KNN of "+type+"\\testfile\\"+name+"\\iteration");
		String path = "D:\\Vincent_Kwok\\KNN of "+type+"\\testfile\\"+name+"\\iteration";
		File[] fl =  f.listFiles();
		for(int i=0;i<fl.length;i++){
			if(fl[i].getName().startsWith("All")){
				continue;
			}else{
				Map<Double, Double> m = new HashMap<Double, Double>();
				@SuppressWarnings("resource")
				BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(path+"\\"+fl[i].getName())));
				while(true){
					String d = bf.readLine();
					if(d == null){
						break;
					}
					String[] data = d.split(" ");
					double num = Double.valueOf(data[0]);
					double error = Double.valueOf(data[1]);
					try{
						double error1 = (Double) m.get(num);
						if(error<error1){
							m.put(num, error);
						}
					}catch(Exception e){
						m.put(num, error);
					}
				}
				File fq = new File(path+"\\"+"b-"+fl[i].getName());
				if(!fq.isFile()){
					fq.createNewFile();
				}
				PrintWriter pw = new PrintWriter(fq);
				Iterator<?> it = m.entrySet().iterator();
				while(it.hasNext()){
					@SuppressWarnings("unchecked")
					Map.Entry<Double, Double> en = (Entry<Double, Double>) it.next();
					Double num = en.getKey();
					Double error = en.getValue();
					pw.write(num+" "+error+"\n");
					pw.flush();
				}
				pw.close();
			}
			
		}
	}
	
	
	public static void main(String[] args) throws IOException {
		Data d = new Data();
//		d.date("zoo", 100, "CMD", 30);
		d.getResult("zoo", "CMD");
	}

}
