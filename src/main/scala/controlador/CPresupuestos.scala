package controlador

import modelo.entidades.Usuario
import modelo.servicios.GestorDatos
import vista.componentes.paneles.PanelPresupuestos

import javax.swing.JOptionPane

class CPresupuestos(private val panelPresupuestos: PanelPresupuestos, private var usuario: Usuario) {

  def obtenerUsuario: Usuario = usuario

  private def validarNombreCategoria(nombre: String): Boolean = {
    if (nombre.trim.isEmpty) {
      JOptionPane.showMessageDialog(null, "El nombre de la categoría no puede estar vacío", "Error", JOptionPane.ERROR_MESSAGE)
      false
    } else if (usuario.categorias.exists(_.nombre.toLowerCase == nombre.trim.toLowerCase)) {
      JOptionPane.showMessageDialog(null, "Ya existe una categoría con ese nombre", "Error", JOptionPane.ERROR_MESSAGE)
      false
    } else {
      true
    }
  }

  def actualizarMontoPresupuesto(nuevoMonto: Double): Boolean = {
    try {
      val anioActual = java.time.LocalDate.now().getYear
      val presupuestoActual = usuario.presupuestos.find(p => 
        p.inicioPresupuesto.getYear == anioActual && p.estaActivo
      )
      
      if (presupuestoActual.isEmpty) {
        JOptionPane.showMessageDialog(null, "No tienes un presupuesto anual activo.", "Error", JOptionPane.ERROR_MESSAGE)
        return false
      }
      
      val presupuesto = presupuestoActual.get
      val presupuestoActualizado = presupuesto.copy(limite = nuevoMonto)
      

      val presupuestosActualizados = usuario.presupuestos.map {
        case p if p.id == presupuesto.id => presupuestoActualizado
        case p => p
      }
      
      usuario = usuario.copy(presupuestos = presupuestosActualizados)
      

      GestorDatos.guardarPresupuestos(usuario.presupuestos)
      actualizarUsuarioEnListaYGuardar()
      

      panelPresupuestos.mostrarPresupuestos(usuario.presupuestos, usuario.movimientos)
      
      JOptionPane.showMessageDialog(null, s"Presupuesto actualizado a $nuevoMonto", "Éxito", JOptionPane.INFORMATION_MESSAGE)
      true
    } catch {
      case e: Exception =>
        JOptionPane.showMessageDialog(null, s"Error al actualizar el presupuesto: ${e.getMessage}", "Error", JOptionPane.ERROR_MESSAGE)
        false
    }
  }

  private def actualizarUsuarioEnListaYGuardar(): Unit = {
    val usuarios = GestorDatos.cargarUsuarios()
    val usuariosActualizados = usuarios.map(u => if (u.id == usuario.id) usuario else u)
    GestorDatos.guardarUsuarios(usuariosActualizados)
  }

  private def finalizarOperacionCategoria(mensaje: String): Unit = {
    GestorDatos.guardarCategorias(usuario.categorias)
    GestorDatos.guardarPresupuestos(usuario.presupuestos)
    actualizarUsuarioEnListaYGuardar()
    panelPresupuestos.mostrarPresupuestos(usuario.presupuestos, usuario.movimientos)
    JOptionPane.showMessageDialog(null, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE)
  }

  def agregarCategoria(nombreCategoria: String): Boolean = {
    try {
      if (!validarNombreCategoria(nombreCategoria)) return false
      
      val nuevaCategoria = modelo.entidades.Categoria(
        nombre = nombreCategoria.trim,
        id = GestorDatos.obtenerSiguienteIdCategoria()
      )
      
      val categoriasActualizadas = nuevaCategoria :: usuario.categorias
      usuario = usuario.copy(categorias = categoriasActualizadas)
      

      val anioActual = java.time.LocalDate.now().getYear
      val presupuestosActualizados = usuario.presupuestos.map { presupuesto =>
        if (presupuesto.inicioPresupuesto.getYear == anioActual && presupuesto.estaActivo) {
          val categoriasActualizadas = presupuesto.categorias + (nuevaCategoria.nombre -> true)
          presupuesto.copy(categorias = categoriasActualizadas)
        } else {
          presupuesto
        }
      }
      
      usuario = usuario.copy(presupuestos = presupuestosActualizados)
      

      finalizarOperacionCategoria(s"Categoría '$nombreCategoria' agregada exitosamente")
      true
    } catch {
      case e: Exception =>
        JOptionPane.showMessageDialog(null, s"Error al agregar la categoría: ${e.getMessage}", "Error", JOptionPane.ERROR_MESSAGE)
        false
    }
  }

  def eliminarCategoria(nombreCategoria: String): Boolean = {
    try {
      val respuesta = JOptionPane.showConfirmDialog(
        null,
        s"¿Estás seguro de que quieres eliminar la categoría '$nombreCategoria'?",
        "Confirmar eliminación",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE
      )

      if (respuesta == JOptionPane.YES_OPTION) {

        val categoriasActualizadas = usuario.categorias.filter(_.nombre != nombreCategoria)
        usuario = usuario.copy(categorias = categoriasActualizadas)
        

        val anioActual = java.time.LocalDate.now().getYear
        val presupuestosActualizados = usuario.presupuestos.map { presupuesto =>
          if (presupuesto.inicioPresupuesto.getYear == anioActual && presupuesto.estaActivo) {
            val categoriasActualizadas = presupuesto.categorias - nombreCategoria
            presupuesto.copy(categorias = categoriasActualizadas)
          } else {
            presupuesto
          }
        }
        
        usuario = usuario.copy(presupuestos = presupuestosActualizados)
        

        finalizarOperacionCategoria(s"Categoría '$nombreCategoria' eliminada exitosamente")
        true
      } else {
        false
      }
    } catch {
      case e: Exception =>
        JOptionPane.showMessageDialog(null, s"Error al eliminar la categoría: ${e.getMessage}", "Error", JOptionPane.ERROR_MESSAGE)
        false
    }
  }

  def editarCategoria(nombreActual: String, nuevoNombre: String): Boolean = {
    try {
      if (!validarNombreCategoria(nuevoNombre)) return false
      

      val categoriasActualizadas = usuario.categorias.map { categoria =>
        if (categoria.nombre == nombreActual) {
          categoria.copy(nombre = nuevoNombre.trim)
        } else {
          categoria
        }
      }
      usuario = usuario.copy(categorias = categoriasActualizadas)
      

      val anioActual = java.time.LocalDate.now().getYear
      val presupuestosActualizados = usuario.presupuestos.map { presupuesto =>
        if (presupuesto.inicioPresupuesto.getYear == anioActual && presupuesto.estaActivo) {
          val categoriasActualizadas = presupuesto.categorias - nombreActual + (nuevoNombre.trim -> true)
          presupuesto.copy(categorias = categoriasActualizadas)
        } else {
          presupuesto
        }
      }
      
      usuario = usuario.copy(presupuestos = presupuestosActualizados)
      

      finalizarOperacionCategoria(s"Categoría '$nombreActual' renombrada a '$nuevoNombre' exitosamente")
      true
    } catch {
      case e: Exception =>
        JOptionPane.showMessageDialog(null, s"Error al editar la categoría: ${e.getMessage}", "Error", JOptionPane.ERROR_MESSAGE)
        false
    }
  }

  def actualizarUsuario(nuevoUsuario: Usuario): Unit = {
    usuario = nuevoUsuario
    panelPresupuestos.mostrarPresupuestos(usuario.presupuestos, usuario.movimientos)
  }
} 