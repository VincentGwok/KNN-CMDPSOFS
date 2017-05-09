package cn.pso.cmdpsofs;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Test_CMDPSOFS {
	public static void main(String[] args) throws IOException {
		Process_CMDPSOFS pso = new Process_CMDPSOFS();
		SortBest_CMDPSOFS sb = new SortBest_CMDPSOFS();
		List<Particle_CMDPSOFS> list = new ArrayList<Particle_CMDPSOFS>();
		for(int i=0;i<30;i++){
			pso.init(30,"german");
			pso.run(500,i);
			pso.showresult(list);
			System.out.println("CMDPSOFS:"+Process_CMDPSOFS.name+"第"+(i+1)+"次测试完成");
		}
		sb.getCMDResult(Process_CMDPSOFS.name,list,"All");
	}
}
