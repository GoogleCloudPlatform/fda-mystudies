import os, sys
import numpy as np
import json
import matplotlib.pyplot as plt
import math
from scipy.signal import butter, lfilter


def butter_bandpass(lowcut, highcut, fs=60, order=5):
    nyq = 0.5 * fs
    low = lowcut / nyq
    high = highcut / nyq
    b, a = butter(order, [low, high], btype='band')
    return b, a

def butter_bandpass_filter(data, lowcut, highcut, fs=60, order=5):
    b, a = butter_bandpass(lowcut, highcut, fs, order=order)
    y = lfilter(b, a, data)
    return y

threshold = .10
lowcut_global = 2
highcut_global = 25

# Parse the json
json_file = "Michael.json"
x_axis = []
y_axis = []
z_axis = []

json_file_camera = "Michael-rgb.json"
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

num_samples = len(x_axis)

# Apply butterworth bandpass filter and plot each axis on the same graph
# The blue is unfiltered, the green is filtered, red is fft, magenta is te
x_filtered = butter_bandpass_filter(x_axis, lowcut_global, highcut_global, 100, 5)

#x_fft = np.fft.fft(x_filtered)
x_te = []
for i in range(0, len(x_filtered), 500):
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
for i in range(0, len(x_filtered), 500):
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
for i in range(0, len(x_filtered), 500):
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
for i in range(0, len(x_filtered), 500):
    x_std.append(np.std(x_filtered[i:i + 1001]))

y_std = []
for i in range(0, len(y_filtered), 500):
    y_std.append(np.std(y_filtered[i:i+1001]))

z_std = []
for i in range(0, len(z_filtered), 500):
    z_std.append(np.std(z_filtered[i:i+1001]))


#plt.figure(3)
#plt.plot(x_std, 'b')
#plt.plot(y_std, 'g')
#plt.plot(z_std, 'r')
#plt.show()
#plt.close(3)


te = np.add(x_te, np.add(y_te, z_te))
tstd = np.add(x_std, np.add(y_std, z_std))
threshold_line = [threshold] * num_samples

#plt.figure(1)
#plt.plot(te, 'b')
##plt.plot(threshold_line, 'b')
#plt.show()
#plt.close(1)

#plt.figure(2)
#plt.plot(tstd[300:], 'g')
#plt.show()
#plt.close(2)



# Look at red, returns a filtered heart rate signal

def filter_heart_rate(hr, order):
	global_mean = np.mean(hr)
	centralized_hr = [x - global_mean for x in hr]

	# Bandpass the centered signal
	bandpass_centralized = butter_bandpass_filter(centralized_hr, lowcut_global, highcut_global, 60, 5)

	# Local Mean Centering and filter
	copy = bandpass_centralized
	y = [0] * len(bandpass_centralized)
	
	for i in range(0, len(bandpass_centralized) - order):
		if i < (order/2):
			continue
		temp_sequence = bandpass_centralized[int(i - order/2): int(i + order/2 + 1)]
		mean = np.mean(temp_sequence)

		temp_sequence = [x - mean for x in temp_sequence]
		
		max_val = max(temp_sequence)
		min_val = min(temp_sequence)
		sum_val = np.sum(temp_sequence)
		constant = 0.0000001


		y[i] = (((bandpass_centralized[i] - max_val - min_val) -
              (sum_val - max_val) / (order - 1)) /
               (max_val - min_val + constant))

	return y

norm = 0
for x in red:
	norm += x*x

norm = math.sqrt(norm)

red = [float(x)/norm for x in red]

#plt.figure(1)
#plt.plot(red[2000:3000], 'r')
#plt.show()
#plt.close(1)

#plt.figure(2)
#plt.plot(filter_heart_rate(red, 67)[2000:3000], 'b')
#plt.show()
#plt.close(2)

#plt.figure(3)
#plt.plot(te, 'r')
#plt.show()
#plt.close(3)

#plt.figure(4)
#plt.plot(tstd, 'r')
#plt.show()
#plt.close(4)

a = []
for i in range(len(x_axis)):
	curr_x = x_axis[0]
	prev_x = x_axis[i]

	curr_y = y_axis[0]
	prev_y = y_axis[i]

	curr_z = z_axis[0]
	prev_z = z_axis[i]

	dp = curr_x * prev_x + curr_y * prev_y + curr_z * prev_z
	a.append(dp)

plt.figure(5)
plt.plot(a, 'r')
plt.show()
plt.close(5)


c = []
for i in range(len(x_axis)):
	curr_x = x_axis[0]
	prev_x = x_axis[i]

	curr_y = y_axis[0]
	prev_y = y_axis[i]

	curr_z = z_axis[0]
	prev_z = z_axis[i]

	left = math.sqrt(curr_x * curr_x + curr_y * curr_y + curr_z * curr_z)
	right = math.sqrt(prev_x * prev_x + prev_y * prev_y + prev_z * prev_z)

	denom = left * right

	num = curr_x * prev_x + curr_y * prev_y + curr_z * prev_z

	c.append(num / denom)

# Normalize c
norm = 0
for x in c:
	norm += x * x

norm = math.sqrt(norm)
c = [x/norm for x in c]

plt.figure(6)
plt.plot(c, 'r')
plt.show()
plt.close(6)

new_std = []
for i in range(0, len(c), 500):
    new_std.append(np.std(c[i:i + 1001]))

plt.figure(7)
plt.plot(new_std, 'r')
plt.show()
plt.close(7)



