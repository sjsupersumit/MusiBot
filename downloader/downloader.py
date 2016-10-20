from __future__ import unicode_literals
import youtube_dl
import os
from bs4 import BeautifulSoup
import urllib2

YT_BASE_URL = "http://www.youtube.com/results?search_query="


def main():
    video_id =  search_for_video("raise ")
    download_mp3(video_id)






def transfer(d , copy_dir):
    print "Copying Files from %s to %s" % (d, copy_dir)
    file_count = 0
    for i in d.walk():
        if i.isfile() and i.endswith('mp3'):
            file_count += 1
            print "Copying %s" % i

    print 'Transferred %s files' % file_count




def download_mp3(video_id):
    ydl_opts = {
    'format': 'bestaudio/best',
    'postprocessors': [{
        'key': 'FFmpegExtractAudio',
        'preferredcodec': 'mp3',
        'preferredquality': '192',
    }],
}
    with youtube_dl.YoutubeDL(ydl_opts) as ydl:
        ydl.download(['http://www.youtube.com/watch?v=' + video_id + ' -k'])
        curr_dir = os.getcwd()


def search_for_video(keyword):
    keyword.strip()
    query=""
    for w in keyword.split(" "):
        query+= w + "+"

    final_url = YT_BASE_URL + query
    page = urllib2.urlopen(final_url)
    soup = BeautifulSoup(page.read(), "lxml")
    results_div = soup.find("div", {"id":"results"})


    for a in results_div.find_all('a', href=True):
        if "watch" in a['href']:
            true_link = a['href']
            true_link_split = true_link.split("=")
            return true_link_split[1]











if __name__ == '__main__':
    main()
