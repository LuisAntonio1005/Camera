package com.example.camera //nombre del paquete

//importaciones de librerias
import android.Manifest //datos y configuraciones del manifiesto
import android.annotation.SuppressLint //anotaciiones
import android.app.ActivityOptions
import android.content.Intent //eventos de navegacion envio de variables
import android.content.pm.PackageManager //paquetes de camara
import android.graphics.Bitmap //uso para bitmap
import android.graphics.BitmapFactory //propiedades de bitmap
import android.graphics.Canvas //propiedades de graficos y metodos dd estos
import android.graphics.Color //colores de graficos
import android.graphics.ColorMatrix //mtriz de colores
import android.graphics.ColorMatrixColorFilter  //mtriz de colores para el filtro
import android.graphics.Paint //propiedad de pintado
import android.graphics.Rect //grafico de Rectangulo
import android.graphics.drawable.ShapeDrawable //propiedad de visualizacion o dibujo
import android.graphics.drawable.shapes.RectShape //pintar Rectangulo
import android.os.Bundle //paquetes
import android.view.SurfaceHolder //parametros de la textura a usar para la camara
import android.view.SurfaceView //textura de la camara
import android.view.View //elemnto de interfaz de visualizacion
import android.widget.Button //elemto de boton
import android.widget.EditText //cajas de texto
import android.widget.FrameLayout
import android.graphics.Matrix
import android.widget.ImageView //elemto para visulizar imagenes
import android.widget.TextView //elemento par avisualizar tetxo
import android.widget.Toast //elemento para visualizar mensajes
import androidx.appcompat.app.AppCompatActivity //elementod de las actividades
import androidx.core.app.ActivityCompat //nucleos
import androidx.core.content.ContextCompat //referencias para su funcionamiento
import com.google.android.gms.vision.CameraSource //uso de camara para el reconocimeinto de texto
import com.google.android.gms.vision.Frame //creacion de frame para obtener texto
import com.google.android.gms.vision.text.TextRecognizer //propiedad de reconocimeinto de tecto

//inicia la clase
class MainActivity2 : AppCompatActivity() {

    //declaraciones de variables privadas
    private val CAMERA_PERMISSION_REQUEST_CODE = 1001//codigo de uso de permiso de camara
    private var mCameraSource: CameraSource? = null
    private lateinit var mCameraView: SurfaceView
    private lateinit var mRedRectangle: View // Rectángulo rojo
    private lateinit var mTextView: TextView
    private lateinit var mTurno: EditText
    private lateinit var mTelar: EditText
    private lateinit var mPlanta: EditText
    private lateinit var mCaptureButton: Button
    private lateinit var mImageView: ImageView


    // Tamaño de foto x, y motoe13
    private val scaleFactorX = 4.3f // Factor de escala en el eje X
    private val scaleFactorY = 3.55f // Factor de escala en el eje Y

    // Desplazamiento de foto x, y
    private var translationY = 2.3f // Desplazamiento en el eje Y
    private var translationx = 2.3f // Desplazamiento en el eje X
//// Tamaño de foto x, y mio
//    private val scaleFactorX = 3.0f // Factor de escala en el eje X
//    private val scaleFactorY = 3.15f // Factor de escala en el eje Y
//
//    // Desplazamiento de foto x, y
//    private var translationY = 2.5f // Desplazamiento en el eje Y
//    private var translationx = 1.5f // Desplazamiento en el eje X

    //oncreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //interfaz - layout a usar
        setContentView(R.layout.activity_main2)

        //id´s de la interfaz a usar
        mPlanta=findViewById(R.id.plantaV)
        mTurno=findViewById(R.id.turnoV)
        mTelar=findViewById(R.id.telarV)
        mCameraView = findViewById(R.id.cameraView)
        mRedRectangle = findViewById(R.id.redRectangle) // Rectángulo rojo
        mTextView = findViewById(R.id.textView)
        mCaptureButton = findViewById(R.id.captureButton)
        mImageView = findViewById(R.id.imageView)


        //recibimiento de datos de la interfaz de registro
        val plantaValue = intent.getStringExtra("planta")
        mPlanta.setText(plantaValue)
        val turnoValue = intent.getStringExtra("turno")
        mTurno.setText(turnoValue)
        val telarValue = intent.getStringExtra("telar")
        mTelar.setText(telarValue)

