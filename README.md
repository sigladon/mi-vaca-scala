# Mi Vaca: Sistema de Gestión Financiera Personal
*By Rafael Baculima*
# Tabla de Contenidos
1. [Descripción](#descripción)
2. [Funcionalidades principale](#funcionalidades-principales)
3. [Descripción de cada reporte y análisis disponible](#descripción-de-cada-reporte-y-análisis-disponible)
4. [Estructura de datos](#estructura-de-datos)
5. [Tecnologías utilizadas](#tecnologías-utilizadas)
6. [Credenciales para ingresar](#usuarios-para-ingresar)

## Descripción

**Mi Vaca** es una aplicación de escritorio desarrollada en Scala para la gestión financiera personal. Permite a los usuarios registrar y analizar sus ingresos, gastos, presupuestos y metas financieras, proporcionando reportes visuales y alertas inteligentes para mejorar la toma de decisiones económicas.

## Funcionalidades principales

- **Gestión de usuarios:** Registro y autenticación de usuarios con datos personales y contraseña segura.
- **Movimientos (Transacciones):** Registro de ingresos y gastos, con descripción, categoría, fecha y notas. Los gastos se registran como montos negativos y los ingresos como positivos.
- **Presupuestos:** Creación y seguimiento de presupuestos anuales, con asignación de límites y categorías. El sistema alerta cuando se está cerca de exceder el presupuesto.
- **Metas financieras:** Definición de metas de ahorro con monto objetivo y fecha límite. El avance se calcula automáticamente según el saldo disponible.
- **Categorías:** Gestión de categorías personalizadas para clasificar los movimientos.
- **Alertas inteligentes:** Notificaciones automáticas cuando se superan ciertos umbrales de gasto o se detectan patrones relevantes.
- **Panel de bienvenida y dashboard:** Resumen visual del estado financiero actual, metas y presupuestos activos.
- **Edición y eliminación:** Posibilidad de editar o eliminar transacciones, presupuestos, metas y categorías.

## Descripción de cada reporte y análisis disponible

- **Tendencia de ingresos y gastos:**
  Muestra un gráfico de línea que permite visualizar cómo evolucionan tus ingresos y gastos a lo largo del tiempo (por día, semana, mes, trimestre o año). Es ideal para identificar patrones, meses con más gastos o ingresos, y analizar el balance financiero real.

- **Porcentaje de gastos por categoría:**
  Presenta un gráfico de pastel que muestra el porcentaje de tus gastos distribuidos en cada categoría (alimentación, transporte, entretenimiento, etc.). Te ayuda a identificar en qué rubros gastas más y tomar decisiones para optimizar tu presupuesto.

- **Alertas Inteligentes:**
  El sistema genera alertas automáticas cuando detecta situaciones importantes, como estar cerca de exceder un presupuesto, realizar un gasto inusual o detectar patrones de comportamiento financiero que requieren atención. Las alertas te ayudan a reaccionar a tiempo y evitar problemas financieros.

- **Eficiencia Financiera:**
  Evalúa tus hábitos de ahorro y gasto, mostrando métricas como la consistencia del ahorro, la variabilidad de tus gastos y la frecuencia de tus compras. Te permite conocer qué tan eficiente eres administrando tu dinero y te da sugerencias para mejorar.

- **Análisis de Comportamiento:**
  Analiza tus patrones de consumo y ahorro, identificando tendencias, hábitos y posibles áreas de mejora. Este reporte te ayuda a entender cómo y cuándo gastas tu dinero, y a detectar comportamientos que podrías ajustar para alcanzar tus metas.

- **Proyecciones Financieras:**
  Genera estimaciones sobre tu futuro financiero en base a tus ingresos, gastos y hábitos actuales. Puedes ver proyecciones de saldo, gastos por categoría y cumplimiento de metas, lo que te permite anticipar problemas y planificar mejor.

- **Comparaciones Temporales:**
  Permite comparar tus ingresos y gastos entre diferentes periodos (meses, trimestres, años) o entre diferentes categorías. Así puedes ver si tu situación financiera está mejorando o empeorando con el tiempo y ajustar tus estrategias.

- **Insights Personalizados:**
  El sistema analiza tus datos y te presenta observaciones y recomendaciones personalizadas, como oportunidades de ahorro, cambios en tus hábitos o sugerencias para mejorar tu salud financiera. Es una sección de consejos inteligentes basada en tu propio comportamiento.

Cada reporte está diseñado para ser visual, fácil de interpretar y útil para la toma de decisiones cotidianas sobre tus finanzas personales.

## Estructura de datos

- Los datos se almacenan en archivos separados para usuarios, movimientos, presupuestos, metas y categorías.
- Cada movimiento tiene: monto, descripción, fecha, notas, categoría, id y estado.
- Los presupuestos son anuales y agrupan movimientos por categorías.
- Las metas se calculan en base al saldo de ingresos y egresos, no se vinculan directamente a movimientos.

## Tecnologías utilizadas
- Scala (Swing para la interfaz gráfica)
- JFreeChart para gráficos
- Serialización de objetos para persistencia de datos

## Usuarios para ingresar
Existen 2 usuarios creados para probar la aplicación
- `datos`/`@admin123` : Usuario con datos cargados
- `admin`/`@admin123` : Usuario sin ningún dato previo
