# MKLoRa LW007-PIR Android SDK

Native Android SDK and demo app for LW007-PIR devices. Supports BLE scanning, connection, protocol parameter read/write, device-initiated disconnect notifications, LoRa configuration, PIR motion detection (settings and real-time status), Hall sensor, temperature and humidity, battery consumption statistics, debugger log export, and Nordic DFU firmware updates.

Cross-platform reference (same protocol): [MKLoRa-LW007-PIR-Flutter](https://github.com/MKLoRa/MKLoRa-LW007-PIR-Flutter.git).


---

## Requirements

| Item | Description |
|------|-------------|
| Android Studio | 3.6+ (8.x recommended) |
| minSdk | 28 |
| compileSdk | 35 |
| Device | Physical device required (emulators do not support BLE) |

---

## Project Structure

```
LW007_Android/
├── app/                 # Demo app (scan, connect, configure, DFU, full UI)
├── mokosupport/         # BLE SDK module (primary integration dependency)
│   ├── LoRaLW007MokoSupport.java   # Connect, send commands, Notify control, event callbacks
│   ├── MokoBleScanner.java           # Scanning
│   ├── OrderTaskAssembler.java       # Read/write task assembly (API entry)
│   └── entity/
│       ├── ParamsKeyEnum.java        # Protocol parameter keys (CHAR_PARAMS)
│       └── ControlKeyEnum.java       # Control channel keys (CHAR_CONTROL)
```

Communication has three stages: **scan → connect → command exchange**. The SDK reports connection status and command results via **EventBus** (you can switch to another bus in `LoRaLW007MokoSupport`).

Protocol traffic uses two GATT characteristics:

| Characteristic | UUID suffix | Purpose |
|----------------|-------------|---------|
| `CHAR_PARAMS` | `AA05` | Device parameters (PIR enable, LoRa region, heartbeat, etc.) |
| `CHAR_CONTROL` | `AA06` | Control commands (time sync, battery info, PIR status read, etc.) |

Real-time sensor data uses dedicated Notify characteristics (`CHAR_PIR`, `CHAR_HALL_STATUS`, `CHAR_TH`, `CHAR_LOG`).

---

## Integrating the SDK

### 1. Add the module

Copy `mokosupport` into your project root and add to `settings.gradle`:

```gradle
include ':app', ':mokosupport'
```

In the app module `build.gradle`:

```gradle
dependencies {
    implementation project(path: ':mokosupport')
}
```

### 2. Initialize

Initialize in `Application.onCreate()` or your first Activity:

```java
LoRaLW007MokoSupport.getInstance().init(getApplicationContext());
```

### 3. Permissions

`mokosupport` declares base BLE permissions in its `AndroidManifest.xml`. On Android 6.0+, scanning requires **runtime location permission**; on Android 12+, also request `BLUETOOTH_SCAN` and `BLUETOOTH_CONNECT`.

```java
// Example: request location (required for scanning)
if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
    ActivityCompat.requestPermissions(this,
            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
            REQUEST_CODE_LOCATION);
}
```

### 4. Register EventBus

Connection status, command results, and Notify data are delivered via EventBus. Register in your Activity/Fragment:

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EventBus.getDefault().register(this);
}

@Override
protected void onDestroy() {
    EventBus.getDefault().unregister(this);
    super.onDestroy();
}
```

---

## 1. Scanning for Devices

### Core classes

| Class | Description |
|-------|-------------|
| `MokoBleScanner` | Start/stop scanning |
| `MokoScanDeviceCallback` | Scan started, per-device callback, scan stopped |
| `DeviceInfoParseable` | Advertisement parser interface; demo impl: `BeaconInfoParseableImpl` |

Scan filters by Service Data UUID: `0000aa05-...` (`OrderServices.SERVICE_ADV`).

### Code example

```java
MokoBleScanner scanner = new MokoBleScanner(context);
BeaconInfoParseableImpl parser = new BeaconInfoParseableImpl();

