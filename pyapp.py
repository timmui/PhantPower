from Tkinter import Tk, BOTH, StringVar
from ttk import Frame, Button, Style, Label
import requests
import wmi
from ctypes import *

toggleURL = "https://api.spark.io/v1/devices/53ff71066667574855462567/relayControl?access_token=ae10c87f15c67cb2148a85125f8ee64e0f7b0d43";
onURL = "https://api.spark.io/v1/devices/53ff71066667574855462567/turnOn?access_token=ae10c87f15c67cb2148a85125f8ee64e0f7b0d43";
offURL = "https://api.spark.io/v1/devices/53ff71066667574855462567/turnOff?access_token=ae10c87f15c67cb2148a85125f8ee64e0f7b0d43";
statusURL = "https://api.spark.io/v1/devices/53ff71066667574855462567/getRelay?access_token=ae10c87f15c67cb2148a85125f8ee64e0f7b0d43";

class Example(Frame):
    def __init__(self, parent):
        Frame.__init__(self, parent)   
         
        self.parent = parent
        
        self.initUI()
        
    def initUI(self):
        global onButton
        global powerclass
        self.parent.title("Toggle")
        self.style = Style()
        self.style.theme_use("default")
        self.style.configure("TFrame", background="white")

        self.pack(fill=BOTH, expand=1)

        toggleButton = Button(self, text="Toggle", 
            command=self.toggleURL)
        toggleButton.place(x=50, y=50)
        
        response = requests.post(statusURL)
        #print type(response.text)
        respUni = response.text
        statusStr = respUni.encode('ascii', 'ignore')[-3:-1]
        if int(statusStr) == 1:
            buttonText = "on"
        elif int(statusStr) == 0:
            buttonText = "off"
        else:
            buttonText = "error"
        onButton = Button(self, text=buttonText,
            command=self.onURL)
        onButton.place(x=100, y=50)
        
        self.status = StringVar()
        label = Label(self, textvariable = self.status)
        label.place(x=0, y=0)
        
        statusButton = Button(self, text="status",
            command=self.statusURL)
        statusButton.place(x=150, y=50)
        
        powerclass = PowerClass()
        result = windll.kernel32.GetSystemPowerStatus(byref(powerclass))
        print "battery class"
        print type(powerclass.BatteryLifePercent)
        
        self.battery = StringVar()
        labelBat = Label(self, textvariable = self.battery)
        label.place(x=0, y = 50)
        
    def toggleURL(self):
        requests.post(toggleURL)
        
    def onURL(self):
        requests.post(onURL)
        #print dir(onButton["text"])
        if onButton["text"] == "on":
            requests.post(offURL)
            onButton["text"] = "off"
        elif onButton["text"] == "off":
            requests.post(onURL)
            onButton["text"] = "on"
    
    def offURL(self):
        requests.post(offURL)
     
    def statusURL(self):
        response = requests.post(statusURL)
        #print type(response.text)
        respUni = response.text
        statusStr = respUni.encode('ascii', 'ignore')[-3:-1]
        #print statusStr
        if int(statusStr) == 1:
            self.status.set("on")
            self.battery.set(str(powerclass.BatteryLifePercent))
        elif int(statusStr) == 0:
            self.status.set("off")
            self.battery.set(str(powerclass.BatteryLifePercent))
        else:
            self.status.set("error")

    #def updateStatus(self, label):
    
class PowerClass(Structure):
    _fields_ = [('ACLineStatus', c_byte),
            ('BatteryFlag', c_byte),
            ('BatteryLifePercent', c_byte),
            ('Reserved1',c_byte),
            ('BatteryLifeTime',c_ulong),
            ('BatteryFullLifeTime',c_ulong)]        
    
def main():
  
    root = Tk()
    root.geometry("250x150+300+300") 
    app = Example(root)
    root.mainloop()  


if __name__ == '__main__':
    main()  
        