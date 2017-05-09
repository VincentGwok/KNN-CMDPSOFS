package cn.pso.cmdpsofs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.read.ReadFile;
import cn.read.Util;

public class Process_CMDPSOFS {
	   /** 
     * 粒子群 
     */  
	Particle_CMDPSOFS[] swarm;  									//粒子群
    Particle_CMDPSOFS global_best;									//全局最优解  
    int pcount;														//粒子的数量  
    
    //CMDPSOFS群体的3个子集
    List<Particle_CMDPSOFS> swarm1 = new ArrayList<Particle_CMDPSOFS>();
    List<Particle_CMDPSOFS> swarm2 = new ArrayList<Particle_CMDPSOFS>();
    List<Particle_CMDPSOFS> swarm3 = new ArrayList<Particle_CMDPSOFS>();
    
    List<Particle_CMDPSOFS> LeaderSet;
    List<Particle_CMDPSOFS> Archive;
    
    public static double[][] recordFe = new double[30][100];
    public static double[][] recordEr = new double[30][100];
    
    public static String name;										//数据集名
    
    /**
     * 将粒子集按照顺序排列
     */
    public List<Particle_CMDPSOFS> sortSet(List<Particle_CMDPSOFS> LeaderSet){
    	Particle_CMDPSOFS p;
        for (int i = 0; i < LeaderSet.size(); i++){
            for (int j = i+1; j < LeaderSet.size(); j++){
            	if(LeaderSet.get(i).pbest_fitness[0] < LeaderSet.get(j).pbest_fitness[0]){
            		p = LeaderSet.get(i);
            		LeaderSet.set(i, LeaderSet.get(j));
            		LeaderSet.set(j, p);
            	}
            }
        }
        return LeaderSet;
    }
    
    /**
     * 将粒子集按照拥挤距离排列
     * @param LeaderSet
     * @return
     * @throws IOException
     */
    public List<Particle_CMDPSOFS> crowdingSort(List<Particle_CMDPSOFS> LeaderSet) throws IOException{
        List<Particle_CMDPSOFS> temp = LeaderSet;
        Particle_CMDPSOFS p;
        for (int i = 0; i < LeaderSet.size()-1; i++){
        	double di;
        	if(i==0){
        		di = Double.POSITIVE_INFINITY;		//第一个和最后一个粒子拥挤距离为无穷大
        		LeaderSet.get(i).dit = di;
        	}else{
        		di = (temp.get(i+1).pbest_fitness[0]-temp.get(i-1).pbest_fitness[0])/ReadFile.getFeatureNum(name);
        		di+=temp.get(i+1).pbest_fitness[1]-temp.get(i-1).pbest_fitness[1];
        		LeaderSet.get(i).dit = di;
        	}
            for (int j = i+1; j < LeaderSet.size();j++){
            	double dj;
            	if(j==LeaderSet.size()-1){
            		dj = Double.POSITIVE_INFINITY;		//第一个和最后一个粒子拥挤距离为无穷大
            		LeaderSet.get(j).dit = di;
            	}else{
            		dj = (temp.get(j+1).pbest_fitness[0]-temp.get(j-1).pbest_fitness[0])/ReadFile.getFeatureNum(name);
            		dj+=temp.get(j+1).pbest_fitness[1]-temp.get(j-1).pbest_fitness[1];
            	}
            	if(di > dj){
            		p = LeaderSet.get(i);
            		LeaderSet.set(i, LeaderSet.get(j));
            		LeaderSet.set(j, p);
            	}
            }
        }
        return LeaderSet;
    }
    
