package project;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Scanner;

public class ExplicitChannelRead {
	
	static String hexify(byte b) {
		String s = Integer.toHexString(b);
		if(s.length() == 1) s = "0" + s;
		if(b < 0) s = s.substring(6);
		return s;
	}
	
	public static void main(String args[]) {
		System.out.println("Enter the name of the file without extension: ");
		try (Scanner keyboard = new Scanner(System.in)) { 
			String filename = keyboard.nextLine();
			FileInputStream fIn;
			FileChannel fChan;
			long fSize;
			ByteBuffer mBuf;
			fIn = new FileInputStream(filename + ".pexe");
			fChan = fIn.getChannel();
			fSize = fChan.size();
			mBuf = ByteBuffer.allocate((int) fSize);
			fChan.read(mBuf);
			fChan.close(); 
			fIn.close(); 
			mBuf.rewind();
			int count = 0;
			for (int i = 0; i < fSize; i++) {
				System.out.print(hexify(mBuf.get()) + " ");
				count++;
				if(count % 30 == 0) System.out.println();
			}
			System.out.println();
		} catch (IOException exc) {
			System.out.println(exc);
			System.exit(1);
		}
	}
}