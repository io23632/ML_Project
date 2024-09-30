import pandas as pd
import numpy as np
from scipy.signal import find_peaks
from scipy import stats

def average_accl(a_list, b_list, c_list):
    avg_result_accl = []
    for a, b, c in zip(a_list, b_list, c_list):
        sum_squares = np.array(a)**2 + np.array(b)**2 + np.array(c)**2
        sqrt = np.sqrt(sum_squares)
        average_accl = np.mean(sqrt)
        avg_result_accl.append(average_accl)
    return avg_result_accl

class FeatureStat:
    
    def __init__(self, df, x_list, y_list, z_list):
        self.df = df
        self.x_list = x_list
        self.y_list = y_list
        self.z_list = z_list
    
    def calculate_mean(self):
        self.df['x-mean'] = pd.Series(self.x_list).apply(np.mean)
        self.df['y-mean'] = pd.Series(self.y_list).apply(np.mean)
        self.df['z-mean'] = pd.Series(self.z_list).apply(np.mean)
        return self.df
    
    def standard_deviation(self):
        self.df['x-std'] = pd.Series(self.x_list).apply(np.std)
        self.df['y-std'] = pd.Series(self.y_list).apply(np.std)
        self.df['z-std'] = pd.Series(self.z_list).apply(np.std)
        return self.df
    
    def absolute_deviation(self):
        self.df['x-aab'] = pd.Series(self.x_list).apply(lambda x: np.mean(np.absolute(x - np.mean(x))))
        self.df['y-aab'] = pd.Series(self.y_list).apply(lambda y: np.mean(np.absolute(y - np.mean(y))))
        self.df['z-aab'] = pd.Series(self.z_list).apply(lambda z: np.mean(np.absolute(z - np.mean(z))))
        return self.df
    
    def min(self):
        self.df['x_min'] = pd.Series(self.x_list).apply(np.min)
        self.df['y_min'] = pd.Series(self.y_list).apply(np.min)
        self.df['z_min'] = pd.Series(self.z_list).apply(np.min)
        return self.df
    
    def median(self):
        self.df['x_median'] = pd.Series(self.x_list).apply(np.median)
        self.df['y_median'] = pd.Series(self.y_list).apply(np.median)
        self.df['z_median'] = pd.Series(self.z_list).apply(np.median)
        return self.df
    
    def interquartile_range(self):
        self.df['x_IQR'] = pd.Series(self.x_list).apply(lambda x: np.percentile(x, 75) - np.percentile(x, 25))
        self.df['y_IQR'] = pd.Series(self.y_list).apply(lambda x: np.percentile(x, 75) - np.percentile(x, 25))
        self.df['z_IQR'] = pd.Series(self.z_list).apply(lambda x: np.percentile(x, 75) - np.percentile(x, 25))
        return self.df
    
    def positive_count(self):
        self.df['x_pos_count'] = pd.Series(self.x_list).apply(lambda x: np.sum(x > 0))
        self.df['y_pos_count'] = pd.Series(self.y_list).apply(lambda x: np.sum(x > 0))
        self.df['z_pos_count'] = pd.Series(self.z_list).apply(lambda x: np.sum(x > 0))
        return self.df
    
    def negative_count(self):
        self.df['x_neg_count'] = pd.Series(self.x_list).apply(lambda x: np.sum(x < 0))
        self.df['y_neg_count'] = pd.Series(self.y_list).apply(lambda x: np.sum(x < 0))
        self.df['z_neg_count'] = pd.Series(self.z_list).apply(lambda x: np.sum(x < 0))
        return self.df
    
    def values_above_mean(self):
        self.df['x-above-mean'] = pd.Series(self.x_list).apply(lambda x: np.sum(x > np.mean(x)))
        self.df['y_above_mean'] = pd.Series(self.y_list).apply(lambda x: np.sum(x > np.mean(x)))
        self.df['z_above_mean'] = pd.Series(self.z_list).apply(lambda x: np.sum(x > np.mean(x)))
        return self.df
    
    def peak(self):
        self.df['x_peak_count'] = pd.Series(self.x_list).apply(lambda x: len(find_peaks(x)[0]))
        self.df['y_peak_count'] = pd.Series(self.y_list).apply(lambda x: len(find_peaks(x)[0]))
        self.df['z_peak_count'] = pd.Series(self.z_list).apply(lambda x: len(find_peaks(x)[0]))
        return self.df
    
    def skewness(self):
        self.df['x_skewness'] = pd.Series(self.x_list).apply(lambda x: stats.skew(x))
        self.df['y_skewness'] = pd.Series(self.y_list).apply(lambda x: stats.skew(x))
        self.df['z_skewness'] = pd.Series(self.z_list).apply(lambda x: stats.skew(x))
        return self.df
    
    def kurtosis(self):
        self.df['x_kurtosis'] = pd.Series(self.x_list).apply(lambda x: stats.kurtosis(x))
        self.df['y_kurtosis'] = pd.Series(self.y_list).apply(lambda x: stats.kurtosis(x))
        self.df['z_kurtosis'] = pd.Series(self.z_list).apply(lambda x: stats.kurtosis(x))
        return self.df
    
    def energy(self):
        self.df['x_energy'] = pd.Series(self.x_list).apply(lambda x: np.sum(x**2)/100)
        self.df['y_energy'] = pd.Series(self.y_list).apply(lambda x: np.sum(x**2)/100)
        self.df['z_energy'] = pd.Series(self.z_list).apply(lambda x: np.sum(x**2)/100)
        return self.df
    
    def cal_average_accel(self):
        self.df['average-accel'] = average_accl(self.x_list, self.y_list, self.z_list)
        return self.df
    
    def sma(self):
        self.df['sma'] = pd.Series(self.x_list).apply(lambda x: np.sum(np.abs(x)/100)) + \
                         pd.Series(self.y_list).apply(lambda y: np.sum(np.abs(y)/100)) + \
                         pd.Series(self.z_list).apply(lambda z: np.sum(np.abs(z)/100))
        return self.df
    def capture_indicies(self):
        self.df['x_argmax'] = pd.Series(self.x_list).apply(lambda x: np.argmax(x))
        self.df['y_argmax'] = pd.Series(self.y_list).apply(lambda x: np.argmax(x))
        self.df['z_argmax'] = pd.Series(self.z_list).apply(lambda x: np.argmax(x))

        # index of min value in time domain
        self.df['x_argmin'] = pd.Series(self.x_list).apply(lambda x: np.argmin(x))
        self.df['y_argmin'] = pd.Series(self.y_list).apply(lambda x: np.argmin(x))
        self.df['z_argmin'] = pd.Series(self.z_list).apply(lambda x: np.argmin(x))

        # absolute difference between above indices
        self.df['x_arg_diff'] = abs(self.df['x_argmax'] - self.df['x_argmin'])
        self.df['y_arg_diff'] = abs(self.df['y_argmax'] - self.df['y_argmin'])
        self.df['z_arg_diff'] = abs(self.df['z_argmax'] - self.df['z_argmin'])

        # index of max value in frequency domain
        self.df['x_argmax_fft'] = pd.Series(self.x_list).apply(lambda x: np.argmax(np.abs(np.fft.fft(x))[1:51]))
        self.df['y_argmax_fft'] = pd.Series(self.y_list).apply(lambda x: np.argmax(np.abs(np.fft.fft(x))[1:51]))
        self.df['z_argmax_fft'] = pd.Series(self.z_list).apply(lambda x: np.argmax(np.abs(np.fft.fft(x))[1:51]))

        # index of min value in frequency domain
        self.df['x_argmin_fft'] = pd.Series(self.x_list).apply(lambda x: np.argmin(np.abs(np.fft.fft(x))[1:51]))
        self.df['y_argmin_fft'] = pd.Series(self.y_list).apply(lambda x: np.argmin(np.abs(np.fft.fft(x))[1:51]))
        self.df['z_argmin_fft'] = pd.Series(self.z_list).apply(lambda x: np.argmin(np.abs(np.fft.fft(x))[1:51]))

        # absolute difference between above indices
        self.df['x_arg_diff_fft'] = abs(self.df['x_argmax_fft'] - self.df['x_argmin_fft'])
        self.df['y_arg_diff_fft'] = abs(self.df['y_argmax_fft'] - self.df['y_argmin_fft'])
        self.df['z_arg_diff_fft'] = abs(self.df['z_argmax_fft'] - self.df['z_argmin_fft'])

        
    
    def all(self):
        self.calculate_mean()
        self.standard_deviation()
        self.absolute_deviation()
        self.min()
        self.median()
        self.interquartile_range()
        self.positive_count()
        self.negative_count()
        self.values_above_mean()
        self.peak()
        self.skewness()
        self.kurtosis()
        self.energy()
        self.cal_average_accel()
        self.sma()
        self.capture_indicies()
        return self.df
