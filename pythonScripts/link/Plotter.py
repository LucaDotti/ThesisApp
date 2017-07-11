import pygal
import userIdToId as u

class BarChartPlotter:
	def __init__(self, subject, tableName, xValues, yValues, path, format):
		self._xValues = xValues
		self._yValues = yValues
		self._subject = subject
		self._tableName = tableName
		self._chart = pygal.Bar(x_label_rotation=90)
		self._path = path
		self._format = format

	def plot(self):
		self._chart.title = "User " + str(self._subject) + ": " + self._tableName + " count"
		self._chart.x_labels = self._xValues
		self._chart.add('count', self._yValues)
		path = self._path + "/" + self._tableName + "_count/user_" + str(self._subject)
		if(self._format == 'png'):
			self._chart.render_to_png(path)
		else:
			self._chart.render_to_file(path)

class ScatterChartPlotter:
	def __init__(self, subject, tableName, xValues, yValues, path, format):
		self._xValues = xValues
		self._yValues = yValues
		self._subject = subject
		self._tableName = tableName
		self._chart = pygal.XY(stroke=False)
		self._path = path
		self._format = format

	def plot(self):
		self._chart.title = "User " + str(self._subject) + ": " + self._tableName + " count"
		self._chart.x_labels = self._xValues
		self._chart.add("", zip(self._xValues, self._yValues))
		path = self._path + "/" + self._tableName + "_count/user_" + str(self._subject)
		if(self._format == 'png'):
			self._chart.render_to_png(path)
		else:
			self._chart.render_to_file(path)

class DotChartPlotter:
	def __init__(self, tableName, xValues, yValues, path, format):
		self._xValues = xValues
		self._yValues = yValues
		self._tableName = tableName
		self._chart = pygal.Dot(x_label_rotation=90, spacing=-50, show_legend=False, show_x_guides=False, show_y_guides=False, width=400, height=150)
		self._path = path
		self._format = format

	def plot(self):
		self._chart.title = "All users: " + self._tableName + " count"
		self._chart.x_labels = self._xValues
		i = 1
		for user in self._yValues:
			self._chart.add('user_' + u.getMemotionUserUid(i), user)
			i = i + 1
		path = self._path + "/" + self._tableName + "_count/all_users"
		if self._format == "png":
			self._chart.render_to_png(path)
		else:
			self._chart.render_to_file(path)

class LineChartPlotter:
	def __init__(self, fileName, xValues, yValues, metricList, path, format):
		self._metricList = metricList
		self._xValues = xValues
		self._yValues = yValues
		self._fileName = fileName
		self._chart = pygal.Line(x_label_rotation=90, show_legend=False, show_x_guides=False, show_y_guides=False, width=200, height=200, dots_size=2)
		self._path = path
		self._format = format

	def plot(self):
		self._chart.title = self._fileName + " / metrics"
		self._chart.x_labels = self._xValues
		print self._yValues
		for i in range(0, len(self._yValues)):
			
			self._chart.add(self._metricList[i], self._yValues[i])
		# i = 1
		# for user in self._yValues:
		# 	self._chart.add('user_' + u.getMemotionUserUid(i), user)
		# 	i = i + 1
		path = self._path + "/" + self._fileName
		if self._format == "png":
			self._chart.render_to_png(path)
		else:
			self._chart.render_to_file(path)
			