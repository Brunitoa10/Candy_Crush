package Score;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Guarda el ranking en una base SQLite local (data/candycrush.db): portable y sin servidor. */
public class RepositorioPuntajesSQLite implements RepositorioPuntajes {

	private static final String DRIVER = "org.sqlite.JDBC";
	private static final String CREAR_TABLA =
			"CREATE TABLE IF NOT EXISTS puntajes (id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ "nombre TEXT NOT NULL, score INTEGER NOT NULL, fecha TEXT DEFAULT CURRENT_TIMESTAMP)";
	private static final String SELECCIONAR = "SELECT nombre, score FROM puntajes ORDER BY score DESC";
	private static final String BORRAR = "DELETE FROM puntajes";
	private static final String INSERTAR = "INSERT INTO puntajes (nombre, score) VALUES (?, ?)";

	private final String url;

	public RepositorioPuntajesSQLite(File archivoBase) throws SQLException {
		try {
			Class.forName(DRIVER);
		} catch (ClassNotFoundException e) {
			throw new SQLException("Falta lib/sqlite-jdbc.jar", e);
		}
		url = "jdbc:sqlite:" + archivoBase.getAbsolutePath();
		try (Connection con = conectar(); Statement st = con.createStatement()) {
			st.execute(CREAR_TABLA);
		}
	}

	/** Indica si el driver de SQLite está en el classpath. */
	public static boolean disponible() {
		try {
			Class.forName(DRIVER);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	@Override
	public List<Jugador> cargar() {
		List<Jugador> jugadores = new ArrayList<>();
		try (Connection con = conectar();
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery(SELECCIONAR)) {
			while (rs.next()) {
				jugadores.add(new Jugador(rs.getString("nombre"), rs.getInt("score")));
			}
		} catch (SQLException e) {
			System.err.println("No se pudo leer el TOP 5: " + e.getMessage());
		}
		return jugadores;
	}

	@Override
	public void guardar(List<Jugador> jugadores) {
		try (Connection con = conectar()) {
			con.setAutoCommit(false); // todo o nada: el ranking nunca queda a medio escribir
			try (Statement st = con.createStatement(); PreparedStatement ps = con.prepareStatement(INSERTAR)) {
				st.executeUpdate(BORRAR);
				for (Jugador jugador : jugadores) {
					ps.setString(1, jugador.get_nombre());
					ps.setInt(2, jugador.get_score());
					ps.addBatch();
				}
				ps.executeBatch();
				con.commit();
			} catch (SQLException e) {
				con.rollback();
				throw e;
			}
		} catch (SQLException e) {
			System.err.println("No se pudo guardar el TOP 5: " + e.getMessage());
		}
	}

	private Connection conectar() throws SQLException {
		return DriverManager.getConnection(url);
	}
}