scanner.startScanDevice(new MokoScanDeviceCallback() {
    @Override
    public void onStartScan() {
        // Clear list, refresh UI
    }

    @Override
    public void onScanDevice(DeviceInfo deviceInfo) {
        AdvInfo adv = parser.parseDeviceInfo(deviceInfo);
        if (adv == null) return;
        // adv.mac / adv.name / adv.rssi
        // adv.deviceType / adv.txPower
        // adv.battery / adv.needPassword
        // adv.connectable
        // adv.isShowTH / adv.temp / adv.humidity (from manufacturer data)
    }

    @Override
    public void onStopScan() {
        // Stop animation, etc.
    }
});

// Stop scanning (call before connecting)
scanner.stopScanDevice();
```

### Advertisement fields (`BeaconInfoParseableImpl`)

- **Service Data** UUID `0000aa05`: `deviceType` (1 byte)
- **Manufacturer data** (14 bytes): battery flag, temperature, humidity, TX power, password flag
- `needPassword` — connection password enabled (`true` → call `setPassword` after connect)
- `isShowTH` — `true` when temperature/humidity values are valid in the advertisement
- `mac`, `name`, `rssi`, `connectable`

---

## 2. Connecting to a Device

### Connect

Only the device **MAC address** is required (from scan result `adv.mac`):

```java
// Stop scanning before connecting
scanner.stopScanDevice();
LoRaLW007MokoSupport.getInstance().connDevice(mac);
```

### Connection status (EventBus)

```java
@Subscribe(threadMode = ThreadMode.MAIN)
public void onConnectStatusEvent(ConnectStatusEvent event) {
    String action = event.getAction();
    if (MokoConstants.ACTION_DISCONNECTED.equals(action)) {
        // GATT disconnected (failed connect, link lost, manual disconnect, etc.)
    }
    if (MokoConstants.ACTION_DISCOVER_SUCCESS.equals(action)) {
        // Service discovery done; commands can be sent
    }
}
```

### Password verification

If advertisement has `needPassword == true`, send the password after service discovery:

```java
if (MokoConstants.ACTION_DISCOVER_SUCCESS.equals(action)) {
    List<OrderTask> tasks = new ArrayList<>();
    tasks.add(OrderTaskAssembler.setPassword("123456"));
    LoRaLW007MokoSupport.getInstance().sendOrder(tasks.toArray(new OrderTask[]{}));
}
```

Result in `ACTION_ORDER_RESULT` for `OrderCHAR.CHAR_PASSWORD`: `value[4] == 1` success, `0` failure — then call `disConnectBle()`.

Devices without password can proceed to your UI right after `ACTION_DISCOVER_SUCCESS`.

### Manual disconnect

```java
LoRaLW007MokoSupport.getInstance().disConnectBle();
```

---

## 3. Reading and Writing Parameters

### Task queue

All reads/writes are wrapped as `OrderTask`, created by `OrderTaskAssembler`, and sent via `sendOrder` **in queue order**. Default timeout per task is 3 seconds.

```java
// Single task (parameter channel)
LoRaLW007MokoSupport.getInstance().sendOrder(OrderTaskAssembler.getLoraRegion());

// Control channel read
LoRaLW007MokoSupport.getInstance().sendOrder(OrderTaskAssembler.getPIR());

