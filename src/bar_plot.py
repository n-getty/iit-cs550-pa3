#!/usr/bin/python
"""
Bar chart demo with pairs of bars grouped for easy comparison.
"""
import numpy as np
import matplotlib.pyplot as plt


n_groups = 7

means_men = (0.0, 16.0, 27.4, 60.8, 81.6, 87.3, 81.0)
#std_men = (2, 3, 4, 1, 2)

means_women = (0.0,3.3, 08.8, 6.3, 11.2, 5.4,4.5)

#women = (3, 5, 2, 3, 3)

fig, ax = plt.subplots()

index = np.arange(n_groups)
bar_width = 0.35

opacity = 0.4
#error_config = {'ecolor': '0.3'}

rects1 = plt.bar(index, means_men, bar_width,
                 alpha=opacity,
                 color='b',
                 #yerr=std_men,
                 #error_kw=error_config,
                 label='Pull')

rects2 = plt.bar(index + bar_width, means_women, bar_width,
                 alpha=opacity,
		 color='r',
		 #yerr=std_women,
		 #error_kw=error_config,
		 label='Push')

plt.xlabel('Number of Concurrent Downloaders')
plt.ylabel('Misses / Hits')
plt.title('Ratio of Misses to Hits')
plt.xticks(index + bar_width / 2, ('1', '2', '3', '4', '5', '6','7'))
plt.legend()

plt.tight_layout()
plt.show()
