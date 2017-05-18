
#if __has_include("RCTBridgeModule.h")
#import "RCTBridgeModule.h"
#else
#import <React/RCTBridgeModule.h>
#endif


#import "BRPtouchPrinterKitW.framework/Headers/BRPtouchNetworkManager.h"

@interface RNBrptouchprinter : NSObject <RCTBridgeModule, BRPtouchNetworkDelegate>
{
    BRPtouchNetworkManager *_networkManager;
}

@end
  
