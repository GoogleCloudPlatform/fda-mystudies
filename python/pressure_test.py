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

def parse_json():
	json_file_camera = user + "-rgb.json"

	parsed_json_camera = {}
	with open(json_file_camera) as data_file:
		parsed_json_camera = json.load(data_file)

	for x in parsed_json_camera:
		if "red" not in list(x):
			continue
		red.append(x["red"])

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
	    if x['sensorType'] == "accelerometer":
	    	x_accel.append(x['x'])
	    	y_accel.append(x['y'])
	    	z_accel.append(x['z'])

def calculate_total_energy(filtered, step):
	te = []
	for i in range(0, len(filtered), step):
		te.append(np.sum(np.square(filtered[i:i + 1001])))
	return te

def calculate_std(filtered, step):
	std = []
	for i in range(0, len(filtered), step):
		std.append(np.std(filtered[i:i + 1001]))
	return std

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

def normalize(original):
	ss = 0
	for x in original:
		ss += x*x

	ss = math.sqrt(ss)

	norm = [float(x)/ss for x in original]
	return norm


# Global variables
user = "Nick-2"
threshold = .10
lowcut_global = 2
highcut_global = 25
step = 500

# Parse the json
json_file = user + ".json"
x_axis = []
y_axis = []
z_axis = []

x_accel = []
y_accel = []
z_accel = []

red = []
parse_json()

num_samples = len(x_axis)

# Apply butterworth bandpass filter and plot each axis on the same graph
# The blue is unfiltered, the green is filtered, red is fft, magenta is te
x_filtered = butter_bandpass_filter(x_axis, lowcut_global, highcut_global, 100, 5)
x_te = calculate_total_energy(x_filtered, step)
x_std = calculate_std(x_filtered, step)


y_filtered = butter_bandpass_filter(y_axis, lowcut_global, highcut_global, 100, 5)
y_te = calculate_total_energy(y_filtered, step)
y_std = calculate_std(y_filtered, step)


z_filtered = butter_bandpass_filter(z_axis, lowcut_global, highcut_global, 100, 5)
z_te = calculate_total_energy(z_filtered, step)
z_std = calculate_std(z_filtered, step)


te = np.add(x_te, np.add(y_te, z_te))
tstd = np.add(x_std, np.add(y_std, z_std))
threshold_line = [threshold] * num_samples

# Look at red, returns a filtered heart rate signal
red = normalize(red)


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
c = normalize(c)

plt.figure(5)
plt.title("Dot Product Equation")
plt.plot(a, 'r')
plt.show()
plt.close(5)

plt.figure(6)
plt.title("Angle Equation")
plt.plot(c, 'r')
plt.show()
plt.close(6)

new_std = calculate_std(c, step)

plt.figure(7)
plt.title("Standard Deviation of Angle Equation")
plt.plot(new_std, 'r')
plt.show()
plt.close(7)

plt.figure(8)
plt.title("Accelerometer Readings")
plt.plot(x_accel, 'r')
plt.plot(y_accel, 'b')
plt.plot(z_accel, 'g')
plt.show()
plt.close(8)







