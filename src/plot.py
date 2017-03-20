#!/usr/bin/python

import sys
import matplotlib.pyplot as plt
import matplotlib
import numpy as np
from math import sqrt

matplotlib.rcParams['ps.useafm'] = True
matplotlib.rcParams['pdf.use14corefonts'] = True
matplotlib.rcParams['text.usetex'] = True

def plot_all():
    
    data = np.loadtxt("data/linear/single/low/parse.txt")
    linear_single_low = np.sort(data)
    linear_single_low_p = 1. * np.arange(len(linear_single_low)) / float(len(linear_single_low) - 1)

    data = np.loadtxt("data/linear/single/mid/parse.txt")
    linear_single_mid = np.sort(data)
    linear_single_mid_p = 1. * np.arange(len(linear_single_mid)) / float(len(linear_single_mid) - 1)
    
    data = np.loadtxt("data/linear/single/high/parse.txt")
    linear_single_high = np.sort(data)
    linear_single_high_p = 1. * np.arange(len(linear_single_high)) / float(len(linear_single_high) - 1)

    data = np.loadtxt("data/linear/half/low/parse.txt")
    linear_half_low = np.sort(data)
    linear_half_low_p = 1. * np.arange(len(linear_half_low)) / float(len(linear_half_low) - 1)

    data = np.loadtxt("data/linear/half/mid/parse.txt")
    linear_half_mid = np.sort(data)
    linear_half_mid_p = 1. * np.arange(len(linear_half_mid)) / float(len(linear_half_mid) - 1)

    data = np.loadtxt("data/linear/half/high/parse.txt")
    linear_half_high = np.sort(data)
    linear_half_high_p = 1. * np.arange(len(linear_half_high)) / float(len(linear_half_high) - 1)


    data = np.loadtxt("data/star/single/low/parse.txt")
    star_single_low = np.sort(data)
    star_single_low_p = 1. * np.arange(len(star_single_low)) / float(len(star_single_low) - 1)

    data = np.loadtxt("data/star/single/mid/parse.txt")
    star_single_mid = np.sort(data)
    star_single_mid_p = 1. * np.arange(len(star_single_mid)) / float(len(star_single_mid) - 1)
    
    data = np.loadtxt("data/star/single/high/parse.txt")
    star_single_high = np.sort(data)
    star_single_high_p = 1. * np.arange(len(star_single_high)) / float(len(star_single_high) - 1)

    data = np.loadtxt("data/star/half/low/parse.txt")
    star_half_low = np.sort(data)
    star_half_low_p = 1. * np.arange(len(star_half_low)) / float(len(star_half_low) - 1)

    data = np.loadtxt("data/star/half/mid/parse.txt")
    star_half_mid = np.sort(data)
    star_half_mid_p = 1. * np.arange(len(star_half_mid)) / float(len(star_half_mid) - 1)

    data = np.loadtxt("data/star/half/high/parse.txt")
    star_half_high = np.sort(data)
    star_half_high_p = 1. * np.arange(len(star_half_high)) / float(len(star_half_high) - 1)

    
    plt.figure()
    font = {'size':'15'}
    matplotlib.rc('font', **font)

    color = ('b','g','r','c','m','y','k','b','g')
    
    plt.subplot(2,1,1)
    plt.title('Linear Topology')
    plt.plot(linear_single_low, linear_single_low_p,
             color=color[0],
             label=('single query, 20\% overlap'))
    plt.plot(linear_single_mid, linear_single_mid_p,
             color=color[1],
             label=('single query, 40\% overlap'))
    plt.plot(linear_single_high, linear_single_high_p,
             color=color[2],
             label=('single query, 70\% overlap'))
    
    plt.plot(linear_half_low, linear_half_low_p,
             color=color[0],
             label=('multiple query, 20\% overlap'),
             linestyle='dashed')
    plt.plot(linear_half_mid, linear_half_mid_p,
             color=color[1],
             label=('multiple query, 40\% overlap'),
             linestyle='dashed')
    plt.plot(linear_half_high, linear_half_high_p,
             color=color[2],
             label=('mutiple query, 70\% overlap'),
             linestyle='dashed')
    plt.xlim([-0.1, 4000000000])
    plt.grid(True)
    plt.ylim([0, 1])
    plt.xlabel('Response time in (nanoSeconds)', fontsize=20)
    plt.ylabel('Fraction of Trials', fontsize=20)
    leg = plt.legend(fancybox=True,loc='lower right')#upper left')
                

    
    plt.subplot(2,1,2)
    plt.title('Star Topology')
    plt.plot(star_single_low, star_single_low_p,
             color=color[0],
             label=('single query, 20\% overlap'))
    plt.plot(star_single_mid, star_single_mid_p,
             color=color[1],
             label=('single query, 40\% overlap'))
    plt.plot(star_single_high, star_single_high_p,
             color=color[2],
             label=('single query, 70\% overlap'))
    
    plt.plot(star_half_low, star_half_low_p,
             color=color[0],
             label=('multiple query, 20\% overlap'),
             linestyle='dashed')
    plt.plot(star_half_mid, star_half_mid_p,
             color=color[1],
             label=('multiple query, 40\% overlap'),
             linestyle='dashed')
    plt.plot(star_half_high, star_half_high_p,
             color=color[2],
             label=('mutiple query, 70\% overlap'),
             linestyle='dashed')

    plt.grid(True)
    #plt.title('Linear Topology')
    leg = plt.legend(fancybox=True,loc='lower right')#upper left')
    leg.get_frame().set_alpha(1)
    plt.xlim([-0.1, 4000000000])
    #plt.xticks(range(time_start+1, time_end+1, 2))
    #plt.yticks(range(0, int(bw) + 1, 100))
    
    plt.ylim([0, 1])
    plt.xlabel('Response time in (nanoSeconds)', fontsize=20)
    plt.ylabel('Fraction of Trials', fontsize=20)
    plt.show()
    
if __name__ == '__main__':
    plot_all()
        
