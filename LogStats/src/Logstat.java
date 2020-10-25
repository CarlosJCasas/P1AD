import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class Logstat {
	private final static String RUTA_ACTIVIDAD = "actividad";
	private final static String RUTA_NET = "net";
	
	public static void main(String[] args) {
		
		crearEstructuraDirectorios();
		try {
			registrarAccesoUsuario();
			
			List<String> resultado = leerResultadoNetStat();
			
			String ruta_fichero ="\\netstat_"+getNumeroUltimoNetStatLog()+".log";
			File fichero = new File(RUTA_NET+ruta_fichero);
			fichero.createNewFile();
			
			List<String> lista = leerNetStatLog(ruta_fichero);
			
			int j=0;
			if (lista.size()+resultado.size()>200) {
				int i= lista.size();
				PrintWriter file = new PrintWriter(new FileWriter(RUTA_NET+ruta_fichero, true));
				file.println(getTimeStamp());
				while(i<199) {
					file.println(resultado.get(j));
					i++;
					j++;
				}
				file.close();
				ruta_fichero ="\\netstat_"+(getNumeroUltimoNetStatLog()+1)+".log";
				fichero.createNewFile();
				//Escribir el resto de la lista
				escribirRestanteNetstat(ruta_fichero, resultado, j);
			}else {
				escribirNetStatLog(ruta_fichero, resultado);
			}
		}catch (IOException e) {
			System.out.println("Excepcion IOexception");
		}
	}
		
	/**
	 * Crea la estructura de directorios en el directorio del proyecto.
	 */
	public static void crearEstructuraDirectorios() {
		File directorio = new File(RUTA_ACTIVIDAD);
		if (!directorio.exists()) {
			// Crea el directorio actividad
			directorio.mkdir();
		}
		directorio = new File(RUTA_NET);
		if (!directorio.exists()) {
			// Crea el directorio net
			directorio.mkdir();
		}
	}
	/**
	 * Obtiene la fecha y la hora del equipo.
	 * 
	 * @return Devuelve un string con la fecha y la hora del equipo.
	 */
	private static String getTimeStamp() {
		DateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		String fecha = formatoFecha.format(cal.getTime());
		return fecha;
	}
	/**
	 * Registra al usuario que ejecuta el programa y crea un archivo de logs con sus conexiones
	 * 
	 * @throws IOException 
	 */
	public static void registrarAccesoUsuario() throws IOException {
		String nombreUsuario = System.getProperty("user.name");
		File usuarioLog = new File("actividad\\" + nombreUsuario + ".log");
		if (!usuarioLog.exists()) {
			usuarioLog.createNewFile();
		}
		FileWriter printLog = new FileWriter(usuarioLog,true);
		printLog.write(Logstat.getTimeStamp()+"\n");
		
		printLog.close();
	}
	/**
	 * Ejecuta el comando netstat y lo guarda en una lista
	 * @return Una lista con toda la información de netstat
	 * @throws IOException
	 */
	public static List<String> leerResultadoNetStat() throws IOException {
		Process netstat = Runtime.getRuntime().exec("netstat");
		List<String> lista = new ArrayList<String>();
		BufferedReader read = new BufferedReader(new InputStreamReader(netstat.getInputStream())); 
		// getInputStream solo redirige el stream de .exec
		String linea;
		while ((linea = read.readLine()) != null) {
			lista.add(linea);
		}
		read.close();
		return lista;
	}
	/**
	 * Obtiene el ultimo registro de netstat
	 * @return Devuelve el número del ultimo archivo
	 */
	public static int getNumeroUltimoNetStatLog() {
		String codigo = "";
		int mayor = 1;
		File directorio = new File(RUTA_NET);
		String[] listaArchivos = directorio.list();
		if (listaArchivos!=null) {
			for (String archivo : listaArchivos) {
				// Recorre la lista y obtiene el string de 8 a length-4
				codigo = archivo.substring(8, archivo.length() - 4);
				int nums = Integer.parseInt(codigo);
				if (nums > mayor) {
					mayor = nums;
				}
			}
		}
		return mayor;
	}
	/**
	 * Escribe los resultados del netstat en un archivo
	 * @param nombreFichero El nombre del fichero donde hay que escribir la información
	 * @param netstat Una lista con los datos del netstat
	 * @throws IOException
	 */
	public static void escribirNetStatLog(String nombreFichero, List<String> netstat) throws IOException {
		PrintWriter file = new PrintWriter(new FileWriter(RUTA_NET+"\\"+nombreFichero, true));
		file.println(getTimeStamp());
		for (String linea : netstat) {
			file.println(linea);
		}
		file.close();
	}
	/**
	 * Escribe el resto de la lista de netsat en el caso de que el primer archivo esté lleno
	 * @param nombreFichero Nombre del fichero donde hay que escribir la información
	 * @param netstat Lista con los datos del Netstat
	 * @param contador Indica que linea de la lista tiene que escribir
	 * @throws IOException
	 */
	public static void escribirRestanteNetstat ( String nombreFichero, List<String> netstat, int contador) throws IOException {
		PrintWriter file = new PrintWriter(new FileWriter(RUTA_NET+"\\"+nombreFichero, true));
		int j;
		file.println(getTimeStamp());
		for (j=contador; j<netstat.size(); j++) {
			file.println(netstat.get(j));
		}
		file.close();
	}
	/**
	 * Lee los ficheros que ya existen de netstat
	 * @param nombreFichero El nombre del fichero que tiene que leer
	 * @return Devuelve una lista que contiene las lineas del archivo
	 * @throws IOException
	 */
	public static List<String> leerNetStatLog(String nombreFichero) throws IOException {
		List<String> lista = new ArrayList<String>();
		String linea;
		// Leer el fichero
		BufferedReader read = new BufferedReader(new FileReader(RUTA_NET+"\\"+nombreFichero));
		// Meter en una lista
		while ((linea = read.readLine()) != null) {
			lista.add(linea);
		}
		read.close();
		return lista;
	}
}
