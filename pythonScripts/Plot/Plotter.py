import pygal

class BarChartPlotter:
	def __init__(self, userID, tableName, xValues, yValues):
		self._xValues = xValues
		self._yValues = yValues
		self._userID = userID
		self._tableName = tableName
		self._chart = pygal.Bar(x_label_rotation=90)

	def plot(self):
		self._chart.title = "User " + str(self._userID) + ": " + self._tableName + " count"
		self._chart.x_labels = self._xValues
		self._chart.add('count', self._yValues)
		self._chart.render_to_file("Plots/" + self._tableName + "_count/user_" + str(self._userID))