package myWeka;

import tools.*;

import java.io.File;

import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.UnsupervisedAttributeEvaluator;
import weka.attributeSelection.UnsupervisedSubsetEvaluator;
import weka.classifiers.evaluation.output.prediction.Null;
import weka.core.*;
import weka.filters.Filter;
import weka.filters.SupervisedFilter;

public class UFSSC extends MyFilter implements SupervisedFilter, OptionHandler{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** the attribute selection evaluation object */
	private weka.attributeSelection.AttributeSelection m_trainSelector;

	/** the attribute evaluator to use */
	private ASEvaluation m_ASEvaluator=new CfsSubsetEval();

	/** the search method if any */
	private ASSearch m_ASSearch=new BestFirst();

	/** holds a copy of the full set of valid options passed to the filter */
	private String[] m_FilterOptions;

	/** holds the selected attributes */
	private int[] m_SelectedAttributes;
 
	public UFSSC() {

		resetOptions();
		System.out.println("test");
	}
	
	/**
	 * set options to their default values
	 */
	protected void resetOptions() {

		m_trainSelector = new weka.attributeSelection.AttributeSelection();
		m_SelectedAttributes = null;
		m_FilterOptions = null;
	}
	
	public double SU(int a, int b, Instances Data){
		SubCfsSubsetEval subCfs = new SubCfsSubsetEval();
		subCfs.setm_trInstances(Data);
		double su = subCfs.symmUncertCorr(a,b);
		return 	su;
	}
	

	
	/**
	*此函数选择到平均冗余度最大的实例下标，即选择代表特征
	*/
	public int CHOSEsu(int[] a,int x, Instances njjData){

		double[] ave= new double [x];
		double sum =0;
		if(x==1)
			return a[0];
		if(x==2)
			return a[0];
		for(int i= 0;i< x;i++){
			sum=0;
			for(int ii=0;ii< x;ii++){
				if(i!=ii){
					sum=sum+SU(a[i],a[ii],njjData);
				}
				else
					continue;
			}
			ave[i]=sum/(double)(x-1);
		}
		
		double max=0;
		int maxID=0;
		
		for(int i=0;i<x;i++){
			if(ave[i]> max){
				max = ave[i];
				maxID=i;
			}
		}			
		return a[maxID];
	}
	
	/**
	*此函数将选择的属性子集结合实例集重新生成标准的arff数据到缓存中
	*/
	protected void convertInstance(Instance instance,int[] G,int clusterNum) throws Exception {
	    double[] newVals = new double[getOutputFormat().numAttributes()];

	    
	      for (int i = 0; i < clusterNum; i++) {
		int current = G[i];
		newVals[i] = instance.value(current);
	      }
	    
	    if (instance instanceof SparseInstance) {
	      push(new SparseInstance(instance.weight(), newVals));
	    } else {
	      push(new SubInstance(instance.weight(), newVals));
	    }
	  }
	
