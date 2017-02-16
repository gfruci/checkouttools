#!/usr/bin/env python3
import sys

import requests

CODE_SEARCH_URL_TEMPLATE = "http://codesearch.hcom/api/v1/search?stats=fosho&repos=*&rng=%3A20&q={}&files={}&i=nope"
GIT_CLONE_URL_TEMPLATE = "ssh://git@stash.hcom:7999/{}/{}.git"


def collect_repos(query, files):
    resp = requests.get(CODE_SEARCH_URL_TEMPLATE.format(query, files)).json()
    for k, v in resp['Results'].items():
        project, repo = k.split("/")
        print(GIT_CLONE_URL_TEMPLATE.format(project, repo))
    pass


if __name__ == '__main__':
    collect_repos(sys.argv[1], sys.argv[2])
