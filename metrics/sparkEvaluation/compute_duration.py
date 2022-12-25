import re
import math

path = './BEST/data_10000.txt'

pattern = r'"batchDuration":(\d+),'
with open(path, 'r') as f:
    text = f.read()
matches = re.findall(pattern, text)

conv = [int(match) for match in matches][2:]
mean = sum(conv) / (len(conv))

print("Average Time of Batchduration ms = ",  mean)
print("Minimum Time of Batchduration in ms = ", min(conv))
print("Maximum Time of Batchduration in ms = ", max(conv))
