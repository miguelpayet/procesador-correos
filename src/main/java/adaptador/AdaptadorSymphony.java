package adaptador;

import config.ConfiguracionException;
import main.ProcesadorCorreos;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class AdaptadorSymphony extends Adaptador {

	private HashMap<String, Integer> campos;
	private DatabaseMysql database;
	private int idSeccion;
	private String seccion;

	public AdaptadorSymphony() throws AdaptadorException {
		try {
			database.connect();
			initSymphonySection();
		} catch (ConfiguracionException e) {
			throw new AdaptadorException(e.getMessage(), e);
		}
	}

	private String getContenido(Message msg) throws IOException, MessagingException {
		return extraerTexto(msg);
	}

	private int getIdSeccion() {
		return idSeccion;
	}

	@SuppressWarnings("unused")
	private String getSeccion() {
		return seccion;
	}

	public void grabarCorreo(Message msg) throws AdaptadorException {
		try {
			ProcesadorCorreos.getLogger().info("mensaje #  " + msg.getSubject());
			String subject = msg.getSubject();
			String body = getContenido(msg);
			insertarSymphony(subject, body);
		} catch (MessagingException e) {
			throw new AdaptadorException(e.getMessage(), e);
		} catch (IOException e) {
			throw new AdaptadorException(e.getMessage(), e);
		}
	}

	public void init() throws AdaptadorException {
		try {
			database = ProcesadorCorreos.getConfiguracion().leerDatabase();
			database.connect();
		} catch (ConfiguracionException e) {
			throw new AdaptadorException(e.getMessage(), e);
		}
	}

	private void initSymphonySection() throws AdaptadorException {
		Connection conn;
		try {
			conn = database.getConnection();
		} catch (SQLException e) {
			throw new AdaptadorException(e.getMessage(), e);
		}
		//identificar numero de seccion
		try {
			PreparedStatement pstmt = conn.prepareStatement("select id from sym_sections where handle = ?");
			pstmt.setString(1, getSeccion());
			ResultSet rs = pstmt.executeQuery();
			rs.first();
			setIdSeccion(rs.getInt("id"));
		} catch (SQLException e) {
			throw new AdaptadorException(e.getMessage(), e);
		}
		// identificar campos
		try {
			PreparedStatement pstmt = conn.prepareStatement("select id from sym_fields where parent_section = ?");
			pstmt.setInt(1, getIdSeccion());
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				campos.put(rs.getString("element_name"), rs.getInt("id"));
			}
		} catch (SQLException e) {
			throw new AdaptadorException(e.getMessage(), e);
		}

	}


	private void insertarSymphony(String subject, String Body) throws AdaptadorException {
		try {
			Connection conn = database.getConnection();
		} catch (SQLException e) {
			throw new AdaptadorException(e.getMessage(), e);
		}
		// insert en tabla maestra de ids
		// insert en tabla de subject
		// insert en tabla de contenido
	}

	private void setIdSeccion(int idSeccion) {
		this.idSeccion = idSeccion;
	}

	@SuppressWarnings("unused")
	public void setSeccion(String seccion) {
		this.seccion = seccion;
	}

}
