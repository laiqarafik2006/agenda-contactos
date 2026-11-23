package agenda;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.IOException;
import java.util.Locale;

public class PainelAgenda extends JPanel {

    private static final Color COR_ERRO = new Color(0xB00020);
    private static final Color COR_OK = new Color(0x1B5E20);
    private static final Locale PT = Locale.forLanguageTag("pt-PT");

    private final Agenda agenda;
    private final JComboBox<String> cbModo =
            new JComboBox<>(new String[] {"Nome (exacto)", "Início do nome", "Telefone"});
    private final JTextField tfPesquisa = new JTextField(18);
    private final DefaultTableModel modelo =
            new DefaultTableModel(new String[] {"Nome", "Telefone", "Email"}, 0) {
                @Override public boolean isCellEditable(int l, int c) { return false; }
            };
    private final JTable tabela = new JTable(modelo);
    private final JTextField tfNome = new JTextField(16);
    private final JTextField tfTelefone = new JTextField(11);
    private final JTextField tfEmail = new JTextField(16);

    private Contacto[] mostrados = new Contacto[0];
    private String nomeSelecionado = null;

    // --- separador Tabela Hash ---
    private final JLabel vCapInicial = new JLabel();
    private final JLabel vCapAtual = new JLabel();
    private final JLabel vContactos = new JLabel();
    private final JLabel vOcupadas = new JLabel();
    private final JLabel vColisoes = new JLabel();
    private final JLabel vMaiorLista = new JLabel();
    private final JLabel vRedimens = new JLabel();
    private final JProgressBar barraCarga = new JProgressBar(0, 100);
    private final JCheckBox chkOcupadas = new JCheckBox("Mostrar só as posições ocupadas", true);
    private final JTextArea areaTabela = new JTextArea();

    private final JTabbedPane abas = new JTabbedPane();
    private final JLabel lblEstado = new JLabel(" ");

    public PainelAgenda(Agenda agenda) {
        this.agenda = agenda;
        setLayout(new BorderLayout());

        abas.addTab("Contactos", criarAbaContactos());
        abas.addTab("Tabela Hash", criarAbaHash());
        abas.addChangeListener(e -> atualizarHash());
        add(abas, BorderLayout.CENTER);

        lblEstado.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        add(lblEstado, BorderLayout.SOUTH);

        mostrarTodos();
        atualizarHash();
    }

    public JTabbedPane getAbas() { return abas; }
    public JTable getTabela() { return tabela; }

