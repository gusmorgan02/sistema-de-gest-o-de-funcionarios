import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;

public class App extends JFrame {

    private JTextField txtNome, txtCargo, txtCarga, txtSalario, txtVa;
    private JButton btnCadastrar, btnEditar, btnExcluir, btnLimpar;
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private int idSelecionado = -1;

    public App() {
        setTitle("Sistema de Funcionários - CRUD");
        setSize(750, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        

        setLayout(new BorderLayout(10, 10));

        JPanel painelTopo = new JPanel(new BorderLayout());


        JPanel panelForm = new JPanel(new GridLayout(5, 2, 8, 8));
        panelForm.setBorder(BorderFactory.createTitledBorder("Dados do Funcionário"));

        panelForm.add(new JLabel("Nome:"));
        txtNome = new JTextField();
        panelForm.add(txtNome);

        panelForm.add(new JLabel("Cargo:"));
        txtCargo = new JTextField();
        panelForm.add(txtCargo);

        panelForm.add(new JLabel("Carga Horária Semanal:"));
        txtCarga = new JTextField();
        panelForm.add(txtCarga);

        panelForm.add(new JLabel("Salário Base:"));
        txtSalario = new JTextField();
        panelForm.add(txtSalario);

        panelForm.add(new JLabel("Vale Alimentação:"));
        txtVa = new JTextField();
        panelForm.add(txtVa);

        painelTopo.add(panelForm, BorderLayout.CENTER);


        JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        
        btnCadastrar = new JButton("Cadastrar");
        btnEditar = new JButton("Editar");
        btnExcluir = new JButton("Excluir");
        btnLimpar = new JButton("Limpar Campos");


        btnCadastrar.setBackground(new Color(135, 206, 250)); 
        
        panelBotoes.add(btnCadastrar);
        panelBotoes.add(btnEditar);
        panelBotoes.add(btnExcluir);
        panelBotoes.add(btnLimpar);

        painelTopo.add(panelBotoes, BorderLayout.SOUTH);


        add(painelTopo, BorderLayout.NORTH);

        String[] colunas = {"ID", "Nome", "Cargo", "Carga Horária", "Salário (R$)", "VA (R$)", "Admissão"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        tabela = new JTable(modeloTabela);
        JScrollPane scrollPane = new JScrollPane(tabela);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Lista de Funcionários"));
        
        add(scrollPane, BorderLayout.CENTER);



        configurarEventos();
        garantirTabelaBanco();
        carregarDadosTabela();
    }

    private void configurarEventos() {
        tabela.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int linha = tabela.getSelectedRow();
                if (linha != -1) {
                    idSelecionado = Integer.parseInt(modeloTabela.getValueAt(linha, 0).toString());
                    txtNome.setText(modeloTabela.getValueAt(linha, 1).toString());
                    txtCargo.setText(modeloTabela.getValueAt(linha, 2).toString());
                    txtCarga.setText(modeloTabela.getValueAt(linha, 3).toString());
                    txtSalario.setText(modeloTabela.getValueAt(linha, 4).toString());
                    txtVa.setText(modeloTabela.getValueAt(linha, 5).toString());
                }
            }
        });

        btnCadastrar.addActionListener(e -> {
            try (Connection conn = FuncionarioDAO.conectar()) {
                Date dataHoje = new Date(System.currentTimeMillis());
                FuncionarioDAO.inserirFuncionaria(conn, txtNome.getText(), txtCargo.getText(), 
                        Integer.parseInt(txtCarga.getText()), 
                        Double.parseDouble(txtSalario.getText()), 
                        Double.parseDouble(txtVa.getText()), dataHoje);
                
                limparCampos();
                carregarDadosTabela();
                JOptionPane.showMessageDialog(this, "Cadastrado com sucesso!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao cadastrar. Verifique se digitou os números corretamente.");
            }
        });

        btnEditar.addActionListener(e -> {
            if (idSelecionado == -1) {
                JOptionPane.showMessageDialog(this, "Selecione um funcionário na tabela clicando nele primeiro.");
                return;
            }
            try (Connection conn = FuncionarioDAO.conectar()) {
                Date dataHoje = new Date(System.currentTimeMillis());
                FuncionarioDAO.editarFuncionario(conn, idSelecionado, txtNome.getText(), txtCargo.getText(), 
                        Integer.parseInt(txtCarga.getText()), 
                        Double.parseDouble(txtSalario.getText()), 
                        Double.parseDouble(txtVa.getText()), dataHoje);
                
                limparCampos();
                carregarDadosTabela();
                JOptionPane.showMessageDialog(this, "Atualizado com sucesso!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao editar. Verifique os dados.");
            }
        });

        btnExcluir.addActionListener(e -> {
            if (idSelecionado == -1) {
                JOptionPane.showMessageDialog(this, "Selecione um funcionário na tabela primeiro.");
                return;
            }
            int confirmacao = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirmacao == JOptionPane.YES_OPTION) {
                try (Connection conn = FuncionarioDAO.conectar()) {
                    FuncionarioDAO.excluirFuncionario(conn, idSelecionado);
                    limparCampos();
                    carregarDadosTabela();
                    JOptionPane.showMessageDialog(this, "Excluído com sucesso!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erro ao excluir.");
                }
            }
        });

        btnLimpar.addActionListener(e -> limparCampos());
    }

    private void limparCampos() {
        txtNome.setText("");
        txtCargo.setText("");
        txtCarga.setText("");
        txtSalario.setText("");
        txtVa.setText("");
        idSelecionado = -1;
        tabela.clearSelection();
    }

    private void garantirTabelaBanco() {
        try (Connection conn = FuncionarioDAO.conectar()) {
            if (conn != null) {
                FuncionarioDAO.criarTabela(conn);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("cargo"),
                    rs.getInt("cargaHorariaSemanal"),
                    rs.getDouble("salarioBase"),
                    rs.getDouble("valeAlimentacao"),
                    rs.getString("dataAdmissao")
                });
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar tabela: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new App().setVisible(true);
        });
    }
}