package vista.componentes.overlays

import com.github.lgooddatepicker.components.DatePicker
import modelo.entidades.Meta

import java.awt._
import java.awt.event.{ActionEvent, KeyAdapter, KeyEvent}
import java.time.LocalDate
import javax.swing._
import scala.util.Try
import vista.componentes.UIUtils

class OverlayMeta extends JDialog {

  private val panel = new JPanel()
  private val txtNombre = new JTextField(20)
  private val txtDescripcion = new JTextArea(3, 20)
  private val txtMonto = new JTextField(10)
  private val datePicker = new DatePicker()
  private val btnGuardar = new JButton("Guardar", new ImageIcon("src/main/scala/assets/guardar.png"))
  private val btnCancelar = new JButton("Cancelar", new ImageIcon("src/main/scala/assets/volver.png"))
  
  private var metaParaEditar: Option[Meta] = None
  private var onGuardar: Option[(String, String, Double, LocalDate) => Boolean] = None
  
  initUI()
  
  private def initUI(): Unit = {
    setTitle("Agregar Meta")
    setModal(true)
    setResizable(false)
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE)
    setMinimumSize(new Dimension(420, 320))
    setSize(420, 320)
    

    panel.setLayout(new BorderLayout())
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20))
    

    val (panelFormulario, gbc) = UIUtils.crearPanelConGbc()
    

    gbc.gridx = 0
    gbc.gridy = 0
    panelFormulario.add(new JLabel("Nombre:"), gbc)
    
    gbc.gridx = 1
    gbc.fill = GridBagConstraints.HORIZONTAL
    panelFormulario.add(txtNombre, gbc)
    

    gbc.gridx = 0
    gbc.gridy = 1
    panelFormulario.add(new JLabel("Descripción:"), gbc)
    
    gbc.gridx = 1
    txtDescripcion.setLineWrap(true)
    txtDescripcion.setWrapStyleWord(true)
    val scrollDescripcion = new JScrollPane(txtDescripcion)
    scrollDescripcion.setPreferredSize(new Dimension(200, 60))
    panelFormulario.add(scrollDescripcion, gbc)
    

    gbc.gridx = 0
    gbc.gridy = 2
    panelFormulario.add(new JLabel("Monto objetivo ($):"), gbc)
    
    gbc.gridx = 1
    panelFormulario.add(txtMonto, gbc)
    

    gbc.gridx = 0
    gbc.gridy = 3
    panelFormulario.add(new JLabel("Fecha límite (YYYY-MM-DD):"), gbc)
    
    gbc.gridx = 1
    datePicker.setPreferredSize(new Dimension(150, 28))
    panelFormulario.add(datePicker, gbc)
    

    val panelBotones = crearPanelBotones(btnGuardar, btnCancelar)
    

    panel.add(panelFormulario, BorderLayout.CENTER)
    panel.add(panelBotones, BorderLayout.SOUTH)
    

    btnGuardar.addActionListener((_: ActionEvent) => {
      guardarMeta()
    })
    
    btnCancelar.addActionListener((_: ActionEvent) => {
      dispose()
    })
    

    val enterListener = new KeyAdapter {
      override def keyPressed(e: KeyEvent): Unit = {
        if (e.getKeyCode == KeyEvent.VK_ENTER) {
          guardarMeta()
        }
      }
    }
    
    txtNombre.addKeyListener(enterListener)
    txtDescripcion.addKeyListener(enterListener)
    txtMonto.addKeyListener(enterListener)
    datePicker.addKeyListener(enterListener)
    

    val escapeListener = new KeyAdapter {
      override def keyPressed(e: KeyEvent): Unit = {
        if (e.getKeyCode == KeyEvent.VK_ESCAPE) {
          dispose()
        }
      }
    }
    
    addKeyListener(escapeListener)
    panel.addKeyListener(escapeListener)
    panelFormulario.addKeyListener(escapeListener)
    

    add(panel)
    

    panel.setFocusable(true)
  }

  private def crearPanelBotones(botones: JButton*): JPanel = {
    val panel = new JPanel(new FlowLayout(FlowLayout.RIGHT))
    botones.foreach(panel.add)
    panel
  }
  
  def configurarParaAgregar(onGuardarCallback: (String, String, Double, LocalDate) => Boolean): Unit = {
    metaParaEditar = None
    onGuardar = Some(onGuardarCallback)
    setTitle("Agregar Meta")
    limpiarCampos()
  }
  
  def configurarParaEditar(meta: Meta, onGuardarCallback: (String, String, Double, LocalDate) => Boolean): Unit = {
    metaParaEditar = Some(meta)
    onGuardar = Some(onGuardarCallback)
    setTitle("Editar Meta")
    llenarCamposConMeta(meta)
  }
  
  private def llenarCamposConMeta(meta: Meta): Unit = {
    txtNombre.setText(meta.nombre)
    txtDescripcion.setText(meta.descripcion)
    txtMonto.setText(meta.montoObjetivo.toString)
    datePicker.setDate(meta.fechaLimite)
  }
  
  private def limpiarCampos(): Unit = {
    txtNombre.setText("")
    txtDescripcion.setText("")
    txtMonto.setText("")
    datePicker.setDate(LocalDate.now())
  }
  
  private def guardarMeta(): Unit = {
    val nombre = txtNombre.getText.trim
    val descripcion = txtDescripcion.getText.trim
    val montoStr = txtMonto.getText.trim
    val fecha = datePicker.getDate
    

    if (nombre.isEmpty) {
      JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío", "Error", JOptionPane.ERROR_MESSAGE)
      txtNombre.requestFocus()
      return
    }
    
    val monto = Try(montoStr.toDouble).toOption
    if (monto.isEmpty || monto.get <= 0) {
      JOptionPane.showMessageDialog(this, "El monto debe ser un número mayor a 0", "Error", JOptionPane.ERROR_MESSAGE)
      txtMonto.requestFocus()
      return
    }
    
    if (fecha.isBefore(LocalDate.now())) {
      JOptionPane.showMessageDialog(this, "La fecha límite no puede ser anterior a hoy", "Error", JOptionPane.ERROR_MESSAGE)
      datePicker.requestFocus()
      return
    }
    

    onGuardar.foreach { callback =>
      if (callback(nombre, descripcion, monto.get, fecha)) {
        dispose()
      }
    }
  }
  
  def mostrar(): Unit = {
    pack()
    setLocationRelativeTo(null)
    setVisible(true)
  }
} 