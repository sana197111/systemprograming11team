# SPDX-FileCopyrightText: 2021 ladyada for Adafruit Industries
# SPDX-License-Identifier: MIT
import os
import signal
import subprocess
import time
import busio
import board
import adafruit_amg88xx
from yeelight import Bulb

yeelight_ip = "192.168.223.60"
bulb = Bulb(yeelight_ip)

i2c = busio.I2C(board.SCL, board.SDA)
amg = adafruit_amg88xx.AMG88XX(i2c)

# 온도 임계값 설정
human_detected_temperature_threshold = 30  # 사람이 있다고 가정할 온도 (섭씨 기준)
subprocess.call(["g++", "server.cpp", "-o", "output_file"])

counte = 0
temp=0
proc = None
while True:
    detected_pixels = 0  # 임계값을 초과하는 픽셀 카운터 초기화

    for row in amg.pixels:
        for temp in row:
            if temp >= human_detected_temperature_threshold:
                detected_pixels += 1

    # 사람 감지 여부 출력
    
    if detected_pixels >= 3:
        print("사람이 감지되었습니다.")
        if counte == 0:
            subprocess.call(["./output_file"])
            counte += 1
            proc = subprocess.Popen(["./output_file"], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
            temp=1
        
        # C 파일 컴파일
        # subprocess.call(["gcc", "server.c", "-o", "output_file"])
        # 컴파일된 프로그램 실행
        # subprocess.call(["./output_file"])
    else:
        print("사람이 감지되지 않았습니다.")
        # bulb.turn_on()
        # bulb.set_rgb(100, 100,0)
        
        if counte != 0 :
            pid = proc.pid
            os.kill(pid, signal.SIGINT)
            print("좋은 꿈 꿔요")
            temp = 0
        counte = 0
    time.sleep(2)
