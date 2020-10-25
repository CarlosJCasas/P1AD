import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Properties;

public class Cliente {

	public static void main(String[] args) {

		Socket cliente = null;
		PrintWriter streamSalida = null;
		BufferedReader streamEntrada = null;
		Properties props = new Properties();
		try {
			props.load(new FileReader("echo.props"));
			cliente = new Socket(props.getProperty("direccion"), Integer.parseInt(props.getProperty("puerto")));
			System.out.println("Introduce el mensaje o exit para salir");
			BufferedReader entradaTeclado = new BufferedReader(new InputStreamReader(System.in));
			streamSalida = new PrintWriter(cliente.getOutputStream(), true);
			streamEntrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()));

			String linea = null;
			//Esta opción no cierra el cliente pero si el servidor y escribe en el log
			while ((linea = entradaTeclado.readLine()) != null) {

				streamSalida.println(linea);
				System.out.println(streamEntrada.readLine());
				streamSalida.flush();
				System.out.println("Introduce un nuevo mensaje o exit para salir");

			}
			//Esta opción cierra el servidor y el cliente al poner exit pero no escribe en el log.
//			while (!(linea = entradaTeclado.readLine()).equalsIgnoreCase("exit")) {
//
//				streamSalida.println(linea);
//				System.out.println(streamEntrada.readLine());
//				streamSalida.flush();
//				System.out.println("Introduce un nuevo mensaje o exit para salir");
//
//			}

		} catch (IOException e) {

			e.printStackTrace();
		} finally {
			try {
				streamEntrada.close();
				streamSalida.close();
				cliente.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}
