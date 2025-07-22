package modelo.servicios

import modelo.entidades._

import java.io.{FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}
import java.nio.file.{Files, Paths}

object GestorDatos {
  
  private val directorioDatos = "./src/main/scala/datos"
  private val archivoUsuarios = "usuarios.dat"
  private val archivoPresupuestos = "presupuestos.dat"
  private val archivoMetas = "metas.dat"
  private val archivoMovimientos = "movimientos.dat"
  private val archivoCategorias = "categorias.dat"
  private val archivoTokens = "tokens.dat"
  private val archivoContadores = "contadores.dat"
  

  private var contadores: Contadores = cargarContadores()
  
  private def asegurarDirectorio(): Unit = {
    val path = Paths.get(directorioDatos)
    if (!Files.exists(path)) {
      Files.createDirectories(path)
    }
  }

  private def cargarArchivo[T](archivo: String, default: => T): T = {
    try {
      val fis = new FileInputStream(s"$directorioDatos/$archivo")
      val ois = new ObjectInputStream(fis)
      try {
        ois.readObject().asInstanceOf[T]
      } finally {
        ois.close()
        fis.close()
      }
    } catch {
      case _: Exception => default
    }
  }

  private def guardarArchivo[T](archivo: String, objeto: T): Unit = {
    val fos = new FileOutputStream(s"$directorioDatos/$archivo")
    val oos = new ObjectOutputStream(fos)
    try {
      oos.writeObject(objeto)
    } finally {
      oos.close()
      fos.close()
    }
  }
  
  private def cargarContadores(): Contadores = {
    cargarArchivo(archivoContadores, Contadores())
  }
  
  private def guardarContadores(): Unit = {
    asegurarDirectorio()
    guardarArchivo(archivoContadores, contadores)
  }
  
  def guardarUsuario(usuario: Usuario): Unit = {
    asegurarDirectorio()
    guardarArchivo(archivoUsuarios, usuario)
  }
  
  def cargarUsuario(): Usuario = {
    cargarArchivo(archivoUsuarios, null.asInstanceOf[Usuario])
  }
  
  def obtenerSiguienteIdUsuario(): Int = {
    val id = contadores.siguienteIdUsuario
    contadores = contadores.copy(siguienteIdUsuario = id + 1)
    guardarContadores()
    id
  }
  
  def guardarUsuarios(usuarios: List[Usuario]): Unit = {
    asegurarDirectorio()
    guardarArchivo(archivoUsuarios, usuarios)
  }

  def cargarUsuarios(): List[Usuario] = {
    cargarArchivo(archivoUsuarios, List.empty[Usuario])
  }

  def agregarUsuario(nuevoUsuario: Usuario): Unit = {
    val usuarios = cargarUsuarios()
    guardarUsuarios(usuarios :+ nuevoUsuario)
  }

  def buscarUsuarioPorIdentificacion(identificacion: String): Option[Usuario] = {
    val usuarios = cargarUsuarios()
    usuarios.find(u => u.username == identificacion || u.correos.contains(identificacion))
  }

  def guardarPresupuestos(presupuestos: List[Presupuesto]): Unit = guardarArchivo(archivoPresupuestos, presupuestos)

  def obtenerSiguienteIdPresupuesto(): Int = {
    val id = contadores.siguienteIdPresupuesto
    contadores = contadores.copy(siguienteIdPresupuesto = id + 1)
    guardarContadores()
    id
  }
  

  def guardarMetas(metas: List[Meta]): Unit = guardarArchivo(archivoMetas, metas)

  def obtenerSiguienteIdMeta(): Int = {
    val id = contadores.siguienteIdMeta
    contadores = contadores.copy(siguienteIdMeta = id + 1)
    guardarContadores()
    id
  }
  

  def guardarMovimientos(movimientos: List[Movimiento]): Unit = guardarArchivo(archivoMovimientos, movimientos)

  def obtenerSiguienteIdMovimiento(): Int = {
    val id = contadores.siguienteIdMovimiento
    contadores = contadores.copy(siguienteIdMovimiento = id + 1)
    guardarContadores()
    id
  }
  

  def guardarCategorias(categorias: List[Categoria]): Unit = guardarArchivo(archivoCategorias, categorias)

  def obtenerSiguienteIdCategoria(): Int = {
    val id = contadores.siguienteIdCategoria
    contadores = contadores.copy(siguienteIdCategoria = id + 1)
    guardarContadores()
    id
  }
  

  def guardarToken(token: Token): Unit = guardarArchivo(archivoTokens, token)
  
  def cargarToken(): Token = cargarArchivo(archivoTokens, null.asInstanceOf[Token])
  
  def eliminarToken(): Unit = {
    try {
      val archivo = new java.io.File(s"$directorioDatos/$archivoTokens")
      if (archivo.exists()) {
        archivo.delete()
      }
    } catch {
      case _: Exception =>
    }
  }
} 