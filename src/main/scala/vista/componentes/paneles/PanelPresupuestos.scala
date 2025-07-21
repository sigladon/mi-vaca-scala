package vista.componentes.paneles

import javax.swing._
import javax.swing.table.DefaultTableModel
import java.awt._
import java.awt.event.{ActionEvent, ActionListener}
import modelo.entidades.{Presupuesto, Categoria}
import scala.collection.immutable.List
import javax.swing.ImageIcon

class PanelPresupuestos extends JPanel {

  private val colorFondo = new Color(0xeff1f5)
  private val colorTexto = new Color(0x4c4f69)
  private val colorBorde = new Color(0xccd0da)
  private val fuenteSansSerif = new Font(Font.SANS_SERIF, Font.PLAIN, 11)
  private val fuenteTitulo = new Font(Font.SANS_SERIF, Font.BOLD, 12)
  
  private val btnEditarMonto = new JButton("Editar Monto")
  private val btnAgregarCategoria = new JButton("Agregar Categoría")
  private val btnEditarCategoria = new JButton("Editar Categoría")
  private val btnEliminarCategoria = new JButton("Eliminar Categoría")
  private var solicitarEditarMonto: Option[(Double) => Unit] = None
  private var solicitarAgregarCategoria: Option[(String) => Unit] = None
  private var solicitarEditarCategoria: Option[(String, String) => Unit] = None
  private var solicitarEliminarCategoria: Option[(String) => Unit] = None
  

  private val modeloTabla = new DefaultTableModel(Array[Object]("Categoría", "Transacciones"), 0) {
    override def isCellEditable(row: Int, column: Int): Boolean = false
  }
  private val tablaCategorias = new JTable(modeloTabla)
  

  private val panelSuperior = new JPanel(new BorderLayout())
  

  private var transaccionesUsuario: List[modelo.entidades.Movimiento] = List.empty
  
  initUI()
  
