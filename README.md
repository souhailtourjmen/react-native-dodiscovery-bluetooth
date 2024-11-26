# react-native-dodiscovery-bluetooth


2. Connecting Method2.1 Bluetooth ConnectionConnect Bluetooth :int context,String portSetting) P:“ ,”+Bluetooth address Example:Print.PortOpen(context,“Bluetooth,”+MAC) MAC: Bluetooth address of printerReturn:0: connection success-1: connection failureDisconnect Bluetooth:Example:Print.PorClose()Return:True: disconnection success False: disconnection failure Whether Bluetooth is connected: PortOpen(Context arameter  context:Contextobject.  portSetting:Bluetooth 6 Example:Print.IsOpened()Return:True: Bluetooth connected False: Bluetooth unconnected2.2 WIFI ConnectionConnect WIFI:int PortOpen(Context context,String portSetting) Example: Print.PortOpen(context,“WiFi,”+IP+”,”+PortNumber) IP: IP address of printerPortNumber: port Def

## Installation

```sh
npm install react-native-dodiscovery-bluetooth
```

## Usage


```js
import { multiply } from 'react-native-dodiscovery-bluetooth';

// ...

const result = await multiply(3, 7);
```


## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)

