from flask import Flask, render_template, request, jsonify
import win32api
import win32con
import keystrokes
import time
import webbrowser
import threading
import atexit
import os

#dummy thread instance for simple exit
backgroundThread = threading.Thread()

def myexit():
  global backgroundThread
  backgroundThread.cancel()

atexit.register(myexit)

#time.sleep(2)
#keystrokes.typer("T")
app = Flask(__name__)
@app.route("/")
def hello():
  return "Hello World!"

@app.route("/cmd", methods=['GET', 'POST'])
def cmd():
  print "method = ",request.method
  if request.method == 'POST':
    data = request.get_json()
    print data
    if(data["cmd"] == "mouse"):
      a = win32api.GetCursorPos()
      a = (int(a[0] - data["x"]), int(a[1] - data["y"]))
      win32api.SetCursorPos(a)
      print "mouse"
    elif(data["cmd"] == "keys"):
      keystrokes.typer(data["keys"])
    elif(data["cmd"] == "scroll"):
      a = win32api.GetCursorPos()
      win32api.mouse_event(win32con.MOUSEEVENTF_WHEEL, a[0], a[1], 10*int(data["y"]), 0)
    elif(data["cmd"] == "click"):
      if(data["keys"] == 1):
        win32api.mouse_event(win32con.MOUSEEVENTF_LEFTDOWN,0,0)
        win32api.mouse_event(win32con.MOUSEEVENTF_LEFTUP,0,0)
      elif(data["keys"] == 2):
        win32api.mouse_event(win32con.MOUSEEVENTF_RIGHTDOWN,0,0)
        win32api.mouse_event(win32con.MOUSEEVENTF_RIGHTUP,0,0)
    elif(data["cmd"] == "url"):
        webbrowser.open(data["url"])
    elif(data["cmd"] == "browser"):
      command = data["command"]
      if(command == "Escape"):
        keystrokes.press("esc")
      elif(command == "Back"):
        keystrokes.press("browser_back")
      elif(command == "Forward"):
        keystrokes.press("browser_forward")
      elif(command == "Refresh"):
        keystrokes.press("browser_refresh")
      elif(command == "Close Tab"):
        keystrokes.pressHoldRelease("ctrl","w")
      elif(command == "UnClose Tab"):
        keystrokes.pressHoldRelease("ctrl","shift","t")
      elif(command == "New Tab"):
        keystrokes.pressHoldRelease("ctrl","t")
      elif(command == "Next Tab"):
        keystrokes.pressHoldRelease("ctrl","tab")
      else:
        print "writing ",command
    return jsonify({"ok":1})
  else:
    return "JSON RPC Only..."

@app.route("/shutdown", methods=['GET', 'POST'])
def shutdown():
  if request.method == 'POST':
    def internalshutdown():
      os.system("shutdown.exe /h")
    t = threading.Timer(2.0, internalshutdown) #it waits 2 seconds -- hopefully the response is already sent!
    t.start()
    return jsonify({"ok":1})
if __name__ == "__main__":
    app.run(host= '0.0.0.0')