// Multiple tasks (executed in order)
List<OrderTask> tasks = new ArrayList<>();
tasks.add(OrderTaskAssembler.getPIREnable());
tasks.add(OrderTaskAssembler.getPIRReportInterval());
LoRaLW007MokoSupport.getInstance().sendOrder(tasks.toArray(new OrderTask[]{}));
```

See `OrderTaskAssembler.java` for the full list of `getXxx` / `setXxx` methods (device info, LoRa, PIR, Hall, T&H, heartbeat, battery, etc.).

### Protocol frame format

**Parameter channel** (`CHAR_PARAMS`) frame layout:

```
ED [flag] [cmd] [len] [data...]
```

| Field | Description |
|-------|-------------|
| `0xED` | Frame header |
| `flag` | `0x00` read, `0x01` write |
| `cmd` | 1 byte, maps to `ParamsKeyEnum` (e.g. `KEY_PIR_ENABLE` = `0x30`) |
| `len` | Payload length |
| `data` | Payload; for write ACK, `data[0] == 1` means success |

**Control channel** (`CHAR_CONTROL`) uses the same `0xED` header; `cmd` maps to `ControlKeyEnum` (e.g. `KEY_PIR` = `0x58`, `KEY_BATTERY_INFO` = `0x5C`).

### Command results (EventBus)

```java
@Subscribe(threadMode = ThreadMode.MAIN)
public void onOrderTaskResponseEvent(OrderTaskResponseEvent event) {
    String action = event.getAction();
    OrderTaskResponse response = event.getResponse();

    if (MokoConstants.ACTION_ORDER_TIMEOUT.equals(action)) {
        // Timeout; check response.orderCHAR for which task
    }
    if (MokoConstants.ACTION_ORDER_FINISH.equals(action)) {
        // All queued tasks finished
    }
    if (MokoConstants.ACTION_ORDER_RESULT.equals(action)) {
        OrderCHAR orderCHAR = (OrderCHAR) response.orderCHAR;
        byte[] value = response.responseValue;
        // Parse value for CHAR_PARAMS or CHAR_CONTROL ...
    }
    if (MokoConstants.ACTION_CURRENT_DATA.equals(action)) {
        // Device-initiated Notify (disconnect, PIR status, Hall, T&H, log data, etc.)
    }
}
```

### Parsing read responses (parameter channel)

```java
if (MokoConstants.ACTION_ORDER_RESULT.equals(action)
        && (OrderCHAR) response.orderCHAR == OrderCHAR.CHAR_PARAMS) {
    byte[] value = response.responseValue;
    if (value.length < 5) return;

    int header = value[0] & 0xFF;   // 0xED
    int flag = value[1] & 0xFF;     // 0x00 = read
    int cmd = value[2] & 0xFF;
    if (header != 0xED) return;

    ParamsKeyEnum key = ParamsKeyEnum.fromParamKey(cmd);
    int length = value[3] & 0xFF;
    if (flag == 0x00 && key != null && length > 0) {
        byte[] payload = Arrays.copyOfRange(value, 4, 4 + length);
        switch (key) {
            case KEY_LORA_REGION:
                int region = payload[0] & 0xFF;
                break;
            case KEY_LORA_MODE:
                int mode = payload[0] & 0xFF; // 1=ABP, 2=OTAA
                break;
            case KEY_PIR_ENABLE:
                int pirEnable = payload[0] & 0xFF;
                break;
            case KEY_PIR_REPORT_INTERVAL:
                int interval = payload[0] & 0xFF; // 1~60 minutes
                break;
            // ...
        }
    }
}
```

### Parsing control channel read responses

```java
if ((OrderCHAR) response.orderCHAR == OrderCHAR.CHAR_CONTROL) {
    byte[] value = response.responseValue;
    int header = value[0] & 0xFF;
    int flag = value[1] & 0xFF;
    int cmd = value[2] & 0xFF;
    ControlKeyEnum key = ControlKeyEnum.fromParamKey(cmd);
    int length = value[3] & 0xFF;
    if (header == 0xED && flag == 0x00 && key == ControlKeyEnum.KEY_PIR && length > 0) {
        int pirStatus = value[4] & 0xFF; // 1 = motion detected, 0 = not detected
    }
}
```

### Example 1: Read LoRa region

```java
LoRaLW007MokoSupport.getInstance().sendOrder(OrderTaskAssembler.getLoraRegion());
// In callback for KEY_LORA_REGION: region 0~12 maps to AS923, AU915, EU868, etc.
```

### Example 2: Configure PIR parameters

```java
List<OrderTask> tasks = new ArrayList<>();
tasks.add(OrderTaskAssembler.setPIREnable(1));           // 0=off, 1=on
tasks.add(OrderTaskAssembler.setPIRReportInterval(5));   // 1~60 (minutes)
tasks.add(OrderTaskAssembler.setPIRSensitivity(2));      // 1~3
tasks.add(OrderTaskAssembler.setPIRDelayTime(1));        // 1~3
LoRaLW007MokoSupport.getInstance().sendOrder(tasks.toArray(new OrderTask[]{}));
```

### Example 3: Sync UTC time (control channel)

```java
LoRaLW007MokoSupport.getInstance().sendOrder(OrderTaskAssembler.setTime());
```

### Example 4: Batch read device info

```java
List<OrderTask> tasks = new ArrayList<>();
tasks.add(OrderTaskAssembler.getDeviceModel());      // GATT 0x2A24
tasks.add(OrderTaskAssembler.getSoftwareVersion());  // GATT 0x2A28
tasks.add(OrderTaskAssembler.getFirmwareVersion());  // GATT 0x2A26
tasks.add(OrderTaskAssembler.getBattery());          // control KEY_BATTERY
tasks.add(OrderTaskAssembler.getMacAddress());       // control KEY_MAC
LoRaLW007MokoSupport.getInstance().sendOrder(tasks.toArray(new OrderTask[]{}));
```

Some writes require a reboot to take effect:

```java
LoRaLW007MokoSupport.getInstance().sendOrder(OrderTaskAssembler.restart());
```

---

## 4. PIR / Hall / T&H Real-Time Notify

Besides queued read/write tasks, LW007-PIR exposes sensor status on dedicated Notify characteristics. Enable them after connect when entering the corresponding screen.

| Method | Characteristic | Purpose |
|--------|----------------|---------|
| `enablePIRNotify()` / `disablePIRNotify()` | `CHAR_PIR` (`AA02`) | PIR motion status push |
| `enableHallStatusNotify()` / `disableHallStatusNotify()` | `CHAR_HALL_STATUS` (`AA03`) | Hall sensor status |
| `enableTHNotify()` / `disableTHNotify()` | `CHAR_TH` (`AA04`) | Temperature & humidity push |
| `enableLogNotify()` / `disableLogNotify()` | `CHAR_LOG` (`AA07`) | Debugger log stream |

### PIR real-time status (Notify)

```java
LoRaLW007MokoSupport.getInstance().enablePIRNotify();

