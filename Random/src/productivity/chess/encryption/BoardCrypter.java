package productivity.chess.encryption;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import productivity.chess.model.Board;

public class BoardCrypter {
	private static byte[] startBoardHash;
	
	public static byte[] encrypt(byte[] input){
		if (startBoardHash == null) loadXorHash();
		byte[] output = new byte[input.length];
		int i;
		for (i = 0; i<input.length && i<startBoardHash.length; i++){
			output[i] = (byte) (input[i] ^ startBoardHash[i]);
		}
		for (;i<input.length; i++){
			output[i] = input[i];
		}
		return output;
	}
	public static byte[] decrypt(byte[] input){
		return encrypt(input);
	}
	
	private static void loadXorHash(){
		FileInputStream fis = null;
		ObjectInputStream in = null;
		ObjectOutputStream out = null;
		ByteArrayOutputStream baos = null;
		try {
			fis = new FileInputStream("resources/default.layout");
			in = new ObjectInputStream(fis);
            Board b = (Board)in.readObject();
            baos = new ByteArrayOutputStream();
            out = new ObjectOutputStream(baos);
            out.writeObject(b);
            startBoardHash = baos.toByteArray();
		}
		catch(IOException ex) {
			ex.printStackTrace();
		}
		catch(ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		finally {
			try{in.close();fis.close();baos.close();out.close();}catch(Throwable t){}
		}
	}
}
