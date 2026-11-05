import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class FuncionarioDAO {
    private static final String URL = "jdbc:sqlite:C:/Temp/sistema-funcionrio.db";

    public static Connection conectar() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            System.setProperty("java.io.tmpdir", "C:\\Temp");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver do SQLite não encontrado.");
        }
        return DriverManager.getConnection(URL);
    }

    public static void criarTabela(Connection conexao) throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS funcionarios (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome TEXT NOT NULL,
                    cargo TEXT,
                    cargaHorariaSemanal INTEGER,
                    salarioBase REAL,
                    valeAlimentacao REAL,
                    dataAdmissao DATE
                );
                """;
        try (Statement stmt = conexao.createStatement()) {
            stmt.execute(sql);
        }
    }

    public static void inserirFuncionario(Connection conexao, String nome, String cargo, int carga, double salario, double va, Date data) throws SQLException {
        String sql = "INSERT INTO funcionarios(nome, cargo, cargaHorariaSemanal, salarioBase, valeAlimentacao, dataAdmissao) VALUES(?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, nome);
            pstmt.setString(2, cargo);
            pstmt.setInt(3, carga);
            pstmt.setDouble(4, salario);
            pstmt.setDouble(5, va);
            pstmt.setDate(6, data);
            pstmt.executeUpdate();
        }
    }

    public static void editarFuncionario(Connection conexao, int id, String nome, String cargo, int carga, double salario, double va, Date data) throws SQLException {
        String sql = "UPDATE funcionarios SET nome=?, cargo=?, cargaHorariaSemanal=?, salarioBase=?, valeAlimentacao=?, dataAdmissao=? WHERE id=?";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, nome);
            pstmt.setString(2, cargo);
            pstmt.setInt(3, carga);
            pstmt.setDouble(4, salario);
            pstmt.setDouble(5, va);
            pstmt.setDate(6, data);
            pstmt.setInt(7, id);
            pstmt.executeUpdate();
        }
    }

    public static void excluirFuncionario(Connection conexao, int id) throws SQLException {
        String sql = "DELETE FROM funcionarios WHERE id = ?";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}