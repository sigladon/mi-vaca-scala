package vista.ventanas

import javax.swing._
import java.awt._
import java.awt.event.{FocusAdapter, FocusEvent, ActionEvent}
import javax.swing.ImageIcon
import vista.componentes.UIUtils

class LoginUI extends JPanel {

  private val colorFondo = new Color(0xeff1f5)
  private val colorTexto = new Color(0x4c4f69)
  private val colorBorde = new Color(0xccd0da)
  private val fuenteSansSerif = new Font(Font.SANS_SERIF, Font.PLAIN, 11)
  private val fuenteTitulo = new Font(Font.SANS_SERIF, Font.BOLD, 12)
  val txtCorreo = new JTextField()
  val txtContrasenia = new JPasswordField()
  private val btnIniciarSesion = new JButton("Iniciar Sesión", new ImageIcon("src/main/scala/assets/guardar.png"))
  val btnRegistrarseUI = new JButton("Registrarse", new ImageIcon("src/main/scala/assets/agregar.png"))

  private var solicitarMostrarRegistro: Option[() => Unit] = None
  private var solicitarMostrarBienvenida: Option[() => Unit] = None
  private val iconoOculto = new ImageIcon("src/main/scala/assets/oculto.png")
  private val iconoVisible = new ImageIcon("src/main/scala/assets/visible.png")
  private val btnVerOcultar = new JButton(new ImageIcon("src/main/scala/assets/ver.png"))
  private var contraseniaVisible = false
  btnVerOcultar.setFocusable(false)
  btnVerOcultar.setPreferredSize(new Dimension(28, 28))
  btnVerOcultar.setIcon(iconoOculto)
  btnVerOcultar.setToolTipText("Mostrar/Ocultar contraseña")
  UIUtils.configurarToggleContrasenia(
    btnVerOcultar,
    () => contraseniaVisible,
    v => contraseniaVisible = v,
    txtContrasenia.setEchoChar,
    btnVerOcultar.setIcon,
    iconoVisible,
    iconoOculto
  )

  initUI()
  override def addNotify(): Unit = {
    super.addNotify()
    Option(getRootPane).foreach(_.setDefaultButton(btnIniciarSesion))
  }

