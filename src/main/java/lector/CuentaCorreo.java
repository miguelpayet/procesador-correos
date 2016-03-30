package lector;

public class CuentaCorreo {

	private String direccion;
	private String password;
	private String servidor;

	public CuentaCorreo(String direc, String passw, String servi) {
		this.direccion = direc;
		this.password = passw;
		this.servidor = servi;
	}

	String getDireccion() {
		return direccion;
	}

	String getPassword() {
		return password;
	}

	String getServidor() {
		return servidor;
	}

	@SuppressWarnings("unused")
	public void info() throws LectorException {
		System.out.println("direccion: " + direccion);
		System.out.println("password: " + password);
		System.out.println("servidor: " + servidor);
	}

	@SuppressWarnings("unused")
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	@SuppressWarnings("unused")
	public void setPassword(String password) {
		this.password = password;
	}

	@SuppressWarnings("unused")
	public void setServidor(String servidor) {
		this.servidor = servidor;
	}

}
