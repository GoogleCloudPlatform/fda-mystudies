import os, sys
import numpy as np
import json
import matplotlib.pyplot as plt
from scipy.signal import butter, lfilter


def butter_bandpass(lowcut, highcut, fs=100, order=5):
    nyq = 0.5 * fs
    low = lowcut / nyq
    high = highcut / nyq
    b, a = butter(order, [low, high], btype='band')
    return b, a

def butter_bandpass_filter(data, lowcut, highcut, fs=100, order=5):
    b, a = butter_bandpass(lowcut, highcut, fs, order=order)
    y = lfilter(b, a, data)
    return y


lowcut_global = 15
highcut_global = 25

# Parse the json
json_file = "cameraHeartRate_motion.json"
x_axis = []
y_axis = []
z_axis = []

json_file_camera = "cameraHeartRate_rgb.json"
red = []
parsed_json_camera = {}
with open(json_file_camera) as data_file:
	parsed_json_camera = json.load(data_file)

for x in parsed_json_camera:
	if "red" not in list(x):
		continue
	red.append(x["red"])

print(str(red[:10]))

parsed_json = {}
with open(json_file) as data_file:
    parsed_json = json.load(data_file)

for x in parsed_json:
    # Don't consider the first json blob
    if "x" not in list(x):
        continue
    if x['sensorType'] == "gyro":
    	x_axis.append(x['x'])
    	y_axis.append(x['y'])
    	z_axis.append(x['z'])

print(str(len(x_axis)))

# Apply butterworth bandpass filter and plot each axis on the same graph
# The blue is unfiltered, the green is filtered, red is fft, magenta is te
x_filtered = butter_bandpass_filter(x_axis, lowcut_global, highcut_global, 100, 5)
#x_fft = np.fft.fft(x_filtered)
x_te = []
for i in range(len(x_filtered)):
    x_te.append(np.sum(np.square(x_filtered[i:i + 1001])))

#plt.figure(1)
#plt.plot(x_axis, 'b')
#plt.plot(x_filtered, 'g')
#plt.plot(x_te, 'm')
#plt.show()
#plt.close(1)

y_filtered = butter_bandpass_filter(y_axis, lowcut_global, highcut_global, 100, 5)
#y_fft = np.fft.fft(y_filtered)
y_te = []
for i in range(len(y_filtered)):
    y_te.append(np.sum(np.square(y_filtered[i:i + 1001])))
#plt.figure(2)
#plt.plot(y_axis, 'b')
#plt.plot(y_filtered, 'g')
#plt.plot(y_te, 'm')
#plt.show()
#plt.close(2)

z_filtered = butter_bandpass_filter(z_axis, lowcut_global, highcut_global, 100, 5)
#z_fft = np.fft.fft(z_filtered)
z_te = []
for i in range(len(z_filtered)):
    z_te.append(np.sum(np.square(z_filtered[i:i + 1001])))
#plt.figure(3)
#plt.plot(z_axis, 'b')
#plt.plot(z_filtered, 'g')
#plt.plot(z_te, 'm')
#plt.show()
#plt.close(3)



#plt.figure(4)
#plt.plot(te, 'b')
#plt.show()
#plt.close(4)

#Plot the energy
#plt.figure(2)
#plt.plot(x_te, 'b')
#plt.plot(y_te, 'g')
#plt.plot(z_te, 'r')
#plt.show()
#plt.close(2)

# Find the standard deviation of sf(t) over time in each window with 10 seconds
x_std = []
for i in range(len(x_filtered)):
    x_std.append(np.std(x_filtered[i:i + 1001]))

y_std = []
for i in range(len(y_filtered)):
    y_std.append(np.std(y_filtered[i:i+1001]))

z_std = []
for i in range(len(z_filtered)):
    z_std.append(np.std(z_filtered[i:i+1001]))


#plt.figure(3)
#plt.plot(x_std, 'b')
#plt.plot(y_std, 'g')
#plt.plot(z_std, 'r')
#plt.show()
#plt.close(3)


te = np.add(x_te, np.add(y_te, z_te))
tstd = np.add(x_std, np.add(y_std, z_std))

plt.figure(1)
plt.plot(te[300:], 'b')
plt.show()


plt.figure(2)
plt.plot(tstd[300:], 'g')
plt.show()


plt.figure(3)
plt.plot(red[300:], 'r')
plt.show()

