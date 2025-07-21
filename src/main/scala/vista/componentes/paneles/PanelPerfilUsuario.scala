package vista.componentes.paneles

import modelo.entidades.PerfilUsuarioData
import javax.swing._
import java.awt._
import java.awt.event.{ActionEvent, ActionListener}
import javax.swing.ImageIcon
import javax.swing.SwingConstants

class PanelPerfilUsuario(
  datos: PerfilUsuarioData,
  onGuardar: (PerfilUsuarioData, Option[(String, String, String)]) => Unit
) extends JPanel {

  private val colorFondo = new Color(0xeff1f5)
  private val colorTexto = new Color(0x4c4f69)
  private val colorBorde = new Color(0xccd0da)
  private val fuenteSansSerif = new Font(Font.SANS_SERIF, Font.PLAIN, 11)
  private val fuenteTitulo = new Font(Font.SANS_SERIF, Font.BOLD, 12)

  setLayout(new BorderLayout())
  setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30))

  val lblTitulo = new JLabel("Perfil de Usuario")
  lblTitulo.setFont(fuenteTitulo)
  lblTitulo.setHorizontalAlignment(SwingConstants.CENTER)

  val lblNombre = new JLabel("Nombre:", new ImageIcon("src/main/scala/assets/usuario.png"), SwingConstants.LEFT)
  lblNombre.setFont(fuenteSansSerif)
  val txtNombre = new JTextField(datos.nombre, 20)
  lblNombre.setForeground(colorTexto)
  txtNombre.setForeground(colorTexto)
  lblNombre.setFont(fuenteSansSerif)

  val lblCorreo = new JLabel("Correo:", new ImageIcon("src/main/scala/assets/correo.png"), SwingConstants.LEFT)
  lblCorreo.setFont(fuenteSansSerif)
  val txtCorreo = new JTextField(datos.correo, 20)
  lblCorreo.setForeground(colorTexto)
  txtCorreo.setForeground(colorTexto)
  lblCorreo.setFont(fuenteSansSerif)

  val lblUsuario = new JLabel("Usuario:", new ImageIcon("src/main/scala/assets/perfil.png"), SwingConstants.LEFT)
  lblUsuario.setFont(fuenteSansSerif)
  val txtUsuario = new JTextField(datos.usuario, 20)
  lblUsuario.setForeground(colorTexto)
  txtUsuario.setForeground(colorTexto)
  lblUsuario.setFont(fuenteSansSerif)


  val lblSeccionContrasenia = new JLabel("Cambiar contraseña (opcional):")
  lblSeccionContrasenia.setFont(fuenteTitulo)
  lblSeccionContrasenia.setForeground(colorTexto)

  val lblContraseniaActual = new JLabel("Contraseña actual:", new ImageIcon("src/main/scala/assets/contrasena.png"), SwingConstants.LEFT)
  lblContraseniaActual.setFont(fuenteSansSerif)
  lblContraseniaActual.setForeground(colorTexto)
  val txtContraseniaActual = new JPasswordField(20)
  txtContraseniaActual.setFont(fuenteSansSerif)
  txtContraseniaActual.setForeground(colorTexto)
  val btnVerActual = new JButton(new ImageIcon("src/main/scala/assets/ver.png"))
  btnVerActual.setFocusable(false)
  btnVerActual.addActionListener((_: ActionEvent) => {
    val visible = txtContraseniaActual.getEchoChar == 0
    txtContraseniaActual.setEchoChar(if (visible) '\u2022' else 0)
  })

  val lblNuevaContrasenia = new JLabel("Nueva contraseña:", new ImageIcon("src/main/scala/assets/contrasena.png"), SwingConstants.LEFT)
  lblNuevaContrasenia.setFont(fuenteSansSerif)
  lblNuevaContrasenia.setForeground(colorTexto)
  val txtNuevaContrasenia = new JPasswordField(20)
  txtNuevaContrasenia.setFont(fuenteSansSerif)
  txtNuevaContrasenia.setForeground(colorTexto)
  val btnVerNueva = new JButton(new ImageIcon("src/main/scala/assets/ver.png"))
  btnVerNueva.setFocusable(false)
  btnVerNueva.addActionListener((_: ActionEvent) => {
    val visible = txtNuevaContrasenia.getEchoChar == 0
    txtNuevaContrasenia.setEchoChar(if (visible) '\u2022' else 0)
  })

  val lblConfirmarContrasenia = new JLabel("Confirmar nueva contraseña:", new ImageIcon("src/main/scala/assets/contrasena.png"), SwingConstants.LEFT)
  lblConfirmarContrasenia.setFont(fuenteSansSerif)
  lblConfirmarContrasenia.setForeground(colorTexto)
  val txtConfirmarContrasenia = new JPasswordField(20)
  txtConfirmarContrasenia.setFont(fuenteSansSerif)
  txtConfirmarContrasenia.setForeground(colorTexto)
  val btnVerConfirmar = new JButton(new ImageIcon("src/main/scala/assets/ver.png"))
  btnVerConfirmar.setFocusable(false)
  btnVerConfirmar.addActionListener((_: ActionEvent) => {
    val visible = txtConfirmarContrasenia.getEchoChar == 0
    txtConfirmarContrasenia.setEchoChar(if (visible) '\u2022' else 0)
  })

  val btnGuardar = new JButton("Guardar Cambios", new ImageIcon("src/main/scala/assets/guardar.png"))
  btnGuardar.setFont(fuenteSansSerif)
  btnGuardar.setForeground(colorTexto)
  btnGuardar.setBackground(new Color(0x89b4fa))
  btnGuardar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10))
  btnGuardar.setFocusable(true)


  val panelCampos = new JPanel(new GridBagLayout())
  val gbc = new GridBagConstraints()
  gbc.insets = new Insets(5, 5, 5, 5)
  gbc.anchor = GridBagConstraints.WEST
  gbc.fill = GridBagConstraints.NONE

  gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0
  panelCampos.add(lblNombre, gbc)
  gbc.gridx = 1; gbc.weightx = 1
  gbc.fill = GridBagConstraints.HORIZONTAL
  panelCampos.add(txtNombre, gbc)

  gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE
  panelCampos.add(lblCorreo, gbc)
  gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL
  panelCampos.add(txtCorreo, gbc)

  gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE
  panelCampos.add(lblUsuario, gbc)
  gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL
  panelCampos.add(txtUsuario, gbc)


  val panelContrasenia = new JPanel(new GridBagLayout())
  val gbc2 = new GridBagConstraints()
  gbc2.insets = new Insets(5, 5, 5, 5)
  gbc2.anchor = GridBagConstraints.WEST
  gbc2.fill = GridBagConstraints.NONE

  gbc2.gridx = 0; gbc2.gridy = 0; gbc2.weightx = 0
  panelContrasenia.add(lblContraseniaActual, gbc2)
  gbc2.gridx = 1; gbc2.weightx = 1; gbc2.fill = GridBagConstraints.HORIZONTAL
  panelContrasenia.add(txtContraseniaActual, gbc2)
  gbc2.gridx = 2; gbc2.weightx = 0; gbc2.fill = GridBagConstraints.NONE
  panelContrasenia.add(btnVerActual, gbc2)

  gbc2.gridx = 0; gbc2.gridy = 1; gbc2.weightx = 0
  panelContrasenia.add(lblNuevaContrasenia, gbc2)
  gbc2.gridx = 1; gbc2.weightx = 1; gbc2.fill = GridBagConstraints.HORIZONTAL
  panelContrasenia.add(txtNuevaContrasenia, gbc2)
  gbc2.gridx = 2; gbc2.weightx = 0; gbc2.fill = GridBagConstraints.NONE
  panelContrasenia.add(btnVerNueva, gbc2)

  gbc2.gridx = 0; gbc2.gridy = 2; gbc2.weightx = 0
  panelContrasenia.add(lblConfirmarContrasenia, gbc2)
  gbc2.gridx = 1; gbc2.weightx = 1; gbc2.fill = GridBagConstraints.HORIZONTAL
  panelContrasenia.add(txtConfirmarContrasenia, gbc2)
  gbc2.gridx = 2; gbc2.weightx = 0; gbc2.fill = GridBagConstraints.NONE
  panelContrasenia.add(btnVerConfirmar, gbc2)


  val fieldDim = new Dimension(220, 28)
  txtNombre.setMaximumSize(fieldDim)
  txtCorreo.setMaximumSize(fieldDim)
  txtUsuario.setMaximumSize(fieldDim)
  txtContraseniaActual.setMaximumSize(fieldDim)
  txtNuevaContrasenia.setMaximumSize(fieldDim)
  txtConfirmarContrasenia.setMaximumSize(fieldDim)

  txtNombre.setPreferredSize(fieldDim)
  txtCorreo.setPreferredSize(fieldDim)
  txtUsuario.setPreferredSize(fieldDim)
  txtContraseniaActual.setPreferredSize(fieldDim)
  txtNuevaContrasenia.setPreferredSize(fieldDim)
  txtConfirmarContrasenia.setPreferredSize(fieldDim)

  val panelCentral = new JPanel()
  panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS))
  panelCentral.setOpaque(false)
  panelCentral.add(lblTitulo)
  panelCentral.add(Box.createVerticalStrut(15))
  panelCentral.add(panelCampos)
  panelCentral.add(Box.createVerticalStrut(15))
  panelCentral.add(lblSeccionContrasenia)
  panelCentral.add(panelContrasenia)
  panelCentral.add(Box.createVerticalStrut(15))
  panelCentral.add(btnGuardar)

  add(panelCentral, BorderLayout.CENTER)

  btnGuardar.addActionListener((_: ActionEvent) => {
    val nombre = txtNombre.getText.trim
    val correo = txtCorreo.getText.trim
    val usuario = txtUsuario.getText.trim
    val actual = new String(txtContraseniaActual.getPassword)
    val nueva = new String(txtNuevaContrasenia.getPassword)
    val confirmar = new String(txtConfirmarContrasenia.getPassword)
    val cambioPass =
      if (actual.nonEmpty || nueva.nonEmpty || confirmar.nonEmpty)
        Some((actual, nueva, confirmar))
      else None
    onGuardar(PerfilUsuarioData(nombre, correo, usuario), cambioPass)
  })
} 