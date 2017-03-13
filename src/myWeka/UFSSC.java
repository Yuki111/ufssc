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
	*�˺���ѡ��ƽ�����������ʵ���±꣬��ѡ���������
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
	*�˺�����ѡ��������Ӽ����ʵ�����������ɱ�׼��arff���ݵ�������
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
	 *������
	 *���ص�������Ӵ�С���±���ɵ����飬�����������Ϊ[4,6,3,7],���ص�����Ϊ[3,1,0,2]
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
		 *���˷��������㷨������
		 */
		@Override
		public boolean batchFinished() throws Exception {

			if (getInputFormat() == null) { //�ж��Ƿ���������ݼ������û�У��׳��쳣
				throw new IllegalStateException("No input instance format defined");
			}

			if (!isOutputFormatDefined()) {
				
				Instances dataRaw = getInputFormat();  //�����ݼ�ת��ΪInstances����
				int numAttrRaw=dataRaw.numAttributes()-1;       //������Ŀ��һ

				if(true){				
				
				Instances data,dataDiscretize;
			
				Filter m_Filter = new weka.filters.supervised.attribute.Discretize();//��ɢ��
				m_Filter.setInputFormat(dataRaw); 	
				dataDiscretize = Filter.useFilter(dataRaw, m_Filter);  //discretize	
				
				Filter m_Filter1 = new weka.filters.unsupervised.attribute.RemoveUseless();//ɾ������
				m_Filter1.setInputFormat(dataDiscretize); 
				data = Filter.useFilter(dataDiscretize, m_Filter1); //data�Ǽ���ɢ��Ҳɾ�������õ����ݼ�		
				
				
		
				
				int numAttr=data.numAttributes()-1; // numAttributes without class�����Լ������Ͽ��Կ�����һ������ΪN ��һάint�������飬�������Ը��õ��������Ĵ���
				int numIns=data.numInstances();  //ʵ������
				
				
				int numSeed = (int)Math.pow(numAttr,1/2.5);   //S��Ĭ�ϵ�������Ŀ�������Ǹ���N��Ҳ���Բ���������ֵ����N������
				if(numSeed<=1)
					numSeed=1;
				int maxnumSeedinCluster = numAttr;	 //ÿ���ص����������Ŀ
				
				
				
				if(numAttr<maxnumSeedinCluster || numAttr<numSeed)
					return false;   // the given data set is too small to run this algorithm
				AppendToFile.appendMethodA("E:/UFSSCResult/sqrtN/time.txt", numIns+","+numAttrRaw+","+numAttr+",");//д����س������е���Ϣ
		
				int numCluster ;		 //���������¼���㷨�Ĵص���Ŀ	
				int [][]attr;            //�����ά����attr[i][j]�����i�����еĵ�j������,��һ�������attr[i][maxnumSeedinCluster]��¼��i�����е����Ը���
					                        //attr[i][0]�ǵ�i���ص���������
			
				int tempNuminCluster=0 ; //����һ���������м��������¼�����ص���Ŀ
		
			
				int maxID=0;//�м��������¼�������к����Ӿ�������������±�
				
				double maxSU = 0; //�м��������¼�������к����Ӿ�����������������Ӽ�ľ���
				double tempSU;   //�м������Ѱ��maxSUʱ��
				
				/* tyh 2013 3 12*/
				int k=numSeed;   //k��ʾk�����ܶ��е�k�������Ǹ���N��Ҳ���Բ���������ֵ����N������
				double [][]attrSU=new double[numAttr][numAttr];
				double []sum=new double[numAttr];
//----Nominal only data set			
				
					
					
				AppendToFile.appendMethodA("E:/UFSSCResult/sqrtN/time.txt", numSeed+","+maxnumSeedinCluster+",");
					
					numCluster=numAttr;  // Number of Clusters
					
					attr = new int[numCluster][maxnumSeedinCluster+1];   //Clustering���ھ���Ĵص���Ϣ
					
					/**
					 * tyh 2013 3 12*/
					
						
				/**
				������δ���������Լ�����֮��ľ���SU
				*/
			        for(int i=0;i<numAttr;i++)
				      for(int j=i;j<numAttr;j++){
						attrSU[i][j]=SU(i,j,data);
						attrSU[j][i]=attrSU[i][j];
				       }
			        /**
					��������Ǽ���ÿ�����ԵĽ��ڵ��±꣬���ڶ�ά����neibourID[][]��
					*/
			        int [][]neibourID=new int[numAttr][numAttr];
			        for(int j=0;j<numAttr;j++)
			         neibourID[j]=tyhPX(attrSU[j]);
						
			            /**
					��������Ǽ���ÿ�����Ե�k�����ܶȣ��������Լ�������һ�ܶ����򣬽��������һά����density[]��
					��֪density[]��Ȼ���������Լ���ֻ�����������Լ�����n�����ܶ��ź����ˣ���ס��һ���������������Ĵ���
					*/
		         	for(int i=0;i<numAttr;i++){
				     for(int j=1;j<k+1;j++)            //????��1��ʼ��
				       sum[i]+=attrSU[neibourID[i][j]][i];
				    }
				
		         	int []densityID=new int [numAttr];
		         	densityID=tyhPX(sum);  //��k�����ܶ��ź���������±�
		         	
					
					/**
					��֪density[]��Ȼ���������Լ������水���ź�������Լ�������������
					*/
			        attr[0][0]=densityID[0];//�����ڵ�0λ��������Ϊ��0���ص���������
			        attr[0][maxnumSeedinCluster]=1;//���ڵ�0�ص�������ĿΪ1
					
					/**
					����308�е�335����δ���ѡ������Ӽ��ϡ�
					*/
			        int []seeds=new int[numAttr];  //seedsΪ���Ӽ��ϣ����������ļ���
			        seeds[0]=densityID[0];          //���ܶ����ģ������ڵ�0λ�ģ�������Ϊ��һ����������
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
						attr[i][maxnumSeedinCluster]=1;           // count   ��һΪ��ʾ���ӣ����һλ��ʾ����Ԫ�صĸ���
					}
					
					//---clustering the rest attributes
					/**
					����303�е�324����δ������λ���ÿ�����ԡ�
					*/					
                   for(int j=0;j<numAttr;j++){	
                   	for(int i=0;i<numSeeds;i++){
                   		if(j==seedsBackup[i])
                   			flag=false;
                   		}
                   	if(!flag)
                   		flag=true;
                   	else {
                   		maxSU=0;                                //��ǰ�����ʣ���ʵ�������Ӽ������SU
							tempSU=0;
								for(int ii=0;ii<numSeeds;ii++){
									tempSU = attrSU[j][attr[ii][0]];  //����ʵ�����������Ӽ��SU,attr[ii][0]�ǵ�ii���ص���������
									if(tempSU > maxSU){
										maxSU = tempSU;
										maxID = ii;
									}
									}
								
								        attr[maxID][attr[maxID][maxnumSeedinCluster]]=j;//������j���뵽��maxIDΪ���ӵĴ���
								        attr[maxID][maxnumSeedinCluster]++;             //����Ԫ�ظ�����һ 
							     }
							   }
                         
							
							
							//328�е�372��ѡ��ÿ���صĴ������ԣ����Ҽ�����˴���ƽ��SU,�ʹؼ�ƽ��SU
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
								aveSU[j]=aveSU[j]/attr[i][maxnumSeedinCluster];//aveSU[]�洢ĳ�����ڵ�ÿ�����Ե�ƽ�������
								}
		                       for(int j=0;j<attr[i][maxnumSeedinCluster];j++){
		                    	   if(aveSU[j]>maxAveSU[i]){
		                    		   maxAveSU[i]=aveSU[j];     //maxAveSU[i]�����i�����ڣ�����������SU���������Ե�ƽ��SUֵ����ƽ������ȣ�  
		                    		   reprAttr=attr[i][j];   //reprAttr�����i�����ڣ�����������SU�������Ե�ֵ����ƽ���������������ֵ��Ҳ���Ǵ������ԣ�
		                    	   }
		                       }
		                       attr[i][maxnumSeedinCluster-1]=reprAttr;	  //��������ֵ
		                        maxAveSU[numSeeds]+=maxAveSU[i]; //maxAveSU[numseeds]�������дص�maxAveSU�ĺ�
							}
							maxAveSU[numSeeds]=maxAveSU[numSeeds]/numSeeds; //maxAveSU[numseeds]�������д���maxAveSU��ƽ��ֵ
							//System.out.println(	maxAveSU[numSeeds]);
							AppendToFile.appendMethodA("E:/UFSSCResult/sqrtN/time.txt", maxAveSU[numSeeds]+",");
							
							//int[]m_SelectedAttributes= new int[numSeeds+1];     
							m_SelectedAttributes=new int[numSeeds+1];//���㷨ѡ�����������ֵ
							double jtemp=0;
							
							m_SelectedAttributes[numSeeds] = numAttr;             //m_SelectedAttributes[]�����һλ����������Ը���
							for (int i = 0; i < numSeeds; i++) {
								m_SelectedAttributes[i] = attr[i][maxnumSeedinCluster-1];
							}
							for(int i=0;i<numSeeds;i++){
							   for(int j=0;j< numSeeds;j++){
								if(i!=j)
								   aveSU[i]+=attrSU[m_SelectedAttributes[i]][m_SelectedAttributes[j]]; //aveSU[]����������Ե�SU֮��
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
					m_SelectedAttributes[numSeeds]=numAttr; //һά����selectedAttr��ѡ���㷨���������ԣ�Ҳ�������ս��
			
				//============================================================
				setOutputFormat(m_SelectedAttributes, tempNuminCluster, data);//����������ڽ�����ѡ������ϳ���instances�������������
			
				if(getM_OutputFormat() != null){
				m_OutputFormat.setRelationName(tempNuminCluster+",UFSSC,"+ data.numAttributes()+ "," +data.relationName() );
				}
				
				// Convert pending input instances
				for (int i = 0; i <data.numInstances(); i++) {
					convertInstance(data.instance(i), m_SelectedAttributes, tempNuminCluster);//����������ڽ�����ѡ������ϳ���instances
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
