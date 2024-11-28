import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class ThunderboltsGUI {
    private JPanel panelPrincipal;
    private JTabbedPane tabbedPane1;
    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JComboBox<String> comboHabilidad;
    private JComboBox<Integer> comboNivel;
    private JComboBox<String> comboMision;
    private JButton Agregar;
    private JTable tableThunderbolts;
    private JTextField txtBuscarCodigo;
    private JButton Buscar;
    private JTextField txtNombreResultado;
    private JComboBox<String> comboHabilidadResultado;
    private JComboBox<Integer> comboNivelResultado;
    private JComboBox<String> comboMisionResultado;
    private JTextArea textAreaConteo;
    private JComboBox<String> comboFiltrarHabilidad;
    private JTable tableFiltrado;
    private JButton FiltrarYOrdenar;
    private JButton CalcularConteo;
    private JButton Editar;

    private DefaultTableModel modeloTablaThunderbolts;
    private DefaultTableModel modeloTablaFiltrado;
    private ArrayList<Thunderbolt> listaThunderbolts;

    public ThunderboltsGUI() {
        listaThunderbolts = new ArrayList<>();

        // Configuración inicial de la tabla principal
        modeloTablaThunderbolts = new DefaultTableModel(new String[]{"Código", "Nombre", "Habilidad", "Nivel", "Misión"}, 0);
        tableThunderbolts.setModel(modeloTablaThunderbolts);

        // Configuración inicial de la tabla filtrada
        modeloTablaFiltrado = new DefaultTableModel(new String[]{"Código", "Nombre", "Habilidad", "Nivel", "Misión"}, 0);
        tableFiltrado.setModel(modeloTablaFiltrado);

        // Inicializar ComboBox
        inicializarComboBox(comboHabilidad, new String[]{"Combate Cuerpo a Cuerpo", "Tiro Preciso", "Tecnología Avanzada", "Sigilo", "Supervelocidad"});
        inicializarComboBox(comboHabilidadResultado, new String[]{"Combate Cuerpo a Cuerpo", "Tiro Preciso", "Tecnología Avanzada", "Sigilo", "Supervelocidad"});
        inicializarComboBox(comboFiltrarHabilidad, new String[]{"Combate Cuerpo a Cuerpo", "Tiro Preciso", "Tecnología Avanzada", "Sigilo", "Supervelocidad"});

        inicializarComboBox(comboNivel, new Integer[]{1, 2, 3, 4, 5});
        inicializarComboBox(comboNivelResultado, new Integer[]{1, 2, 3, 4, 5});

        inicializarComboBox(comboMision, new String[]{"Rescate", "Infiltración", "Defensa", "Neutralización", "Recuperación de Objetos"});
        inicializarComboBox(comboMisionResultado, new String[]{"Rescate", "Infiltración", "Defensa", "Neutralización", "Recuperación de Objetos"});

        // Lógica para agregar Thunderbolt
        Agregar.addActionListener(e -> {
            try {
                int codigo = Integer.parseInt(txtCodigo.getText().trim());
                if (buscarThunderbolt(codigo) != null) {
                    JOptionPane.showMessageDialog(panelPrincipal, "El código ya existe. No se puede registrar.");
                    return;
                }

                String nombre = txtNombre.getText().trim();
                if (nombre.isEmpty()) {
                    JOptionPane.showMessageDialog(panelPrincipal, "El nombre no puede estar vacío.");
                    return;
                }

                String habilidad = (String) comboHabilidad.getSelectedItem();
                int nivel = (int) comboNivel.getSelectedItem();
                String mision = (String) comboMision.getSelectedItem();

                Thunderbolt nuevoThunderbolt = new Thunderbolt(codigo, nombre, habilidad, nivel, mision);
                listaThunderbolts.add(0, nuevoThunderbolt);
                modeloTablaThunderbolts.insertRow(0, new Object[]{codigo, nombre, habilidad, nivel, mision});
                limpiarCampos();
                JOptionPane.showMessageDialog(panelPrincipal, "Thunderbolt agregado exitosamente.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panelPrincipal, "Ingrese un código válido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Lógica para buscar Thunderbolt
        Buscar.addActionListener(e -> {
            try {
                int codigo = Integer.parseInt(txtBuscarCodigo.getText().trim());
                Thunderbolt encontrado = buscarThunderbolt(codigo);

                if (encontrado != null) {
                    txtNombreResultado.setText(encontrado.getNombre());
                    comboHabilidadResultado.setSelectedItem(encontrado.getHabilidadPrincipal());
                    comboNivelResultado.setSelectedItem(encontrado.getNivelRedencion());
                    comboMisionResultado.setSelectedItem(encontrado.getMisionAsignada());
                } else {
                    JOptionPane.showMessageDialog(panelPrincipal, "Thunderbolt no encontrado.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panelPrincipal, "Ingrese un código válido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Lógica para editar Thunderbolt
        Editar.addActionListener(e -> {
            try {
                int codigo = Integer.parseInt(txtBuscarCodigo.getText().trim());
                Thunderbolt encontrado = buscarThunderbolt(codigo);

                if (encontrado != null) {
                    encontrado.setNombre(txtNombreResultado.getText().trim());
                    encontrado.setHabilidadPrincipal((String) comboHabilidadResultado.getSelectedItem());
                    encontrado.setNivelRedencion((int) comboNivelResultado.getSelectedItem());
                    encontrado.setMisionAsignada((String) comboMisionResultado.getSelectedItem());

                    actualizarTabla();
                    JOptionPane.showMessageDialog(panelPrincipal, "Thunderbolt editado exitosamente.");
                } else {
                    JOptionPane.showMessageDialog(panelPrincipal, "Thunderbolt no encontrado.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panelPrincipal, "Ingrese un código válido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Lógica para filtrar y ordenar por habilidad
        FiltrarYOrdenar.addActionListener(e -> {
            String habilidadSeleccionada = (String) comboFiltrarHabilidad.getSelectedItem();
            modeloTablaFiltrado.setRowCount(0);

            listaThunderbolts.stream()
                    .filter(t -> t.getHabilidadPrincipal().equals(habilidadSeleccionada))
                    .sorted(Comparator.comparing(Thunderbolt::getNivelRedencion).reversed())
                    .forEach(t -> modeloTablaFiltrado.addRow(new Object[]{t.getCodigo(), t.getNombre(), t.getHabilidadPrincipal(), t.getNivelRedencion(), t.getMisionAsignada()}));
        });

        // Lógica para contar misiones
        CalcularConteo.addActionListener(e -> {
            HashMap<String, Integer> conteo = new HashMap<>();
            for (Thunderbolt t : listaThunderbolts) {
                conteo.put(t.getMisionAsignada(), conteo.getOrDefault(t.getMisionAsignada(), 0) + 1);
            }

            StringBuilder resultado = new StringBuilder();
            for (String mision : conteo.keySet()) {
                resultado.append(mision).append(": ").append(conteo.get(mision)).append("\n");
            }

            textAreaConteo.setText(resultado.toString());
        });
    }

    private void limpiarCampos() {
        txtCodigo.setText("");
        txtNombre.setText("");
        comboHabilidad.setSelectedIndex(0);
        comboNivel.setSelectedIndex(0);
        comboMision.setSelectedIndex(0);
    }

    private void actualizarTabla() {
        modeloTablaThunderbolts.setRowCount(0);
        for (Thunderbolt t : listaThunderbolts) {
            modeloTablaThunderbolts.addRow(new Object[]{t.getCodigo(), t.getNombre(), t.getHabilidadPrincipal(), t.getNivelRedencion(), t.getMisionAsignada()});
        }
    }

    private <T> void inicializarComboBox(JComboBox<T> comboBox, T[] valores) {
        comboBox.removeAllItems();
        for (T valor : valores) {
            comboBox.addItem(valor);
        }
    }

    private Thunderbolt buscarThunderbolt(int codigo) {
        return listaThunderbolts.stream().filter(t -> t.getCodigo() == codigo).findFirst().orElse(null);
    }

    public JPanel getPanelPrincipal() {
        return panelPrincipal;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("ThunderboltsGUI");
        frame.setContentPane(new ThunderboltsGUI().getPanelPrincipal());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}