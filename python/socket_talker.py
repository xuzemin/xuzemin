#!/usr/bin/env python
 
import threading
import time
import rospy
from std_msgs.msg import String
import socket, traceback
#ADD by xuzemin start
host = ''
port = 58888
 
ser = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
ser.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
ser.bind((host, port))

pub = rospy.Publisher('chatter', String, queue_size=10)

ADDRESS = 0

def callback(data):
  rospy.loginfo(rogpy.get_caller_id()+"I heard %s",data.data)
  print "Got data from: ", data.data
  
#ADD by xuzemin end

#ADD by xuzemin start
def talker(name):
    
    hello_str = "hello world " +name
    rospy.loginfo(name)
    pub.publish(name)

def server():
  global ADDRESS
  while 1:
      try:
          message, address = ser.recvfrom(8192)
          print "Got data from", address, ": ", message
          print ADDRESS
          if ADDRESS == 0:
            if message== 'connect':
              ser.sendto("get", address);
              talker(message)
              ADDRESS = address
              timer = threading.Timer(10,close)
              timer.start()
            else :
              ADDRESS = 0
              ser.sendto("please connect", address);
          elif ADDRESS == address:
              if message== 'connect':
                  ser.sendto("get", address);
              elif message== 'heartbeat' :
                  ser.sendto("heartbeat", address);
                  talker(message)
                  timer.cancel()
                  timer = threading.Timer(10,close)
                  timer.start()
              elif message== 'left' :
                  ser.sendto("left", address);
                  talker(message)
              elif message== 'right' :
                  ser.sendto("right", address);
                  talker(message)
              elif message== 'down' :
                  ser.sendto("down", address);
                  talker(message)
              elif message== 'up' :
                  ser.sendto("up", address);
                  talker(message)
              elif message== 'stop' :
                  ser.sendto("stop", address);
                  talker(message)
              elif message== 'left' :
                  ser.sendto("left", address);
                  talker(message)
              elif message== 'cancel' :
                  ser.sendto("cancel", address);
                  talker(message)
                  ADDRESS = 0
                  timer.cancel()
              else :
                  ser.sendto("please input correct", address);
                  ADDRESS = 0
                  timer.cancel()
          else:
              ser.sendto("isconnected", address);
      except (KeyboardInterrupt, SystemExit):
          raise
      except:
          traceback.print_exc()

def close():
    global ADDRESS
    print 'hello timer'
    print ADDRESS
    ADDRESS = 0
    print ADDRESS
    talker("break")
    
    
#ADD by xuzemin end
def main():
  rospy.init_node('scan_to_angle')
  server()
  
if __name__ == '__main__':
  main()
  

