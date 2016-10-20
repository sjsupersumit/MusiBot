#!flask/bin/python
from flask import Flask
from flask import abort

from flask import Response, request
from werkzeug.datastructures import Headers
from time import time
from re import findall
from flask import send_file
import os
import downloader

app = Flask(__name__)


@app.route('/')
def index():
    return "I am BoT, MusiBot. "


@app.route('/musicbot/download/song/<string:query_string>', methods=['GET'])
def get_mp3_file(query_string):
    if len(query_string) == 0:
        return "GTH"
    # video_id =  downloader.search_for_video("raise ")
    # downloader.download_mp3(video_id)

    fname = '/Users/sumit.jha/Documents/personal/MusiBot/downloader/Dorothy - Raise Hell-rmYyPcEQKU4.mp3'
    f = open(fname, "rb")

    return send_file(fname, mimetype='audio/mp3')




if __name__ == '__main__':
    app.run()
