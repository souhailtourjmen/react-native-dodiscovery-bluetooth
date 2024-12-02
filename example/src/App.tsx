import { useState } from 'react';
import { StyleSheet, View, Text, FlatList, Button, Alert } from 'react-native';
import {
  startDiscovery,
  // getPairedDevices,
  cancelDiscovery,
  scanBluetoothDevices,
} from 'react-native-dodiscovery-bluetooth';

export default function App() {
  // const [pairedDevices, setPairedDevices] = useState([]);
  const [discoveredDevices, setDiscoveredDevices] = useState<any>([]);
  const [isDiscovering, setIsDiscovering] = useState(false);
  // Récupérer les appareils appariés
  // const fetchPairedDevices = () => {
  //   getPairedDevices()
  //     .then((devices) => {
  //       setPairedDevices(devices);
  //     })
  //     .catch((error) => {
  //       Alert.alert(
  //         'Erreur',
  //         `Erreur lors de la récupération des appareils appariés: ${error}`
  //       );
  //     });
  // };

  // Démarrer la découverte des appareils
  const handleStartDiscovery = async () => {
    setIsDiscovering(true);
    startDiscovery()
      .then((deviceInfo) => {
        setDiscoveredDevices((prevDevices: any) => [
          ...prevDevices,
          deviceInfo,
        ]);
      })
      .catch((error) => {
        Alert.alert(
          'Erreur',
          `Erreur lors de la découverte des appareils: ${error}`
        );
      });
  };

  // Annuler la découverte des appareils
  const handleCancelDiscovery = () => {
    cancelDiscovery()
      .then(() => {
        setIsDiscovering(false);
      })
      .catch((error) => {
        Alert.alert(
          'Erreur',
          `Erreur lors de l'annulation de la découverte: ${error}`
        );
      });
  };

  // useEffect(() => {
  //   fetchPairedDevices();
  // }, []);
  const [devices, setDevices] = useState([]);

  const scanDevices = async () => {
    try {
      const foundDevices = await scanBluetoothDevices();
      setDevices(foundDevices);
    } catch (error) {
      console.error('Error scanning devices:', error);
    }
  };

  // const connectToDevice = async (mac: string) => {
  //   try {
  //     const response = await connectToDevice(mac);
  //     console.log('Connected successfully:', response);
  //   } catch (error) {
  //     console.error('Connection failed:', error);
  //   }
  // };
  console.log('Connects,', devices);
  return (
    <View style={styles.container}>
      <Text style={styles.title}>Appareils Bluetooth</Text>

      {/* Liste des appareils appariés */}
      <Text style={styles.subtitle}>Appareils Appariés</Text>
      <Button title="Scan Bluetooth Devices" onPress={scanDevices} />

      {/* Boutons pour démarrer et annuler la découverte */}
      <Button
        title={
          isDiscovering ? 'Annuler la découverte' : 'Démarrer la découverte'
        }
        onPress={isDiscovering ? handleCancelDiscovery : handleStartDiscovery}
      />

      {/* Liste des appareils découverts */}
      <Text style={styles.subtitle}>Appareils Découverts</Text>
      <FlatList
        data={discoveredDevices}
        keyExtractor={(item) => item.address}
        renderItem={({ item }) => (
          <View style={styles.deviceItem}>
            <Text>{item.name}</Text>
            <Text>{item.address}</Text>
          </View>
        )}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 20,
    backgroundColor: '#fff',
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    textAlign: 'center',
    marginBottom: 20,
  },
  subtitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginTop: 20,
  },
  deviceItem: {
    padding: 10,
    marginVertical: 5,
    backgroundColor: '#f0f0f0',
    borderRadius: 5,
  },
});