	 /**
	 *排序函数
	 *返回的是数组从大到小的下标组成的数组，例如参数数组为[4,6,3,7],返回的数组为[3,1,0,2]
	 */
	 public static int []tyhPX(double[] a){
			double temp;
			int temp1;
			int len = a.length;
			int []b=new int [len];
			double []sorted=new double[len];
			for(int m=0;m<len;m++){
				sorted[m]=a[m];
			}
			for(int i=0;i<len;i++)
				b[i]=i;
			boolean tag = true;
			while (tag) {
				tag = false;
				len--;
				for (int j = 1; j <= len; j++) {
					if (sorted[j - 1] < sorted[j]) {
						temp = sorted[j - 1];
						sorted[j - 1] = sorted[j];
						sorted[j] = temp;
						temp1=b[j-1];
						b[j-1]=b[j];
						b[j]=temp1;
						tag = true;
					}
				}
			}
			return b;
		
		}
        /**
		 *过滤方法，本算法的主体
		 */
		@Override
		public boolean batchFinished() throws Exception {

			if (getInputFormat() == null) { //判断是否读入了数据集，如果没有，抛出异常
				throw new IllegalStateException("No input instance format defined");
			}

			if (!isOutputFormatDefined()) {
				
				Instances dataRaw = getInputFormat();  //将数据集转存为Instances对象
				int numAttrRaw=dataRaw.numAttributes()-1;       //属性数目减一

				if(true){				
				
				Instances data,dataDiscretize;
			
				Filter m_Filter = new weka.filters.supervised.attribute.Discretize();//离散化
				m_Filter.setInputFormat(dataRaw); 	
				dataDiscretize = Filter.useFilter(dataRaw, m_Filter);  //discretize	
				
				Filter m_Filter1 = new weka.filters.unsupervised.attribute.RemoveUseless();//删除无用
				m_Filter1.setInputFormat(dataDiscretize); 
				data = Filter.useFilter(dataDiscretize, m_Filter1); //data是既离散化也删除了无用的数据集		
				
				
		
				
				int numAttr=data.numAttributes()-1; // numAttributes without class，属性集本质上可以看作是一个长度为N 的一维int类型数组，这样可以更好的理解下面的代码
				int numIns=data.numInstances();  //实例个数
				
				
				int numSeed = (int)Math.pow(numAttr,1/2.5);   //S是默认的种子数目，可以是根号N，也可以采用其他数值比如N开立方
				if(numSeed<=1)
					numSeed=1;
				int maxnumSeedinCluster = numAttr;	 //每个簇的最大种子数目
				
				
				
				if(numAttr<maxnumSeedinCluster || numAttr<numSeed)
					return false;   // the given data set is too small to run this algorithm
				AppendToFile.appendMethodA("E:/UFSSCResult/sqrtN/time.txt", numIns+","+numAttrRaw+","+numAttr+",");//写入相关程序运行的信息
		
				int numCluster ;		 //这个变量记录本算法的簇的数目	
				int [][]attr;            //这个二维数组attr[i][j]代表第i个簇中的第j个属性,有一个特殊的attr[i][maxnumSeedinCluster]记录第i个簇中的属性个数
					                        //attr[i][0]是第i个簇的种子属性
			
				int tempNuminCluster=0 ; //这是一个计数的中间变量，记录各个簇的数目
		
			
				int maxID=0;//中间变量，记录各个簇中和种子距离最近的属性下标
				
				double maxSU = 0; //中间变量，记录各个簇中和种子距离最近的属性与种子间的距离
				double tempSU;   //中间变量，寻找maxSU时用
				
				/* tyh 2013 3 12*/
				int k=numSeed;   //k表示k近邻密度中的k，可以是根号N，也可以采用其他数值比如N开立方
				double [][]attrSU=new double[numAttr][numAttr];
				double []sum=new double[numAttr];
//----Nominal only data set			
				
					
					
				AppendToFile.appendMethodA("E:/UFSSCResult/sqrtN/time.txt", numSeed+","+maxnumSeedinCluster+",");
					
					numCluster=numAttr;  // Number of Clusters
					
					attr = new int[numCluster][maxnumSeedinCluster+1];   //Clustering正在聚类的簇的信息
					
					/**
					 * tyh 2013 3 12*/
					
						
				/**
				下面这段代码计算属性集两两之间的距离SU
				*/
			        for(int i=0;i<numAttr;i++)
				      for(int j=i;j<numAttr;j++){
						attrSU[i][j]=SU(i,j,data);
						attrSU[j][i]=attrSU[i][j];
				       }
			        /**
					下面这段是计算每个属性的近邻的下标，存在二维数组neibourID[][]中
					*/
			        int [][]neibourID=new int[numAttr][numAttr];
			        for(int j=0;j<numAttr;j++)
			         neibourID[j]=tyhPX(attrSU[j]);
						
			            /**
					下面这段是计算每个属性的k近邻密度，并将属性集按照这一密度排序，将结果存在一维数组density[]中
					可知density[]依然是整个属性集，只不过现在属性集按照n近邻密度排好序了，记住这一点有利于理解下面的代码
					*/
		         	for(int i=0;i<numAttr;i++){
				     for(int j=1;j<k+1;j++)            //????从1开始？
				       sum[i]+=attrSU[neibourID[i][j]][i];
				    }
				
		         	int []densityID=new int [numAttr];
		         	densityID=tyhPX(sum);  //按k紧邻密度排好序的属性下标
		         	
					
					/**
					可知density[]依然是整个属性集，下面按照排好序的属性集挨个划分属性
					*/
			        attr[0][0]=densityID[0];//拿排在第0位的属性作为第0个簇的种子属性
			        attr[0][maxnumSeedinCluster]=1;//现在第0簇的属性数目为1
					
					/**
					下面308行到335行这段代码选择出种子集合。
					*/
			        int []seeds=new int[numAttr];  //seeds为种子集合，即聚类中心集合
			        seeds[0]=densityID[0];          //拿密度最大的（即排在第0位的）属性作为第一个种子属性
			        int numSeeds=1;
			   
			        boolean flag=true;
					int []seedsBackup=new int[numAttr];
					for(int i=1;i<numAttr;i++){
						flag=true;
						for(int j=0;j<numSeeds;j++)
						for(int ii=0;ii<j+1;ii++){
							if(densityID[i]==neibourID[seeds[j]][ii]){
								flag=false;
								break;
						      }		
							else if(neibourID[densityID[i]][ii]==seeds[j]){
								flag=false;
								break;
							}
						}
						if (flag==true){
							  seedsBackup[numSeeds]=densityID[i];
							  seeds[numSeeds]=densityID[i];
							  numSeeds++;
						}
					}
			        flag=true;

					for(int i=0;i<numSeeds;i++)
					{
						attr[i][0]=seedsBackup[i];  //the seed
						attr[i][maxnumSeedinCluster]=1;           // count   第一为表示种子，最后一位表示簇中元素的个数
					}
					
					//---clustering the rest attributes
					/**
					下面303行到324行这段代码依次划分每个属性。
					*/					
                   for(int j=0;j<numAttr;j++){	
                   	for(int i=0;i<numSeeds;i++){
                   		if(j==seedsBackup[i])
                   			flag=false;
                   		}
                   	if(!flag)
                   		flag=true;
                   	else {
                   		maxSU=0;                                //当前处理的剩余的实例与种子间的最大的SU
							tempSU=0;
								for(int ii=0;ii<numSeeds;ii++){
									tempSU = attrSU[j][attr[ii][0]];  //计算实例与所有种子间的SU,attr[ii][0]是第ii个簇的种子属性
									if(tempSU > maxSU){
										maxSU = tempSU;
										maxID = ii;
									}
									}
								
								        attr[maxID][attr[maxID][maxnumSeedinCluster]]=j;//把属性j加入到以maxID为种子的簇中
								        attr[maxID][maxnumSeedinCluster]++;             //数组元素个数加一 
							     }
							   }
                         
							
							
							//328行到372行选择每个簇的代表属性，并且计算出了簇内平均SU,和簇间平均SU
							double []aveSU=new double[numAttr];
							double []maxAveSU=new double [numSeeds+1];
							int reprAttr=0;
							for(int i= 0; i< numSeeds; i++){
								maxAveSU[i]=0.0;
								for(int j=0;j<attr[i][maxnumSeedinCluster];j++){
									for(int ii=0;ii<attr[i][maxnumSeedinCluster];ii++){
										if(j!=ii)
									    aveSU[j]+=attrSU[attr[i][ii]][attr[i][j]];
								    }
								aveSU[j]=aveSU[j]/attr[i][maxnumSeedinCluster];//aveSU[]存储某个簇内的每个属性的平均冗余度
								}
		                       for(int j=0;j<attr[i][maxnumSeedinCluster];j++){
		                    	   if(aveSU[j]>maxAveSU[i]){
		                    		   maxAveSU[i]=aveSU[j];     //maxAveSU[i]保存第i个簇内，与其他属性SU和最大的属性的平均SU值（即平均冗余度）  
		                    		   reprAttr=attr[i][j];   //reprAttr保存第i个簇内，与其他属性SU最大的属性的值（即平均冗余度最大的属性值，也就是代表属性）
		                    	   }
		                       }
		                       attr[i][maxnumSeedinCluster-1]=reprAttr;	  //代表属性值
		                        maxAveSU[numSeeds]+=maxAveSU[i]; //maxAveSU[numseeds]保存所有簇的maxAveSU的和
							}
							maxAveSU[numSeeds]=maxAveSU[numSeeds]/numSeeds; //maxAveSU[numseeds]保存所有簇内maxAveSU的平均值
							//System.out.println(	maxAveSU[numSeeds]);
							AppendToFile.appendMethodA("E:/UFSSCResult/sqrtN/time.txt", maxAveSU[numSeeds]+",");
							
							//int[]m_SelectedAttributes= new int[numSeeds+1];     
							m_SelectedAttributes=new int[numSeeds+1];//该算法选择出来的属性值
							double jtemp=0;
							
							m_SelectedAttributes[numSeeds] = numAttr;             //m_SelectedAttributes[]的最后一位保存的是属性个数
							for (int i = 0; i < numSeeds; i++) {
								m_SelectedAttributes[i] = attr[i][maxnumSeedinCluster-1];
							}
							for(int i=0;i<numSeeds;i++){
							   for(int j=0;j< numSeeds;j++){
								if(i!=j)
								   aveSU[i]+=attrSU[m_SelectedAttributes[i]][m_SelectedAttributes[j]]; //aveSU[]保存代表属性的SU之和
							     }
							    }
							   for(int i=0;i<numSeeds;i++){
								 if(jtemp<aveSU[i])
									 jtemp=aveSU[i];
							   }
							jtemp=jtemp/(numSeeds);
							jtemp=maxAveSU[numSeeds]/jtemp;
							//AppendToFile.appendMethodA("E:/UFSSCResult/sqrtN/time.txt", jtemp+",");
							//System.out.println("*****************************************************");
							//System.out.println(jtemp);
							
					tempNuminCluster=numSeeds+1;
					m_SelectedAttributes[numSeeds]=numAttr; //一维数组selectedAttr是选择本算法出来的属性，也就是最终结果
			
				//============================================================
				setOutputFormat(m_SelectedAttributes, tempNuminCluster, data);//这个方法用于将属性选择结果组合成新instances，并存入输出流
			
				if(getM_OutputFormat() != null){
				m_OutputFormat.setRelationName(tempNuminCluster+",UFSSC,"+ data.numAttributes()+ "," +data.relationName() );
				}
				
				// Convert pending input instances
				for (int i = 0; i <data.numInstances(); i++) {
					convertInstance(data.instance(i), m_SelectedAttributes, tempNuminCluster);//这个方法用于将属性选择结果组合成新instances
				}
				//AppendToFile.appendMethodA("E:/UFSSCResult/sqrtN/time.txt", tempNuminCluster+",");

				flushInput();
			}
			}
			m_NewBatch = true;
			return (numPendingOutput() != 0);
			

			// ************************
		}
	
