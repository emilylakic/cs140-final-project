package project;

public class DivideByZeroException extends RuntimeException {
	private static final long serialVersionUID = -1654913475830275194L;
	
	public DivideByZeroException(String s) {
		super(s);
	}
}
