import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

public class Servidor {
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Properties props = new Properties();
		String texto = "";

		try {
			Servidor.crearDirectorios();
			PrintWriter trafico = new PrintWriter(new FileWriter("log\\trafic.log", true));
			props.load(new FileReader("server.props"));
			// Obtiene el puerto del archivo de propiedades
			ServerSocket servidor = new ServerSocket(Integer.parseInt(props.getProperty("puerto")));
			System.out.println("Inicio servidor");
			Socket cliente = servidor.accept();
			PrintWriter flujoSalida = new PrintWriter(cliente.getOutputStream(), true);
			BufferedReader flujoEntrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
			System.out.println("Cliente conectado");
			while(!texto.equalsIgnoreCase("EXIT")){
				// Escribe la fecha, la dirección IP y el texto en trafic.log
				String ipCliente = cliente.getLocalAddress().toString();
				texto = flujoEntrada.readLine();
				trafico.println(getTimeStamp() + " " + ipCliente + " - " + texto);
				flujoSalida.println("Servidor: " + texto);
				flujoSalida.flush();
			}

			trafico.close();
			flujoEntrada.close();
			flujoSalida.close();
			cliente.close();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			System.out.println("Cliente desconectado");
		}
	}

	private static String getTimeStamp() {
		DateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		String fecha = formatoFecha.format(cal.getTime());
		return fecha;
	}

	private static void crearDirectorios() throws IOException {
		File directorio = new File("log");
		if (!directorio.exists()) {
			directorio.mkdir();
		}
		File registro = new File("log\\trafic.log");
		if (!registro.exists()) {
			registro.createNewFile();
		}
	}
}
