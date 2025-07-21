package vista.ventanas

import javax.swing._
import java.awt._
import java.awt.event.{FocusAdapter, FocusEvent, ActionEvent}
import javax.swing.ImageIcon
import vista.componentes.UIUtils

class RegistrarseUI extends JPanel {


  private val colorFondo = new Color(0xeff1f5)
  private val colorTexto = new Color(0x4c4f69)
  private val colorBorde = new Color(0xccd0da)
  private val fuenteSansSerif = new Font(Font.SANS_SERIF, Font.PLAIN, 11)
  private val fuenteTitulo = new Font(Font.SANS_SERIF, Font.BOLD, 12)

  val txtNombre = new JTextField()
  val txtUsername = new JTextField()
  val txtCorreo = new JTextField()
  val txtContrasenia = new JPasswordField()
  val txtRepetirContrasenia = new JPasswordField()
  
  val btnRegistrarseUI = new JButton("Registrarse", new ImageIcon("src/main/scala/assets/guardar.png"))
  val btnVolver = new JButton("Volver", new ImageIcon("src/main/scala/assets/volver.png"))

  private val iconoOculto = new ImageIcon("src/main/scala/assets/oculto.png")
  private val iconoVisible = new ImageIcon("src/main/scala/assets/visible.png")
  private val btnVerOcultar1 = new JButton(new ImageIcon("src/main/scala/assets/ver.png"))
  private val btnVerOcultar2 = new JButton(new ImageIcon("src/main/scala/assets/ver.png"))
  private var contraseniaVisible1 = false
  private var contraseniaVisible2 = false
  btnVerOcultar1.setFocusable(false)
  btnVerOcultar1.setPreferredSize(new Dimension(28, 28))
  btnVerOcultar1.setIcon(iconoOculto)
  btnVerOcultar1.setToolTipText("Mostrar/Ocultar contraseña")
  UIUtils.configurarToggleContrasenia(
    btnVerOcultar1,
    () => contraseniaVisible1,
    v => contraseniaVisible1 = v,
    txtContrasenia.setEchoChar,
    btnVerOcultar1.setIcon,
    iconoVisible,
    iconoOculto
  )
  btnVerOcultar2.setFocusable(false)
  btnVerOcultar2.setPreferredSize(new Dimension(28, 28))
  btnVerOcultar2.setIcon(iconoOculto)
  btnVerOcultar2.setToolTipText("Mostrar/Ocultar contraseña")
  UIUtils.configurarToggleContrasenia(
    btnVerOcultar2,
    () => contraseniaVisible2,
    v => contraseniaVisible2 = v,
    txtRepetirContrasenia.setEchoChar,
    btnVerOcultar2.setIcon,
    iconoVisible,
    iconoOculto
  )

  private var solicitarMostrarLogin: Option[() => Unit] = None

  initUI()
  override def addNotify(): Unit = {
    super.addNotify()
    Option(getRootPane).foreach(_.setDefaultButton(btnRegistrarseUI))
  }

