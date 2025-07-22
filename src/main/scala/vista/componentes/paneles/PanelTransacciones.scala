package vista.componentes.paneles

import javax.swing._
import javax.swing.table.DefaultTableModel
import java.awt._
import java.awt.event.{ActionEvent, ActionListener}
import modelo.entidades.Movimiento
import scala.collection.immutable.List
import java.time.format.DateTimeFormatter
import java.time.LocalDate
import javax.swing.ImageIcon
import vista.componentes.UIUtils

class PanelTransacciones extends JPanel {

  private val colorFondo = new Color(0xeff1f5)
  private val colorTexto = new Color(0x4c4f69)
  private val colorBorde = new Color(0xccd0da)
  private val fuenteSansSerif = new Font(Font.SANS_SERIF, Font.PLAIN, 11)
  private val fuenteTitulo = new Font(Font.SANS_SERIF, Font.BOLD, 12)
  
  private val btnAgregarTransaccion = new JButton("Agregar", new ImageIcon("src/main/scala/assets/agregar.png"))
  private val btnEditarTransaccion = new JButton("Editar", new ImageIcon("src/main/scala/assets/editar.png"))
  private val btnEliminarTransaccion = new JButton("Eliminar", new ImageIcon("src/main/scala/assets/eliminar.png"))
  private var solicitarAgregarTransaccion: Option[() => Unit] = None
  private var solicitarEditarTransaccion: Option[(Movimiento) => Unit] = None
  private var solicitarEliminarTransaccion: Option[(String) => Unit] = None
  private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
  

  private val comboCategoria = new JComboBox[String]()
  private val comboMes = new JComboBox[String]()
  private val comboTipo = new JComboBox[String](Array("Todos", "Gastos", "Ingresos"))
  private val txtBuscar = new JTextField(15)
  private val btnFiltrar = new JButton("🔍 Filtrar")
  private val btnLimpiar = new JButton("🧹 Limpiar")
  

  private val modeloTabla = new DefaultTableModel(Array[Object]("Fecha", "Descripción", "Categoría", "Monto", "Tipo", "Estado"), 0) {
    override def isCellEditable(row: Int, column: Int): Boolean = false
  }
  private val tablaTransacciones = new JTable(modeloTabla)
  

  private var transaccionesOriginales: List[Movimiento] = List.empty
  private var transaccionesMostradas: List[Movimiento] = List.empty
  
  initUI()
  