// In ACTION_CURRENT_DATA when orderCHAR == CHAR_PIR:
// Frame: ED 02 01 01 [status]
// status: 1 = motion detected, 0 = motion not detected (0xFF = hidden in demo)
```

### PIR settings read (on screen open)

```java
List<OrderTask> tasks = new ArrayList<>();
tasks.add(OrderTaskAssembler.getPIR());              // control channel: current status
tasks.add(OrderTaskAssembler.getPIREnable());
tasks.add(OrderTaskAssembler.getPIRReportInterval());
tasks.add(OrderTaskAssembler.getPIRSensitivity());
tasks.add(OrderTaskAssembler.getPIRDelayTime());
LoRaLW007MokoSupport.getInstance().sendOrder(tasks.toArray(new OrderTask[]{}));
```

Hall and T&H settings follow the same pattern via `HallSettingsActivity` and `THSettingsActivity` (`getHallStatusEnable` / `setHallStatusEnable`, `getTHEnable` / `setTHSampleRate`, threshold alarms, etc.). See `OrderTaskAssembler` and the demo activities for full APIs.

---

## 5. Disconnect Notifications

Handle two kinds of disconnect events separately.

### 5.1 BLE link disconnect (`ConnectStatusEvent`)

Triggered when the device powers off, goes out of range, connection fails, or you call `disConnectBle()`:

```java
if (MokoConstants.ACTION_DISCONNECTED.equals(action)) {
    // Close config UI, return to scan page, restart startScanDevice
}
```

### 5.2 Device-initiated disconnect Notify (`CHAR_DISCONNECTED_NOTIFY`)

After connect, the SDK enables Notify on characteristic `0000AA01`. The device may push a frame before disconnecting; receive it in `ACTION_CURRENT_DATA`:

```java
if (MokoConstants.ACTION_CURRENT_DATA.equals(action)) {
    OrderCHAR orderCHAR = (OrderCHAR) response.orderCHAR;
    if (orderCHAR == OrderCHAR.CHAR_DISCONNECTED_NOTIFY) {
        byte[] value = response.responseValue;
        // Fixed 5 bytes: ED 02 01 01 [type]
        if (value.length == 5
                && (value[0] & 0xFF) == 0xED
                && (value[1] & 0xFF) == 0x02
                && (value[2] & 0xFF) == 0x01
                && (value[3] & 0xFF) == 0x01) {
            int type = value[4] & 0xFF;
            // 1 = password verification timeout
            // 2 = password changed successfully (reconnect required)
            // 3 = no data exchange for 3 minutes
            // 4 = reboot successful (reconnect required)
            // 5 = factory reset successful (reconnect required)
        }
    }
}
```

`ACTION_DISCONNECTED` usually follows. The demo shows a dialog in `DeviceInfoActivity` based on `type`, then `finish()` back to the scan page.

**During DFU**, ignore disconnect dialogs (demo uses `isUpgrade` flag in `SystemInfoActivity`).

---

## 6. DFU Firmware Update

The demo uses the **Nordic Android DFU Library**. UI entry: **Device → System Information → DFU**.

### Dependencies

The demo pulls Nordic DFU via `MKLoRaUILib` or project dependencies. If you only integrate `mokosupport`, add it in your app module, for example:

```gradle
dependencies {
    implementation 'no.nordicsemi.android:dfu:2.3.0'
}
```

Use the version that matches your successful demo build (check transitive versions in `app/build/outputs/logs/manifest-merger-*-report.txt`).

Register the service in `AndroidManifest.xml`:

```xml
<service android:name="com.moko.lw007.service.DfuService" />
```

`DfuService` extends `DfuBaseService` (see `app/.../service/DfuService.java`).

### Flow

1. Connected and device MAC read (`OrderTaskAssembler.getMacAddress()`)
2. User selects a **`.zip`** firmware package
3. Start DFU with MAC (no need to keep the original GATT session; device reboots when done)
4. Show progress via `DfuProgressListener`
5. Return to scan page and reconnect

### Code example

```java
// Register listener
DfuServiceListenerHelper.registerProgressListener(context, mDfuProgressListener);

