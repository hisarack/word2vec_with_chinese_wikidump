
import logging
import os.path
import sys


from gensim.corpora import WikiCorpus

if __name__ == '__main__':

    program = os.path.basename(sys.argv[0])
    logger = logging.getLogger(program)

    logging.basicConfig(format='%(asctime)s: %(levelname)s:%(message)s')
    logging.root.setLevel(level=logging.INFO)

    inp, outp = sys.argv[1:3]

    space = " "
    i = 0

    output = open(outp,'w')
    wiki = WikiCorpus(inp, lemmatize=False, dictionary={})
    for text in wiki.get_texts():
        output.write(space.join(text) + "\n")
        i = i + 1
        if ( i % 10000 == 0 ):
            logger.info("Saved {} articles".format(i))

    output.close()
    logger.info("Finished {} articles".format(i))
