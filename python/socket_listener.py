#!/usr/bin/env python
import rospy
from std_msgs.msg import String


#ADD by xuzemin start
def callback(data):
    rospy.loginfo(rospy.get_caller_id() + "I heard %s", data.data)
    
def listener():

    rospy.Subscriber("chatter", String, callback)
    rospy.spin()

if __name__ == '__main__':
    rospy.init_node('calibrate_angular', anonymous=False)
    listener()