  private def initUI(): Unit = {
    setLayout(new BorderLayout())
    setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20))
    

    panelSuperior.setMaximumSize(new Dimension(Int.MaxValue, 40))
    panelSuperior.setPreferredSize(new Dimension(Int.MaxValue, 40))
    panelSuperior.setMinimumSize(new Dimension(200, 40))
    panelSuperior.setAlignmentX(Component.LEFT_ALIGNMENT)
    
    val lblTitulo = new JLabel("Presupuesto Anual")
    lblTitulo.setFont(new Font("Arial", Font.BOLD, 24))
    lblTitulo.setHorizontalAlignment(SwingConstants.LEFT)
    lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT)
    panelSuperior.add(lblTitulo, BorderLayout.WEST)
    

    btnEditarMonto.addActionListener(new ActionListener {
      override def actionPerformed(e: ActionEvent): Unit = {
        val input = JOptionPane.showInputDialog(
          PanelPresupuestos.this,
          "Ingresa el nuevo monto del presupuesto:",
          "Editar Monto",
          JOptionPane.QUESTION_MESSAGE
        )
        
        if (input != null && input.trim.nonEmpty) {
          try {
            val nuevoMonto = input.trim.toDouble
            if (nuevoMonto > 0) {
              solicitarEditarMonto.foreach(_(nuevoMonto))
            } else {
              JOptionPane.showMessageDialog(PanelPresupuestos.this, "El monto debe ser mayor a 0", "Error", JOptionPane.ERROR_MESSAGE)
            }
          } catch {
            case _: NumberFormatException =>
              JOptionPane.showMessageDialog(PanelPresupuestos.this, "Por favor ingresa un número válido", "Error", JOptionPane.ERROR_MESSAGE)
          }
        }
      }
    })
    
    btnAgregarCategoria.addActionListener(new ActionListener {
      override def actionPerformed(e: ActionEvent): Unit = {
        val nombreCategoria = JOptionPane.showInputDialog(
          PanelPresupuestos.this,
          "Ingresa el nombre de la nueva categoría:",
          "Agregar Categoría",
          JOptionPane.QUESTION_MESSAGE
        )
        
        if (nombreCategoria != null && nombreCategoria.trim.nonEmpty) {
          solicitarAgregarCategoria.foreach(_(nombreCategoria.trim))
        }
      }
    })
    
    btnEditarCategoria.addActionListener(new ActionListener {
      override def actionPerformed(e: ActionEvent): Unit = {
        val filaSeleccionada = tablaCategorias.getSelectedRow
        if (filaSeleccionada >= 0) {
          val nombreActual = tablaCategorias.getValueAt(filaSeleccionada, 0).toString
          val nuevoNombre = JOptionPane.showInputDialog(
            PanelPresupuestos.this,
            "Ingresa el nuevo nombre de la categoría:",
            "Editar Categoría",
            JOptionPane.QUESTION_MESSAGE,
            null,
            null,
            nombreActual
          )
          
          if (nuevoNombre != null) {
            val nuevoNombreStr = nuevoNombre.toString
            if (nuevoNombreStr.trim.nonEmpty && nuevoNombreStr.trim != nombreActual) {
              solicitarEditarCategoria.foreach(_(nombreActual, nuevoNombreStr.trim))
            }
          }
        } else {
          JOptionPane.showMessageDialog(PanelPresupuestos.this, "Por favor selecciona una categoría para editar", "Error", JOptionPane.ERROR_MESSAGE)
        }
      }
    })
    
    btnEliminarCategoria.addActionListener(new ActionListener {
      override def actionPerformed(e: ActionEvent): Unit = {
        val filaSeleccionada = tablaCategorias.getSelectedRow
        if (filaSeleccionada >= 0) {
          val nombreCategoria = tablaCategorias.getValueAt(filaSeleccionada, 0).toString
          val cantidadTransacciones = contarTransaccionesPorCategoria(nombreCategoria)
          
          if (cantidadTransacciones > 0) {
            JOptionPane.showMessageDialog(
              PanelPresupuestos.this, 
              s"No se puede eliminar la categoría '$nombreCategoria' porque tiene $cantidadTransacciones transacción(es) asociada(s).\nPrimero debe eliminar o cambiar la categoría de esas transacciones.", 
              "No se puede eliminar", 
              JOptionPane.WARNING_MESSAGE
            )
          } else {
            solicitarEliminarCategoria.foreach(_(nombreCategoria))
          }
        } else {
          JOptionPane.showMessageDialog(PanelPresupuestos.this, "Por favor selecciona una categoría para eliminar", "Error", JOptionPane.ERROR_MESSAGE)
        }
      }
    })
    

    tablaCategorias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
    tablaCategorias.getTableHeader.setReorderingAllowed(false)
    

    add(panelSuperior, BorderLayout.NORTH)
    

    mostrarMensajeVacio()
  }
  
  def setSolicitarEditarMonto(callback: (Double) => Unit): Unit = {
    solicitarEditarMonto = Some(callback)
  }
  
  def setSolicitarAgregarCategoria(callback: (String) => Unit): Unit = {
    solicitarAgregarCategoria = Some(callback)
  }
  
  def setSolicitarEditarCategoria(callback: (String, String) => Unit): Unit = {
    solicitarEditarCategoria = Some(callback)
  }
  
  def setSolicitarEliminarCategoria(callback: (String) => Unit): Unit = {
    solicitarEliminarCategoria = Some(callback)
  }
  
  def actualizarTransacciones(transacciones: List[modelo.entidades.Movimiento]): Unit = {
    transaccionesUsuario = transacciones
  }
  
  def mostrarMensajeVacio(): Unit = {

    removeAll()
    

    add(panelSuperior, BorderLayout.NORTH)
    
    val lblMensaje = new JLabel("No hay presupuesto anual configurado.")
    lblMensaje.setFont(new Font("Arial", Font.PLAIN, 16))
    lblMensaje.setHorizontalAlignment(SwingConstants.CENTER)
    
    val panelCentro = new JPanel(new BorderLayout())
    panelCentro.add(lblMensaje, BorderLayout.CENTER)
    add(panelCentro, BorderLayout.CENTER)
    
    revalidate()
    repaint()
  }
  
  def mostrarPresupuestos(presupuestos: List[Presupuesto], transacciones: List[modelo.entidades.Movimiento]): Unit = {

    transaccionesUsuario = transacciones
    

    removeAll()
    

    add(panelSuperior, BorderLayout.NORTH)
    

    val añoActual = java.time.LocalDate.now().getYear
    val presupuestoAnual = presupuestos.find(p => 
      p.inicioPresupuesto.getYear == añoActual && p.estaActivo
    )
    
    if (presupuestoAnual.isEmpty) {
      mostrarMensajeVacio()
    } else {
      val panelPresupuesto = crearPanelPresupuesto(presupuestoAnual.get)
      panelPresupuesto.setMaximumSize(new Dimension(Int.MaxValue, 120))
      panelPresupuesto.setPreferredSize(new Dimension(Int.MaxValue, 120))
      
      val panelTabla = crearPanelTablaCategorias(presupuestoAnual.get)
      

      val panelCentro = new JPanel(new BorderLayout())
      panelCentro.add(panelPresupuesto, BorderLayout.NORTH)
      panelCentro.add(panelTabla, BorderLayout.CENTER)
      
      add(panelCentro, BorderLayout.CENTER)
    }
    
    revalidate()
    repaint()
  }
  
  private def contarTransaccionesPorCategoria(nombreCategoria: String): Int = {
    transaccionesUsuario.count(_.categoria.contains(nombreCategoria))
  }
  
  private def crearPanelPresupuesto(presupuesto: Presupuesto): JPanel = {
    val panel = new JPanel(new BorderLayout())
    panel.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createLineBorder(Color.GRAY),
      BorderFactory.createEmptyBorder(8, 8, 8, 8)
    ))
    panel.setMaximumSize(new Dimension(Int.MaxValue, 80))
    panel.setPreferredSize(new Dimension(Int.MaxValue, 80))
    panel.setAlignmentX(Component.LEFT_ALIGNMENT)


    val panelInfoSuperior = new JPanel(new BorderLayout())
    val lblTitulo = new JLabel("Información del Presupuesto")
    lblTitulo.setFont(new Font("Arial", Font.BOLD, 14))
    panelInfoSuperior.add(lblTitulo, BorderLayout.WEST)
    panelInfoSuperior.add(btnEditarMonto, BorderLayout.EAST)


    val panelInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0))
    panelInfo.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0))

    val lblLimite = new JLabel(s"Límite Anual: ${presupuesto.limite}")
    lblLimite.setFont(new Font("Arial", Font.PLAIN, 12))

    val lblPeriodo = new JLabel(s"Período: ${presupuesto.inicioPresupuesto.getYear}")
    lblPeriodo.setFont(new Font("Arial", Font.ITALIC, 11))

    panelInfo.add(lblLimite)
    panelInfo.add(new JLabel(" | "))
    panelInfo.add(lblPeriodo)


    val totalGastado = transaccionesUsuario.filter { mov =>
      mov.monto < 0 &&
      !mov.fechaTransaccion.isBefore(presupuesto.inicioPresupuesto) &&
      !mov.fechaTransaccion.isAfter(presupuesto.finPresupuesto)
    }.map(_.monto).sum.abs
    val porcentaje = if (presupuesto.limite > 0) ((totalGastado / presupuesto.limite) * 100).min(100).toInt else 0

    val barraProgreso = new JProgressBar(0, 100)
    barraProgreso.setValue(porcentaje)
    barraProgreso.setStringPainted(true)
    barraProgreso.setString(f"$porcentaje%% usado ($totalGastado / ${presupuesto.limite})")
    barraProgreso.setPreferredSize(new Dimension(180, 18))
    barraProgreso.setFont(new Font("Arial", Font.PLAIN, 11))


    if (porcentaje > 90) {
      barraProgreso.setForeground(Color.RED)
    } else if (porcentaje > 75) {
      barraProgreso.setForeground(Color.ORANGE)
    } else {
      barraProgreso.setForeground(Color.GREEN)
    }

    val panelInferior = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0))
    panelInferior.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0))
    panelInferior.add(barraProgreso)

    panel.add(panelInfoSuperior, BorderLayout.NORTH)
    panel.add(panelInfo, BorderLayout.CENTER)
    panel.add(panelInferior, BorderLayout.SOUTH)

    panel
  }
  
  private def crearPanelTablaCategorias(presupuesto: Presupuesto): JPanel = {
    val panel = new JPanel(new BorderLayout())
    panel.setBorder(BorderFactory.createTitledBorder("Categorías del Presupuesto"))
    panel.setAlignmentX(Component.LEFT_ALIGNMENT)
    

    modeloTabla.setRowCount(0)
    

    presupuesto.categorias.keys.toList.sorted.foreach { categoria =>
      val cantidadTransacciones = contarTransaccionesPorCategoria(categoria)
      modeloTabla.addRow(Array[Object](categoria, cantidadTransacciones.toString))
    }
    

    tablaCategorias.setRowHeight(25)
    tablaCategorias.getColumnModel.getColumn(1).setPreferredWidth(100)
    tablaCategorias.getColumnModel.getColumn(1).setMaxWidth(100)
    
    val scrollTabla = new JScrollPane(tablaCategorias)
    

    val panelBotones = crearPanelBotones(btnAgregarCategoria, btnEditarCategoria, btnEliminarCategoria)

    panel.add(panelBotones, BorderLayout.CENTER)
    panel.add(scrollTabla, BorderLayout.SOUTH)

    panel
  }

  private def crearPanelBotones(botones: JButton*): JPanel = {
    val panel = new JPanel(new FlowLayout(FlowLayout.LEFT))
    botones.foreach(panel.add)
    panel
  }
}