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


# Parse the json
json_file = "heartRate_motion.json"
x_axis = []
y_axis = []
z_axis = []

parsed_json = {}
with open(json_file) as data_file:
    parsed_json = json.load(data_file)

for x in parsed_json:
    # Don't consider the first json blob
    if "x" not in list(x):
        continue
    x_axis.append(x['x'])
    y_axis.append(x['y'])
    z_axis.append(x['z'])

# Apply butterworth bandpass filter and plot each axis on the same graph
# The blue is unfiltered, the green is filtered, red is fft
x_filtered = butter_bandpass_filter(x_axis, 15, 25, 100, 5)
x_fft = np.fft.fft(x_filtered)
x_te = np.sqrt(np.square(x_fft))
plt.figure(1)
plt.plot(x_axis, 'b')
plt.plot(x_filtered, 'g')
plt.plot(x_fft, 'r')
#plt.plot(x_te[:5000], 'm')
plt.show()
plt.close(1)

y_filtered = butter_bandpass_filter(y_axis, 15, 25, 100, 5)
y_fft = np.fft.fft(y_filtered)
y_te = np.sqrt(np.square(y_fft))
plt.figure(2)
plt.plot(y_axis, 'b')
plt.plot(y_filtered, 'g')
plt.plot(y_fft, 'r')
#plt.plot(y_te[:5000], 'm')
plt.show()
plt.close(2)

z_filtered = butter_bandpass_filter(z_axis, 15, 25, 100, 5)
z_fft = np.fft.fft(z_filtered)
z_te = np.sqrt(np.square(z_fft))
plt.figure(3)
plt.plot(z_axis, 'b')
plt.plot(z_filtered, 'g')
plt.plot(z_fft, 'r')
#plt.plot(z_te[:5000], 'm')
plt.show()
plt.close(3)


te = np.add(x_te, np.add(y_te, z_te))
plt.figure(4)
plt.plot(te, 'b')
plt.show()
plt.close(4)


# Plot the total energy over time
