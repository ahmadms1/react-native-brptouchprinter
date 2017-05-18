
import { NativeModules, Platform } from 'react-native';

const { RNBrptouchprinter } = NativeModules;

const component = NativeModules.RNBrptouchprinter;

class BRPtouchPrinter {
    async print(ipAddress, name, position, company) {
      return await component.print(ipAddress, name, position, company);
    }

    async getConnectedPrinters() {
      return await component.getConnectedPrinters();
    }
}

export default BRPtouchPrinter;
