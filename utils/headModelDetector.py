import json
import os

fileNames = []

for fileName in os.listdir("item/"):
    fileNames.append("item/" + fileName)

for fileName in os.listdir("block/"):
    fileNames.append("block/" + fileName)

for fileName in fileNames:
    with open(fileName) as file:
        data = json.load(file)
        try:
            data["display"]["head"]  # If this rendering rule does not exist, an exception will be thrown
            print(fileName)
        except Exception:
            pass