    private JPanel criarAbaContactos() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel pesquisa = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pesquisa.add(new JLabel("Pesquisar por:"));
        pesquisa.add(cbModo);
        pesquisa.add(tfPesquisa);
        JButton bPesq = new JButton("Pesquisar");
        JButton bTodos = new JButton("Mostrar todos");
        pesquisa.add(bPesq);
        pesquisa.add(bTodos);
        bPesq.addActionListener(e -> pesquisar());
        tfPesquisa.addActionListener(e -> pesquisar());
        bTodos.addActionListener(e -> { tfPesquisa.setText(""); mostrarTodos(); });
        p.add(pesquisa, BorderLayout.NORTH);

        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setRowHeight(26);
        tabela.getColumnModel().getColumn(0).setPreferredWidth(280);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(140);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(280);
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int linha = tabela.getSelectedRow();
            if (linha >= 0 && linha < mostrados.length) {
                Contacto c = mostrados[linha];
                nomeSelecionado = c.getNome();
                tfNome.setText(c.getNome());
                tfTelefone.setText(c.getTelefone());
                tfEmail.setText(c.getEmail());
            } else {
                nomeSelecionado = null;
            }
        });
        p.add(new JScrollPane(tabela), BorderLayout.CENTER);

        // --- formulario ---
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Dados do contacto"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 6, 4, 6);
        g.anchor = GridBagConstraints.WEST;

        g.gridy = 0;
        g.gridx = 0; form.add(new JLabel("Nome:"), g);
        g.gridx = 1; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL; form.add(tfNome, g);
        g.gridx = 2; g.weightx = 0; g.fill = GridBagConstraints.NONE; form.add(new JLabel("Telefone:"), g);
        g.gridx = 3; form.add(tfTelefone, g);
        g.gridx = 4; form.add(new JLabel("Email:"), g);
        g.gridx = 5; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL; form.add(tfEmail, g);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JButton bAdd = new JButton("Adicionar");
        JButton bEdit = new JButton("Editar seleccionado");
        JButton bRem = new JButton("Remover seleccionado");
        JButton bLimpar = new JButton("Limpar");
        botoes.add(bAdd);
        botoes.add(bEdit);
        botoes.add(bRem);
        botoes.add(bLimpar);
        bAdd.addActionListener(e -> adicionar());
        bEdit.addActionListener(e -> editar());
        bRem.addActionListener(e -> remover());
        bLimpar.addActionListener(e -> limpar());
        g.gridx = 0; g.gridy = 1; g.gridwidth = 6; g.weightx = 0; g.fill = GridBagConstraints.NONE;
        form.add(botoes, g);

        p.add(form, BorderLayout.SOUTH);
        return p;
    }

    private JPanel criarAbaHash() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topo = new JPanel(new BorderLayout(8, 8));
        JPanel stats = new JPanel(new GridLayout(0, 4, 12, 6));
        stats.setBorder(BorderFactory.createTitledBorder("Estatísticas da Tabela Hash"));
        adicionarPar(stats, "Capacidade inicial:", vCapInicial);
        adicionarPar(stats, "Capacidade actual:", vCapAtual);
        adicionarPar(stats, "Contactos armazenados:", vContactos);
        adicionarPar(stats, "Posições ocupadas:", vOcupadas);
        adicionarPar(stats, "Colisões:", vColisoes);
        adicionarPar(stats, "Maior lista:", vMaiorLista);
        adicionarPar(stats, "Redimensionamentos:", vRedimens);
        topo.add(stats, BorderLayout.CENTER);

        barraCarga.setStringPainted(true);
        JPanel carga = new JPanel(new BorderLayout(8, 0));
        carga.setBorder(BorderFactory.createTitledBorder("Factor de carga (n / m)"));
        carga.add(barraCarga, BorderLayout.CENTER);
        topo.add(carga, BorderLayout.SOUTH);
        p.add(topo, BorderLayout.NORTH);

        areaTabela.setEditable(false);
        areaTabela.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        JPanel meio = new JPanel(new BorderLayout(4, 4));
        meio.setBorder(BorderFactory.createTitledBorder("Tabela por dentro: posição → lista ligada"));
        JPanel opcoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JButton bAtualizar = new JButton("Actualizar");
        bAtualizar.addActionListener(e -> atualizarHash());
        chkOcupadas.addActionListener(e -> atualizarHash());
        opcoes.add(chkOcupadas);
        opcoes.add(bAtualizar);
        meio.add(opcoes, BorderLayout.NORTH);
        meio.add(new JScrollPane(areaTabela), BorderLayout.CENTER);
        p.add(meio, BorderLayout.CENTER);
        return p;
    }

    private static void adicionarPar(JPanel p, String titulo, JLabel valor) {
        p.add(new JLabel(titulo));
        valor.setFont(valor.getFont().deriveFont(Font.BOLD));
        p.add(valor);
    }

    private void pesquisar() {
        String texto = tfPesquisa.getText().trim();
        if (texto.isEmpty()) {
            mostrarTodos();
            return;
        }
        Contacto[] v;
        switch (cbModo.getSelectedIndex()) {
            case 0: {
                Contacto c = agenda.pesquisar(texto);
                v = c == null ? new Contacto[0] : new Contacto[] {c};
                break;
            }
            case 1:
                v = agenda.pesquisarPorPrefixo(texto);
                break;
            default: {
                Contacto c = agenda.pesquisarPorTelefone(texto);
                v = c == null ? new Contacto[0] : new Contacto[] {c};
            }
        }
        mostrar(v);
        if (v.length == 0) setEstado("Nenhum contacto encontrado.", true);
        else setEstado(v.length + " contacto(s) encontrado(s).", false);
    }

    private void adicionar() {
        String erro = agenda.adicionar(tfNome.getText(), tfTelefone.getText(), tfEmail.getText());
        if (erro != null) {
            avisar(erro);
            return;
        }
        String nome = tfNome.getText().trim();
        gravar();
        tfPesquisa.setText("");
        mostrarTodos();
        selecionarPorNome(nome);
        atualizarHash();
        setEstado("Contacto adicionado: " + nome + ".", false);
    }

    private void editar() {
        if (nomeSelecionado == null) {
            avisar("Seleccione primeiro um contacto na tabela.");
            return;
        }
        String erro = agenda.editarTudo(nomeSelecionado, tfNome.getText(), tfTelefone.getText(), tfEmail.getText());
        if (erro != null) {
            avisar(erro);
            return;
        }
        String nome = tfNome.getText().trim();
        gravar();
        tfPesquisa.setText("");
        mostrarTodos();
        selecionarPorNome(nome);
        atualizarHash();
        setEstado("Contacto actualizado: " + nome + ".", false);
    }

    private void remover() {
        if (nomeSelecionado == null) {
            avisar("Seleccione primeiro um contacto na tabela.");
            return;
        }
        int r = JOptionPane.showConfirmDialog(this,
                "Remover o contacto '" + nomeSelecionado + "'?", "Confirmar remoção",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (r != JOptionPane.YES_OPTION) return;
        String nome = nomeSelecionado;
        agenda.remover(nome);
        gravar();
        limpar();
        tfPesquisa.setText("");
        mostrarTodos();
        atualizarHash();
        setEstado("Contacto removido: " + nome + ".", false);
    }

    private void limpar() {
        tabela.clearSelection();
        nomeSelecionado = null;
        tfNome.setText("");
        tfTelefone.setText("");
        tfEmail.setText("");
    }

    private void mostrarTodos() {
        Contacto[] v = agenda.listarOrdenado();
        mostrar(v);
        setEstado(v.length + " contacto(s) na agenda.", false);
    }

    private void mostrar(Contacto[] v) {
        mostrados = v;
        modelo.setRowCount(0);
        for (int i = 0; i < v.length; i++) {
            modelo.addRow(new Object[] {v[i].getNome(), v[i].getTelefone(),
                    v[i].getEmail().isEmpty() ? "-" : v[i].getEmail()});
        }
    }

    private void selecionarPorNome(String nome) {
        String chave = Contacto.normalizar(nome);
        for (int i = 0; i < mostrados.length; i++) {
            if (mostrados[i].getChave().equals(chave)) {
                tabela.setRowSelectionInterval(i, i);
                tabela.scrollRectToVisible(tabela.getCellRect(i, 0, true));
                return;
            }
        }
    }

    private void atualizarHash() {
        TabelaHash t = agenda.getTabela();
        vCapInicial.setText(String.valueOf(t.capacidadeInicial()));
        vCapAtual.setText(String.valueOf(t.capacidade()));
        vContactos.setText(String.valueOf(t.tamanho()));
        vOcupadas.setText(String.valueOf(t.bucketsOcupados()));
        vColisoes.setText(String.valueOf(t.colisoes()));
        vMaiorLista.setText(String.valueOf(t.maiorBucket()));
        vRedimens.setText(String.valueOf(t.redimensionamentos()));
        double alfa = t.fatorCarga();
        barraCarga.setValue((int) Math.round(alfa * 100));
        barraCarga.setString(String.format(PT, "%.2f  (limite 0,75: acima disto a tabela cresce)", alfa));
        areaTabela.setText(t.descrever(chkOcupadas.isSelected()));
        areaTabela.setCaretPosition(0);
    }

    private void gravar() {
        try {
            agenda.guardar();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Não foi possível gravar o ficheiro:\n" + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void avisar(String mensagem) {
        setEstado(mensagem, true);
        JOptionPane.showMessageDialog(this, mensagem, "Atenção", JOptionPane.WARNING_MESSAGE);
    }

    public void setEstado(String mensagem, boolean erro) {
        lblEstado.setForeground(erro ? COR_ERRO : COR_OK);
        lblEstado.setText(mensagem);
    }
}
