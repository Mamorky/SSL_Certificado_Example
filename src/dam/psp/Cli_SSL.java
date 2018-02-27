package dam.psp;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.UnknownHostException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class Cli_SSL {
	static final String DESTINO = "localhost";
	static final int PUERTO = 5555;

	private void MostrarCifrados(SSLSocket socket) {
		String[] protocolos = socket.getEnabledProtocols();
		System.out.println("Protocolos habilitados");
		for (int i = 0; i < protocolos.length; i++) {
			System.out.println(protocolos[i] + ", ");
		}
		
		String[] protocolosSoportados = socket.getSupportedProtocols();
		System.out.println("Protocolos habilitados");
		for (int i = 0; i < protocolosSoportados.length; i++) {
			System.out.println(protocolosSoportados[i] + ", ");
		}
		
		String[] protocolosDeseados = new String[1];
		protocolosSoportados[0] = "TLSv1.1";
		socket.setEnabledProtocols(protocolosDeseados);
		
		protocolos = socket.getEnabledProtocols();
		System.out.println("Protocolos activos: ");
		System.out.println("Protocolos habilitados");
		for (int i = 0; i < protocolos.length; i++) {
			System.out.println(protocolos[i] + ", ");
		}
	}
	
	public Cli_SSL(String mensaje) throws UnknownHostException, IOException {
		System.out.println("Obteniendo factoria de socket de cliente ...");
		SSLSocketFactory socketCLIFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		
		System.out.println("Creando el socket del cliente ...");
		SSLSocket socketCli = (SSLSocket) socketCLIFactory.createSocket(DESTINO,PUERTO);
		
		MostrarCifrados(socketCli); //Mostramos los cifrados que actualmente tenemos disponibles y habilitados
		
		PrintWriter pWriter = new PrintWriter(new BufferedOutputStream(socketCli.getOutputStream()));
		pWriter.println(mensaje);
		pWriter.flush(); //Por seguridad
		
		System.out.println("Mensaje enviado: "+mensaje);
		
		//Esperamos la respuesta cifrada con has desde el servidor y la mostramos por consola
		BufferedReader bReader = new BufferedReader(new InputStreamReader(socketCli.getInputStream()));
		
		System.out.println("Mensaje cifrado recibido: "+bReader.readLine());
		System.out.println("Cerrando la conexión");
		pWriter.close();
		bReader.close();
		socketCli.close();
		System.out.println("Finalizado");
	}
	public static void main(String[] args) {
		System.setProperty("javax.net.ssl.keyStore","./cert/AlmacenCLI");
		System.setProperty("javax.net.ssl.keyStorePassword","87654321");
		System.setProperty("javax.net.ssl.trustStore","./cert/AlmacenCLI");
		System.setProperty("javax.net.ssl.trustStorePassword","87654321");
		
		try {
			new Cli_SSL("Hola mundo");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
