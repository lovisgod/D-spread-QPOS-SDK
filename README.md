### Payble QPOS SDK Integration Documentation

The Payble QPOS SDK allows for seamless integration of QPOS device functionalities into your Android application. Below are the steps to integrate and use the SDK effectively:

#### 1. Initialize the Payble QPOS SDK

In your `Application` class, initialize the SDK using the `initPayble` method:

```kotlin
class SampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        QposInitializer.getInstance().initPayble("merchantKey", "merchantId", applicationContext)
    }
}
```

#### 2. Initialize the QPOS Device

Before interacting with the QPOS device, you need to initialize it. This can be done in your `MainActivity` or any other activity:

```kotlin
QposInitializer.getInstance().initializeQpos(SampleApplication(), this, this)
```

#### 3. Get Agent Details

You can fetch the agent details anytime before or after initializing the QPOS device:

```kotlin
QposInitializer.getInstance().getAgentDetails("agentId", this, this)
```

#### 4. Search for Devices

To search for available QPOS devices:

```kotlin
QposInitializer.getInstance().searchForDevices(this)
```

#### 5. Connect to a Device

Once a device is found, you can connect to it using its Bluetooth address:

```kotlin
QposInitializer.getInstance().connectBluetoothDevice("30:3D:51:46:CF:C6")
```

#### 6. Set Transaction Parameters and Start a Transaction

To perform a transaction, you first need to set the transaction parameters and then start the trade:

```kotlin
QposInitializer.getInstance().setTransactionParam("10000", "566")
QposInitializer.getInstance().startTrade()
```

### Event Handling

The SDK provides several callback methods to handle different EMV events, such as card insertion, card reading, transaction processing, and more. Here’s an example implementation in `MainActivity`:

```kotlin
class MainActivity : AppCompatActivity(), EMVEvents {
    // Override various EMV event methods
    override fun onInsertCard() { /* handle card insertion */ }
    override fun onRemoveCard(isContactlessTransLimit: Boolean, message: String) { /* handle card removal */ }
    override fun onCardRead(pan: String, cardType: PaybleConstants.CardType) { /* handle card read */ }
    override fun onCardDetected(contact: Boolean) { /* handle card detected */ }
    override fun onEmvProcessing(message: String) { /* handle EMV processing */ }
    override fun onPinInput(): String? { return "1234" }  // Example of returning a fixed PIN
    override fun onEmvProcessed(data: Any) { /* handle processed EMV data */ }
    // Other event methods...
}
```

### Conclusion

This documentation provides a basic overview of how to integrate the Payble QPOS SDK into your Android application. By following these steps, you can ensure a smooth integration process, enabling your app to handle device connections, transactions, and EMV events effectively.
