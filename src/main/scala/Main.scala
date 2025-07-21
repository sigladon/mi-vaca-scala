import controlador.ControladorPrincipal
import javax.swing.SwingUtilities

object Main extends App {

  SwingUtilities.invokeLater(() => {
    ControladorPrincipal.iniciar()
  })
}

