#encoding=UTF-8

import logging
import os.path
import sys
import multiprocessing

from gensim.corpora import WikiCorpus
from gensim.models import Word2Vec
from gensim.models.word2vec import LineSentence

from sklearn.cluster import MiniBatchKMeans

import uniout

if __name__ == '__main__':

    program = os.path.basename(sys.argv[0])
    logger = logging.getLogger(program)

    logging.basicConfig(format='%(asctime)s: %(levelname)s:%(message)s')
    logging.root.setLevel(level=logging.INFO)

    inp = sys.argv[1]
    model = Word2Vec(LineSentence(inp), size=100, window=100, min_count=10, workers=multiprocessing.cpu_count())
    model.init_sims(replace=False)
    
    print model.most_similar(positive=[u'臺北'], topn=10)
    print model.most_similar(positive=[u'臺北', u'淡水'], topn=10)
    print model.most_similar(positive=[u'臺北', u'日月潭'], negative=[u'南投'], topn=10)
    #print model.most_similar(positive=[u'柯文哲', u'臺北市'], topn=10)
    #print model.most_similar(positive=[u'柯文哲', u'新北市'], negative=[u'臺北市'], topn=10)
 
    model.clear_sims()

    """
    num_clusters = word_vectors.shape[0] / 10

    kmeans_clustering = MiniBatchKMeans(n_clusters=num_clusters, verbose=1)
    idx = kmeans_clustering.fit_predict(word_vectors)

    word_centroid_map = dict(zip(model.index2word, idx))

    for cluster in xrange(0,10):
        words = []
        for i in xrange(0,len(word_centroid_map.values())):
            if(word_centroid_map.values()[i] == cluster):
                words.append(word_centroid_map.keys()[i])
        print(words)
    """
