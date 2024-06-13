# simple python script for replacing '\r\n' with '@#' in the CSV file

import csv
import itertools as it
import operator as op

def grouper(iterable, n):
    return it.zip_longest(*[iter(iterable)] * n)

with open('Python_dataset.csv', encoding='utf-8') as inf, open('out.csv', 'w', newline='', encoding='utf-8') as outf:
    r, w = csv.reader(inf), csv.writer(outf)
    hdr = next(r)
    w.writerow(hdr)
    for row in grouper(filter(bool, map(op.methodcaller('replace', '\n', '@#'), it.chain.from_iterable(r))), len(hdr)):
        w.writerow(row)