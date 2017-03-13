package tools;

import java.util.Enumeration;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

public class SubInstance implements Instance{

	  /** for serialization */
	  static final long serialVersionUID = 1482635194499365122L;
	  
	  /** Constant representing a missing value. */
	  protected static final double MISSING_VALUE = Double.NaN;

	  /** 
	   * The dataset the instance has access to.  Null if the instance
	   * doesn't have access to any dataset.  Only if an instance has
	   * access to a dataset, it knows about the actual attribute types.  
	   */
	  protected /*@spec_public@*/ Instances m_Dataset;

	  /** The instance's attribute values. */
	  protected /*@spec_public non_null@*/ double[] m_AttValues;

	  /** The instance's weight. */
	  protected double m_Weight;
	  public SubInstance(double weight,  /*@non_null@*/ double[]attValues){
		    
		    m_AttValues = attValues;
		    m_Weight = weight;
		    m_Dataset = null;
		  }
	  
	@Override
	public Object copy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Attribute attribute(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Attribute attributeSparse(int indexOfIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Attribute classAttribute() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int classIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean classIsMissing() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double classValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Instance copy(double[] values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Instances dataset() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteAttributeAt(int position) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Enumeration<Attribute> enumerateAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean equalHeaders(Instance inst) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String equalHeadersMsg(Instance inst) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasMissingValue() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int index(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void insertAttributeAt(int position) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isMissing(int attIndex) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isMissingSparse(int indexOfIndex) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isMissing(Attribute att) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Instance mergeInstance(Instance inst) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int numAttributes() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int numClasses() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int numValues() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void replaceMissingValues(double[] array) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setClassMissing() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setClassValue(double value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setClassValue(String value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDataset(Instances instances) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMissing(int attIndex) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMissing(Attribute att) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setValue(int attIndex, double value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setValueSparse(int indexOfIndex, double value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setValue(int attIndex, String value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setValue(Attribute att, double value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setValue(Attribute att, String value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setWeight(double weight) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Instances relationalValue(int attIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Instances relationalValue(Attribute att) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String stringValue(int attIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String stringValue(Attribute att) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double[] toDoubleArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toStringNoWeight(int afterDecimalPoint) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toStringNoWeight() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toStringMaxDecimalDigits(int afterDecimalPoint) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString(int attIndex, int afterDecimalPoint) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString(int attIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString(Attribute att, int afterDecimalPoint) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString(Attribute att) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double value(int attIndex) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double valueSparse(int indexOfIndex) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double value(Attribute att) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double weight() {
		// TODO Auto-generated method stub
		return 0;
	}

}
