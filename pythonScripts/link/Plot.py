import numpy as np
import matplotlib.pyplot as plt

def getColumn(data, idx):
	column = []
	for d in data:
		column.append(d[idx])

	return column

def plotCorrelations(xVals, yVals, metricsList, err, path):
	fig, ax = plt.subplots()

	print len(err[0])
	for i in range(0, len(yVals)):
		# plt.errorbar(xVals, yVals[i], xerr=0.0, yerr=err[i])
		
		ax.plot(xVals, yVals[i], '-', linewidth=2, label=metricsList[i])
	
	# plt.show()

	lgd = ax.legend(loc='upper center', bbox_to_anchor=(0.5,-0.1))
	ax.set_xlabel('Week index')
	ax.set_ylabel("Pearson's coefficent")
	plt.axhline(0, color='black')
	plt.savefig(path, bbox_extra_artists=(lgd,), bbox_inches='tight')

def plotDistMetrics(xVals, yVals, metricList,path):
	fig, ax = plt.subplots()

	for i in range(0, len(yVals)):
		ax.plot(xVals, yVals[i], '-', linewidth=2, label=metricList[i])

	lgd = ax.legend(loc='upper center', bbox_to_anchor=(0.5,-0.1))
	ax.set_xlabel('Two-days period index')
	ax.set_ylabel("Meters")
	plt.xticks(xVals)
	plt.savefig(path, bbox_extra_artists=(lgd,), bbox_inches='tight')

def plotDevAndGyr(xVals, yVals, metricList, path):
	fig, ax = plt.subplots()

	for i in range(0, len(yVals)):
		ax.plot(xVals, yVals[i], '-', linewidth=2, label=metricList[i])

	lgd = ax.legend(loc='upper center', bbox_to_anchor=(0.5,-0.1))
	ax.set_xlabel('Two-days period index')
	ax.set_ylabel("Value")
	plt.xticks(xVals)
	plt.savefig(path, bbox_extra_artists=(lgd,), bbox_inches='tight')

def plotDiffPlaces(xVals, yVals, path):
	fig, ax = plt.subplots()
	ax.plot(xVals, yVals[0], '-', linewidth=2, label='Different places visited')
	ax.plot(xVals, yVals[1], '-', linewidth=2, label='Relevant places visited')

	lgd = ax.legend(loc='upper center', bbox_to_anchor=(0.5,-0.1))
	ax.set_xlabel('Two-days period index')
	ax.set_ylabel("Value")
	plt.xticks(xVals)
	plt.savefig(path, bbox_extra_artists=(lgd,), bbox_inches='tight')

def plotMetrics(xVals, yVals, metricList, path):

	plotDistMetrics(xVals,[yVals[0], yVals[1], yVals[4]], ['Total distance', 'Max distance', 'Max distance from home'],path + "_distMetrics")
	plotDevAndGyr(xVals, [yVals[2], yVals[3]], ['Radius of gyration', 'Standard deviation of displacements'],path + "_devGyrMetrics")
	plotDiffPlaces(xVals, [yVals[5], yVals[6]], path + "_placesMetric")

	fig, ax = plt.subplots()

	for i in range(0, len(yVals)):
		# plt.errorbar(xVals, yVals[i], xerr=0.0, yerr=err[i])
		
		ax.plot(xVals, yVals[i], '-', linewidth=2, label=metricList[i])
	
	# plt.show()

	lgd = ax.legend(loc='upper center', bbox_to_anchor=(0.5,-0.1))
	ax.set_xlabel('Two-days period index')
	ax.set_ylabel("Value")
	plt.axhline(0, color='black')
	plt.xticks(xVals)
	plt.savefig(path + "_allMetrics", bbox_extra_artists=(lgd,), bbox_inches='tight')

def plotDataCountHeatMap(rowLabels, colLabels, data):
	fig, ax = plt.subplots()
	heatmap = ax.pcolor(data, cmap=plt.cm.Blues)

	xPos = []
	for i in range(0, len(colLabels)):
		xPos.append(i + 0.5)

	yPos = []
	for i in range(0, len(rowLabels)):
		yPos.append(i + 0.5)

	ax.set_xticks(xPos, minor=False)
	ax.set_yticks(yPos, minor=False)

	ax.set_xticklabels(colLabels, minor=False, rotation='vertical')
	ax.set_yticklabels(rowLabels, minor=False)
	cbar = plt.colorbar(heatmap)
	# ax.xaxis.tick_bottom()
	plt.show()