    /**
     * 二元竞赛选择gbest
     * @param LeaderSet
     * @return
     */
    public Particle_CMDPSOFS getGbest(List<Particle_CMDPSOFS> LeaderSet){
    	Particle_CMDPSOFS g = new Particle_CMDPSOFS();
    	if(LeaderSet.size()==1){
    		return LeaderSet.get(0);
    	}
    	int a = Util.randomNum(0, LeaderSet.size(), 1)[0];
    	for(int i=0;i<1;i++){
    		int b = Util.randomNum(0, LeaderSet.size(), 1)[0];
    		if(LeaderSet.get(a).dit>=LeaderSet.get(b).dit){
    			a=b;
    		}
    	}
    	g = LeaderSet.get(a);
		return g;
    }
    /** 
     * 显示程序求解结果 
     */  
    public void showresult(List<Particle_CMDPSOFS> list) {
    	INNER:for(int i = 0 ; i<Archive.size() ; i++){
    		int k=0;
    		for(Particle_CMDPSOFS p:list){
    			if(p.pbest_fitness[0]==Archive.get(i).pbest_fitness[0]){
    				if(p.pbest_fitness[1]<=Archive.get(i).pbest_fitness[1]){
    					continue INNER;
    				}else{
    					k=list.indexOf(p);
    				}
    			}
    		}
    		if(k>0){
    			list.remove(k);
    		}
    		list.add(Archive.get(i));
    	}
    }
    /** 
     * 粒子群初始化 
     * @param n 粒子的数量 
     * @throws IOException 
     */  
    public void init(int n,String name) throws IOException {
    	Process_CMDPSOFS.name = name;
    	Particle_CMDPSOFS.dims = ReadFile.getFeatureNum(name); 
    	pcount = n;
        
    	Archive = new ArrayList<Particle_CMDPSOFS>();
    	LeaderSet = new ArrayList<Particle_CMDPSOFS>();
        swarm = new Particle_CMDPSOFS[pcount];  //粒子群
        global_best = new Particle_CMDPSOFS();
         
        for (int i = 0; i < pcount; ++i) {
            swarm[i] = new Particle_CMDPSOFS();
            swarm[i].initial(Particle_CMDPSOFS.dims);
            swarm[i].evaluate();
            LeaderSet.add(swarm[i]);
        }
        int sp = swarm.length/3;
        for(int i = 0;i<swarm.length;i++){
        	if(i<sp){
        		swarm1.add(swarm[i]);
        	}else if(i>=sp){
        		if(i<2*sp){
        			swarm2.add(swarm[i]);
        		}else{
        			swarm3.add(swarm[i]);
        		}
        	}
        }
        //将LeaderSet中的粒子按照位置排序
        LeaderSet = sortSet(LeaderSet);
        
        //求拥挤距离,按拥挤距离排序
        LeaderSet = crowdingSort(LeaderSet);
    }
    
    /** 
     * 粒子群的运行 
     * @throws IOException 
     */  
    public void run(int runtimes,int times) throws IOException {
        int index;
        int T=runtimes;
        int count = 1;
        List<Particle_CMDPSOFS> record;
        SortBest_CMDPSOFS sb = new SortBest_CMDPSOFS();
        while (runtimes > 0) {
            index = -1;
            //每个粒子更新位置和适应值
            for (int i = 0; i < swarm.length; i++) {
            	Particle_CMDPSOFS c = getGbest(LeaderSet);
            	swarm[i].gbest = c.pbest;
            	swarm[i].gbest_fitness = c.pbest_fitness;
                swarm[i].updatev(); 
            }
            
            //变异
            int sp = swarm.length/3;
            for(int i = 0;i<swarm.length;i++){
            	if(i>=sp){
            		if(i<2*sp){
            			swarm[i].mutation(swarm[i].Mr);
            		}else{
            			swarm[i].nonUniMutation(runtimes, T, swarm[i].Mr);
            		}
            	}
            }
            
            //评价
            for (int i = 0; i < swarm.length; ++i){
                swarm[i].evaluate();
            }
	        
	        //update LeaderSet
	    	 for (int i = 0; i < pcount;i++){
	        	index = -1; 
	        	for (int j = 0; j < pcount; j++){
	            	if(swarm[j].pbest_fitness[0] <= swarm[i].pbest_fitness[0]&&swarm[j].pbest_fitness[1] < swarm[i].pbest_fitness[1]){
	            		index=1;
	            	}else if(swarm[j].pbest_fitness[0] < swarm[i].pbest_fitness[0]&&swarm[j].pbest_fitness[1] <= swarm[i].pbest_fitness[1]){
	            		index=1;
	            	}
	            }
	        	for(int k = 0;k<LeaderSet.size();k++){
	        		if(LeaderSet.get(k).pbest_fitness[0] == swarm[i].pbest_fitness[0]&&LeaderSet.get(k).pbest_fitness[1] == swarm[i].pbest_fitness[1]){
	        			index=1;
	        		}
	        	}
	            if(index == -1){
	            	double cf = 0;
	            	int cfn = 0;
	            	for(int k=0;k<LeaderSet.size();k++){
            			if(cf<LeaderSet.get(k).dit){
            				cf = LeaderSet.get(k).dit;
            				cfn = k;
            			}
	            	}
	            	LeaderSet.add(swarm[i]);

	                LeaderSet = sortSet(LeaderSet);
	                
                    //求拥挤距离,按拥挤距离排序
	                LeaderSet = crowdingSort(LeaderSet);
	                
                    if(cf>LeaderSet.get(swarm.length).dit){
                    	LeaderSet.remove(cfn);
                    }else{
                    	LeaderSet.remove(swarm.length);
                    }
        		}
	        }
	        Archive.addAll(LeaderSet);
	        record = Archive;
	        if(count%10==0 || count==1){
	        	sb.getCMDResult(Process_CMDPSOFS.name, record, count+"-"+times);
	        }
	        count++;
            runtimes--;
        }
    }
}
