package adaptador;

public class CuentaDatabase {

	String database;
	String password;
	String port;
	String server;
	String usuario;

	public CuentaDatabase() {
	}

	public String getDatabase() {
		return database;
	}

	public String getPassword() {
		return password;
	}

	public String getPort() {
		return port;
	}

	public String getServer() {
		return server;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
}
