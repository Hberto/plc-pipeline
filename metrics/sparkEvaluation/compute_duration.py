import re
import math

path = 'data.txt'

pattern = r'"batchDuration":(\d+),'
with open(path, 'r') as f:
    text = f.read()
matches = re.findall(pattern, text)

conv = [int(match) for match in matches][2:]
mean = sum(conv) / (len(conv))
print(mean)
