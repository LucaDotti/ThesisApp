MemotionUidToId = {
	'240262d85b37317c': 1,
	'98a902cd34898cf4': 2,
	'b49af04b7c27771b': 3,
	'30bcc59acd26e34': 4,
	'b3e6e8cc19df4850': 5,
	'b9aeaa3d64d97374': 6,
	'8800572df1fb6dc5': 7,
	'4fc1a56647115a4b': 8,
	'a2204db4772585d9': 9,
	'c9cb25e1deddb09d': 10,
	'bdb44fff25f4cddd': 11,
	'd0c4a8e5d7f6545f': 12,
}

MemotionIdToUid = {
	1: '240262d85b37317c',
	2: '98a902cd34898cf4',
	3: 'b49af04b7c27771b',
	4: '30bcc59acd26e34',
	5: 'b3e6e8cc19df4850',
	6: 'b9aeaa3d64d97374',
	7: '8800572df1fb6dc5',
	8: '4fc1a56647115a4b',
	9: 'a2204db4772585d9',
	10: 'c9cb25e1deddb09d',
	11: 'bdb44fff25f4cddd',
	12: 'd0c4a8e5d7f6545f'
}

def getMemotionUserId(username):
	return MemotionUidToId[username]

def getMemotionUserUid(id):
	return MemotionIdToUid[id]

def getUsersList():
	return MemotionIdToUid