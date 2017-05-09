package cn.pso.cmdpsofs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class SortBest_CMDPSOFS {
	public void getCMDResult(String dataSetName,List<Particle_CMDPSOFS> list,String type) throws IOException {
		String path1 = "testfile\\"+dataSetName;
		File f = new File(path1);
		if(!f.exists()){
			f.mkdir();
		}
		String path = path1+"\\"+type+dataSetName+"-CMD-result.dat";
		File file = new File(path);
		if(file.isFile()){
			file.delete();
		}
		file.createNewFile();
		PrintWriter pw = new PrintWriter(new FileOutputStream(path, true));
		
		for(int i = 0;i<list.size();i++){
			for(int j = list.size()-1;j>i;j--){
				if(list.get(i).pbest_fitness[1]==list.get(j).pbest_fitness[1]){
					if(list.get(i).pbest_fitness[0]<=list.get(j).pbest_fitness[0]){
						list.remove(j);
					}else{
						list.set(i, list.get(j));
						list.remove(j);
					}
				}
			}
		}
		
		List<Particle_CMDPSOFS> nonDomS = new ArrayList<Particle_CMDPSOFS>();
	   	 for (int i = 0; i < list.size();i++){
	     	int index = -1; 
	     	for (int j = 0; j < list.size(); j++){
	         	if(list.get(j).pbest_fitness[0] <= list.get(i).pbest_fitness[0]&&list.get(j).pbest_fitness[1] <list.get(i).pbest_fitness[1]){
	         		index=1;
	         	}else if(list.get(j).pbest_fitness[0] < list.get(i).pbest_fitness[0]&&list.get(j).pbest_fitness[1] <=list.get(i).pbest_fitness[1]){
	         		index=1;
	         	}
	         }
	     	
	     	for(int k = 0;k<nonDomS.size();k++){
	     		if(nonDomS.get(k).pbest_fitness[0] == list.get(i).pbest_fitness[0]&&nonDomS.get(k).pbest_fitness[1] == list.get(i).pbest_fitness[1]){
	     			index=1;
	     		}
	     	}
	     	
	         if(index == -1){
	         	nonDomS.add(list.get(i));
	         }
	     }
		for(Particle_CMDPSOFS p:nonDomS){
			System.out.println(p.pbest_fitness[0]+"-------"+p.pbest_fitness[1]);
			
	    	List<String> choose = new ArrayList<String>();
	    	for(int i=0;i<p.pbest.length;i++){
	    		if(p.pbest[i]>0.6){
	    			choose.add(String.valueOf(i+1));
	    		}
	    	}
			
			for(int i=0;i<choose.size();i++){
				if(i==choose.size()-1){
					pw.write(String.valueOf(choose.get(i)));
					continue;
				}
				pw.write(String.valueOf(choose.get(i))+" ");
			}
			pw.write("*"+p.pbest_fitness[0]+" "+p.pbest_fitness[1]);
			pw.write("\n");
			pw.flush();
		}
		pw.close();
		System.out.println(dataSetName+":CMDdone!");
	}
	public void record(double[][] recordFe,double[][] recordEr,String dataSetName) throws IOException{
		String path = "testfile\\"+dataSetName+"--fe--record.dat";
		String path2 = "testfile\\"+dataSetName+"--er--record.dat";
		new File(path).createNewFile();
		new File(path2).createNewFile();
		PrintWriter printWriterFe = new PrintWriter(new FileOutputStream(path,true));
		PrintWriter printWriterEr = new PrintWriter(new FileOutputStream(path2,true));
		for(int i=0;i<recordFe[0].length;i++){
			double er = recordFe[0][i];
			double fe = recordFe[0][i];
			double argEr = 0;
			double argFe = 0;
			for(int j=0;j<recordFe.length;j++){
				argEr+=recordEr[j][i];
				argFe+=recordFe[j][i];
				
				if(er>recordEr[j][i]){
					er = recordEr[j][i];
				}
				if(fe>recordFe[j][i]){
					fe = recordFe[j][i];
				}
			}
			argEr = argEr/30;
			argFe = argFe/30;
			printWriterFe.write((i+1)+" "+fe+" "+argFe+"\n");
			printWriterEr.write((i+1)+" "+er+" "+argEr+"\n");
			printWriterEr.flush();
			printWriterFe.flush();
		}
		printWriterEr.close();
		printWriterFe.close();
		
	}
}
