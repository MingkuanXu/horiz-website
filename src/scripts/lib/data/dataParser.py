# -*- coding: utf-8 -*-

import csv
import os, time, sys
import pandas as pd # pylint: disable=import-error
import xml.etree.ElementTree as ET
import mysql, mysql.connector # pylint: disable=import-error
import chardet
import tqdm

currentDir = os.getcwd()
dataDir = os.path.join(currentDir, "../../../main/resources/static")
hsFilePath = os.path.join(dataDir, 'data/xlsxFiles/activities_HS.xlsx')
cFilePath = os.path.join(dataDir, 'data/xlsxFiles/activities_C.xlsx')
hsCatFilePath = '../hsAttributes.cat'
cCatFilePath = '../cAttributes.cat'
#xmlDir = os.path.join(currentDir, '../../../data/xmlFiles')

xmlDir = os.path.join(dataDir, 'data/xmlFiles')
#imgDir = os.path.join(currentDir, '../../../data/imgFiles')
imgDir = os.path.join(dataDir, 'data/imgFiles')

print(imgDir)

def getAttr(catFilePath):
    with open(catFilePath, 'r') as f:
        attrStr = f.readline()
        attr = attrStr.split(',')
        if('\n' in attr[-1]):
            attr[-1] = attr[-1].replace('\n', '')
        return attr

def readCSV(filePath, CatFilePath):
    activities = []
    with open(filePath, 'r') as csvFile:
        csvReader = csv.reader(csvFile, delimiter = ',')
        for row in csvReader:
            print(row)
    attrs = getAttr(CatFilePath)
    return attrs, activities

def readXLSX(filePath, catFilePath):
    xFile = pd.ExcelFile(filePath)
    xDataFrame = pd.read_excel(xFile)
    if('HS' in filePath):
        columnToRemove = xDataFrame.columns.values[-3:]
        xDataFrame = xDataFrame.drop(columnToRemove, axis = 1)
    attrs = getAttr(catFilePath)
    activities = []
    print('REDING FROM XLSM')
    for _, row in tqdm.tqdm(xDataFrame.iterrows()):
        attrList = []
        activityName = row['Name']
        try:
            activityName = str(activityName)
        except:
            activityName = activityName.encode("utf-8")
        if(chardet.detect(activityName)['encoding'] == 'ascii'):
            activityName = activityName.encode('utf-8')
        if(chardet.detect(activityName)['encoding'] != 'utf-8'):
            activityName = activityName.decode(chardet.detect(activityName)['encoding']).encode('utf-8')
        attrNum = 0
        missingVal = row.isnull()
        for i in range(1, len(attrs)):
            if not missingVal[attrs[i]]:
                attrNum += 1
                attr = row[attrs[i]]
                try:
                    attr = str(attr)
                except:
                    attr = attr.encode("utf-8")
                if(chardet.detect(attr)['encoding'] == 'ascii'):
                    attr = attr.encode('utf-8')
                if(chardet.detect(attr)['encoding'] != 'utf-8'):
                    attr = attr.decode(chardet.detect(attr)['encoding']).encode('utf-8')
                attrList.append(attr)
            else:
                attr = 'NaN'
                try:
                    attr = str(attr)
                except:
                    attr = attr.encode("utf-8")
                if(chardet.detect(attr)['encoding'] == 'ascii'):
                    attr = attr.encode('utf-8')
                if(chardet.detect(attr)['encoding'] != 'utf-8'):
                    attr = attr.decode(chardet.detect(attr)['encoding']).encode('utf-8')
                attrList.append(attr)
        attrList.append(attrNum)
        activities.append((activityName, attrList))
    return attrs, activities
    

def toSql(filePath, tableName, catFilePath):
    horizDB = mysql.connector.connect(
        host="localhost",
        user="root",
        passwd="12345.asdfg",
        port = "3306",
        charset="utf8"
        )
    dbCursor = horizDB.cursor()

    createDBQuery = 'CREATE DATABASE IF NOT EXISTS {}'.format('HorizDB')
    dbCursor.execute(createDBQuery)

    dbCursor.execute('USE {}'.format('HorizDB'))

    if('xlsx' in filePath):
        attrs, activities = readXLSX(filePath, catFilePath)
    elif('csv' in filePath):
        attrs, activities = readCSV(filePath, catFilePath)
    else:
        raise Exception('error: unrecognized file type')
    
    clearTableQuery = 'DROP TABLE IF EXISTS {}'.format(tableName)
    dbCursor.execute(clearTableQuery)

    createTableQuery = "CREATE TABLE IF NOT EXISTS {tableName} (Id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY"
    createTableQuery += ', Name VARCHAR(255) NOT NULL'
    createTableQuery += ', xmlPath VARCHAR(255) NOT NULL'
    createTableQuery += ', imgPath VARCHAR(255)'
    createTableQuery = createTableQuery.format(tableName = tableName)
    for i in range(1, len(attrs)):
        if(attrs[i] == 'Group'):
            attrs[i] = '`Group`'
        newAttrQuery = ', {attrName} VARCHAR(255)'.format(attrName = attrs[i])
        createTableQuery += newAttrQuery
    createTableQuery += ', Priority INT)'
    dbCursor.execute(createTableQuery)

    encodeQuery = 'ALTER TABLE {} CONVERT TO CHARACTER SET utf8 COLLATE utf8_unicode_ci'.format(tableName)
    dbCursor.execute(encodeQuery)

    print('WRITING TO DB')
    Id = 0
    for activity in tqdm.tqdm(activities):
        Id += 1
        insertQuery = "INSERT INTO {tableName} VALUES ({id}, {activityName}, '{xmlPath}', '{imgPath}'{valueStr})"
        valueStr = ", '{}'"*(len(activity[1]))
        valueStr = valueStr.format(*activity[1])
        relImgPath = "data/imgFiles/{}/{}-{}.jpg".format(tableName, tableName, Id)
        xmlPath = os.path.join(os.path.join(xmlDir, tableName), "{}-{}.xml".format(tableName, Id))
        relXmlPath = "data/xmlFiles/{}/{}-{}.xml".format(tableName, tableName, Id)
        insertQuery = insertQuery.format(
            tableName = tableName,
            id = Id,
            activityName = "'{}'".format(activity[0]),
            xmlPath = relXmlPath,
            imgPath = relImgPath,
            valueStr = valueStr
        )
        try:
            dbCursor.execute(insertQuery)
        except:
            print('unable to execute query')
            continue
        createXml(xmlPath, activity[0])
    horizDB.commit()

def createXml(xmlPath, name):
    root = ET.Element("contest", name = name.decode("utf8"))
    sampleComment = ET.SubElement(root, "comment")
    ET.SubElement(sampleComment, "content").text = "emptyDefaultComment".decode("utf8")
    ET.SubElement(sampleComment, "author").text = "John Doe".decode("utf8")
    ET.SubElement(sampleComment, "time").text = ""
    ET.SubElement(sampleComment, "authorType").text = ""
    tree = ET.ElementTree(root)
    tree.write(xmlPath, encoding='utf-8')

    
if __name__ == "__main__":
    toSql(hsFilePath, 'HSContest', hsCatFilePath)
    toSql(cFilePath, 'CContest', cCatFilePath)

