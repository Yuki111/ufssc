package tools;

import weka.attributeSelection.CfsSubsetEval;
import weka.core.ContingencyTables;
import weka.core.Instance;
import weka.core.Instances;

public class SubCfsSubsetEval extends CfsSubsetEval{

	  /** Treat missing values as seperate values */
	  private boolean m_missingSeperate;
	  /** The training instances */
	  private Instances m_trainInstances;
	  /** Number of instances in the training data */
	  private int m_numInstances;
	  /** Number of attributes in the training data */
	  private int m_numAttribs;
	  /** The class index */
	  private int m_classIndex;
	  public String[] getOptions () {
		    String[] options = new String[2];
		    int current = 0;

		    if (getMissingSeperate()) {
		      options[current++] = "-M";
		    }

		    if (!getLocallyPredictive()) {
		      options[current++] = "-L";
		    }

		    while (current < options.length) {
		      options[current++] = "";
		    }

		    return  options;
		  }
	  
	  public boolean getMissingSeperate () {
		    return  m_missingSeperate;
		  }
	  
	  public int setm_trInstances(Instances x){
		  m_trainInstances=x;
		  m_numInstances=x.numInstances();
		  m_numAttribs=x.numAttributes();
		  m_classIndex=x.classIndex();	  
		  return 1;
	  }
	  /**
	   * ����the contingency table
	   * ����the symmetrical uncertainty for base 2
	   * @param att1
	   * @param att2
	   * @return
	   */
	  public double symmUncertCorr (int att1, int att2) {
		    int i, j, k, ii, jj;
		    int ni, nj;
		    double sum = 0.0;
		    double sumi[], sumj[];
		    double counts[][];//�ۼ������Բ�ֵͬ�Ե���Ŀ
		    Instance inst;
		    double corr_measure;
		    boolean flag = false;
		    double temp = 0.0;
		    //�Ƚϵ�����������һ����������
		    if (att1 == m_classIndex || att2 == m_classIndex) {
		      flag = true;
		    }

		    ni = m_trainInstances.attribute(att1).numValues() + 1;//��ǰ���Բ�ֵͬ����+1
		    nj = m_trainInstances.attribute(att2).numValues() + 1;//ͬ��
		    counts = new double[ni][nj];
		    sumi = new double[ni];
		    sumj = new double[nj];

		    for (i = 0; i < ni; i++) {//���鸳��ֵ
		      sumi[i] = 0.0;

		      for (j = 0; j < nj; j++) {
		        sumj[j] = 0.0;
		        counts[i][j] = 0.0;
		      }
		    }

		    // Fill the contingency table
		    for (i = 0; i < m_numInstances; i++) {//����ʵ�����ڼ���
		      inst = m_trainInstances.instance(i);

		      if (inst.isMissing(att1)) {//ȱֵ�Ļ�ii��ֵΪ�����������ȱֵ
		        ii = ni - 1;
		      }
		      else {
		        ii = (int)inst.value(att1);//��ȱ��Ϊ��ǰֵ����������
		      }

		      if (inst.isMissing(att2)) {//����ͬ����1
		        jj = nj - 1;
		      }
		      else {
		        jj = (int)inst.value(att2);
		      }
		      //����1 Ϊֵii������2Ϊֵjj�ĸ����ۼ�
		      counts[ii][jj]++;
		    }

		    // get the row totals
		    //sumi[i]����1Ϊ��ֵͬ������Ϊi��ֵ���ĸ�������ά�������
		    //sumΪ����1��2������ȡֵ��������ά������ۼӺ�
		    for (i = 0; i < ni; i++) {
		      sumi[i] = 0.0;

		      for (j = 0; j < nj; j++) {
		        sumi[i] += counts[i][j];
		        sum += counts[i][j];
		      }
		    }

		    // get the column totals
		    //����2��ֵͬ�ĸ�������
		    for (j = 0; j < nj; j++) {
		      sumj[j] = 0.0;

		      for (i = 0; i < ni; i++) {
		        sumj[j] += counts[i][j];
		      }
		    }

		    // distribute missing counts
		    /** Treat missing values as seperate values */
		    //private boolean m_missingSeperate;
		    //����ȱֵ��Ϊһ��ֵ������ȱֵû��ȫ��
		    if (!m_missingSeperate && 
		        (sumi[ni-1] < m_numInstances) && 
		        (sumj[nj-1] < m_numInstances)) {
		      double[] i_copy = new double[sumi.length];
		      double[] j_copy = new double[sumj.length];
		      double[][] counts_copy = new double[sumi.length][sumj.length];

		      for (i = 0; i < ni; i++) {
		    	  //��ָ��Դ�����и���һ�����飬���ƴ�ָ����λ�ÿ�ʼ����Ŀ�������ָ��λ�ý�����
		        System.arraycopy(counts[i], 0, counts_copy[i], 0, sumj.length);
		      }

		      System.arraycopy(sumi, 0, i_copy, 0, sumi.length);
		      System.arraycopy(sumj, 0, j_copy, 0, sumj.length);
		      double total_missing = 
		        (sumi[ni - 1] + sumj[nj - 1] - counts[ni - 1][nj - 1]);

		      // do the missing i's
		      if (sumi[ni - 1] > 0.0) {
		        for (j = 0; j < nj - 1; j++) {
		          if (counts[ni - 1][j] > 0.0) {
		            for (i = 0; i < ni - 1; i++) {
		              temp = ((i_copy[i]/(sum - i_copy[ni - 1]))*counts[ni - 1][j]);
		              counts[i][j] += temp;
		              sumi[i] += temp;
		            }

		            counts[ni - 1][j] = 0.0;
		          }
		        }
		      }

		      sumi[ni - 1] = 0.0;

		      // do the missing j's
		      if (sumj[nj - 1] > 0.0) {
		        for (i = 0; i < ni - 1; i++) {
		          if (counts[i][nj - 1] > 0.0) {
		            for (j = 0; j < nj - 1; j++) {
		              temp = ((j_copy[j]/(sum - j_copy[nj - 1]))*counts[i][nj - 1]);
		              counts[i][j] += temp;
		              sumj[j] += temp;
		            }

		            counts[i][nj - 1] = 0.0;
		          }
		        }
		      }

		      sumj[nj - 1] = 0.0;

		      // do the both missing
		      if (counts[ni - 1][nj - 1] > 0.0 && total_missing != sum) {
		        for (i = 0; i < ni - 1; i++) {
		          for (j = 0; j < nj - 1; j++) {
		            temp = (counts_copy[i][j]/(sum - total_missing)) * 
		              counts_copy[ni - 1][nj - 1];
		            
		            counts[i][j] += temp;
		            sumi[i] += temp;
		            sumj[j] += temp;
		          }
		        }

		        counts[ni - 1][nj - 1] = 0.0;
		      }
		    }

		    corr_measure = ContingencyTables.symmetricalUncertainty(counts);
		    return  (corr_measure);
	  }

}
