
#import "RNBrptouchprinter.h"
#import <UIKit/UIKit.h>
//#import "BRPtouchPrinterKit.h"
#import "BRPtouchPrinterKitW.framework/Headers/BRPtouchPrinterKit.h"

@implementation RNBrptouchprinter
- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}


- (UIImage *)imageFromString:(NSString *)string attributes:(NSDictionary *)attributes size:(CGSize)size
{
    UIGraphicsBeginImageContextWithOptions(size, NO, 0);
    [string drawInRect:CGRectMake(0, 0, size.width, size.height) withAttributes:attributes];
    UIImage *image = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    
    return image;
}



RCT_EXPORT_MODULE()

RCT_EXPORT_METHOD(print:(NSString *)ipAddress
                  name:(NSString *)name
                  position:(NSString *)position
                  company:(NSString *)company
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject) {
    BRPtouchPrintInfo* printInfo;
    
    printInfo = [[BRPtouchPrintInfo alloc] init];
    
    printInfo.strPaperName = @"38mmx90mm";
    printInfo.nPrintMode = PRINT_FIT;
    printInfo.nDensity = 0;
    printInfo.nOrientation = ORI_LANDSCAPE;
    printInfo.nHalftone = HALFTONE_ERRDIF;
    
    printInfo.nHorizontalAlign = ALIGN_CENTER;
    printInfo.nVerticalAlign = ALIGN_MIDDLE;
    
    printInfo.nAutoCutFlag = 1;
    printInfo.nAutoCutCopies = 1;
    
    BRPtouchPrinter* ptp;
    //NSArray *printerNames = @[@"Brother QL-720NW"];
    ptp = [[BRPtouchPrinter alloc]
           initWithPrinterName:@"Brother QL-720NW"
           interface: CONNECTION_TYPE_WLAN];
    [ptp setIPAddress:ipAddress];
    
    [ptp setPrintInfo:printInfo];
    
    CGImageRef imgRef;
    NSString *test = name;
    NSString *test1 = [test stringByAppendingString:@"\n"];
    NSString *test2 = [test1 stringByAppendingString:position];
    test = [test2 stringByAppendingString:@"\n"];
    test2 = [test stringByAppendingString:company];
    
    NSString *string = test2;
    
    NSDictionary *attributes = @{NSFontAttributeName : [UIFont systemFontOfSize:18],
                                 NSForegroundColorAttributeName : [UIColor blackColor],
                                 NSBackgroundColorAttributeName : [UIColor clearColor]};
    
    //convert text to image
    UIImage *image2 = [self imageFromString:string attributes:attributes size:CGSizeMake(237,100)];//(237,100)];
    
    imgRef = [image2 CGImage];
    BOOL startCommunicationResult = [ptp startCommunication];
    NSLog(startCommunicationResult ? @"comm is Yes" : @"comm is No");
    if (startCommunicationResult) {
        //print
        BOOL printerReadiness = [ptp isPrinterReady];
        NSLog(printerReadiness ? @"Printer is ready" : @"Printer is not ready");
        if(printerReadiness){
            NSLog(@"Printing....");
            int printImageReturnCode = [ptp printImage:imgRef copy:1];
            NSLog(@"%d", printImageReturnCode);
        }
        
    }
    [ptp endCommunication];

   /*  NSLog(@"Communication started");
    int64_t delayInSeconds = 8.0;
    dispatch_time_t popTime = dispatch_time(DISPATCH_TIME_NOW, delayInSeconds * NSEC_PER_SEC);
    dispatch_after(popTime, dispatch_get_main_queue(), ^(void){
        NSLog(@"Communication waited..printing started..");
        

    });

    
    delayInSeconds = 15.0;
    popTime = dispatch_time(DISPATCH_TIME_NOW, delayInSeconds * NSEC_PER_SEC);
    dispatch_after(popTime, dispatch_get_main_queue(), ^(void){
        NSLog(@"_Printed!..ending comm..");
        [ptp endCommunication];
    });
*/
    //BRPtouchPrinterData *data;
    //NSLog(@"_brotherDeviceList [%@]",(NSMutableArray*)[data getPrinterList]);
     //NSLog(@"beforeStartSearch");
    /*_networkManager = [[BRPtouchNetworkManager alloc] init];
    [_networkManager setPrinterName:@"QL"];
    _networkManager.isEnableIPv6Search = YES;
    [_networkManager startSearch:10.0];
    int64_t delayInSeconds = 10.0;
    dispatch_time_t popTime = dispatch_time(DISPATCH_TIME_NOW, delayInSeconds * NSEC_PER_SEC);
    dispatch_after(popTime, dispatch_get_main_queue(), ^(void){
         NSLog(@"_brotherDeviceList [%@]",(NSMutableArray*)[_networkManager getPrinterNetInfo]);
    });
   */
    
    resolve(string);
}

RCT_EXPORT_METHOD(getConnectedPrinters:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    _networkManager = [[BRPtouchNetworkManager alloc] initWithPrinterNames:@[@"Brother QL-720NW",]];
    _networkManager.isEnableIPv6Search = YES;
    [_networkManager setDelegate:self];
    NSString *startSeacrhResult = [NSString stringWithFormat:@"%d",[_networkManager startSearch:5]];
    //NSLog(startSeacrhResult);
    int64_t delayInSeconds = 7.0;
    dispatch_time_t popTime = dispatch_time(DISPATCH_TIME_NOW, delayInSeconds * NSEC_PER_SEC);
    dispatch_after(popTime, dispatch_get_main_queue(), ^(void){
        NSMutableArray* printers = [[NSMutableArray alloc] init];
        NSMutableArray* connectedPrinters = (NSMutableArray*)[_networkManager getPrinterNetInfo];
        for (BRPtouchDeviceInfo *printer in connectedPrinters) {
            NSDictionary *dict = [[NSDictionary alloc] initWithObjectsAndKeys:
                                  printer.strModelName, @"modelName",
                                  printer.strSerialNumber, @"serNo",
                                  printer.strIPAddress, @"ipAddress",
                                  printer.strMACAddress, @"macAddress",
                                  printer.strNodeName, @"nodeName"];
            [printers addObject:dict];
        }
        resolve(printers);
    });
}

//delegate method
-(void)didFinishSearch:(id)sender
{
    NSLog(@"didFinishedSearch");
    NSLog(@"_brotherDeviceList [%@]",(NSMutableArray*)[_networkManager getPrinterNetInfo]);
    return;
}


@end
