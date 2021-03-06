package adaptador;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import config.ConfiguracionException;
import main.ProcesadorCorreos;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseMysql {

	private ComboPooledDataSource cpds;
	private CuentaDatabase cuenta;

	public DatabaseMysql() {
	}

	public void connect() throws ConfiguracionException {
		try {
			cpds = new ComboPooledDataSource();
			cpds.setDriverClass("com.mysql.jdbc.Driver");
			String connString = "jdbc:mysql://" + cuenta.getServer() + "/" + cuenta.getDatabase() ;
			ProcesadorCorreos.getLogger().info(connString);
			cpds.setJdbcUrl(connString);
			cpds.setUser(cuenta.getUsuario());
			cpds.setPassword(cuenta.getPassword());
			cpds.setAcquireRetryAttempts(2);
		} catch (PropertyVetoException e) {
			throw new ConfiguracionException(e.getMessage(), e);
		}
	}

	public Connection getConnection() throws SQLException {
		return cpds.getConnection();
	}

	@SuppressWarnings("unused")
	public CuentaDatabase getCuenta() {
		return cuenta;
	}

	public void setCuenta(CuentaDatabase cuenta) {
		this.cuenta = cuenta;
	}

}
