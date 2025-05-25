# Aplicación Android de Bodegas, Llagares y Queserías de Asturias

## **Funcionalidades de la aplicación**

- Visualización de una lista de bodegas, llagares y queserías de Asturias.
- Búsqueda y filtrado de bodegas por nombre, zona y concejo.
- Marcado y gestión de bodegas favoritas.
- Visualización de detalles completos de cada bodega, incluyendo imágenes, horarios, dirección, web y teléfonos.
- Acceso rápido a la web de la bodega y llamada directa a los teléfonos de contacto.
- Mapa interactivo con localización de todas las bodegas y acceso a sus detalles.
- Configuración de idioma (español/inglés) y tema (claro/oscuro).
- Ocultación de la barra de navegación para una experiencia de usuario más inmersiva.
- Persistencia de datos y favoritos mediante base de datos local.
- Actualización automática de datos desde un servicio web.

## **Arquitectura de la aplicación**

La aplicación sigue una arquitectura basada en MVVM (Model-View-ViewModel), que separa claramente la lógica de negocio, la gestión de datos y la interfaz de usuario.

*Nota: Para simplificar la terminología, en las distintas clases cuando se menciona el término "bodega(s)", se hace referencia a "bodegas, llagares y queserías".*

## Capa de Presentación (UI/View)

La capa de Presentación es responsable de mostrar la interfaz de usuario y de interactuar con el usuario, mostrando datos y recibiendo acciones. Recibe los datos del ViewModel y los muestra, y envía las acciones del usuario al ViewModel.

### Fragmentos

- HomeFragment: Fragmento que muestra la lista principal de bodegas, gestiona la búsqueda y los filtros.
- FavoritesFragment: Fragmento que muestra la lista de bodegas favoritas y permite buscarlas o filtrarlas.
- DetailFragment: Fragmento que muestra los detalles de una bodega seleccionada.
- MapFragment: Fragmento que muestra un mapa con marcadores de bodegas.
- SettingsFragment: Fragmento que permite al usuario cambiar preferencias como idioma y tema.
- FilterDialogFragment: Fragmento de diálogo para filtrar bodegas por zona y concejo.

### Adaptadores y ViewHolders

- BodegaDataListAdapter: Adaptador para mostrar la lista de bodegas y gestiona los clics y favoritos.
- BodegaViewHolder: ViewHolder que representa cada elemento de la lista de bodegas, mostrando información básica y el botón de favorito.
- BodegaDetailViewHolder: ViewHolder que muestra los detalles completos de una bodega en la pantalla de detalle.
- ImagePagerAdapter: Adaptador para mostrar varias imágenes de una bodega en un ViewPager.

### Activity

- MainActivity: Actividad principal que gestiona la navegación y la configuración global de la app.

## Capa de Dominio (ViewModel)

La capa de dominio es responsable de la lógica de negocio de la aplicación y la gestión de datos entre la UI y el repositorio. Se gestionan las reglas, operaciones y procesos principales que definen el funcionamiento interno, independientemente de cómo se presenten los datos o cómo se almacenen.

- BodegaViewModel: ViewModel que gestiona la lógica de negocio relacionada con la obtención, filtrado y búsqueda de bodegas. Se comunica con el repositorio para obtener los datos, aplica filtros por zona y concejo, hace búsqueda por nombre, y expone los estados de la UI mediante LiveData.
- BodegaViewModelFactory: Factory para crear instancias de BodegaViewModel.
- BodegaDetailsViewModel: ViewModel que gestiona la lógica de negocio para la pantalla de detalle de una bodega. Obtiene los datos de una bodega específica a partir de su ID y los expone mediante LiveData.
- BodegaDetailsViewModelFactory: Factory para crear instancias de BodegaDetailsViewModel.
- BodegaFavoritosViewModel: ViewModel que gestiona la lógica de negocio relacionada con las bodegas marcadas como favoritas. Permite obtener, buscar y filtrar las bodegas favoritas, así como marcar o desmarcar favoritos.
- BodegaFavoritosViewModelFactory: Factory para crear instancias de BodegaFavoritosViewModel.
- MapViewModel: ViewModel que gestiona la obtención de bodegas para el mapa. Carga la lista de bodegas y las expone para que la vista pueda mostrar los marcadores en el mapa.
- MapViewModelFactory: Factory para crear instancias de MapViewModel.
- AppUIState: Representa los diferentes estados de la UI (cargando, éxito, error) y se utiliza para comunicar el estado desde el ViewModel a la UI.

## Capa de Datos (Repositorio)

La capa de Datos es responsable de gestionar el acceso y manipular los datos, ya sea desde la base de datos local (Room) o desde la red.

- BodegaRepository: Proporciona una interfaz unificada para acceder a los datos de bodegas, ya sea desde la base de datos local o desde la red.
- Converters: Convierte objetos complejos a JSON y viceversa para almacenarlos en la base de datos Room.
- ApiResult: Representa el resultado de una operación de red: éxito, error o cargando.
- EmailAdapter: Adaptador para el campo email en los modelos usados por Moshi y Room

## Capa de Red (Network)

La capa de Red se encarga de la comunicación con servicios externos (APIs REST). Define las interfaces y configuraciones necesarias para obtener datos de la red.

- RestApi: Singleton que expone el servicio de red configurado con Retrofit.
- RestApiService: Interfaz de Retrofit que define los endpoints para obtener los datos de la API.
- WebService: Implementa la lógica de acceso a la API REST y devuelve los datos a la capa de repositorio.

## Capa de Modelo (Model)
La capa de Modelo define las entidades y modelos de datos que representan la información de la aplicación, así como los DAOs para el acceso a la base de datos.

- Bodega: Entidad principal que representa una bodega, con sus atributos y relaciones.
- BodegaDAO: Interfaz DAO de Room para acceder y manipular los datos de bodegas en la base de datos local.
- BodegaDatabase: Clase que define la base de datos Room y expone los DAOs.

*Nota: Dentro de la carpeta Model están las demás entidades de esta capa*

## Capa de Utilidades (Utils)

Incluye funciones auxiliares, extensiones y utilidades generales que facilitan tareas comunes en la aplicación.

- LiveDataExtensions: Archivo con funciones de extensión para LiveData, como observar una sola vez.

## Diagrama de clases UML
![DiagramaApp](https://github.com/user-attachments/assets/56b59b1a-8353-4f99-b88c-78aa2d8c76b5)

