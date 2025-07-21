package vista.componentes.paneles

import javax.swing._
import java.awt._

class PanelAcercaDe extends JPanel {
  setLayout(new BorderLayout())
  setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30))

  val lblTitulo = new JLabel("🐄 Mi Vaca - Sistema de Gestión Financiera")
  lblTitulo.setFont(new Font("Arial", Font.BOLD, 18))
  lblTitulo.setHorizontalAlignment(SwingConstants.CENTER)

  val lblVersion = new JLabel("Versión 1.0")
  lblVersion.setFont(new Font("Arial", Font.PLAIN, 14))
  lblVersion.setHorizontalAlignment(SwingConstants.CENTER)

  val lblAutor = new JLabel("Desarrollado por Rafael Baculima (@sigladon)")
  lblAutor.setFont(new Font("Arial", Font.PLAIN, 13))
  lblAutor.setHorizontalAlignment(SwingConstants.CENTER)

  val lblDerechos = new JLabel("© 2025 Mi Vaca. Todos los derechos reservados.")
  lblDerechos.setFont(new Font("Arial", Font.ITALIC, 12))
  lblDerechos.setHorizontalAlignment(SwingConstants.CENTER)

  val panelCentral = new JPanel()
  panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS))
  panelCentral.setOpaque(false)
  panelCentral.add(lblTitulo)
  panelCentral.add(Box.createVerticalStrut(10))
  panelCentral.add(lblVersion)
  panelCentral.add(Box.createVerticalStrut(10))
  panelCentral.add(lblAutor)
  panelCentral.add(Box.createVerticalStrut(10))
  panelCentral.add(lblDerechos)

  add(panelCentral, BorderLayout.CENTER)
} 