// After selecting zip
DfuServiceInitiator starter = new DfuServiceInitiator(deviceMac)
        .setDeviceName(deviceName)
        .setKeepBond(false)
        .setForeground(false)
        .disableMtuRequest()
        .setDisableNotification(true);
starter.setZip(null, firmwareFilePath);
starter.start(context, DfuService.class);

// Listener example
private final DfuProgressListener mDfuProgressListener = new DfuProgressListenerAdapter() {
    @Override
    public void onProgressChanged(String address, int percent, float speed,
            float avgSpeed, int currentPart, int partsTotal) {
        // Progress: percent%
    }

    @Override
    public void onDfuCompleted(String deviceAddress) {
        // Success — prompt user to scan and reconnect
    }

    @Override
    public void onError(String deviceAddress, int error, int errorType, String message) {
        // Upgrade failed
    }
};

@Override
protected void onDestroy() {
    DfuServiceListenerHelper.unregisterProgressListener(context, mDfuProgressListener);
    super.onDestroy();
}
```

Notes:

- Firmware must be a valid non-empty **ZIP** file
- Call `disConnectBle()` before upgrading to avoid conflicting with normal BLE traffic
- Abort DFU if `onDeviceConnecting` retries more than 3 times (see `SystemInfoActivity`)

---

## 7. Typical Flow

```
Scan page (LoRaLW007MainActivity)
  ├─ MokoBleScanner.startScanDevice
  ├─ Parse advertisements → device list (T&H preview when isShowTH)
  ├─ connDevice(mac)
  ├─ [optional] setPassword
  └─ DeviceInfoActivity
       ├─ LoRa / General / BLE / Device tabs
       ├─ sendOrder read/write parameters (CHAR_PARAMS / CHAR_CONTROL)
       ├─ LoRa: LoRaConnSettingActivity, LoRaConnSettingNewActivity, LoRaAppSettingActivity
       ├─ General: heartbeat; PIRSettingsActivity, HallSettingsActivity, THSettingsActivity
       ├─ BLE: advertisement name, TX power, connectable, password
       ├─ Device: low-power payload, device mode, etc.
       ├─ SystemInfoActivity
       │    ├─ BatteryConsumeActivity → battery / PIR duration statistics
       │    ├─ ExportDataActivity → debugger log sync (CHAR_LOG Notify)
       │    ├─ SelfTestActivity / SelfTestNewActivity
       │    └─ DFU → back to scan and reconnect
       ├─ ACTION_CURRENT_DATA → disconnect Notify / PIR / Hall / T&H / log
       └─ ACTION_DISCONNECTED → link lost