		protected void setOutputFormat(int[] G,int clusterNum, Instances Data) throws Exception {
		    Instances informat;
			/*if (m_SelectedAttributes == null) {
				setOutputFormat(null);
				return;
			}*/
		    
		    //System.out.println("TEST");
		    FastVector attributes = new FastVector(m_SelectedAttributes.length);
		    //System.out.println("TEST2");
		    int i;

		      informat = Data;
		    
				/*if (m_ASEvaluator instanceof AttributeTransformer) {
					informat = ((AttributeTransformer) m_ASEvaluator).transformedData();
				} else {
					informat = getInputFormat();
				}
				*/

		    for (i=0;i < clusterNum;i++) {
		      attributes.addElement(informat.attribute(G[i]).copy());
		    }

		    Instances outputFormat = 
		      new Instances(getInputFormat().relationName()+String.valueOf(clusterNum), attributes, 0);


		    if (!(m_ASEvaluator instanceof UnsupervisedSubsetEvaluator) &&
			!(m_ASEvaluator instanceof UnsupervisedAttributeEvaluator)) {
		      outputFormat.setClassIndex(-1);
		    }
		    
		    setOutputFormat(outputFormat);  
		  }
		
		public static void main(String[] argv){
			
			File file = new File("E:/datasetless/");
			//AppendToFile.appendMethodB("E:/UFSSCResult/sqrtN/time.txt", "name,I,F,Fr,numSeed,maxnumSeedinCluster,newF,in_SU,SU_performance,FenTime\r\n");
			AppendToFile.appendMethodB("E:/UFSSCResult/sqrtN/time.txt", "name,numIns,numAttrRaw,numAttr,numSeed,maxnumSeedinCluster,AveSUofReprAttr,FenTime\r\n");
			File[] lf = file.listFiles();
					for (int i = 0; i < lf.length; i++) {
				System.out.println(lf[i].getName());
				String[] arg = {
//						"-E", "weka.attributeSelection.CfsSubsetEval -L",
//						"-E", "weka.attributeSelection.SymmetricalUncertAttributeSetEval ",
//						"-S", "weka.attributeSelection.BestFirst -S 8",
						"-c", "last",

						"-i", "E:/datasetless/" + lf[i].getName(), 
						"-o", "E:/UFSSCResult/sqrtN/" + lf[i].getName()
				};
				AppendToFile.appendMethodA("E:/UFSSCResult/sqrtN/time.txt", lf[i].getName()+",");
				long st =System.currentTimeMillis();
				runFilter(new UFSSC(), arg);
				long end =System.currentTimeMillis();
				long time=end-st;
				AppendToFile.appendMethodA("E:/UFSSCResult/sqrtN/time.txt", time + "\r\n");
			}
		}

}
