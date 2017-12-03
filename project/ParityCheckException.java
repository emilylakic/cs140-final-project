package project;

import java.util.IllegalFormatFlagsException;

public class ParityCheckException extends IllegalFormatFlagsException {
	private static final long serialVersionUID = 7434815269190400129L;

	public ParityCheckException(String s) {
		super(s);
	}
}
