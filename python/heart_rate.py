import os, sys
import math
import numpy as np

# Bandpass signal functions useful in filtering
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

# Gets the heartrate signal from json?
def get_heartrate(heartrate_data, window_length=10, lowcut=1, highcut=25, bandpass_order=128):
	return 0


#' Bandpass and sorted mean filter the given signal
#'
#' @param x A time series numeric data
#' @param mean_filter_order Length of the sorted mean filter window
#' @param frequency_range Frequency range in Hz for the bandpass filter parameters
#' @param bandpass_order Order (length) of the bandpass filter to be used for filtering
#' @param sampling_rate The sampling rate (fs) of the time series data
#' @return The filtered time series data
def get_filtered_signal(x, sampling_rate, mean_filter_order=33, bandpass_order=128, lowcut=2, highcut=25):
	# If we have NaN data, make it 0
	for i in range(len(x)):
		if x[i] is None:
			x[i] = 0

	# Butter Bandpass Filter
	x = butter_bandpass_filter(x, lowcut, highcut, sampling_rate, bandpass_order)

	# Do mean filter on given signal
	y = [0] * len(x)
	for i in range(len(x)):
		if i < mean_filter_order:
			continue
		temp_sequence = x[int(i - order/2): int(i + order/2 + 1)]
		mean = np.mean(temp_sequence)

		temp_sequence = [x - mean for x in temp_sequence]
		
		max_val = max(temp_sequence)
		min_val = min(temp_sequence)
		sum_val = np.sum(temp_sequence)
		constant = 0.0000001


		y[i] = (((x[i] - max_val - min_val) - (sum_val - max_val) / (order - 1)) /
               (max_val - min_val + constant))

	return y


def get_hr_from_time_series(x, sampling_rate, min_hr=40, max_hr=200):
	# Remove NaN from x
	for i in range(len(x)):
		if x[i] is None:
			x[i] = 0
			

	return 0

