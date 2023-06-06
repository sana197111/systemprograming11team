import time
from yeelight import Bulb

yeelight_ip = "192.168.223.60"
bulb = Bulb(yeelight_ip)

bulb.turn_off()
bulb.set_brightness(50)


# def light_toggle(bulb) :
	# global x
	# bulb.get_properties()
	# dic = bulb.get_properties()
	# dic['power']
	# if x ==1:
	#	x=0
	#	if dic["power"] == "off":
	#		bulb.turn_on()
	#		time.sleep(5)