        //creacion del bitmap del rectangulo
        val bitmap: Bitmap = Bitmap.createBitmap(100, 1, Bitmap.Config.ARGB_8888)
        val canvas: Canvas = Canvas(bitmap) //propiedad  grafica del bitmap

        //variable para dibujar
        val shapeDrawable: ShapeDrawable
        //Obtén los valores de translationX y translationY del elemento <View>
        val translationXR = mRedRectangle.translationX+17
        val translationYR = mRedRectangle.translationY+185
//        //mio
//        val translationXR = mRedRectangle.translationX-30
//        val translationYR = mRedRectangle.translationY+65

        // Dibuja el rectángulo en el lienzo
        shapeDrawable = ShapeDrawable(RectShape()) // se inicializa el grafico del rectangulo
        shapeDrawable.paint.color = Color.TRANSPARENT // Color de fondo transparente
        shapeDrawable.setBounds(0, 0, mRedRectangle.width, mRedRectangle.height) //tamaño
        shapeDrawable.paint.style = Paint.Style.STROKE // Establecer el estilo del trazo
        shapeDrawable.paint.strokeWidth = 10f // Ancho del trazo rojo
        shapeDrawable.paint.color = Color.RED // Color del trazo rojo
        shapeDrawable.draw(canvas)//se inicializa para pintar

        mRedRectangle.background = shapeDrawable//se pinta en su view

        // Asigna los valores a translationY y translationx
        this.translationY = translationYR
        this.translationx = translationXR

