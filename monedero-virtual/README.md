# Monedero Virtual con sistema de Puntos

Proyecto Final · Programación I · Universidad del Quindío (2026-2)

## Cómo ejecutarlo

- **Desde IntelliJ:** abrir la carpeta como proyecto Maven y ejecutar `App` (o el goal `javafx:run`).
- **Desde consola:** `mvn clean javafx:run`
- **Pruebas unitarias:** `mvn test` (26 pruebas JUnit 5)

Requiere JDK 17 o superior. Las dependencias (JavaFX 21 y JUnit 5) las descarga Maven.

## Estructura

```
src/main/java/co/edu/uniquindio/poo/
├── App.java            instancia estática de SistemaMonedero, métodos openX y datos de ejemplo
├── model/              clases del dominio, enums, interfaces y excepciones personalizadas
├── controller/         puente entre las vistas y el modelo
└── viewController/     un controlador por cada vista FXML
src/main/resources/co/edu/uniquindio/poo/
└── primary, cliente, transacciones, puntos, programadas y reportes (.fxml)
src/test/java/co/edu/uniquindio/poo/
└── TransaccionTest, PuntosTest, ReversionTest, AnalisisTest, ProgramadaTest
```

## Datos de ejemplo al iniciar

| Cliente | Situación |
|---|---|
| Ana Gómez | 2 monederos, historial de 3 meses, 101 puntos (puede canjear el beneficio de 100) |
| Luis Pérez | Rango PLATA, 2 monederos, una transferencia semanal programada ya vencida |
| Carla Ruiz | Saldo bajo (alerta generada) |

Las alertas por correo, SMS y WhatsApp son simuladas: se imprimen en la consola y quedan en "Reportes y alertas".

## Supuestos del sistema

- Comisión del 1% en retiros y del 2% en transferencias; los depósitos no cobran.
- Alerta de saldo bajo cuando un monedero queda por debajo de 100.
- Beneficios por rango (descuento en comisión / bono de puntos): Plata 5%/5%, Oro 10%/10%, Platino 20%/15%.
- El descuento del 10% en transferencias aplica a las 5 transferencias siguientes.
- El rango se calcula con los puntos acumulados; canjear puntos no baja el rango.
- Una transacción es atípica si su monto supera promedio + 2 desviaciones estándar del historial (mínimo 3 transacciones previas).
