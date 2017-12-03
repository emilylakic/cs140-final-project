package project;

public class Test {
	public static void main(String[] args) {
		byte b = 0011;
		Instruction i = new Instruction(b,2);
		Instruction.checkParity(i);
	}
}
