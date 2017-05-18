
# react-native-brptouchprinter

## Getting started

`$ npm install react-native-brptouchprinter --save`

### Mostly automatic installation

`$ react-native link react-native-brptouchprinter`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-brptouchprinter` and add `RNBrptouchprinter.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNBrptouchprinter.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.reactlibrary.RNBrptouchprinterPackage;` to the imports at the top of the file
  - Add `new RNBrptouchprinterPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-brptouchprinter'
  	project(':react-native-brptouchprinter').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-brptouchprinter/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-brptouchprinter')
  	```


## Usage
```javascript
import RNBrptouchprinter from 'react-native-brptouchprinter';

// TODO: What to do with the module?
RNBrptouchprinter;
```
  