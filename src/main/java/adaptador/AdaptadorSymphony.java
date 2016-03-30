package adaptador;

import config.ConfiguracionException;
import main.ProcesadorCorreos;
import org.markdownj.MarkdownProcessor;

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
		} catch (ConfiguracionException e) {
			throw new AdaptadorException(e.getMessage(), e);
		}
	}

	private ResultSet ejecutarSQL(Connection unaConexion, String unaSentencia, int unParametro) throws SQLException {
		PreparedStatement pstmt = unaConexion.prepareStatement(unaSentencia);
		pstmt.setInt(1, unParametro);
		return pstmt.executeQuery();
	}

	private String getContenido(Message msg) throws IOException, MessagingException {
		return extraerTexto(msg);
	}

	private int getIdSeccion() {
		return idSeccion;
	}

	private String getSeccion() {
		return seccion;
	}

	public void grabarCorreo(Message msg) throws AdaptadorException {
		try {
			ProcesadorCorreos.getLogger().info("mensaje #  " + msg.getSubject());
			String subject = msg.getSubject();
			String body = getContenido(msg);
			insertarSymphony(subject, body);
		} catch (MessagingException | IOException e) {
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

	public void initSymphonySection() throws AdaptadorException {
		Connection conn = null;
		campos = new HashMap<>();
		try {
			conn = database.getConnection();
			//identificar numero de seccion
			PreparedStatement pstmt = null;
			try {
				pstmt = conn.prepareStatement("select id from sym_sections where handle = ?");
				pstmt.setString(1, getSeccion());
				ResultSet rs = pstmt.executeQuery();
				rs.first();
				setIdSeccion(rs.getInt("id"));
				ProcesadorCorreos.getLogger().info(String.format("sección es %s", getIdSeccion()));
			} finally {
				if (pstmt != null) {
					pstmt.close();
				}
			}
			// identificar campos
			try {
				ResultSet rs = ejecutarSQL(conn, "select id,element_name from sym_fields where parent_section = ?", getIdSeccion());
				while (rs.next()) {
					campos.put(rs.getString("element_name"), rs.getInt("id"));
				}
			} finally {
				pstmt.close();
			}
		} catch (SQLException e) {
			throw new AdaptadorException(e.getMessage(), e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					throw new AdaptadorException(e.getMessage(), e);
				}
			}
		}
	}

	private void insertarSymphony(String unTitulo, String unTexto) throws AdaptadorException {
		Connection conn = null;
		try {
			conn = database.getConnection();
			// obtener nuevo número de entry id
			ResultSet rs = ejecutarSQL(conn, "select max(id) id from sym_entries where section_id=?", getIdSeccion());
			rs.first();
			int idEntry = rs.getInt("id") + 1;
			// insert en tabla maestra de ids
			{
				String sqlSeccion = "insert into sym_entries (id, section_id, author_id, creation_date, creation_date_gmt, modification_date, modification_date_gmt) values (?, ?, ?, CURRENT_TIMESTAMP, UTC_TIMESTAMP(), CURRENT_TIMESTAMP, UTC_TIMESTAMP())";
				PreparedStatement pstmt = conn.prepareStatement(sqlSeccion);
				pstmt.setInt(1, idEntry);
				pstmt.setInt(2, getIdSeccion());
				pstmt.setInt(3, 1); // todo: configurar y validar el autor
				pstmt.execute();
			}
			// insert en tabla de subject
			{
				int idSubject = campos.get("titulo");
				String sqlSubject = String.format("insert into sym_entries_data_%s (entry_id, handle, value) values (?, ?, ?)", idSubject);
				PreparedStatement pstmt = conn.prepareStatement(sqlSubject);
				pstmt.setInt(1, idEntry);
				pstmt.setString(2, unTitulo.toLowerCase().replace(" ", "-"));
				pstmt.setString(3, unTitulo);
				pstmt.execute();
			}
			// insert en tabla de contenido
			{
				String texto = unTexto.replace(">", "").replace("<", "");
				MarkdownProcessor markup = new MarkdownProcessor();
				String markdownText = markup.markdown(texto);
				int idSubject = campos.get("texto");
				String sqlSubject = String.format("insert into sym_entries_data_%s (entry_id, value, value_formatted) values (?, ?, ?)", idSubject);
				PreparedStatement pstmt = conn.prepareStatement(sqlSubject);
				pstmt.setInt(1, idEntry);
				pstmt.setString(2, texto);
				pstmt.setString(3, markdownText);
				pstmt.execute();
			}
		} catch (SQLException e) {
			throw new AdaptadorException(e.getMessage(), e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					throw new AdaptadorException(e.getMessage(), e);
				}
			}
		}
	}

	private void setIdSeccion(int idSeccion) {
		this.idSeccion = idSeccion;
	}

	public void setSeccion(String seccion) {
		this.seccion = seccion;
	}

}
