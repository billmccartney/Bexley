# Bexley
This a simple home control project. It's main purpose is to have an app to locally control our
living romm PC. There are two main parts, a server (written in python and runs on the PC) and a
remote control app.

Currently the server is just a simple http server written in http://flask.pocoo.org/.

The app actually just makes HTTP post requests to send different commands to the server.