import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class App extends JFrame {
    private JTable tabela;
    private DefaultTableModel modeloTabela;

    public App() {
        setTitle("Sistema de Funcionários - Listagem");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        String[] colunas = {"ID", "Nome", "Cargo", "Carga Horária", "Salário (R$)", "VA (R$)", "Admissão"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabela = new JTable(modeloTabela);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton btnCadastrar = new JButton("Cadastrar Novo");
        JButton btnEditar = new JButton("Editar Selecionado");
        JButton btnExcluir = new JButton("Excluir Selecionado");

        panelBotoes.add(btnCadastrar);
        panelBotoes.add(btnEditar);
        panelBotoes.add(btnExcluir);
        add(panelBotoes, BorderLayout.SOUTH);

        btnCadastrar.addActionListener(e -> {
            new TelaCadastro(this, -1, "", "", "", "", "", "").setVisible(true);
            carregarDadosTabela(); 
        });

        btnEditar.addActionListener(e -> {
            int linha = tabela.getSelectedRow();
            if (linha == -1) {
                JOptionPane.showMessageDialog(this, "Selecione um funcionário na tabela primeiro.");
                return;
            }
            
            int id = Integer.parseInt(modeloTabela.getValueAt(linha, 0).toString());
            String nome = String.valueOf(modeloTabela.getValueAt(linha, 1));
            String cargo = String.valueOf(modeloTabela.getValueAt(linha, 2));
            String carga = String.valueOf(modeloTabela.getValueAt(linha, 3));
            String salario = String.valueOf(modeloTabela.getValueAt(linha, 4));
            String va = String.valueOf(modeloTabela.getValueAt(linha, 5));
            
            Object objAdmissao = modeloTabela.getValueAt(linha, 6);
            String admissao = (objAdmissao != null) ? objAdmissao.toString() : "";

            new TelaCadastro(this, id, nome, cargo, carga, salario, va, admissao).setVisible(true);
            carregarDadosTabela();
        });

        btnExcluir.addActionListener(e -> {
            int linha = tabela.getSelectedRow();
            if (linha == -1) {
                JOptionPane.showMessageDialog(this, "Selecione um funcionário na tabela primeiro.");
                return;
            }
            int id = Integer.parseInt(modeloTabela.getValueAt(linha, 0).toString());
            int confirmacao = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirmacao == JOptionPane.YES_OPTION) {
                try (Connection conn = FuncionarioDAO.conectar()) {
                    FuncionarioDAO.excluirFuncionario(conn, id);
                    carregarDadosTabela();
                    JOptionPane.showMessageDialog(this, "Excluído com sucesso!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erro ao excluir: " + ex.getMessage());
                }
            }
        });

        inicializarBanco();
        carregarDadosTabela();
    }

    private void inicializarBanco() {
        try (Connection conn = FuncionarioDAO.conectar()) {
            FuncionarioDAO.criarTabela(conn);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao criar banco: " + e.getMessage());
        }
    }

    private void carregarDadosTabela() {
        modeloTabela.setRowCount(0);
        String sql = "SELECT * FROM funcionarios";
        try (Connection conn = FuncionarioDAO.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                modeloTabela.addRow(new Object[]{
                    rs.getInt("id"), rs.getString("nome"), rs.getString("cargo"),
                    rs.getInt("cargaHorariaSemanal"), rs.getDouble("salarioBase"),
                    rs.getDouble("valeAlimentacao"), rs.getDate("dataAdmissao")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar tabela.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App().setVisible(true));
    }
}


class TelaCadastro extends JDialog {
    private JTextField txtNome, txtCargo, txtCarga, txtSalario, txtVa, txtAdmissao;
    private int idFuncionario; 

    public TelaCadastro(JFrame parent, int id, String nome, String cargo, String carga, String salario, String va, String admissao) {
        super(parent, id == -1 ? "Cadastrar Funcionário" : "Editar Funcionário", true);
        this.idFuncionario = id;
        setSize(400, 350);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel panelForm = new JPanel(new GridLayout(6, 2, 8, 8));
        panelForm.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelForm.add(new JLabel("Nome:"));
        txtNome = new JTextField(nome);
        panelForm.add(txtNome);

        panelForm.add(new JLabel("Cargo:"));
        txtCargo = new JTextField(cargo);
        panelForm.add(txtCargo);

        panelForm.add(new JLabel("Carga Horária (h):"));
        txtCarga = new JTextField(carga);
        panelForm.add(txtCarga);

        panelForm.add(new JLabel("Salário Base (ex: 2500.50):"));
        txtSalario = new JTextField(salario);
        panelForm.add(txtSalario);

        panelForm.add(new JLabel("Vale Alimentação (ex: 400.00):"));
        txtVa = new JTextField(va);
        panelForm.add(txtVa);

        panelForm.add(new JLabel("Admissão (AAAA-MM-DD):"));
        txtAdmissao = new JTextField(admissao);
        panelForm.add(txtAdmissao);

        add(panelForm, BorderLayout.CENTER);

        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.addActionListener(e -> salvarDados());
        
        JPanel panelBotao = new JPanel();
        panelBotao.add(btnSalvar);
        add(panelBotao, BorderLayout.SOUTH);
    }

    private void salvarDados() {
        if (txtNome.getText().trim().isEmpty() || txtCargo.getText().trim().isEmpty() ||
            txtCarga.getText().trim().isEmpty() || txtSalario.getText().trim().isEmpty() ||
            txtVa.getText().trim().isEmpty() || txtAdmissao.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos.");
            return;
        }

        try {
            String nome = txtNome.getText().trim();
            String cargo = txtCargo.getText().trim();
            int cargaHoraria = Integer.parseInt(txtCarga.getText().trim());
            double salario = Double.parseDouble(txtSalario.getText().trim().replace(",", "."));
            double va = Double.parseDouble(txtVa.getText().trim().replace(",", "."));
            Date dataAdmissao = Date.valueOf(txtAdmissao.getText().trim()); 

            try (Connection conn = FuncionarioDAO.conectar()) {
                if (idFuncionario == -1) {
                    FuncionarioDAO.inserirFuncionario(conn, nome, cargo, cargaHoraria, salario, va, dataAdmissao);
                    JOptionPane.showMessageDialog(this, "Cadastrado com sucesso!");
                } else {
                    FuncionarioDAO.editarFuncionario(conn, idFuncionario, nome, cargo, cargaHoraria, salario, va, dataAdmissao);
                    JOptionPane.showMessageDialog(this, "Atualizado com sucesso!");
                }
                dispose(); 
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Erro: Carga Horária tem de ser número inteiro. Salário e VA em decimais.");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Erro: A data deve estar no formato AAAA-MM-DD (ex: 2026-10-25).");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro no banco de dados: " + ex.getMessage());
        }
    }
}