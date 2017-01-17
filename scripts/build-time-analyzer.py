import re
import sys
import urllib.request
from datetime import datetime, timedelta

build_id = sys.argv[1]

tasks = []

pattern = ".*\[INFO\] --- .*"

_url = "http://bamboo.hcom/download/BOOKING-BA-BUTA/build_logs/BOOKING-BA-BUTA-%s.log" % build_id
with urllib.request.urlopen(_url) as fetcher:
    for _line in fetcher:
        line = _line.decode('utf-8')
        if re.match(pattern, line):
            parts = line.split()
            timestamp = datetime.strptime("".join(parts[1:3]), "%d-%b-%Y%H:%M:%S")
            task = parts[5]
            target = parts[8]
            tasks.append((task, target, timestamp))

print("%10s | %20s | %s" % ('*' * 10, '*' * 20, '*' * 20))
for i in range(len(tasks)):
    duration = timedelta()
    if len(tasks) - 1 > i:
        duration = tasks[i + 1][2] - tasks[i][2]
    print("%10s | %-30s | %s" % (duration, tasks[i][1], tasks[i][0]))
