import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class FuncionarioDAO {

    private static final String URL = "jdbc:sqlite:C:/Temp/sistema-funcionrio.db";


    public static Connection conectar() {
        Connection conexao = null;
        try {
            Class.forName("org.sqlite.JDBC");
            System.setProperty("java.io.tmpdir", "C:\\Temp");
            
            conexao = DriverManager.getConnection(URL);
            System.out.println("Conexão com o SQLite estabelecida com sucesso!");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver do SQLite não encontrado no VS Code: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Erro ao conectar ao SQLite: " + e.getMessage());
        }
        return conexao;
    }

    public static void criarTabela(Connection conexao) {
        String sql = """
                CREATE TABLE IF NOT EXISTS funcionarios (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome TEXT NOT NULL,
                    cargo TEXT,
                    cargaHorariaSemanal INTEGER,
                    salarioBase REAL,
                    valeAlimentacao REAL,
                    dataAdmissao TEXT
                );
                """;
        try (Statement stmt = conexao.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabela 'funcionarios' pronta para uso.");
        } catch (SQLException e) {
            System.err.println("Erro ao criar tabela: " + e.getMessage());
        }
    }


    public static void inserirFuncionaria(Connection conexao, String nome, String cargo, int cargaHorariaSemanal, double salarioBase, double valeAlimentacao, Date dataAdmissao) {
        String sql = "INSERT INTO funcionarios(nome, cargo, cargaHorariaSemanal, salarioBase, valeAlimentacao, dataAdmissao) VALUES(?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, nome);
            pstmt.setString(2, cargo);
            pstmt.setInt(3, cargaHorariaSemanal);
            pstmt.setDouble(4, salarioBase);
            pstmt.setDouble(5, valeAlimentacao);
            pstmt.setDate(6, dataAdmissao); 
            pstmt.executeUpdate();
            System.out.println("Funcionária(o) inserida(o) com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao inserir funcionária(o): " + e.getMessage());
        }
    }


    public static void listarFuncionarios(Connection conexao) {
        String sql = "SELECT * FROM funcionarios";
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            System.out.println("\n--- Lista de Funcionários Cadastrados ---");
            while (rs.next()) {
                System.out.println(
                    "ID: " + rs.getInt("id") +
                    " | Nome: " + rs.getString("nome") +
                    " | Cargo: " + rs.getString("cargo") +
                    " | Carga Horária: " + rs.getInt("cargaHorariaSemanal") + "h" +
                    " | Salário: R$ " + rs.getDouble("salarioBase") +
                    " | VA: R$ " + rs.getDouble("valeAlimentacao") +
                    " | Admissão: " + rs.getString("dataAdmissao")
                );
            }
            System.out.println("-----------------------------------------\n");
        } catch (SQLException e) {
            System.err.println("Erro ao listar funcionários: " + e.getMessage());
        }
    }


    public static void editarFuncionario(Connection conexao, int id, String nome, String cargo, int cargaHorariaSemanal, double salarioBase, double valeAlimentacao, Date dataAdmissao) {
        String sql = "UPDATE funcionarios SET nome = ?, cargo = ?, cargaHorariaSemanal = ?, salarioBase = ?, valeAlimentacao = ?, dataAdmissao = ? WHERE id = ?";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, nome);
            pstmt.setString(2, cargo);
            pstmt.setInt(3, cargaHorariaSemanal);
            pstmt.setDouble(4, salarioBase);
            pstmt.setDouble(5, valeAlimentacao);
            pstmt.setDate(6, dataAdmissao); 
            pstmt.setInt(7, id);
            
            int linhasAfetadas = pstmt.executeUpdate();
            if (linhasAfetadas > 0) {
                System.out.println("Registro do funcionário atualizado com sucesso!");
            } else {
                System.out.println("Nenhum funcionário encontrado com o ID " + id);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao editar funcionário: " + e.getMessage());
        }
    }

    public static void excluirFuncionario(Connection conexao, int id) {
        String sql = "DELETE FROM funcionarios WHERE id = ?";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            int linhasAfetadas = pstmt.executeUpdate();
            if (linhasAfetadas > 0) {
                System.out.println("Funcionário excluído com sucesso!");
            } else {
                System.out.println("Nenhum funcionário encontrado com o ID " + id);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao excluir funcionário: " + e.getMessage());
        }
    }
}