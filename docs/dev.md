## Running Solr

To run solr locally, navigate to the project folder in your terminal and execute:

```bash
docker run -it \
  -v "$PWD/src/main/resources/solr/:/var/solr/data/vault-catalog/:ro" \
  --network host \
  --rm \
  --name solr \
  solr
```

The index will be deleted as soon as the container is stopped. To persist the index, remove the `--rm` flag.

