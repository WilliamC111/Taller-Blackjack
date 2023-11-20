import java.io.Serializable;

public class Decision implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2998180469166813043L;
	String value;
	
	public Decision(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
}
