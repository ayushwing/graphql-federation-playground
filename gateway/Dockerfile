# Apollo Router gateway
# The supergraph schema is composed at image build time using rover.
# In CI, regenerate via: rover supergraph compose --config supergraph.yaml > supergraph.graphql

FROM ghcr.io/apollographql/router:v1.45.0

# Copy the pre-composed supergraph schema and router config
COPY supergraph.graphql /dist/schema/supergraph.graphql
COPY router.yaml /dist/config.yaml

EXPOSE 4000

CMD ["--config", "/dist/config.yaml", "--supergraph", "/dist/schema/supergraph.graphql"]