        //evento para capturar imagen
        mCaptureButton.setOnClickListener {
            captureImage()
        }
        //***************************************************************************************************************
    }//onCreate

    //boton back
    override fun onBackPressed() {
        //incia a la interfaz 1
        val uno = Intent(this, MainActivity::class.java)
        startActivity(uno)
    }
    //***************************************************************************************************************
    override fun onResume() {
        super.onResume()
        //verifica permios de camara
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera()//incializa camara
        } else {
            //realiza la peticion de permisos de camara en caso de que no se hayan aceptado anteriormente
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
        }
    }

    //se incializa la camara
    private fun startCamera() {
        //se incluye el reconocimeinto a la hora de incializar l acamara
        val textRecognizer = TextRecognizer.Builder(applicationContext).build()

        if (!textRecognizer.isOperational) {
            // se Maneja el caso en el que el reconocimiento de texto no esté disponible
            Toast.makeText(this, "verifique google play", Toast.LENGTH_SHORT).show()
            return
        }
        //se incializa la camara y el reconcimeinto en la textura
        mCameraSource = CameraSource.Builder(applicationContext, textRecognizer)
            //se confiigura la camara a usar
            //camara trasera
            .setFacing(CameraSource.CAMERA_FACING_BACK)
            //se incializa el enfoque automatico
            .setAutoFocusEnabled(true)
            //se crea
            .build()

        //se manejan las excepciones
        mCameraView.holder.addCallback(object : SurfaceHolder.Callback {
            @SuppressLint("MissingPermission")
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    mCameraSource?.start(holder)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
            //se para la camara para evitar fugaz de rendimiento en segundo plano por parte de la aplicacion
            override fun surfaceDestroyed(holder: SurfaceHolder) {
                mCameraSource?.stop()
            }
        })
    }
    //funcion para tomar la foto
    private fun captureImage() {
        //toma la foto del momento en tiempo real sobre la textura
        mCameraSource?.takePicture(null, CameraSource.PictureCallback { data ->
            // Obtén las dimensiones y posición del rectángulo rojo en el View
            val redRect = getRedRectangleDimensions()


            // Ajusta la región de interés (ROI) en función del rectángulo rojo y el scaleFactor
            val rect = Rect(
                ((redRect.left + translationx)* scaleFactorX).toInt(),
                ((redRect.top + translationY) * scaleFactorY).toInt(),
                ((redRect.right +translationx)* scaleFactorX).toInt(),
                ((redRect.bottom + translationY) * scaleFactorY).toInt()
            )
            // Procesa la imagen con la ROI y aplica el filtro de blanco y negro
            processImage(data, rect)
        })
    }
    //funcion para procesar la imagen y el reconcimiento de texto
    private fun processImage(imageData: ByteArray, rect: Rect) {
        //se pausa la visiblidad del botton para evitar crasheos
        mCaptureButton.visibility = View.INVISIBLE
        // Obtener la imagen completa capturada
        val options = BitmapFactory.Options()
        val fullBitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size, options)

        // Asegurarse de que las coordenadas estén dentro de los límites de la imagen completa
        val imageWidth = fullBitmap.width
        val imageHeight = fullBitmap.height
        //se ajustan los parametros
        if (rect.left < 0) rect.left = 0
        if (rect.top < 0) rect.top = 0
        if (rect.right > imageWidth) rect.right = imageWidth
        if (rect.bottom > imageHeight) rect.bottom = imageHeight

        // Calcular la región de interés (ROI) en la imagen completa
        val roiBitmap = Bitmap.createBitmap(
            fullBitmap,
            rect.left,
            rect.top,
            rect.width(),
            rect.height()
        )

        // Aplicar el filtro de tonalidad azul:
        val blueBitmap = applyBlueFilter(roiBitmap)
        mImageView.setImageBitmap(blueBitmap)

        // Crear un nuevo frame con la imagen de la ROI
        val frame = Frame.Builder()
            .setBitmap(roiBitmap)
            .build()

        // Detectar texto en el frame
        val textRecognizer = TextRecognizer.Builder(applicationContext).build()
        val textBlocks = textRecognizer.detect(frame)
        val text = StringBuilder()

        val numberPattern = "\\d+" // Expresión regular para buscar números

        for (i in 0 until textBlocks.size()) {
            val textBlock = textBlocks.valueAt(i)
            val blockText = textBlock.value
            // Reemplazar 'i', 'I' y 'l' por '1'
            val replacedText = blockText.replace("[iIl]".toRegex(), "1")

            // Filtrar solo números usando expresión regular
            val numbersOnly = replacedText.replace("[^0-9]".toRegex(), "")

            text.append(numbersOnly)
        }

        // El texto resultante contiene solo números
        val datos: String = text.toString()
        //se añaden los parametros al paquete
        val parametros = Bundle()
        parametros.putString("datos", datos)
        //ademas de obtener los datos a enviar
        val plantaValue1 = mPlanta.text.toString()
        val turnoValue1 = mTurno.text.toString()
        val TelarValue1 = mTelar.text.toString()
        //se delacra el evento para enviar la informacion y comenzar la navegacion
        val intent = Intent(this, FormUno::class.java)

        intent.putExtra("FROM", "FROM_2") // Agrega esta línea para identificar el origen
        intent.putExtras(parametros)    //se agregan los valores al paquete
        intent.putExtra("planta", plantaValue1)
        intent.putExtra("turno", turnoValue1)
        intent.putExtra("telar", TelarValue1)
        //comienza la navegacion y el envio de datos
        startActivity(intent,
            ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    }
    //se comienzan los ajustes para el filtro a usar
    //esto con el fin de obtener el mejor ajuste de visualizacion y evitar errores por causas naturales
    private fun applyBlueFilter(bitmap: Bitmap): Bitmap {
        //se ajusta el filtro
        val width = bitmap.width
        val height = bitmap.height
        //se crea la capa del filtro
        val blueBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        //se le da la propiedad de CANVA
        val canvas = Canvas(blueBitmap)
        //Se pinta
        val paint = Paint()

        // Crear una matriz de color para darle una tonalidad
        val colorMatrix = ColorMatrix()
        //se pensaba hacelo de color azul para resalar el rojo
        //pero hubomejores resultados con un filtro en blanco y negro
        colorMatrix.set(floatArrayOf(
//            0f, 0f, 1.2f, 0f, 0f,     // Red (sin cambios)
            0.333f, 0.333f, 0.333f, 0f, 0f,     // Red (cero)
            0.333f, 0.333f, 0.333f, 0f, 0f,     // Green (cero)
            0.333f, 0.333f, 0.333f, 0f, 0f,     // Blue (cero)
            0f, 0f, 0f, 1f, 0f      // Alpha (sin cambios)
        ))

        //se pinta el filtro con este ajuste
        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        //se le otrogan klas medidas del rectangulo
        val rect = Rect(0, 0, width, height)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        //el return
        return blueBitmap
    }

    //se obtendran los tamaños del rectangulo
    private fun getRedRectangleDimensions(): Rect {
        // Obtener las dimensiones del rectángulo rojo desde el View
        val rect = Rect()
        mRedRectangle.getDrawingRect(rect)
        return rect
    }
    //se verifican los permisos de camara
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (ContextCompat.checkSelfPermission(
                this,
                //se valida la decicion de asignacion
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "Otorga los permisos necesarios por favor", Toast.LENGTH_SHORT).show()
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
        } else {
            //se inicializa la camara
            startCamera()
        }
    }
}