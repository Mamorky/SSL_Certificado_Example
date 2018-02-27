package dam.psp;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;

public class Srv_SSL {
	static int PUERTO = 5555;
	
	public Srv_SSL() throws IOException {
		System.out.println("Obteniendo factoría del socket para el servidor");
		SSLServerSocketFactory socketSRVFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
		
		System.out.println("Creando el socket ...");
		SSLServerSocket socketSRV = (SSLServerSocket) socketSRVFactory.createServerSocket(PUERTO);
		
		while(true) {
			System.out.println("Aceptando conexiones ...");
			SSLSocket socketAtencion = (SSLSocket) socketSRV.accept();
			System.out.println("Atendiendo la conexión nueva con un hilo dedicado...");
			
			new Srv_SSL_Hilo(socketAtencion).start();
		}
	}
	
	public static void main(String[] args) {
		System.setProperty("javax.net.ssl.keyStore","./cert/AlmacenSRV");
		System.setProperty("javax.net.ssl.keyStorePassword","12345678");
		System.setProperty("javax.net.ssl.trustStore","./cert/AlmacenSRV");
		System.setProperty("javax.net.ssl.trustStorePassword","12345678");
		
		try {
			new Srv_SSL();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

class Srv_SSL_Hilo extends Thread{
	SSLSocket mySocket;
	
	public Srv_SSL_Hilo(SSLSocket socket) {
		this.mySocket = socket;
	}
	
	@Override
	public void run() {
		try {
			InputStreamReader is = new InputStreamReader(mySocket.getInputStream(),"utf8");
			BufferedReader br = new BufferedReader(is);
			
			String mensajeRecibido = br.readLine();
			System.out.println("Mensaje recibido desde el cliente: "+mensajeRecibido);
			
			//Enviamos como respuesta el mensaj en hash SHA-256
			PrintWriter pWriter = new PrintWriter(new BufferedOutputStream(mySocket.getOutputStream()),true);
			
			byte[] mensajeEnBytes = mensajeRecibido.getBytes("utf8");
			MessageDigest sha = MessageDigest.getInstance("SHA-256");
			
			pWriter.println(sha.digest(mensajeEnBytes));
			pWriter.flush();
			pWriter.close();
			
			System.out.println("Cerrando socket y el hilo de atención");
			mySocket.close();
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
} 
