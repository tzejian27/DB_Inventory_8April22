package com.example.db_inventory;

import com.example.db_inventory.reader.SerialPort;

public class RFID_UhfReaderDevice {

  private static RFID_UhfReaderDevice readerDevice;
  private static SerialPort devPower;

  public static RFID_UhfReaderDevice getInstance()
  {
    if (devPower == null)
    {
      try
      {
        devPower = new SerialPort();
      }
      catch (Exception e)
      {
        return null;
      }
      // devPower.psampoweron();
    }

    if (readerDevice == null) {
      readerDevice = new RFID_UhfReaderDevice();
    }

    return readerDevice;
  }

  public void powerOn()
  {
    //devPower.psampoweron();
  }

  public void powerOff()
  {
    if (devPower != null) {
      //devPower.psampoweroff();
      devPower = null;
    }
  }
}
