import sys

def prependZeroIfNeeded(integer):
	if(len(str(integer)) == 1):
		return "0" + str(integer);
	else:
		return str(integer);


def generateIntervals(start):
	split = start.split(":");

	hours = int(split[0]);
	mins = int(split[1]);

	firstMins = mins;
	firstHours = hours;
	
	secondMins = -1;	
	secondHours = hours;

	items = "";
	cItem = "";

	for i in range(0, 10):
		cItem = "<item>";

		cItem += prependZeroIfNeeded(firstHours);
		cItem += ":";
		cItem += prependZeroIfNeeded(firstMins);
		cItem += ",";

		secondMins = firstMins + 2;
		if(secondMins >= 60):
			secondHours = firstHours + 1;
			secondMins = 0;

		cItem += prependZeroIfNeeded(secondHours);
		cItem += ":";
		cItem += prependZeroIfNeeded(secondMins);
		cItem += "</item>"

		firstMins = secondMins;
		firstHours = secondHours;

		items += cItem + "\n";

	return items[0:len(items)-1]

arg = sys.argv;

print generateIntervals(arg[1])

