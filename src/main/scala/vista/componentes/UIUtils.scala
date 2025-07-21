package vista.componentes

import javax.swing.{JPanel, Icon, JButton}
import java.awt.{GridBagConstraints, GridBagLayout, Insets, Color}
import java.awt.event.ActionEvent

object UIUtils {
  def crearPanelConGbc(): (JPanel, GridBagConstraints) = {
    val panel = new JPanel(new GridBagLayout())
    val gbc = new GridBagConstraints()
    gbc.insets = new Insets(5, 5, 5, 5)
    gbc.anchor = GridBagConstraints.WEST
    (panel, gbc)
  }

  def crearPanelFormularioConC(fondo: Color): (JPanel, GridBagConstraints) = {
    val panel = new JPanel(new GridBagLayout())
    panel.setBackground(fondo)
    val c = new GridBagConstraints()
    c.insets = new Insets(5, 5, 5, 5)
    c.anchor = GridBagConstraints.LINE_START
    c.gridy = 0
    (panel, c)
  }

  def configurarPanelPrincipal(panel: JPanel, colorFondo: Color): GridBagConstraints = {
    panel.setLayout(new GridBagLayout())
    panel.setBackground(colorFondo)
    val constraints = new GridBagConstraints()
    constraints.gridx = 0
    constraints.gridy = 0
    constraints.anchor = GridBagConstraints.CENTER
    constraints.fill = GridBagConstraints.NONE
    constraints.weightx = 1.0
    constraints.weighty = 1.0
    constraints
  }

  def alternarVisibilidadContrasenia(
    visible: Boolean,
    setVisible: Boolean => Unit,
    setEchoChar: Char => Unit,
    setIcon: Icon => Unit,
    iconoVisible: Icon,
    iconoOculto: Icon
  ): Boolean = {
    val nuevoEstado = !visible
    setVisible(nuevoEstado)
    setEchoChar(if (nuevoEstado) 0 else '\u2022')
    setIcon(if (nuevoEstado) iconoVisible else iconoOculto)
    nuevoEstado
  }

  def configurarToggleContrasenia(
    btn: JButton,
    getVisible: () => Boolean,
    setVisible: Boolean => Unit,
    setEchoChar: Char => Unit,
    setIcon: Icon => Unit,
    iconoVisible: Icon,
    iconoOculto: Icon
  ): Unit = {
    btn.addActionListener((_: ActionEvent) => {
      setVisible(
        alternarVisibilidadContrasenia(
          getVisible(),
          setVisible,
          setEchoChar,
          setIcon,
          iconoVisible,
          iconoOculto
        )
      )
    })
  }
} 