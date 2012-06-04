from CrcMoose import *
import serial
import getopt, sys, os
import struct, math
from datetime import *
import csv
import time
import sys, traceback

# This file must be configured according to your computer.
# A total number of three open TODOs are marked below

class Plugwise:
  def __init__(self):

    # TODO 1/3: define address   -vvv-   of serial Plugwise dongle here
    self.serial = serial.Serial("COM4", "115200", timeout=1)

    self.HEADER = '\x05\x05\x03\x03'
    self.ENDLINE = '\x0d\x0a'
    self.CALIBRATIONCODE = '0026'
    self.POWERCHANGECODE = '0017'
    self.POWERINFOCODE = '0012'
    self.CALIBRATIONRESPONSECODE = '0027'
    self.POWERINFORESPONSECODE = '0013'
    self.NODEOFFLINERESPONECODE = '00E1'
    self.macaddresses = []
    self.localCSVFiles = {}
    self.lastPowerMeasure1 = {}
    self.lastPowerMeasure8 = {}
    self.logFiles = {}
    self.createdNewCSVs = "false"

  def SetCollectParams(self):

    # TODO 2/3: define address of text file with device identifiers (only last 6 digit) here
    devices = open('C:\Python27\Plugwise\devices.dat', 'rb')

    for line in devices.readlines():
      line.split()
      self.macaddresses.append(line.strip())
      self.localCSVFiles[line.strip()] = None
      self.lastPowerMeasure1[line.strip()] = 0
      self.lastPowerMeasure8[line.strip()] = 0	
      rowsList = []
      self.logFiles[line.strip()] = rowsList
	 
  def createCSVs(self):
    today = timestamp = datetime.now().strftime("%Y.%m.%d")

    # TODO 3/3: Change following path to the destination where CSV files should be collected
    for key in self.localCSVFiles.keys():
      obj = open('C:\Python27\Plugwise\dev' + "_"+ key[10:] + "_" + today + ".csv", 'ab')
      self.localCSVFiles[key] = csv.writer(obj, delimiter=';', quotechar='"')
			
  def writteToCSV(self, mac, row):
    self.logFiles[mac].append(row)		
    self.localCSVFiles[mac].writerow(row)
	
  def pulseToKWH(self, pulses):
    output = (pulses / 1) / 468.9385193;
    return output

  def pulseToWatt(self, pulses):
    result = self.pulseToKWH(pulses) * 1000
    return result

  def GetPowerInfo(self, device):
    self.SendCommand(self.POWERINFOCODE + device)
    result = self.GetResult(self.POWERINFORESPONSECODE)
        
    if(result == "-1"):
      return result
		   
    mac = result[0:16]
		
    if (self.macaddresses.count(mac) == 0):
      return "-1"
      
    try:
      resultint1s = self.hexToInt(result[16:20])
      resultint8s = self.hexToInt(result[20:24]) / 8 
    
      r1 = self.pulseToWatt(resultint1s)
      r2 = self.pulseToWatt(resultint8s)
        
      intedRound1 = int(round(r1))
      intedRound2 = int(round(r2))
    except:
      print '-'*60
      print "hexToInt in GetPowerInfo:", result
      print '-'*60
      return "-1"

    timestamp = datetime.now().strftime("%d/%m/%Y %H:%M:%S")
    checkTime = datetime.now().strftime("%H")

    if (checkTime == "00" and self.createdNewCSVs == "false"): 
      for key in self.logFiles.keys():
        self.localCSVFiles[key].writerows(self.logFiles[key])
        self.logFiles[key] = []
      self.createCSVs()
      self.createdNewCSVs = "true"
		   
    if (checkTime != "00" and self.createdNewCSVs != "false"):
      self.createdNewCSVs = "false"
      
    if(intedRound1 > 5000 or intedRound2 > 5000):
      print '-'*60
      print "1s:", intedRound1, "8s:", intedRound2
      print '-'*60
      intedRound1 = self.lastPowerMeasure1[mac]
      intedRound2 = self.lastPowerMeasure8[mac]
			
    row = [timestamp, intedRound1, intedRound2]
    self.writteToCSV(mac, row)
    self.lastPowerMeasure1[mac] = intedRound1
    self.lastPowerMeasure8[mac] = intedRound2
    print mac[10:], timestamp, intedRound1, intedRound2
        
  def SetPowerState(self, newstate):
    self.SendCommand(self.POWERCHANGECODE + self.macaddress + newstate)
    self.serial.readline()

  def GetCRC16(self, value):
    value = CRC16X.calcString(value)
    format = ("%%0%dX" % ((CRC16X.width + 3) // 4))
    return format % value

  def hexToFloat(self, hexstr):
    intval = int(hexstr, 16)
    bits = struct.pack('L', intval)
    return struct.unpack('f', bits)[0]

  def hexToInt(self, hexstr):
    return int(hexstr, 16)

  def SendCommand(self, command):
    self.serial.write(self.HEADER + command + self.GetCRC16(command) + \
    self.ENDLINE )

  def GetResult(self, responsecode):
    readbytes = 0
        
    if responsecode == self.POWERINFORESPONSECODE:
      readbytes = 28
    elif responsecode == "0000":
      readbytes = 0

    data = ''
		
    while 1:
      check = data
      data += self.serial.read(1)
		
      if data.endswith(responsecode):
        data = self.serial.read(readbytes)
        return data[4:]

      if (data == check):
        return "-1"

  def endProgram(self):
    print "Closing Serial Port.."
    self.serial.close()
    sys.exit(0)

# Program main function. Periodically polls Plugwise sensors
def main():
  
  plugwise = Plugwise()
  plugwise.SetCollectParams()
  plugwise.createCSVs()

  try:
    while True:
      for s in plugwise.macaddresses:
        time.sleep(0.1)
        tBefore = time.clock()
        try:
          plugwise.GetPowerInfo(s)
        except (KeyboardInterrupt, SystemExit): plugwise.endProgram()
        except: traceback.print_exc(file=sys.stdout)
        tAfter = time.clock()
        tDiff = tAfter - tBefore
        print "Elapsed time: " + str(tDiff)
        print
  except KeyboardInterrupt:
    plugwise.endProgram()
		  
main()
