
package com.reactlibrary;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.uimanager.IllegalViewOperationException;


import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Color;
import android.graphics.Canvas;

import com.brother.ptouch.sdk.LabelInfo;
import com.brother.ptouch.sdk.Printer;
import com.brother.ptouch.sdk.PrinterInfo;
import com.brother.ptouch.sdk.PrinterInfo.ErrorCode;
import com.brother.ptouch.sdk.PrinterInfo.Model;
import com.brother.ptouch.sdk.PrinterStatus;
import com.brother.ptouch.sdk.NetPrinter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.util.Iterator;
import com.google.gson.Gson;


public class RNBrptouchprinterModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;
    Bitmap ImageToPrint;


    PrinterStatus printResult;

  public RNBrptouchprinterModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNBrptouchprinter";
  }

    public Bitmap textAsBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); //ascent is negative
        int width = (int) (paint.measureText(text) + 0.5f); //round
        int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap image =  Bitmap.createBitmap(width + 500, height + 350, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawRect(0, 0, width + 500, height + 350, paint);
        paint.setColor(textColor);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }

    protected class PrinterThread extends Thread {
        @Override
        public void run() {
            //print
            //tobe removed
            Printer myPrinter = new Printer();
            printResult = new PrinterStatus();
            //call startCommunication prior to PrintImage or other print functions to open a socket connection
            //and keep the number connections to one
            myPrinter.startCommunication();
            //call printImage to print a Bitmap
            printResult = myPrinter.printImage(ImageToPrint);
            if(printResult.errorCode != PrinterInfo.ErrorCode.ERROR_NONE) {
                //add an alert message if you want
            }
            //When done printing call endCommunication
            myPrinter.endCommunication();
        }
    }

    @ReactMethod
    public void print(String ipAddress, String name, String position, String company, final Promise promise) {
        /*Thread trd = new Thread(new Runnable() {
            @Override
            public void run() {

            }
        });*/
        Printer myPrinter = new Printer();
        //create printer info
        PrinterInfo myPrinterInfo = new PrinterInfo();
        try{
            //set printer information
            myPrinterInfo = myPrinter.getPrinterInfo();
            myPrinterInfo.printerModel   = PrinterInfo.Model.QL_720NW;
            myPrinterInfo.port           = PrinterInfo.Port.NET;
            myPrinterInfo.printMode      = PrinterInfo.PrintMode.FIT_TO_PAGE;
            myPrinterInfo.orientation    = PrinterInfo.Orientation.LANDSCAPE;
            myPrinterInfo.paperSize      = PrinterInfo.PaperSize.CUSTOM;
            myPrinterInfo.ipAddress      = ipAddress;
            myPrinterInfo.labelNameIndex = LabelInfo.QL700.valueOf("W38H90").ordinal();
            myPrinterInfo.isAutoCut      = true;
            myPrinterInfo.isCutAtEnd     = true;
            myPrinterInfo.isHalfCut      = false;
            myPrinterInfo.isSpecialTape  = false;
            myPrinter.setPrinterInfo(myPrinterInfo);

            String FullName = "M Syazwan Ahmad";
            ImageToPrint = textAsBitmap(FullName, 96, Color.BLACK);
            printResult = new PrinterStatus();
            //call startCommunication prior to PrintImage or other print functions to open a socket connection
            //and keep the number connections to one
            myPrinter.startCommunication();
            //call printImage to print a Bitmap
            printResult = myPrinter.printImage(ImageToPrint);
            if(printResult.errorCode != PrinterInfo.ErrorCode.ERROR_NONE) {
                //add an alert message if you want
                promise.resolve(printResult.errorCode.toString());
            }
            else{
                promise.resolve("Printing started!");
            }
            //When done printing call endCommunication
            //myPrinter.endCommunication();
        }
        catch(Exception e)
        {
            promise.reject("error", e);
        }
        //run printing on a thread
        //PrinterThread printThread = new PrinterThread();
        //printThread.run();

        //trd.start();
    }

    @ReactMethod
    public void getConnectedPrinters(Promise promise)
    {
        Gson gson = new Gson();
        Printer myPrinter = new Printer();
        NetPrinter[] net_printers = myPrinter.getNetPrinters("QL-720NW");
        try{
            WritableArray array = new WritableNativeArray();
            for(NetPrinter net_printer : net_printers) {
                WritableMap netPrinterDictionary = convertJsonToMap(new JSONObject(gson.toJson(net_printer)));
                array.pushMap(netPrinterDictionary);
            }
            promise.resolve(array);
        }catch(JSONException e)
        {
            promise.reject("",e);
        }
    }

    @ReactMethod
    public void concatStr(
                          String string1,
                          String string2,
                          Promise promise) {
        promise.resolve(string1 + " jhj" + string2);
    }

    private WritableMap convertJsonToMap(JSONObject jsonObject) throws JSONException {
        WritableMap map = new WritableNativeMap();

        Iterator<String> iterator = jsonObject.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Object value = jsonObject.get(key);
            if (value instanceof JSONObject) {
                map.putMap(key, convertJsonToMap((JSONObject) value));
            } else if (value instanceof JSONArray) {
                map.putArray(key, convertJsonToArray((JSONArray) value));
                if(("option_values").equals(key)) {
                    map.putArray("options", convertJsonToArray((JSONArray) value));
                }
            } else if (value instanceof Boolean) {
                map.putBoolean(key, (Boolean) value);
            } else if (value instanceof Integer) {
                map.putInt(key, (Integer) value);
            } else if (value instanceof Double) {
                map.putDouble(key, (Double) value);
            } else if (value instanceof String)  {
                map.putString(key, (String) value);
            } else {
                map.putString(key, value.toString());
            }
        }
        return map;
    }

    private WritableArray convertJsonToArray(JSONArray jsonArray) throws JSONException {
        WritableArray array = new WritableNativeArray();

        for (int i = 0; i < jsonArray.length(); i++) {
            Object value = jsonArray.get(i);
            if (value instanceof JSONObject) {
                array.pushMap(convertJsonToMap((JSONObject) value));
            } else if (value instanceof JSONArray) {
                array.pushArray(convertJsonToArray((JSONArray) value));
            } else if (value instanceof Boolean) {
                array.pushBoolean((Boolean) value);
            } else if (value instanceof Integer) {
                array.pushInt((Integer) value);
            } else if (value instanceof Double) {
                array.pushDouble((Double) value);
            } else if (value instanceof String)  {
                array.pushString((String) value);
            } else {
                array.pushString(value.toString());
            }
        }
        return array;
    }
}