  private def initUI(): Unit = {
    setLayout(new GridBagLayout())
    setBackground(colorFondo)
    val constraints = UIUtils.configurarPanelPrincipal(this, colorFondo)

    val lblNombre = new JLabel("Nombre:")
    lblNombre.setFont(fuenteSansSerif)
    lblNombre.setForeground(colorTexto)
    val lblUsername = new JLabel("Usuario:")
    lblUsername.setFont(fuenteSansSerif)
    lblUsername.setForeground(colorTexto)
    val lblCorreo = new JLabel("Correo:")
    lblCorreo.setFont(fuenteSansSerif)
    lblCorreo.setForeground(colorTexto)
    val lblContrasenia = new JLabel("Contraseña:")
    lblContrasenia.setFont(fuenteSansSerif)
    lblContrasenia.setForeground(colorTexto)
    val panelContrasenia = new JPanel(new BorderLayout())
    panelContrasenia.setBackground(colorFondo)
    txtContrasenia.setFont(fuenteSansSerif)
    txtContrasenia.setForeground(colorTexto)
    txtContrasenia.setBackground(Color.WHITE)
    txtContrasenia.setBorder(BorderFactory.createLineBorder(colorBorde))
    panelContrasenia.add(txtContrasenia, BorderLayout.CENTER)
    panelContrasenia.add(btnVerOcultar1, BorderLayout.EAST)
    val lblRepetir = new JLabel("Repetir contraseña:")
    lblRepetir.setFont(fuenteSansSerif)
    lblRepetir.setForeground(colorTexto)
    val panelRepetir = new JPanel(new BorderLayout())
    panelRepetir.setBackground(colorFondo)
    txtRepetirContrasenia.setFont(fuenteSansSerif)
    txtRepetirContrasenia.setForeground(colorTexto)
    txtRepetirContrasenia.setBackground(Color.WHITE)
    txtRepetirContrasenia.setBorder(BorderFactory.createLineBorder(colorBorde))
    panelRepetir.add(txtRepetirContrasenia, BorderLayout.CENTER)
    panelRepetir.add(btnVerOcultar2, BorderLayout.EAST)

    val panelCentral = new JPanel()
    panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS))
    panelCentral.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30))
    panelCentral.setAlignmentX(Component.CENTER_ALIGNMENT)
    panelCentral.setAlignmentY(Component.CENTER_ALIGNMENT)
    panelCentral.setBackground(colorFondo)

    val lblTitulo = new JLabel("Registrarse")
    lblTitulo.setFont(fuenteTitulo)
    lblTitulo.setForeground(colorTexto)
    lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT)

    val (panelFormulario, c) = UIUtils.crearPanelFormularioConC(colorFondo)

    c.gridx = 0; c.weightx = 0.0; c.fill = GridBagConstraints.NONE
    panelFormulario.add(lblNombre, c)
    c.gridx = 1; c.weightx = 1.0; c.fill = GridBagConstraints.HORIZONTAL
    txtNombre.setPreferredSize(new Dimension(180, 28))
    panelFormulario.add(txtNombre, c)

    c.gridx = 0; c.gridy = 1; c.weightx = 0.0; c.fill = GridBagConstraints.NONE
    panelFormulario.add(lblUsername, c)
    c.gridx = 1; c.weightx = 1.0; c.fill = GridBagConstraints.HORIZONTAL
    txtUsername.setPreferredSize(new Dimension(180, 28))
    panelFormulario.add(txtUsername, c)

    c.gridx = 0; c.gridy = 2; c.weightx = 0.0; c.fill = GridBagConstraints.NONE
    panelFormulario.add(lblCorreo, c)
    c.gridx = 1; c.weightx = 1.0; c.fill = GridBagConstraints.HORIZONTAL
    txtCorreo.setPreferredSize(new Dimension(180, 28))
    panelFormulario.add(txtCorreo, c)

    c.gridx = 0; c.gridy = 3; c.weightx = 0.0; c.fill = GridBagConstraints.NONE
    val lblCondiciones = new JLabel("La contraseña debe tener al menos 8 caracteres, incluir una letra, un número y un carácter especial.")
    lblCondiciones.setFont(fuenteSansSerif)
    panelFormulario.add(lblCondiciones, c)
    c.gridx = 1; c.weightx = 1.0; c.fill = GridBagConstraints.HORIZONTAL

    c.gridx = 0; c.gridy = 4; c.weightx = 0.0; c.fill = GridBagConstraints.NONE
    panelFormulario.add(lblContrasenia, c)
    c.gridx = 1; c.weightx = 1.0; c.fill = GridBagConstraints.HORIZONTAL
    panelContrasenia.setPreferredSize(new Dimension(180, 28))
    panelFormulario.add(panelContrasenia, c)

    c.gridx = 0; c.gridy = 5; c.weightx = 0.0; c.fill = GridBagConstraints.NONE
    panelFormulario.add(lblRepetir, c)
    c.gridx = 1; c.weightx = 1.0; c.fill = GridBagConstraints.HORIZONTAL
    panelRepetir.setPreferredSize(new Dimension(180, 28))
    panelFormulario.add(panelRepetir, c)

    btnRegistrarseUI.setFont(fuenteSansSerif)
    btnRegistrarseUI.setForeground(colorTexto)
    btnRegistrarseUI.setBackground(colorFondo)
    btnRegistrarseUI.setBorder(BorderFactory.createLineBorder(colorBorde))
    btnVolver.setFont(fuenteSansSerif)
    btnVolver.setForeground(colorTexto)
    btnVolver.setBackground(colorFondo)
    btnVolver.setBorder(BorderFactory.createLineBorder(colorBorde))

    panelCentral.add(lblTitulo)
    panelCentral.add(Box.createRigidArea(new Dimension(0, 20)))
    panelCentral.add(panelFormulario)
    panelCentral.add(Box.createRigidArea(new Dimension(0, 20)))
    val panelBotones = new JPanel()
    panelBotones.setBackground(colorFondo)
    panelBotones.add(btnRegistrarseUI)
    panelBotones.add(btnVolver)
    panelCentral.add(panelBotones)

    add(panelCentral, constraints)
  }

  def setSolicitarMostrarLogin(callback: () => Unit): Unit = {
    solicitarMostrarLogin = Some(callback)
  }

  def emitirSolicitarMostrarLogin(): Unit = {
    solicitarMostrarLogin.foreach(_())
  }

  def getTxtNombre: JTextField = txtNombre
  def getTxtUsername: JTextField = txtUsername
  def getTxtCorreo: JTextField = txtCorreo
  def getTxtContrasenia: JPasswordField = txtContrasenia
  def getTxtRepetirContrasenia: JPasswordField = txtRepetirContrasenia
  def getBtnRegistrarseUI: JButton = btnRegistrarseUI
  def getBtnVolver: JButton = btnVolver
}
