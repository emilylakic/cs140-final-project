package project;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

public class InstructionTester {

	MachineModel model = new MachineModel(true, () ->  {});
	int[] dataCopy = new int[Memory.DATA_SIZE];
	int accInit;
	int pcInit;

	@Before
	public void setup() {
		for (int i = 0; i < Memory.DATA_SIZE; i++) {
			dataCopy[i] = -5*Memory.DATA_SIZE + 10*i;
			model.setData(i, -5*Memory.DATA_SIZE + 10*i);
			// Initially the machine will contain a known spread
			// of different numbers: 
			// -2560, -2550, -2540, ..., 0, 10, 20, ..., 2550 
			// This allows us to check that the instructions do 
			// not corrupt machine unexpectedly.
			// 0 is at index 256
		}
		accInit = 7;
		pcInit = 4;
	}


	@Test 
	// NOP only increments the program counter
	public void testNOP(){
		Instruction instr = new Instruction((byte)0b00000000,0);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		//Test machine is not changed
		assertArrayEquals(dataCopy, model.getData());
		//Test program counter incremented
		assertEquals("Program counter incremented", pcInit+1,
				model.getPC());
		//Test accumulator untouched
		assertEquals("Accumulator unchanged", accInit,
				model.getAccum());
	}

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testNOPbadParity(){
		Instruction instr = new Instruction((byte)0b00000001,0);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testNOPimmedBadParity(){
		Instruction instr = new Instruction((byte)0b00000010,0);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test (expected=IllegalInstructionException.class)
	// Check NOP cannot have an immediate flag
	public void testNOPimmedIllegal(){
		Instruction instr = new Instruction((byte)0b00000011,0);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testNOPindirBadParity(){
		Instruction instr = new Instruction((byte)0b00000100,0);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test (expected=IllegalInstructionException.class)
	// Check NOP cannot have an indirect flag
	public void testNOPindirIllegal(){
		Instruction instr = new Instruction((byte)0b00000101,0);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test (expected=IllegalInstructionException.class)
	// Check NOP cannot have the special flags for Jumps
	public void testNOPflags6Illegal(){
		Instruction instr = new Instruction((byte)0b00000110,0);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testNOPbadFlagsBadParity(){
		Instruction instr = new Instruction((byte)0b00000111,0);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}
	
	@Test
	// Check NOT with accumulator greater than 0 gives false
	public void testNOTaccumGT0() {
		Instruction instr = new Instruction((byte)0b00001001,0);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		//Test machine is not changed
        assertArrayEquals(dataCopy, model.getData()); 
        //Test program counter incremented
        assertEquals("Program counter incremented", pcInit + 1,
                model.getPC());
        //Accumulator is 1
        assertEquals("Accumulator is 0", 0,
                model.getAccum());
	}

	@Test
	// Check NOT with accumulator equal to 0 gives true
	public void testNOTaccumEQ0() {
		Instruction instr = new Instruction((byte)0b00001001,0);
		accInit = 0;
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		//Test machine is not changed
        assertArrayEquals(dataCopy, model.getData()); 
        //Test program counter incremented
        assertEquals("Program counter incremented", pcInit + 1,
                model.getPC());
        //Accumulator is 1
        assertEquals("Accumulator is 0", 1,
                model.getAccum());
	}

	@Test
	// Check NOT with accumulator less than 0 gives false
	public void testNOTaccumLT0() {
		Instruction instr = new Instruction((byte)0b00001001,0);
		accInit = -5;
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		//Test machine is not changed
        assertArrayEquals(dataCopy, model.getData()); 
        //Test program counter incremented
        assertEquals("Program counter incremented", pcInit + 1,
                model.getPC());
        //Accumulator is 1
        assertEquals("Accumulator is 0", 0,
                model.getAccum());
	}

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testNOTdirectBadParity(){
		Instruction instr = new Instruction((byte)0b00001000,0);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test (expected=IllegalInstructionException.class)
	// Check NOT cannot have an immediate flag
	public void testNOTimmedIllegal(){
		Instruction instr = new Instruction((byte)0b00001010,0);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testNOTimmedBadParity(){
		Instruction instr = new Instruction((byte)0b00001011,0);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test (expected=IllegalInstructionException.class)
	// Check NOT cannot have an indirect flag
	public void testNOTindirIllegal(){
		Instruction instr = new Instruction((byte)0b00001100,0);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testNOTindirBadParity(){
		Instruction instr = new Instruction((byte)0b00001101,0);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test (expected=IllegalInstructionException.class)
	// Check NOT cannot have an special flags for Jump instructions
	public void testNOTbadFlags(){
		Instruction instr = new Instruction((byte)0b00001111,0);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testNOTBadFlagsAndParity(){
		Instruction instr = new Instruction((byte)0b00001110,0);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}
	
	@Test
	// Check HALT accumulator greater than 0 gives false
	public void testHALT() {
		Instruction instr = new Instruction((byte)0b00010001,0);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		//Test machine is not changed
        assertArrayEquals(dataCopy, model.getData()); 
        //Test program counter incremented
        assertEquals("Program counter incremented", pcInit,
                model.getPC());
        //Accumulator is 1
        assertEquals("Accumulator is 0", accInit,
                model.getAccum());
	}

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testHALTdirectBadParity(){
		Instruction instr = new Instruction((byte)0b00010000,0);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test (expected=IllegalInstructionException.class)
	// Check HALT cannot have an immediate flag
	public void testHALTimmedIllegal(){
		Instruction instr = new Instruction((byte)0b00010010,0);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testHALTimmedBadParity(){
		Instruction instr = new Instruction((byte)0b00010011,0);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test (expected=IllegalInstructionException.class)
	// Check HALT cannot have an indirect flag
	public void testHALTindirIllegal(){
		Instruction instr = new Instruction((byte)0b00010100,0);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testHALTindirBadParity(){
		Instruction instr = new Instruction((byte)0b00010101,0);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test (expected=IllegalInstructionException.class)
	// Check HALT cannot have special flags for Jump instructions
	public void testHALTbadFlags(){
		Instruction instr = new Instruction((byte)0b00010111,0);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testHALTBadFlagsAndParity(){
		Instruction instr = new Instruction((byte)0b00010110,0);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}
	
	@Test
	// Test whether LOD is correct with direct addressing
	public void testLODdirect(){
		Instruction instr = new Instruction((byte)0b00011000,12);
		accInit = 27;
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		//Should load data[12] = -2560+120 into the accumulator
		//Test machine is not changed
        assertArrayEquals(dataCopy, model.getData());
        //Test program counter incremented
        assertEquals("Program counter incremented", pcInit+1,
        		model.getPC());
        //Test accumulator modified
        assertEquals("Accumulator changed", -2560+120,
        		model.getAccum());
	}

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testLODdirectBadParity(){
		Instruction instr = new Instruction((byte)0b00011001,12);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test
	// Test whether LOD is correct with immediate addressing
	public void testLODimmed(){
		Instruction instr = new Instruction((byte)0b00011011,12);
		accInit = 27;
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		//Should load 12 into the accumulator
		//Test machine is not changed
        assertArrayEquals(dataCopy, model.getData());
        //Test program counter incremented
        assertEquals("Program counter incremented", pcInit+1,
                model.getPC());
        //Test accumulator modified
        assertEquals("Accumulator changed", 12,
                model.getAccum());
	}

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testLODimmedBadParity(){
		Instruction instr = new Instruction((byte)0b00011010,12);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test
	// Test whether LOD is correct with indirect addressing
	public void testLODindirect() {
		Instruction instr = new Instruction((byte)0b00011101,260);
		accInit = 27;
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		//Should load data[data[260]] = data[-2560+2600]] = data[40] = -2560 + 400
		//into the accumulator
		//Test machine is not changed
        assertArrayEquals(dataCopy, model.getData());
        //Test program counter incremented
        assertEquals("Program counter incremented", pcInit+1,
                model.getPC());
        //Test accumulator modified
        assertEquals("Accumulator changed", -2560+400,
                model.getAccum());
	}	

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testLODindirectBadParity() {
		Instruction instr = new Instruction((byte)0b00011100,260);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}	

	@Test (expected=IllegalInstructionException.class)
	// Check LOD cannot have special flags for Jump instructions
	public void testLODBadflags() {
		Instruction instr = new Instruction((byte)0b00011110,260);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}	

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testLODBadflagsAndParity() {
		Instruction instr = new Instruction((byte)0b00011111,260);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}	

	@Test
	// Test whether STO is correct with direct addressing
	public void testSTOdirect() {
		Instruction instr = new Instruction((byte)0b00100001,12);
		accInit = 567;
		dataCopy[12] = 567;
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		//Test machine is changed correctly
		assertArrayEquals(dataCopy, model.getData());
		//Test program counter incremented
		assertEquals("Program counter incremented", pcInit+1,
				model.getPC());
		//Test accumulator unchanged
		assertEquals("Accumulator unchanged", 567,
				model.getAccum());
	}

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testSTOdirectBadParity() {
		Instruction instr = new Instruction((byte)0b00100000,12);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test (expected=IllegalInstructionException.class)
	// Check STO cannot have immediate addressing
	public void testSTOimmedIllegal() {
		Instruction instr = new Instruction((byte)0b00100010,12);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testSTOimmedBadParity() {
		Instruction instr = new Instruction((byte)0b00100011,12);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test
	// Test whether STO is correct with indirect addressing
	public void testSTOindir() {
		Instruction instr = new Instruction((byte)0b00100100,260);
		accInit = 567;
		//Should store accum at data[data[260]] = data[=2560+2600] = data[40]
		dataCopy[40] = 567;
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		//Test machine is changed correctly
		assertArrayEquals(dataCopy, model.getData());
		//Test program counter incremented
		assertEquals("Program counter incremented", pcInit+1,
				model.getPC());
		//Test accumulator unchanged
		assertEquals("Accumulator unchanged", 567,
				model.getAccum());
	}

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testSTOindirBadParity() {
		Instruction instr = new Instruction((byte)0b00100101,260);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testSTOflags6BadParity() {
		Instruction instr = new Instruction((byte)0b00100110,260);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test (expected=IllegalInstructionException.class)
	// Check STO cannot have special flags for Jump instructions
	public void testSTOflags6Illegal() {
		Instruction instr = new Instruction((byte)0b00100111,260);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test
	// Test whether ADD is correct with direct addressing
	public void testADDdirect(){
		Instruction instr = new Instruction((byte)0b00101000,12);
		accInit = 27;
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		//Should add data[12] = -2560+120 to the accumulator
		//Test machine is not changed
        assertArrayEquals(dataCopy, model.getData());
        //Test program counter incremented
        assertEquals("Program counter incremented", pcInit+1,
        		model.getPC());
        //Test accumulator modified
        assertEquals("Accumulator changed", accInit-2560+120,
        		model.getAccum());
	}

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testADDdirectBadParity(){
		Instruction instr = new Instruction((byte)0b00101001,12);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test
	// Test whether ADD is correct with immediate addressing
	public void testADDimmed(){
		Instruction instr = new Instruction((byte)0b00101011,12);
		accInit = 27;
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		//Should add 12 to the accumulator
		//Test machine is not changed
        assertArrayEquals(dataCopy, model.getData());
        //Test program counter incremented
        assertEquals("Program counter incremented", pcInit+1,
                model.getPC());
        //Test accumulator modified
        assertEquals("Accumulator changed", accInit+12,
                model.getAccum());
	}

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testADDimmedBadParity(){
		Instruction instr = new Instruction((byte)0b00101010,12);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test
	// Test whether ADD is correct with indirect addressing
	public void testADDindirect() {
		Instruction instr = new Instruction((byte)0b00101101,260);
		accInit = 27;
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		//Note data[260] = -2560+2600 = 40
		//Should add data[data[260]] = data[40] = -2560 + 400
		//to the accumulator
		//Test machine is not changed
        assertArrayEquals(dataCopy, model.getData());
        //Test program counter incremented
        assertEquals("Program counter incremented", pcInit+1,
                model.getPC());
        //Test accumulator modified
        assertEquals("Accumulator changed", accInit-2560+400,
                model.getAccum());
	}	

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testADDindirectBadParity() {
		Instruction instr = new Instruction((byte)0b00101100,260);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}	

	@Test (expected=IllegalInstructionException.class)
	// Check ADD cannot have special flags for Jump instructions
	public void testADDBadflags() {
		Instruction instr = new Instruction((byte)0b00101110,260);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}	

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testADDBadflagsAndParity() {
		Instruction instr = new Instruction((byte)0b00101111,260);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}	

	@Test
	// Test whether SUB is correct with direct addressing
	public void testSUBdirect(){
		Instruction instr = new Instruction((byte)0b00110000,12);
		accInit = 27;
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		//Should subtract data[12] = -2560+120 from the accumulator
		//Test machine is not changed
        assertArrayEquals(dataCopy, model.getData());
        //Test program counter incremented
        assertEquals("Program counter incremented", pcInit+1,
        		model.getPC());
        //Test accumulator modified
        assertEquals("Accumulator changed", accInit-(-2560+120),
        		model.getAccum());
	}

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testSUBdirectBadParity(){
		Instruction instr = new Instruction((byte)0b00110001,12);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test
	// Test whether SUB is correct with immediate addressing
	public void testSUBimmed(){
		Instruction instr = new Instruction((byte)0b00110011,12);
		accInit = 27;
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		//Should subtract 12 from the accumulator
		//Test machine is not changed
        assertArrayEquals(dataCopy, model.getData());
        //Test program counter incremented
        assertEquals("Program counter incremented", pcInit+1,
                model.getPC());
        //Test accumulator modified
        assertEquals("Accumulator changed", accInit-12,
                model.getAccum());
	}

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testSUBimmedBadParity(){
		Instruction instr = new Instruction((byte)0b00110010,12);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test
	// Test whether SUB is correct with indirect addressing
	public void testSUBindirect() {
		Instruction instr = new Instruction((byte)0b00110101,260);
		accInit = 27;
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		//Note data[260] = -2560+2600 = 40
		//Should subtract data[data[260]] = data[40] = -2560 + 400
		//from the accumulator
		//Test machine is not changed
        assertArrayEquals(dataCopy, model.getData());
        //Test program counter incremented
        assertEquals("Program counter incremented", pcInit+1,
                model.getPC());
        //Test accumulator modified
        assertEquals("Accumulator changed", accInit-(-2560+400),
                model.getAccum());
	}	

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testSUBindirectBadParity() {
		Instruction instr = new Instruction((byte)0b00110100,260);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}	

	@Test (expected=IllegalInstructionException.class)
	// Check SUB cannot have special flags for Jump instructions
	public void testSUBBadflags() {
		Instruction instr = new Instruction((byte)0b00110110,260);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}	

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testSUBBadflagsAndParity() {
		Instruction instr = new Instruction((byte)0b00110111,260);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}	

	@Test
	// Test whether load is correct with direct addressing
	public void testMULdirect(){
		Instruction instr = new Instruction((byte)0b00111001,12);
		accInit = 27;
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		// should load -2560+120 into the accumulator
		//Test machine is not changed
        assertArrayEquals(dataCopy, model.getData());
        //Test program counter incremented
        assertEquals("Program counter incremented", pcInit+1,
        		model.getPC());
        //Test accumulator modified
        assertEquals("Accumulator changed", accInit*(-2560+120),
        		model.getAccum());
	}

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testMULdirectBadParity(){
		Instruction instr = new Instruction((byte)0b00111000,12);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test
	// Test whether load is correct with immediate addressing
	public void testMULimmed(){
		Instruction instr = new Instruction((byte)0b00111010,12);
		accInit = 27;
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		// should load 12 into the accumulator
		//Test machine is not changed
        assertArrayEquals(dataCopy, model.getData());
        //Test program counter incremented
        assertEquals("Program counter incremented", pcInit+1,
                model.getPC());
        //Test accumulator modified
        assertEquals("Accumulator changed", accInit*12,
                model.getAccum());
	}

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testMULimmedBadParity(){
		Instruction instr = new Instruction((byte)0b00111011,12);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test
	// Test whether load is correct with indirect addressing
	public void testMULindirect() {
		Instruction instr = new Instruction((byte)0b00111100,260);
		accInit = 27;
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		// should load data[-2560+2600] = data[40] = -2560 + 400
		// into the accumulator
		//Test machine is not changed
        assertArrayEquals(dataCopy, model.getData());
        //Test program counter incremented
        assertEquals("Program counter incremented", pcInit+1,
                model.getPC());
        //Test accumulator modified
        assertEquals("Accumulator changed", accInit*(-2560+400),
                model.getAccum());
	}	

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testMULindirectBadParity() {
		Instruction instr = new Instruction((byte)0b00111101,260);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}	

	@Test (expected=IllegalInstructionException.class)
	// Check MUL cannot have special flags for Jump instructions
	public void testMULBadflags() {
		Instruction instr = new Instruction((byte)0b00111111,260);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}	

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testMULBadflagsAndParity() {
		Instruction instr = new Instruction((byte)0b00111110,260);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}	

	@Test
	// Test whether DIV is correct with direct addressing
	public void testDIVdirect(){
		Instruction instr = new Instruction((byte)0b01000001,260);
		accInit = 275;
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		//Should divide accumulator by data[260]  
		//Change data[260] and dataCopy[260] to 12
		model.setData(260, 12);
		dataCopy[260] = 12;
		model.step();
		//Test machine is not changed
        assertArrayEquals(dataCopy, model.getData());
        //Test program counter incremented
        assertEquals("Program counter incremented", pcInit+1,
        		model.getPC());
        //Test accumulator modified
        assertEquals("Accumulator changed", accInit/12,
        		model.getAccum());
	}

	@Test (expected=DivideByZeroException.class)
	// Test for divide by zero with direct addressing
	public void testDIVzeroDirect(){
		Instruction instr = new Instruction((byte)0b01000001,260);
		accInit = 275;
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		//Should divide accumulator by data[260]  
		//Change data[260] to 0
		model.setData(260, 0);
		model.step();
	}

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testDIVdirectBadParity(){
		Instruction instr = new Instruction((byte)0b01000000,12);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test
	// Test whether DIV is correct with immediate addressing
	public void testDIVimmed(){
		Instruction instr = new Instruction((byte)0b01000010,12);
		accInit = 275;
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		//Should divide accumulator by 12
		//Test machine is not changed
        assertArrayEquals(dataCopy, model.getData());
        //Test program counter incremented
        assertEquals("Program counter incremented", pcInit+1,
                model.getPC());
        //Test accumulator modified
        assertEquals("Accumulator changed", accInit/12,
                model.getAccum());
	}

	@Test (expected=DivideByZeroException.class)
	// Test for divide by zero with immediate addressing
	public void testDIVZeroImmed(){
		Instruction instr = new Instruction((byte)0b01000010,0);
		accInit = 275;
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
	}

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testDIVimmedBadParity(){
		Instruction instr = new Instruction((byte)0b01000011,12);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test
	// Test whether DIV is correct with indirect addressing
	public void testDIVindirect() {
		Instruction instr = new Instruction((byte)0b01000100,260);
		accInit = 275;
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		// should divide accumulator by 
		// data[data[260]] = data[-2560+2600] = data[40]
		// change data[40] and dataCopy[40] to 12
		model.setData(40, 12);
		dataCopy[40] = 12;
		model.step();
		//Test machine is not changed
        assertArrayEquals(dataCopy, model.getData());
        //Test program counter incremented
        assertEquals("Program counter incremented", pcInit+1,
                model.getPC());
        //Test accumulator modified
        assertEquals("Accumulator changed", accInit/12,
                model.getAccum());
	}	

	@Test (expected=DivideByZeroException.class)
	// Test for divide by zero with indirect addressing
	public void testDIVzeroIndirect() {
		Instruction instr = new Instruction((byte)0b01000100,260);
		accInit = 275;
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		//Should divide accumulator by data[data[260] = 
		//data[-2560+2600] = data[40]
		//change data[40] to 0
		model.setData(40, 0);
		model.step();
	}	

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testDIVindirectBadParity() {
		Instruction instr = new Instruction((byte)0b01000101,260);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}	

	@Test (expected=IllegalInstructionException.class)
	// Check DIV cannot have special flags for Jump instructions
	public void testDIVBadflags() {
		Instruction instr = new Instruction((byte)0b01000111,260);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}	

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testDIVBadflagsAndParity() {
		Instruction instr = new Instruction((byte)0b01000110,260);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}	

	@Test
	// Check AND direct when accum and arg equal to 0 gives false
	public void testANDdirectAccEQ0argEQ0() {
		Instruction instr = new Instruction((byte)0b01001000,256);
		accInit = 0;
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		//accumulator and data[256] = -2560+2560 both 0
		//Test machine is not changed
        assertArrayEquals(dataCopy, model.getData());
        //Test program counter incremented
        assertEquals("Program counter incremented", pcInit+1,
        		model.getPC());
        //Test accumulator modified
        assertEquals("Accumulator false", 0,
        		model.getAccum());
	}

	@Test
	// Check AND direct when accum > 0 and arg equal to 0 gives false
	public void testANDdirectAccGT0argEQ0() {
		Instruction instr = new Instruction((byte)0b01001000,256);
		accInit = 10;
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		//accumulator > 0 and data[256] = -2560+2560 = 0
		//Test machine is not changed
        assertArrayEquals(dataCopy, model.getData());
        //Test program counter incremented
        assertEquals("Program counter incremented", pcInit+1,
        		model.getPC());
        //Test accumulator modified
        assertEquals("Accumulator false", 0,
        		model.getAccum());
	}

	@Test
	// Check AND direct when accum < 0 and arg equal to 0 gives false
	public void testANDdirectAccLT0argEQ0() {
		Instruction instr = new Instruction((byte)0b01001000,256);
		accInit = -10;
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		// accumulator < 0 and data[256] = -2560+2560 = 0
		//Test machine is not changed
        assertArrayEquals(dataCopy, model.getData());
        //Test program counter incremented
        assertEquals("Program counter incremented", pcInit+1,
        		model.getPC());
        //Test accumulator modified
        assertEquals("Accumulator false", 0,
        		model.getAccum());
	}

	@Test
	// Check AND direct when accum = 0 and arg > 0 gives false
	public void testANDdirectAccEQ0argGT0() {
		Instruction instr = new Instruction((byte)0b01001000,257);
		accInit = 0;
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		//accumulator = 0 and data[257] = -2560+2570 = 10
		//Test machine is not changed
        assertArrayEquals(dataCopy, model.getData());
        //Test program counter incremented
        assertEquals("Program counter incremented", pcInit+1,
        		model.getPC());
        //Test accumulator modified
        assertEquals("Accumulator false", 0,
        		model.getAccum());
	}

	@Test
	// Check AND direct when accum > 0 and arg > 0 gives true
	public void testANDdirectAccGT0argGT0() {
		Instruction instr = new Instruction((byte)0b01001000,257);
		accInit = 10;
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		//accumulator = 10 and data[257] = -2560+2570 = 10
		//Test machine is not changed
        assertArrayEquals(dataCopy, model.getData());
        //Test program counter incremented
        assertEquals("Program counter incremented", pcInit+1,
        		model.getPC());
        //Test accumulator modified
        assertEquals("Accumulator true", 1,
        		model.getAccum());
	}

	@Test
	// Check AND direct when accum < 0 and arg > 0 gives true
	public void testANDdirectAccLT0argGT0() {
		Instruction instr = new Instruction((byte)0b01001000,257);
		accInit = -10;
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		//accumulator = -10 and data[257] = -2560+2570 = 10
		//Test machine is not changed
        assertArrayEquals(dataCopy, model.getData());
        //Test program counter incremented
        assertEquals("Program counter incremented", pcInit+1,
        		model.getPC());
        //Test accumulator modified
        assertEquals("Accumulator true", 1,
        		model.getAccum());
	}

	@Test
	// Check AND direct when accum = 0 and arg < 0 gives false
	public void testANDdirectAccEQ0argLT0() {
		Instruction instr = new Instruction((byte)0b01001000,255);
		accInit = 0;
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		//accumulator = 0 and data[255] = -2560+2550 = -10
		//Test machine is not changed
        assertArrayEquals(dataCopy, model.getData());
        //Test program counter incremented
        assertEquals("Program counter incremented", pcInit+1,
        		model.getPC());
        //Test accumulator modified
        assertEquals("Accumulator false", 0,
        		model.getAccum());
	}

	@Test
	// Check AND direct when accum > 0 and arg < 0 gives true
	public void testANDdirectAccGT0argLT0() {
		Instruction instr = new Instruction((byte)0b01001000,255);
		accInit = 10;
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		//accumulator = 10 and data[255] = -2560+2550 = -10
		//Test machine is not changed
        assertArrayEquals(dataCopy, model.getData());
        //Test program counter incremented
        assertEquals("Program counter incremented", pcInit+1,
        		model.getPC());
        //Test accumulator modified
        assertEquals("Accumulator true", 1,
        		model.getAccum());
	}

	@Test
	// Check AND direct when accum < 0 and arg < 0 gives true
	public void testANDdirectAccLT0argLT0() {
		Instruction instr = new Instruction((byte)0b01001000,255);
		accInit = -10;
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		//accumulator = -10 and data[255] = -2560+2550 = -10
		//Test machine is not changed
        assertArrayEquals(dataCopy, model.getData());
        //Test program counter incremented
        assertEquals("Program counter incremented", pcInit+1,
        		model.getPC());
        //Test accumulator modified
        assertEquals("Accumulator true", 1,
        		model.getAccum());
	}

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testANDdirectBadParity() {
		Instruction instr = new Instruction((byte)0b01001001,260);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}	

	@Test
	// Check AND immediate when accum and arg equal to 0 gives false
	public void testANDimmedAccEQ0argEQ0() {
		Instruction instr = new Instruction((byte)0b01001011,0);
		accInit = 0;
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		//accumulator = 0 and arg = 0
		//Test machine is not changed
        assertArrayEquals(dataCopy, model.getData());
        //Test program counter incremented
        assertEquals("Program counter incremented", pcInit+1,
        		model.getPC());
        //Test accumulator modified
        assertEquals("Accumulator false", 0,
        		model.getAccum());
	}

	@Test
	// Check AND immediate when accum > 0 and arg equal to 0 gives false
	public void testANDimmedAccGT0argEQ0() {
		Instruction instr = new Instruction((byte)0b01001011,0);
		accInit = 10;
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		//accumulator = 10 and arg = 0
		//Test machine is not changed
        assertArrayEquals(dataCopy, model.getData());
        //Test program counter incremented
        assertEquals("Program counter incremented", pcInit+1,
        		model.getPC());
        //Test accumulator modified
        assertEquals("Accumulator false", 0,
        		model.getAccum());
	}

	@Test
	// Check AND immediate when accum < 0 and arg equal to 0 gives false
	public void testANDimmedAccLT0argEQ0() {
		Instruction instr = new Instruction((byte)0b01001011,0);
		accInit = -10;
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		//accumulator = -10 and arg = 0
		//Test machine is not changed
        assertArrayEquals(dataCopy, model.getData());
        //Test program counter incremented
        assertEquals("Program counter incremented", pcInit+1,
        		model.getPC());
        //Test accumulator modified
        assertEquals("Accumulator false", 0,
        		model.getAccum());
	}

	@Test
	// Check AND immediate when accum = 0 and arg > 0 gives false
	public void testANDimmedAccEQ0argGT0() {
		Instruction instr = new Instruction((byte)0b01001011,12);
		accInit = 0;
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		//accumulator = 0 and arg = 12
		//Test machine is not changed
        assertArrayEquals(dataCopy, model.getData());
        //Test program counter incremented
        assertEquals("Program counter incremented", pcInit+1,
        		model.getPC());
        //Test accumulator modified
        assertEquals("Accumulator false", 0,
        		model.getAccum());
	}

	@Test
	// Check AND immediate when accum > 0 and arg > 0 gives true
	public void testANDimmedAccGT0argGT0() {
		Instruction instr = new Instruction((byte)0b01001011,12);
		accInit = 10;
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		//accumulator = 10 and arg = 12
		//Test machine is not changed
        assertArrayEquals(dataCopy, model.getData());
        //Test program counter incremented
        assertEquals("Program counter incremented", pcInit+1,
        		model.getPC());
        //Test accumulator modified
        assertEquals("Accumulator true", 1,
        		model.getAccum());
	}

	@Test
	// Check AND immediate when accum < 0 and arg > 0 gives true
	public void testANDimmedAccLT0argGT0() {
		Instruction instr = new Instruction((byte)0b01001011,12);
		accInit = -10;
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		//accumulator = -10 and arg = 12
		//Test machine is not changed
        assertArrayEquals(dataCopy, model.getData());
        //Test program counter incremented
        assertEquals("Program counter incremented", pcInit+1,
        		model.getPC());
        //Test accumulator modified
        assertEquals("Accumulator true", 1,
        		model.getAccum());
	}

	@Test
	// Check AND immediate when accum = 0 and arg < 0 gives false
	public void testANDimmedAccEQ0argLT0() {
		Instruction instr = new Instruction((byte)0b01001011,-12);
		accInit = 0;
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		//accumulator = 0 and arg = -12
		//Test machine is not changed
        assertArrayEquals(dataCopy, model.getData());
        //Test program counter incremented
        assertEquals("Program counter incremented", pcInit+1,
        		model.getPC());
        //Test accumulator modified
        assertEquals("Accumulator false", 0,
        		model.getAccum());
	}

	@Test
	// Check AND immediate when accum > 0 and arg < 0 gives true
	public void testANDimmedAccGT0argLT0() {
		Instruction instr = new Instruction((byte)0b01001011,-12);
		accInit = 10;
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		//accumulator = 10 and arg = -12
		//Test machine is not changed
        assertArrayEquals(dataCopy, model.getData());
        //Test program counter incremented
        assertEquals("Program counter incremented", pcInit+1,
        		model.getPC());
        //Test accumulator modified
        assertEquals("Accumulator true", 1,
        		model.getAccum());
	}

	@Test
	// Check AND immediate when accum < 0 and arg < 0 gives true
	public void testANDimmedAccLT0argLT0() {
		Instruction instr = new Instruction((byte)0b01001011,-12);
		accInit = -10;
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		//accumulator = -10 and arg = -12
		//Test machine is not changed
        assertArrayEquals(dataCopy, model.getData());
        //Test program counter incremented
        assertEquals("Program counter incremented", pcInit+1,
        		model.getPC());
        //Test accumulator modified
        assertEquals("Accumulator true", 1,
        		model.getAccum());
	}

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testANDimmedBadParity() {
		Instruction instr = new Instruction((byte)0b01001010,260);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}	

	@Test (expected=IllegalInstructionException.class)
	// Check AND cannot have indirect addressing
	public void testANDindirIllegal() {
		Instruction instr = new Instruction((byte)0b01001101,12);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testANDindirBadParity() {
		Instruction instr = new Instruction((byte)0b01001100,260);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}	

	@Test (expected=IllegalInstructionException.class)
	// Check AND cannot have special flags for Jump instructions
	public void testANDbadFlags() {
		Instruction instr = new Instruction((byte)0b01001110,12);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testANDbadFlagsAndParity() {
		Instruction instr = new Instruction((byte)0b01001111,260);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}	
	
	@Test 
	// this test checks whether the relative jump is done correctly, when
	// address is the argument
	public void testJUMPdirect() {
		Instruction instr = new Instruction((byte)0b01010000,12);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		//Should have added 12 to the program counter
		assertArrayEquals(dataCopy, model.getData()); 
		assertEquals("Program counter was changed", pcInit + 12,
				model.getPC());
		assertEquals("Accumulator was not changed", accInit,
				model.getAccum());
	}

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testJUMPdirectBadParity() {
		Instruction instr = new Instruction((byte)0b01010001,12);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}
	
	@Test 
	// this test checks whether the immediate jump is done correctly, when
	// address is the argument
	public void testJUMPimmed() {
		Instruction instr = new Instruction((byte)0b01010011,12);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		//Should have set the program counter to 12
		assertArrayEquals(dataCopy, model.getData()); 
		assertEquals("Program counter was changed", 12,
				model.getPC());
		assertEquals("Accumulator was not changed", accInit,
				model.getAccum());
	}

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testJUMPimmedBadParity() {
		Instruction instr = new Instruction((byte)0b01010010,12);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test 
	// this test checks whether the indirect jump is done correctly, when
	// address is the argument
	public void testJUMPindir() {
		Instruction instr = new Instruction((byte)0b01010101,260);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		//Should have added data[260] = -2560+2600 = 40 to program counter
		assertArrayEquals(dataCopy, model.getData()); 
		assertEquals("Program counter was changed", pcInit + 40,
				model.getPC());
		assertEquals("Accumulator was not changed", accInit,
				model.getAccum());
	}

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testJUMPindirBadParity() {
		Instruction instr = new Instruction((byte)0b01010100,12);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test 
	// this test checks whether the special jump is done correctly, when
	// address is the argument
	public void testJUMPspecial() {
		Instruction instr = new Instruction((byte)0b01010110,260);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		//Should have set the program counter to data[260] = 40
		assertArrayEquals(dataCopy, model.getData()); 
		assertEquals("Program counter was changed", 40,
				model.getPC());
		assertEquals("Accumulator was not changed", accInit,
				model.getAccum());
	}

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testJUMPspecialBadParity() {
		Instruction instr = new Instruction((byte)0b01010111,12);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test 
	// this test checks whether the direct JMPZ is done correctly, when
	// accumulator = 0
	public void testJMPZaccEQ0direct() {
		Instruction instr = new Instruction((byte)0b01011001,12);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(0);
		model.step();
		//Should have added 12 to the program counter
		assertArrayEquals(dataCopy, model.getData()); 
		assertEquals("Program counter was changed", pcInit + 12,
				model.getPC());
		assertEquals("Accumulator was not changed", 0,
				model.getAccum());
	}

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testJMPZaccEQ0directBadParity() {
		Instruction instr = new Instruction((byte)0b01011000,12);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(0);
		model.step();
	}
	
	@Test 
	// this test checks whether the immediate JMPZ is done correctly, when
	// accumulator = 0
	public void testJMPZaccEQ0immed() {
		Instruction instr = new Instruction((byte)0b01011010,12);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(0);
		model.step();
		//Should have set the program counter to 12
		assertArrayEquals(dataCopy, model.getData()); 
		assertEquals("Program counter was changed", 12,
				model.getPC());
		assertEquals("Accumulator was not changed", 0,
				model.getAccum());
	}

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testJMPZaccEQ0immedBadParity() {
		Instruction instr = new Instruction((byte)0b01011011,12);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(0);
		model.step();
	}

	@Test 
	// this test checks whether the indirect JMPZ is done correctly, when
	// accumulator = 0
	public void testJMPZaccEQ0indir() {
		Instruction instr = new Instruction((byte)0b01011100,260);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(0);
		model.step();
		//Should have added data[260] = -2560+2600 = 40 to program counter
		assertArrayEquals(dataCopy, model.getData()); 
		assertEquals("Program counter was changed", pcInit + 40,
				model.getPC());
		assertEquals("Accumulator was not changed", 0,
				model.getAccum());
	}

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testJMPZaccEQ0indirBadParity() {
		Instruction instr = new Instruction((byte)0b01011101,12);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(0);
		model.step();
	}

	@Test 
	// this test checks whether the special JMPZ is done correctly, when
	// accumulator = 0
	public void testJMPZaccEQ0special() {
		Instruction instr = new Instruction((byte)0b01011111,260);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(0);
		model.step();
		//Should have set the program counter to data[260] = 40
		assertArrayEquals(dataCopy, model.getData()); 
		assertEquals("Program counter was changed", 40,
				model.getPC());
		assertEquals("Accumulator was not changed", 0,
				model.getAccum());
	}

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testJMPZaccEQ0specialBadParity() {
		Instruction instr = new Instruction((byte)0b01011110,12);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
	}

	@Test 
	// this test checks whether the direct JMPZ is done correctly, when
	// accumulator not 0
	public void testJMPZdirect() {
		Instruction instr = new Instruction((byte)0b01011001,12);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		//accumulator not zero, should increment program counter
		assertArrayEquals(dataCopy, model.getData()); 
		assertEquals("Program counter was changed", pcInit + 1,
				model.getPC());
		assertEquals("Accumulator was not changed", accInit,
				model.getAccum());
	}

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testJMPZdirectBadParity() {
		Instruction instr = new Instruction((byte)0b01011000,12);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}
	
	@Test 
	// this test checks whether the immediate JMPZ is done correctly, when
	// accumulator not 0
	public void testJMPZimmed() {
		Instruction instr = new Instruction((byte)0b01011010,12);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		//accumulator not zero, should increment program counter
		assertArrayEquals(dataCopy, model.getData()); 
		assertEquals("Program counter was changed", pcInit + 1,
				model.getPC());
		assertEquals("Accumulator was not changed", accInit,
				model.getAccum());
	}

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testJMPZimmedBadParity() {
		Instruction instr = new Instruction((byte)0b01011011,12);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
	}

	@Test 
	// this test checks whether the indirect JMPZ is done correctly, when
	// accumulator not 0
	public void testJMPZindir() {
		Instruction instr = new Instruction((byte)0b01011100,260);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		//accumulator not zero, should increment program counter
		assertArrayEquals(dataCopy, model.getData()); 
		assertEquals("Program counter was changed", pcInit + 1,
				model.getPC());
		assertEquals("Accumulator was not changed", accInit,
				model.getAccum());
	}

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testJMPZindirBadParity() {
		Instruction instr = new Instruction((byte)0b01011101,12);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
	}

	@Test 
	// this test checks whether the special JMPZ is done correctly, when
	// accumulator not 0
	public void testJMPZspecial() {
		Instruction instr = new Instruction((byte)0b01011111,260);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		//accumulator not zero, should increment program counter
		assertArrayEquals(dataCopy, model.getData()); 
		assertEquals("Program counter was changed", pcInit+1,
				model.getPC());
		assertEquals("Accumulator was not changed", accInit,
				model.getAccum());
	}

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testJMPZspecialBadParity() {
		Instruction instr = new Instruction((byte)0b01011110,12);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(0);
		model.step();
	}

	@Test
	// Check CMPL when comparing less than 0 gives true
	public void testCMPLmemLT0() {
		Instruction instr = new Instruction((byte)0b01100000,200);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		// data[200] = -2560+2000 < 0
		//Test machine is not changed
        assertArrayEquals(dataCopy, model.getData()); 
        //Test program counter incremented
        assertEquals("Program counter incremented", pcInit + 1,
                model.getPC());
        //Accumulator is 1
        assertEquals("Accumulator is 1", 1,
                model.getAccum());
	}

	@Test
	// Check CMPL when comparing 0 gives false
	public void testCMPLmemEQ0() {
		Instruction instr = new Instruction((byte)0b01100000,256);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		// data[200] = -2560+2560 = 0
		//Test machine is not changed
        assertArrayEquals(dataCopy, model.getData()); 
        //Test program counter incremented
        assertEquals("Program counter incremented", pcInit + 1,
                model.getPC());
        //Accumulator is 1
        assertEquals("Accumulator is 0", 0,
                model.getAccum());
	}

	@Test
	// Check CMPL when comparing greater than 0 gives false
	public void testCMPLmemGT0() {
		Instruction instr = new Instruction((byte)0b01100000,260);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		// data[200] = -2560+2600 > 0
		//Test machine is not changed
        assertArrayEquals(dataCopy, model.getData()); 
        //Test program counter incremented
        assertEquals("Program counter incremented", pcInit + 1,
                model.getPC());
        //Accumulator is 1
        assertEquals("Accumulator is 0", 0,
                model.getAccum());
	}

	@Test (expected=IllegalInstructionException.class)
	// Check that CMPL cannot have immediate flag
	public void testCMPLbadImmedFlags() {
		Instruction instr = new Instruction((byte)0b01100011,12);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test (expected=IllegalInstructionException.class)
	// Check that CMPL cannot have indirect flag
	public void testCMPLbadIndirFlags() {
		Instruction instr = new Instruction((byte)0b01100101,12);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test (expected=IllegalInstructionException.class)
	// Check that CMPL cannot have special flag for Jump instruction
	public void testCMPLbadFlags() {
		Instruction instr = new Instruction((byte)0b01100110,12);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testCMPLbadParity() {
		Instruction instr = new Instruction((byte)0b01100001,260);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testCMPLimmedBadParity() {
		Instruction instr = new Instruction((byte)0b01100010,12);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testCMPLindirBadParity() {
		Instruction instr = new Instruction((byte)0b01100100,12);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testCMPLbadFlagsAndParity() {
		Instruction instr = new Instruction((byte)0b01100111,12);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test
	// Check CMPZ when comparing less than 0 gives false
	public void testCMPZmemLT0() {
		Instruction instr = new Instruction((byte)0b01101001,200);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		// data[200] = -2560+2000 < 0
		//Test machine is not changed
        assertArrayEquals(dataCopy, model.getData()); 
        //Test program counter incremented
        assertEquals("Program counter incremented", pcInit + 1,
                model.getPC());
        //Accumulator is 1
        assertEquals("Accumulator is 0", 0,
                model.getAccum());
	}

	@Test
	// Check CMPZ when comparing 0 gives true
	public void testCMPZmemEQ0() {
		Instruction instr = new Instruction((byte)0b01101001,256);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		// data[200] = -2560+2560 = 0
		//Test machine is not changed
        assertArrayEquals(dataCopy, model.getData()); 
        //Test program counter incremented
        assertEquals("Program counter incremented", pcInit + 1,
                model.getPC());
        //Accumulator is 1
        assertEquals("Accumulator is 1", 1,
                model.getAccum());
	}

	@Test
	// Check CMPZ when comparing greater than 0 gives false
	public void testCMPZmemGT0() {
		Instruction instr = new Instruction((byte)0b01101001,260);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.setAccum(accInit);
		model.step();
		// data[260] = -2560+2600 > 0
		//Test machine is not changed
        assertArrayEquals(dataCopy, model.getData()); 
        //Test program counter incremented
        assertEquals("Program counter incremented", pcInit + 1,
                model.getPC());
        //Accumulator is 1
        assertEquals("Accumulator is 0", 0,
                model.getAccum());
	}

	@Test (expected=IllegalInstructionException.class)
	// Check that CMPZ cannot have immediate flag
	public void testCMPZbadImmedFlags() {
		Instruction instr = new Instruction((byte)0b01101010,12);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test (expected=IllegalInstructionException.class)
	// Check that CMPZ cannot have indirect flag
	public void testCMPZbadIndirFlags() {
		Instruction instr = new Instruction((byte)0b01101100,12);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test (expected=IllegalInstructionException.class)
	// Check that CMPZ cannot have special flag for Jump instruction
	public void testCMPZbadFlags() {
		Instruction instr = new Instruction((byte)0b01101111,12);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testCMPZbadParity() {
		Instruction instr = new Instruction((byte)0b01101000,260);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testCMPZimmedBadParity() {
		Instruction instr = new Instruction((byte)0b01101011,12);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testCMPZindirBadParity() {
		Instruction instr = new Instruction((byte)0b01101101,12);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}

	@Test (expected=ParityCheckException.class)
	// Verify parity checking is working
	public void testCMPZbadFlagsAndParity() {
		Instruction instr = new Instruction((byte)0b01101110,12);
		model.setCode(pcInit, instr);
		model.setPC(pcInit);
		model.step();
	}
}

