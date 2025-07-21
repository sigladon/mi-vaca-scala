package vista.componentes.overlays

import javax.swing._
import java.awt._
import java.awt.event.{ActionEvent, ActionListener}
import java.util.Date
import javax.swing.SpinnerDateModel
import java.sql.Date
import java.time.LocalDate
import javax.swing.ImageIcon
import com.github.lgooddatepicker.components.DatePicker
import modelo.entidades.Movimiento

class OverlayTransaccion(
  parent: java.awt.Component,
  categoriasDisponibles: Seq[String],
  onGuardar: (Option[Int], Double, String, Option[String], String, java.time.LocalDate) => Boolean,
  transaccionEditar: Option[Movimiento] = None
) extends JDialog() {
  setModal(true)
  setTitle(if (transaccionEditar.isDefined) "Editar transacción" else "Agregar transacción")

  private val txtMonto = new JTextField(15)
  private val comboTipo = new JComboBox(Array("Ingreso", "Gasto"))
  private val txtDescripcion = new JTextField(20)
  private val comboCategoria = new JComboBox(categoriasDisponibles.toArray)
  private val txtNotas = new JTextField(20)
  private val datePicker = new com.github.lgooddatepicker.components.DatePicker()
  private val btnGuardar = new JButton(if (transaccionEditar.isDefined) "Guardar cambios" else "Agregar")
  private val btnCancelar = new JButton("Cancelar")

  // Prellenar campos si es edición
  transaccionEditar.foreach { t =>
    txtMonto.setText(Math.abs(t.monto).toString)
    comboTipo.setSelectedItem(if (t.monto < 0) "Gasto" else "Ingreso")
    txtDescripcion.setText(t.descripcion)
    comboCategoria.setSelectedItem(t.categoria.getOrElse(""))
    txtNotas.setText(t.notas)
    datePicker.setDate(t.fechaTransaccion)
  }

    setLayout(new BorderLayout())
    val panelCampos = new JPanel(new GridBagLayout())
    val gbc = new GridBagConstraints()
    gbc.insets = new Insets(5, 5, 5, 5)
    gbc.anchor = GridBagConstraints.WEST
    gbc.fill = GridBagConstraints.HORIZONTAL
    gbc.weightx = 1.0

    gbc.gridx = 0; gbc.gridy = 0
    panelCampos.add(new JLabel("Monto:"), gbc)
    gbc.gridx = 1
    panelCampos.add(txtMonto, gbc)

    gbc.gridx = 0; gbc.gridy = 1
    panelCampos.add(new JLabel("Tipo:"), gbc)
    gbc.gridx = 1
    panelCampos.add(comboTipo, gbc)

    gbc.gridx = 0; gbc.gridy = 2
    panelCampos.add(new JLabel("Descripción:"), gbc)
    gbc.gridx = 1
    panelCampos.add(txtDescripcion, gbc)

    gbc.gridx = 0; gbc.gridy = 3
    panelCampos.add(new JLabel("Categoría:"), gbc)
    gbc.gridx = 1
    panelCampos.add(comboCategoria, gbc)

    gbc.gridx = 0; gbc.gridy = 4
    panelCampos.add(new JLabel("Notas:"), gbc)
    gbc.gridx = 1
    panelCampos.add(txtNotas, gbc)

    gbc.gridx = 0; gbc.gridy = 5
    panelCampos.add(new JLabel("Fecha:"), gbc)
    gbc.gridx = 1
    datePicker.setPreferredSize(new Dimension(150, 28))
    panelCampos.add(datePicker, gbc)

    val panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT))
    panelBotones.add(btnGuardar)
    panelBotones.add(btnCancelar)

    add(panelCampos, BorderLayout.CENTER)
    add(panelBotones, BorderLayout.SOUTH)

    setMinimumSize(new Dimension(420, 320))
    setSize(420, 320)
    setLocationRelativeTo(parent)

    btnGuardar.addActionListener(new ActionListener {
      override def actionPerformed(e: ActionEvent): Unit = {
        val montoTexto = txtMonto.getText.trim
        if (montoTexto.isEmpty) {
          JOptionPane.showMessageDialog(OverlayTransaccion.this, "El monto no puede estar vacío", "Error", JOptionPane.ERROR_MESSAGE)
          return
        }
        val monto = try {
          montoTexto.toDouble
        } catch {
          case _: NumberFormatException =>
            JOptionPane.showMessageDialog(OverlayTransaccion.this, "El monto debe ser un número válido", "Error", JOptionPane.ERROR_MESSAGE)
            return
        }
        println(monto)
        if (monto <= 0) {
          JOptionPane.showMessageDialog(OverlayTransaccion.this, "El monto debe ser mayor a 0", "Error", JOptionPane.ERROR_MESSAGE)
          return
        }
        val descripcion = txtDescripcion.getText.trim
        if (descripcion.isEmpty) {
          JOptionPane.showMessageDialog(OverlayTransaccion.this, "La descripción no puede estar vacía", "Error", JOptionPane.ERROR_MESSAGE)
          return
        }
        val categoriaSeleccionada = comboCategoria.getSelectedItem
        if (categoriaSeleccionada == null || categoriaSeleccionada.toString.trim.isEmpty) {
          JOptionPane.showMessageDialog(OverlayTransaccion.this, "Debe seleccionar una categoría", "Error", JOptionPane.ERROR_MESSAGE)
          return
        }
        val tipo = comboTipo.getSelectedItem.toString
        val categoria = Option(categoriaSeleccionada.toString.trim)
        val notas = txtNotas.getText.trim
        val fecha = datePicker.getDate
        val montoFinal = if (tipo == "Gasto") -monto else monto
        val exito = onGuardar(transaccionEditar.map(_.id), montoFinal, descripcion, categoria, notas, fecha)
        if (exito) dispose()
      }
    })

    btnCancelar.addActionListener(_ => dispose())
} 