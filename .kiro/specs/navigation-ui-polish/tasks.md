# Tasks: navigation-ui-polish

## Task List

- [x] 1. Crear archivos de animación faltantes
  - [x] 1.1 Crear `res/anim/slide_in_left.xml` (entrada desde la izquierda, -100% → 0%, 250ms, fast_out_slow_in)
  - [x] 1.2 Crear `res/anim/slide_out_right.xml` (salida hacia la derecha, 0% → 30%, 250ms, fast_out_slow_in)

- [x] 2. Crear NavigationAnimationHelper
  - [x] 2.1 Crear `NavigationAnimationHelper.kt` en el paquete `com.example.valkyria` con constantes de posición y función `getAnimForDirection(fromPos, toPos): AnimPair`

- [x] 3. Aplicar animaciones en Baul_contrasenas
  - [x] 3.1 Añadir `bottomNav.selectedItemId = R.id.nav_home` en `onCreate`
  - [x] 3.2 Reemplazar el listener de la bottom nav para usar `NavigationAnimationHelper` y `overridePendingTransition`
  - [x] 3.3 Unificar la animación del FAB (`btn_add`) para usar el mismo helper (eliminar el uso de `MaterialSharedAxis`)

- [x] 4. Aplicar animaciones en generador_contra
  - [x] 4.1 Reemplazar el listener de la bottom nav para usar `NavigationAnimationHelper`
  - [x] 4.2 Eliminar el `overridePendingTransition` del método `finish()` sobreescrito (ya no es necesario con el helper)

- [x] 5. Aplicar animaciones en Configuracion
  - [x] 5.1 Reemplazar el listener de la bottom nav para usar `NavigationAnimationHelper`

- [x] 6. Rediseñar layout del Generador — Header y fondo
  - [x] 6.1 Cambiar el fondo del `ConstraintLayout` raíz a `#101322`
  - [x] 6.2 Reemplazar el header actual (back + título centrado) por el header estilo Baúl: logo `icono_inicio_sesion` + "Valkyria" bold + subtítulo "Generador" en gris

- [x] 7. Rediseñar layout del Generador — Tarjeta de contraseña
  - [x] 7.1 Cambiar el background de la tarjeta de contraseña a `@drawable/bg_card`
  - [x] 7.2 Añadir etiqueta "TU CONTRASEÑA" con `textColor="@color/gris_1"` y `textStyle="bold"` encima de la tarjeta
  - [x] 7.3 Asegurar que los botones copiar y refresh usan `@drawable/bg_icono` (36×36dp, padding 6dp)

- [x] 8. Rediseñar layout del Generador — Controles
  - [x] 8.1 Añadir `android:progressTint="@color/azul_1"` y `android:thumbTint="@color/azul_1"` al SeekBar
  - [x] 8.2 Cambiar el `TextView` del número de longitud a `textColor="@color/azul_1"` y `textStyle="bold"`
  - [x] 8.3 Cambiar el background de la tarjeta de switches a `@drawable/bg_card`
  - [x] 8.4 Cambiar los `Switch` a `com.google.android.material.switchmaterial.SwitchMaterial`
  - [x] 8.5 Cambiar el background de los campos `EditText` (nombre app, correo) a `@drawable/bg_entrada_redondeado`
  - [x] 8.6 Reemplazar el `Button` de guardar por `MaterialButton` con `backgroundTint="@color/azul_1"`, `cornerRadius="12dp"`, `fontFamily="@font/inter_font"`, `textAllCaps="false"`

- [x] 9. Implementar lógica funcional del Generador
  - [x] 9.1 Implementar función `generatePassword(config: GeneratorConfig): String` en `generador_contra.kt`
  - [x] 9.2 Conectar `SeekBar` con el `TextView` de longitud (listener `setOnSeekBarChangeListener`)
  - [x] 9.3 Conectar cada `SwitchMaterial` para regenerar la contraseña al cambiar estado
  - [x] 9.4 Implementar validación: si el usuario intenta desactivar el último switch activo, revertir y mostrar `Toast`
  - [x] 9.5 Conectar botón copiar al `ClipboardManager` (mismo patrón que `PasswordAdapter`)
  - [x] 9.6 Conectar botón refresh para llamar a `generatePassword` y actualizar el `TextView`
