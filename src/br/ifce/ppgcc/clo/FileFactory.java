package br.ifce.ppgcc.clo;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class FileFactory {

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		//Cria arquivo de 1MB
		byte[] dados = new byte[1048576];
		
		for (int i = 0; i < 1048576; i++) {
			dados[i] = Byte.parseByte(Integer.toBinaryString((int)(Math.random() * 128)), 2);		
		}	
		
    	PrintWriter file = new PrintWriter("D:/clo/arquivo", "UTF-8");
    	for (int i = 0; i < 1048576; i++) {
    		file.write(dados[i]);
    	}
    	file.close();

		System.out.println("FIM!");
	}

}