  private def initUI(): Unit = {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS))
    setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20))
    

    val panelSuperior = new JPanel(new BorderLayout())
    panelSuperior.setMaximumSize(new Dimension(Int.MaxValue, 40))
    panelSuperior.setPreferredSize(new Dimension(Int.MaxValue, 40))
    
    val lblTitulo = new JLabel("Transacciones")
    lblTitulo.setFont(new Font("Arial", Font.BOLD, 24))
    panelSuperior.add(lblTitulo, BorderLayout.WEST)
    

    val panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0))
    panelBotones.add(btnAgregarTransaccion)
    panelBotones.add(btnEditarTransaccion)
    panelBotones.add(btnEliminarTransaccion)
    panelSuperior.add(panelBotones, BorderLayout.EAST)
    

    btnAgregarTransaccion.addActionListener(new ActionListener {
      override def actionPerformed(e: ActionEvent): Unit = {
        solicitarAgregarTransaccion.foreach(_())
      }
    })
    
    btnEditarTransaccion.addActionListener(new ActionListener {
      override def actionPerformed(e: ActionEvent): Unit = {
        val filaSeleccionada = tablaTransacciones.getSelectedRow
        if (filaSeleccionada >= 0) {
          val transaccionSeleccionada = obtenerTransaccionDeFila(filaSeleccionada)
          solicitarEditarTransaccion.foreach(_(transaccionSeleccionada))
        } else {
          JOptionPane.showMessageDialog(PanelTransacciones.this, "Por favor selecciona una transacción para editar", "Sin selección", JOptionPane.WARNING_MESSAGE)
        }
      }
    })
    
    btnEliminarTransaccion.addActionListener(new ActionListener {
      override def actionPerformed(e: ActionEvent): Unit = {
        val filaSeleccionada = tablaTransacciones.getSelectedRow
        if (filaSeleccionada >= 0) {
          val transaccionSeleccionada = obtenerTransaccionDeFila(filaSeleccionada)
          val respuesta = JOptionPane.showConfirmDialog(
            PanelTransacciones.this,
            s"¿Estás seguro de que quieres eliminar la transacción '${transaccionSeleccionada.descripcion}'?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
          )
          if (respuesta == JOptionPane.YES_OPTION) {
            solicitarEliminarTransaccion.foreach(_(transaccionSeleccionada.id.toString))
          }
        } else {
          JOptionPane.showMessageDialog(PanelTransacciones.this, "Por favor selecciona una transacción para eliminar", "Sin selección", JOptionPane.WARNING_MESSAGE)
        }
      }
    })
    

    val panelFiltros = crearPanelFiltros()
    panelFiltros.setMaximumSize(new Dimension(Int.MaxValue, 80))
    panelFiltros.setPreferredSize(new Dimension(Int.MaxValue, 80))
    

    tablaTransacciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
    tablaTransacciones.getTableHeader.setReorderingAllowed(false)
    tablaTransacciones.setRowHeight(25)
    

    tablaTransacciones.getColumnModel.getColumn(0).setPreferredWidth(80)
    tablaTransacciones.getColumnModel.getColumn(1).setPreferredWidth(200)
    tablaTransacciones.getColumnModel.getColumn(2).setPreferredWidth(120)
    tablaTransacciones.getColumnModel.getColumn(3).setPreferredWidth(100)
    tablaTransacciones.getColumnModel.getColumn(4).setPreferredWidth(80)
    tablaTransacciones.getColumnModel.getColumn(5).setPreferredWidth(80)
    
    val scrollTabla = new JScrollPane(tablaTransacciones)
    scrollTabla.setPreferredSize(new Dimension(800, 400))
    

    add(panelSuperior)
    add(Box.createVerticalStrut(10))
    add(panelFiltros)
    add(Box.createVerticalStrut(10))
    add(scrollTabla)
    

    mostrarMensajeVacio()
  }
  
  private def crearPanelFiltros(): JPanel = {
    val panel = new JPanel(new BorderLayout())
    panel.setBorder(BorderFactory.createTitledBorder("Filtros"))


    val (panelFiltrosPrincipales, gbc) = UIUtils.crearPanelConGbc()
    gbc.gridx = 0; gbc.gridy = 0
    panelFiltrosPrincipales.add(new JLabel("Categoría:"), gbc)
    gbc.gridx = 1
    panelFiltrosPrincipales.add(comboCategoria, gbc)

    gbc.gridx = 2
    panelFiltrosPrincipales.add(new JLabel("Mes:"), gbc)
    gbc.gridx = 3
    panelFiltrosPrincipales.add(comboMes, gbc)

    gbc.gridx = 4
    panelFiltrosPrincipales.add(new JLabel("Tipo:"), gbc)
    gbc.gridx = 5
    panelFiltrosPrincipales.add(comboTipo, gbc)

    gbc.gridx = 6; gbc.gridy = 0; gbc.weightx = 0.0
    panelFiltrosPrincipales.add(new JLabel("Buscar:"), gbc)
    gbc.gridx = 7; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL
    panelFiltrosPrincipales.add(txtBuscar, gbc)
    gbc.weightx = 0.0; gbc.fill = GridBagConstraints.HORIZONTAL


    val panelBotones = crearPanelBotones(btnFiltrar, btnLimpiar)

    panel.add(panelFiltrosPrincipales, BorderLayout.CENTER)
    panel.add(panelBotones, BorderLayout.SOUTH)

    panel
  }
  
  private def crearGbc(): GridBagConstraints = {
    val gbc = new GridBagConstraints()
    gbc.insets = new Insets(5, 5, 5, 5)
    gbc.anchor = GridBagConstraints.WEST
    gbc
  }

  private def crearPanelBotones(botones: JButton*): JPanel = {
    val panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2))
    botones.foreach(panel.add)
    panel
  }
  
  private def aplicarFiltros(): Unit = {
    val categoriaSeleccionada = Option(comboCategoria.getSelectedItem).map(_.toString)
    val mesSeleccionado = Option(comboMes.getSelectedItem).map(_.toString)
    val tipoSeleccionado = Option(comboTipo.getSelectedItem).map(_.toString)
    val textoBuscar = txtBuscar.getText.trim.toLowerCase
    
    val transaccionesFiltradas = transaccionesOriginales.filter { transaccion =>

      val cumpleCategoria = categoriaSeleccionada.isEmpty || 
                           categoriaSeleccionada.contains("Todas") ||
                           transaccion.categoria.contains(categoriaSeleccionada.get)
      

      val cumpleMes = mesSeleccionado.isEmpty || 
                     mesSeleccionado.contains("Todos") ||
                     transaccion.fechaTransaccion.getMonth.toString == mesSeleccionado.get
      

      val cumpleTipo = tipoSeleccionado.isEmpty || 
                      tipoSeleccionado.contains("Todos") ||
                      (tipoSeleccionado.contains("Gastos") && transaccion.monto < 0) ||
                      (tipoSeleccionado.contains("Ingresos") && transaccion.monto > 0)
      

      val cumpleTexto = textoBuscar.isEmpty || 
                       transaccion.descripcion.toLowerCase.contains(textoBuscar) ||
                       transaccion.categoria.map(_.toLowerCase).getOrElse("").contains(textoBuscar)
      
      cumpleCategoria && cumpleMes && cumpleTipo && cumpleTexto
    }
    
    mostrarTransaccionesEnTabla(transaccionesFiltradas)
  }
  
  private def limpiarFiltros(): Unit = {
    comboCategoria.setSelectedIndex(0)
    comboMes.setSelectedIndex(0)
    comboTipo.setSelectedIndex(0)
    txtBuscar.setText("")
    mostrarTransaccionesEnTabla(transaccionesOriginales)
  }
  
  private def mostrarTransaccionesEnTabla(transacciones: List[Movimiento]): Unit = {
    modeloTabla.setRowCount(0)
    
    val transaccionesOrdenadas = transacciones.sortBy(_.fechaTransaccion)(Ordering[java.time.LocalDate].reverse)
    transaccionesMostradas = transaccionesOrdenadas
    
    transaccionesOrdenadas.foreach { transaccion =>
      val fecha = transaccion.fechaTransaccion.format(dateFormatter)
      val descripcion = transaccion.descripcion
      val categoria = transaccion.categoria.getOrElse("Sin categoría")
      val monto = f"$$${Math.abs(transaccion.monto)}"
      val tipo = if (transaccion.monto > 0) "Ingreso" else "Gasto"
      val estado = transaccion.estado
      
      modeloTabla.addRow(Array[Object](fecha, descripcion, categoria, monto, tipo, estado))
    }
  }
  
  private def obtenerTransaccionDeFila(fila: Int): Movimiento = {
    transaccionesMostradas(fila)
  }
  
  def setSolicitarAgregarTransaccion(callback: () => Unit): Unit = {
    solicitarAgregarTransaccion = Some(callback)
  }
  
  def setSolicitarEditarTransaccion(callback: (Movimiento) => Unit): Unit = {
    solicitarEditarTransaccion = Some(callback)
  }
  
  def setSolicitarEliminarTransaccion(callback: (String) => Unit): Unit = {
    solicitarEliminarTransaccion = Some(callback)
  }
  
  def mostrarMensajeVacio(): Unit = {
    modeloTabla.setRowCount(0)
    transaccionesOriginales = List.empty
    actualizarFiltros()
  }
  
  def mostrarTransacciones(transacciones: List[Movimiento]): Unit = {
    transaccionesOriginales = transacciones
    actualizarFiltros()
    mostrarTransaccionesEnTabla(transacciones)
  }
  
  private def actualizarFiltros(): Unit = {

    val categorias = ("Todas" :: transaccionesOriginales.flatMap(_.categoria).distinct.sorted).toArray
    comboCategoria.setModel(new DefaultComboBoxModel[String](categorias))
    

    val meses = ("Todos" :: transaccionesOriginales.map(_.fechaTransaccion.getMonth.toString).distinct.sorted).toArray
    comboMes.setModel(new DefaultComboBoxModel[String](meses))
  }

  btnFiltrar.addActionListener(_ => aplicarFiltros())
  btnLimpiar.addActionListener(_ => limpiarFiltros())
} 