```

---

## 8. Core Classes Quick Reference

| Stage | Class | Role |
|-------|-------|------|
| Scan | `MokoBleScanner` | Scan control |
| Scan | `MokoScanDeviceCallback` | Scan callbacks |
| Scan | `BeaconInfoParseableImpl` | Parse LW007-PIR advertisements |
| Connect | `LoRaLW007MokoSupport` | Connect, send commands, Notify enable/disable |
| Comm | `OrderTaskAssembler` | Build read/write tasks |
| Comm | `ParamsKeyEnum` | Parameter channel command keys |
| Comm | `ControlKeyEnum` | Control channel command keys |
| Event | `ConnectStatusEvent` | Connected / disconnected |
| Event | `OrderTaskResponseEvent` | Command results, Notify data |

---

## 9. Notes

1. **Permissions**: Android 6.0+ requires runtime location for scanning; Android 12+ needs `BLUETOOTH_SCAN` / `BLUETOOTH_CONNECT`.
2. **EventBus**: The SDK posts events internally. To use LiveData/RxJava instead, change `orderFinish` / `orderTimeout` / `orderResult` / `orderNotify` in `LoRaLW007MokoSupport`.
3. **Logging**: The SDK uses `XLog` with file output and storage permission. To disable file logging, keep only `XLog.init(config)` in `BaseApplication`.
4. **Parameters**: Each `ParamsKeyEnum` maps to `OrderTaskAssembler` methods. When adding parameters, extend `ParamsReadTask` / `ParamsWriteTask` accordingly. Control commands extend `ControlReadTask` / `ControlWriteTask`.
5. **Notify lifecycle**: Call `disablePIRNotify()` (and similar) in `onDestroy()` when leaving sensor screens to reduce idle traffic.
6. **Demo references**: Scan/connect — `LoRaLW007MainActivity`; LoRa — `LoRaConnSettingActivity`, `LoRaFragment`; PIR/Hall/T&H — `PIRSettingsActivity`, `HallSettingsActivity`, `THSettingsActivity`; parameters — `DeviceInfoActivity`, `DeviceFragment`, `GeneralFragment`, `BleFragment`; battery — `BatteryConsumeActivity`; log export — `ExportDataActivity`; DFU — `SystemInfoActivity`.

---

## Changelog

| Date | Version | Notes |
|------|---------|-------|
| 2021.03.11 | mokosupport 1.0 | Initial release |
| — | mokosupport 4.0 | compileSdk 35, minSdk 28 |