  private def initUI(): Unit = {
    setLayout(new GridBagLayout())
    setBackground(colorFondo)
    val constraints = UIUtils.configurarPanelPrincipal(this, colorFondo)

    val panelCentral = new JPanel()
    panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS))
    panelCentral.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30))
    panelCentral.setAlignmentX(Component.CENTER_ALIGNMENT)
    panelCentral.setAlignmentY(Component.CENTER_ALIGNMENT)
    panelCentral.setBackground(colorFondo)


    val lblTitulo = new JLabel("Iniciar Sesión")
    lblTitulo.setFont(fuenteTitulo)
    lblTitulo.setForeground(colorTexto)
    lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT)

    txtCorreo.setFont(fuenteSansSerif)
    txtContrasenia.setFont(fuenteSansSerif)

    val (panelFormulario, c) = UIUtils.crearPanelFormularioConC(colorFondo)

    c.gridx = 0; c.weightx = 0.0; c.fill = GridBagConstraints.NONE
    val lblCorreo = new JLabel("Correo/Usuario:")
    lblCorreo.setFont(fuenteSansSerif)
    lblCorreo.setForeground(colorTexto)
    panelFormulario.add(lblCorreo, c)
    c.gridx = 1; c.weightx = 1.0; c.fill = GridBagConstraints.HORIZONTAL
    txtCorreo.setPreferredSize(new Dimension(180, 28))
    panelFormulario.add(txtCorreo, c)

    c.gridx = 0; c.gridy = 1; c.weightx = 0.0; c.fill = GridBagConstraints.NONE
    val lblContrasenia = new JLabel("Contraseña:")
    lblContrasenia.setFont(fuenteSansSerif)
    lblContrasenia.setForeground(colorTexto)
    panelFormulario.add(lblContrasenia, c)
    c.gridx = 1; c.weightx = 1.0; c.fill = GridBagConstraints.HORIZONTAL
    val panelContrasenia = new JPanel(new BorderLayout())
    txtContrasenia.setFont(fuenteSansSerif)
    txtContrasenia.setForeground(colorTexto)
    txtContrasenia.setBackground(Color.WHITE)
    txtContrasenia.setBorder(BorderFactory.createLineBorder(colorBorde))
    panelContrasenia.add(txtContrasenia, BorderLayout.CENTER)
    panelContrasenia.add(btnVerOcultar, BorderLayout.EAST)
    panelContrasenia.setPreferredSize(new Dimension(180, 28))
    panelFormulario.add(panelContrasenia, c)
    txtCorreo.setColumns(12)
    txtContrasenia.setColumns(12)

    txtCorreo.setForeground(Color.GRAY)
    txtCorreo.setText("Correo o usuario")
    txtCorreo.addFocusListener(new FocusAdapter() {
      override def focusGained(e: FocusEvent): Unit = {
        if (txtCorreo.getText == "Correo o usuario") {
          txtCorreo.setText("")
          txtCorreo.setForeground(Color.BLACK)
        }
      }
      override def focusLost(e: FocusEvent): Unit = {
        if (txtCorreo.getText.isEmpty) {
          txtCorreo.setForeground(Color.GRAY)
          txtCorreo.setText("Correo o usuario")
        }
      }
    })
    txtContrasenia.setForeground(Color.GRAY)
    txtContrasenia.setText("Contraseña")
    txtContrasenia.setEchoChar(0)
    txtContrasenia.addFocusListener(new FocusAdapter() {
      override def focusGained(e: FocusEvent): Unit = {
        if (new String(txtContrasenia.getPassword) == "Contraseña") {
          txtContrasenia.setText("")
          txtContrasenia.setForeground(Color.BLACK)
          if (!contraseniaVisible) txtContrasenia.setEchoChar('\u2022')
        }
      }
      override def focusLost(e: FocusEvent): Unit = {
        if (new String(txtContrasenia.getPassword).isEmpty) {
          txtContrasenia.setForeground(Color.GRAY)
          txtContrasenia.setText("Contraseña")
          txtContrasenia.setEchoChar(0)
        }
      }
    })

    btnIniciarSesion.setFont(fuenteSansSerif)
    btnIniciarSesion.setForeground(colorTexto)
    btnIniciarSesion.setBackground(colorFondo)
    btnIniciarSesion.setBorder(BorderFactory.createLineBorder(colorBorde))
    btnRegistrarseUI.setFont(fuenteSansSerif)
    btnRegistrarseUI.setForeground(colorTexto)
    btnRegistrarseUI.setBackground(colorFondo)
    btnRegistrarseUI.setBorder(BorderFactory.createLineBorder(colorBorde))
    val panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0))
    panelBotones.setBackground(colorFondo)
    panelBotones.add(btnIniciarSesion)
    panelBotones.add(btnRegistrarseUI)

    panelCentral.add(lblTitulo)
    panelCentral.add(Box.createVerticalStrut(30))
    panelCentral.add(panelFormulario)
    panelCentral.add(Box.createVerticalStrut(20))
    panelCentral.add(panelBotones)

    add(panelCentral, constraints)
  }

  def setSolicitarMostrarRegistro(callback: () => Unit): Unit = {
    solicitarMostrarRegistro = Some(callback)
  }

  def setSolicitarMostrarBienvenida(callback: () => Unit): Unit = {
    solicitarMostrarBienvenida = Some(callback)
  }

  def emitirSolicitarMostrarRegistro(): Unit = {
    solicitarMostrarRegistro.foreach(_())
  }

  def emitirSolicitarMostrarBienvenida(): Unit = {
    solicitarMostrarBienvenida.foreach(_())
  }

  def getBtnIniciarSesion: JButton = btnIniciarSesion
  def getBtnRegistrarseUI: JButton = btnRegistrarseUI
  def getTxtCorreo: JTextField = txtCorreo
  def getTxtContrasenia: JPasswordField = txtContrasenia
}
